package com.brixcore.task;

import com.brixcore.util.DigestUtils;
import com.brixcore.util.Hex;
import com.brixcore.util.Logging;
import com.brixcore.util.io.ChecksumMismatchException;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/* JADX INFO: loaded from: classes7.dex */
public class FileDownloadTask extends FetchTask<Void> {
    public static final IntegrityCheckHandler ZIP_INTEGRITY_CHECK_HANDLER = new IntegrityCheckHandler() { // from class: com.brixcore.task.FileDownloadTask$$ExternalSyntheticLambda0
        @Override // com.brixcore.task.FileDownloadTask.IntegrityCheckHandler
        public final void checkIntegrity(Path path, Path path2) throws IOException {
            FileDownloadTask.lambda$static$0(path, path2);
        }
    };
    private Path candidate;
    private final File file;
    private final IntegrityCheck integrityCheck;
    private final ArrayList<IntegrityCheckHandler> integrityCheckHandlers;

    public interface IntegrityCheckHandler {
        void checkIntegrity(Path path, Path path2) throws IOException;
    }

    public static class IntegrityCheck {
        private final String algorithm;
        private final String checksum;

        public IntegrityCheck(String algorithm, String checksum) {
            this.algorithm = (String) Objects.requireNonNull(algorithm);
            this.checksum = (String) Objects.requireNonNull(checksum);
        }

        public static IntegrityCheck of(String algorithm, String checksum) {
            if (checksum == null) {
                return null;
            }
            return new IntegrityCheck(algorithm, checksum);
        }

        public String getAlgorithm() {
            return this.algorithm;
        }

        public String getChecksum() {
            return this.checksum;
        }

        public MessageDigest createDigest() {
            return DigestUtils.getDigest(this.algorithm);
        }

        public void performCheck(MessageDigest digest) throws ChecksumMismatchException {
            String actualChecksum = Hex.encodeHex(digest.digest());
            if (!this.checksum.equalsIgnoreCase(actualChecksum)) {
                throw new ChecksumMismatchException(this.algorithm, this.checksum, actualChecksum);
            }
        }
    }

    public FileDownloadTask(URL url, File file) {
        this(url, file, (IntegrityCheck) null);
    }

    public FileDownloadTask(URL url, File file, IntegrityCheck integrityCheck) {
        this((List<URL>) Collections.singletonList(url), file, integrityCheck);
    }

    public FileDownloadTask(URL url, File file, IntegrityCheck integrityCheck, int retry) {
        this((List<URL>) Collections.singletonList(url), file, integrityCheck, retry);
    }

    public FileDownloadTask(List<URL> urls, File file) {
        this(urls, file, (IntegrityCheck) null);
    }

    public FileDownloadTask(List<URL> urls, File file, IntegrityCheck integrityCheck) {
        this(urls, file, integrityCheck, 3);
    }

    public FileDownloadTask(List<URL> urls, File file, IntegrityCheck integrityCheck, int retry) {
        super(urls, retry);
        this.integrityCheckHandlers = new ArrayList<>();
        this.file = file;
        this.integrityCheck = integrityCheck;
        setName(file.getName());
    }

    public File getFile() {
        return this.file;
    }

    public FileDownloadTask setCandidate(Path candidate) {
        this.candidate = candidate;
        return this;
    }

    public void addIntegrityCheckHandler(IntegrityCheckHandler handler) {
        this.integrityCheckHandlers.add((IntegrityCheckHandler) Objects.requireNonNull(handler));
    }

    @Override // com.brixcore.task.FetchTask
    protected FetchTask.EnumCheckETag shouldCheckETag() {
        if (this.integrityCheck != null && this.caching) {
            Optional<Path> cache = this.repository.checkExistentFile(this.candidate, this.integrityCheck.getAlgorithm(), this.integrityCheck.getChecksum());
            if (cache.isPresent()) {
                try {
                    FileUtils.copyFile(cache.get().toFile(), this.file);
                    Logging.LOG.log(Level.FINER, "Successfully verified file " + this.file + " from " + this.urls.get(0));
                    return FetchTask.EnumCheckETag.CACHED;
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Failed to copy cache files", (Throwable) e);
                }
            }
            return FetchTask.EnumCheckETag.NOT_CHECK_E_TAG;
        }
        return FetchTask.EnumCheckETag.CHECK_E_TAG;
    }

    @Override // com.brixcore.task.FetchTask
    protected void beforeDownload(URL url) {
        Logging.LOG.log(Level.FINER, "Downloading " + url + " to " + this.file);
    }

    @Override // com.brixcore.task.FetchTask
    protected void useCachedResult(Path cache) throws IOException {
        FileUtils.copyFile(cache.toFile(), this.file);
    }

    @Override // com.brixcore.task.FetchTask
    protected FetchTask.Context getContext(final URLConnection conn, final boolean checkETag) throws IOException {
        final Path temp = Files.createTempFile(null, null, new FileAttribute[0]);
        final RandomAccessFile rFile = new RandomAccessFile(temp.toFile(), "rw");
        final MessageDigest digest = this.integrityCheck != null ? this.integrityCheck.createDigest() : null;
        return new FetchTask.Context() { // from class: com.brixcore.task.FileDownloadTask.1
            @Override // com.brixcore.task.FetchTask.Context
            public void write(byte[] buffer, int offset, int len) throws IOException {
                if (digest != null) {
                    digest.update(buffer, offset, len);
                }
                rFile.write(buffer, offset, len);
            }

            @Override // java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                try {
                    rFile.close();
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Failed to close file: " + rFile, (Throwable) e);
                }
                if (!isSuccess()) {
                    try {
                        Files.delete(temp);
                        return;
                    } catch (IOException e2) {
                        Logging.LOG.log(Level.WARNING, "Failed to delete file: " + rFile, (Throwable) e2);
                        return;
                    }
                }
                for (IntegrityCheckHandler handler : FileDownloadTask.this.integrityCheckHandlers) {
                    handler.checkIntegrity(temp, FileDownloadTask.this.file.toPath());
                }
                Files.deleteIfExists(FileDownloadTask.this.file.toPath());
                if (!FileUtils.makeDirectory(FileDownloadTask.this.file.getAbsoluteFile().getParentFile())) {
                    throw new IOException("Unable to make parent directory " + FileDownloadTask.this.file);
                }
                try {
                    FileUtils.moveFile(temp.toFile(), FileDownloadTask.this.file);
                    if (FileDownloadTask.this.integrityCheck != null) {
                        FileDownloadTask.this.integrityCheck.performCheck(digest);
                    }
                    if (FileDownloadTask.this.caching && FileDownloadTask.this.integrityCheck != null) {
                        try {
                            FileDownloadTask.this.repository.lambda$checkExistentFile$0(FileDownloadTask.this.file.toPath(), FileDownloadTask.this.integrityCheck.getAlgorithm(), FileDownloadTask.this.integrityCheck.getChecksum());
                        } catch (IOException e3) {
                            Logging.LOG.log(Level.WARNING, "Failed to cache file", (Throwable) e3);
                        }
                    }
                    if (checkETag) {
                        FileDownloadTask.this.repository.cacheRemoteFile(FileDownloadTask.this.file.toPath(), conn);
                    }
                } catch (Exception e4) {
                    throw new IOException("Unable to move temp file from " + temp + " to " + FileDownloadTask.this.file, e4);
                }
            }
        };
    }

    static /* synthetic */ void lambda$static$0(Path filePath, Path destinationPath) throws IOException {
        FileSystem ignored;
        String ext = FileUtils.getExtension(destinationPath).toLowerCase(Locale.ROOT);
        if ((ext.equals(ArchiveStreamFactory.ZIP) || ext.equals("jar")) && (ignored = CompressingUtils.createReadOnlyZipFileSystem(filePath)) != null) {
            ignored.close();
        }
    }
}
