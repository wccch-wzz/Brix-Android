package com.brixcore.util.io;

import com.brixcore.util.Lang;
import com.brixcore.util.platform.OperatingSystem;
import com.brixcore.util.tree.ZipFileTree;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.sun.nio.zipfs.ZipFileSystemProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.CharEncoding;

/* JADX INFO: loaded from: classes3.dex */
public final class CompressingUtils {
    private static final FileSystemProvider ZIPFS_PROVIDER = new ZipFileSystemProvider();

    private CompressingUtils() {
    }

    private static CharsetDecoder newCharsetDecoder(Charset charset) {
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    public static boolean testEncoding(Path zipFile, Charset encoding) throws IOException {
        ZipFile zf = openZipFile(zipFile, encoding);
        try {
            boolean zTestEncoding = testEncoding(zf, encoding);
            if (zf != null) {
                zf.close();
            }
            return zTestEncoding;
        } catch (Throwable th) {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static boolean testEncoding(ZipFile zipFile, Charset encoding) {
        CharsetDecoder cd = newCharsetDecoder(encoding);
        CharBuffer cb = CharBuffer.allocate(32);
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            if (!entry.getGeneralPurposeBit().usesUTF8ForNames()) {
                cd.reset();
                byte[] ba = entry.getRawName();
                int clen = (int) (ba.length * cd.maxCharsPerByte());
                if (clen != 0) {
                    if (clen <= cb.capacity()) {
                        cb.clear();
                    } else {
                        cb = CharBuffer.allocate(clen);
                    }
                    ByteBuffer bb = ByteBuffer.wrap(ba, 0, ba.length);
                    CoderResult cr = cd.decode(bb, cb, true);
                    if (!cr.isUnderflow()) {
                        return false;
                    }
                    CoderResult cr2 = cd.flush(cb);
                    if (!cr2.isUnderflow()) {
                        return false;
                    }
                } else {
                    continue;
                }
            }
        }
        return true;
    }

    public static Charset findSuitableEncoding(Path zipFile) throws IOException {
        ZipFile zf = openZipFile(zipFile, StandardCharsets.UTF_8);
        try {
            Charset charsetFindSuitableEncoding = findSuitableEncoding(zf);
            if (zf != null) {
                zf.close();
            }
            return charsetFindSuitableEncoding;
        } catch (Throwable th) {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static Charset findSuitableEncoding(ZipFile zipFile) throws IOException {
        if (testEncoding(zipFile, StandardCharsets.UTF_8)) {
            return StandardCharsets.UTF_8;
        }
        if (OperatingSystem.NATIVE_CHARSET != StandardCharsets.UTF_8 && testEncoding(zipFile, OperatingSystem.NATIVE_CHARSET)) {
            return OperatingSystem.NATIVE_CHARSET;
        }
        String[] candidates = {"GB18030", "Big5", "Shift_JIS", "EUC-JP", "ISO-2022-JP", "EUC-KR", "ISO-2022-KR", "KOI8-R", "windows-1251", "x-MacCyrillic", "IBM855", "IBM866", "windows-1252", CharEncoding.ISO_8859_1, "ISO-8859-5", "ISO-8859-7", "ISO-8859-8", CharEncoding.UTF_16LE, CharEncoding.UTF_16BE, "UTF-32LE", "UTF-32BE"};
        for (String candidate : candidates) {
            try {
                Charset charset = Charset.forName(candidate);
                if (!charset.equals(OperatingSystem.NATIVE_CHARSET) && testEncoding(zipFile, charset)) {
                    return charset;
                }
            } catch (IllegalArgumentException e) {
            }
        }
        throw new IOException("Cannot find suitable encoding for the zip.");
    }

    public static ZipFileTree openZipTree(Path zipFile) throws IOException {
        return new ZipFileTree(openZipFile(zipFile));
    }

    public static ZipFileTree openZipTree(Path zipFile, Charset charset) throws IOException {
        return new ZipFileTree(openZipFile(zipFile, charset));
    }

    public static ZipFile openZipFile(Path zipFile) throws IOException {
        return openZipFileWithPossibleEncoding(zipFile, StandardCharsets.UTF_8);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static ZipFile openZipFile(Path zipFile, Charset charset) throws IOException {
        return ((ZipFile.Builder) ZipFile.builder().setPath(zipFile)).setCharset(charset).get();
    }

    public static ZipFile openZipFileWithPossibleEncoding(Path zipFile, Charset possibleEncoding) throws Throwable {
        Charset suitableEncoding;
        if (possibleEncoding == null) {
            possibleEncoding = StandardCharsets.UTF_8;
        }
        ZipFile zipReader = ZipFile.builder().setSeekableByteChannel(Files.newByteChannel(zipFile, new OpenOption[0])).get();
        try {
            if (possibleEncoding != StandardCharsets.UTF_8 && testEncoding(zipReader, possibleEncoding)) {
                suitableEncoding = possibleEncoding;
            } else {
                suitableEncoding = findSuitableEncoding(zipReader);
                if (suitableEncoding == StandardCharsets.UTF_8) {
                    return zipReader;
                }
            }
            zipReader.close();
            return ZipFile.builder().setSeekableByteChannel(Files.newByteChannel(zipFile, new OpenOption[0])).setCharset(suitableEncoding).get();
        } catch (Throwable e) {
            IOUtils.closeQuietly(zipReader, e);
            throw e;
        }
    }

    public static final class Builder {
        private final boolean create;
        private final Path zip;
        private boolean autoDetectEncoding = false;
        private Charset encoding = StandardCharsets.UTF_8;
        private boolean useTempFile = false;

        public Builder(Path zip, boolean create) {
            this.zip = zip;
            this.create = create;
        }

        public Builder setAutoDetectEncoding(boolean autoDetectEncoding) {
            this.autoDetectEncoding = autoDetectEncoding;
            return this;
        }

        public Builder setEncoding(Charset encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder setUseTempFile(boolean useTempFile) {
            this.useTempFile = useTempFile;
            return this;
        }

        public FileSystem build() throws IOException {
            if (this.autoDetectEncoding && !CompressingUtils.testEncoding(this.zip, this.encoding)) {
                this.encoding = CompressingUtils.findSuitableEncoding(this.zip);
            }
            return CompressingUtils.createZipFileSystem(this.zip, this.create, this.useTempFile, this.encoding);
        }
    }

    public static Builder readonly(Path zipFile) {
        return new Builder(zipFile, false);
    }

    public static Builder writable(Path zipFile) {
        return new Builder(zipFile, true).setUseTempFile(true);
    }

    public static FileSystem createReadOnlyZipFileSystem(Path zipFile) throws IOException {
        return createReadOnlyZipFileSystem(zipFile, null);
    }

    public static FileSystem createReadOnlyZipFileSystem(Path zipFile, Charset charset) throws IOException {
        return createZipFileSystem(zipFile, false, false, charset);
    }

    public static FileSystem createWritableZipFileSystem(Path zipFile) throws IOException {
        return createWritableZipFileSystem(zipFile, null);
    }

    public static FileSystem createWritableZipFileSystem(Path zipFile, Charset charset) throws IOException {
        return createZipFileSystem(zipFile, true, true, charset);
    }

    public static FileSystem createZipFileSystem(Path zipFile, boolean create, boolean useTempFile, Charset encoding) throws IOException {
        Map<String, Object> env = new HashMap<>();
        if (create) {
            env.put("create", "true");
        }
        if (encoding != null) {
            env.put("encoding", encoding.name());
        }
        if (useTempFile) {
            env.put("useTempFile", true);
        }
        try {
            if (ZIPFS_PROVIDER == null) {
                throw new FileSystemNotFoundException("Module jdk.zipfs does not exist");
            }
            return ZIPFS_PROVIDER.newFileSystem(zipFile, (Map<String, ?>) env);
        } catch (UnsupportedOperationException e) {
            throw new ZipException("Not a zip file");
        } catch (FileSystemNotFoundException ex) {
            throw ((ZipException) Lang.apply(new ZipException("Java Environment is broken"), new Consumer() { // from class: com.brixcore.util.io.CompressingUtils$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ZipException) obj).initCause(ex);
                }
            }));
        } catch (ZipError e2) {
            ZipException exception = new ZipException("Corrupted zip file");
            exception.initCause(e2);
            throw exception;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String readTextZipEntry(Path zipFile, String name) throws Throwable {
        ZipFile s = ((ZipFile.Builder) ZipFile.builder().setPath(zipFile)).get();
        try {
            String textZipEntry = readTextZipEntry(s, name);
            if (s != null) {
                s.close();
            }
            return textZipEntry;
        } catch (Throwable th) {
            if (s != null) {
                try {
                    s.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static String readTextZipEntry(ZipFile zipFile, String name) throws IOException {
        return IOUtils.readFullyAsString(zipFile.getInputStream(zipFile.getEntry(name)));
    }

    public static String readTextZipEntry(Path zipFile, String name, Charset encoding) throws IOException {
        ZipFile s = openZipFile(zipFile, encoding);
        try {
            String fullyAsString = IOUtils.readFullyAsString(s.getInputStream(s.getEntry(name)));
            if (s != null) {
                s.close();
            }
            return fullyAsString;
        } catch (Throwable th) {
            if (s != null) {
                try {
                    s.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static Optional<String> readTextZipEntryQuietly(Path file, String name, Charset encoding) {
        try {
            return Optional.of(readTextZipEntry(file, name, encoding));
        } catch (IOException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public static void extract(File archive, File destination) throws Throwable {
        String name = archive.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".zip") || name.endsWith(".jar") || name.endsWith(".mrpack")) {
            extractZip(archive, destination);
        } else if (name.endsWith(".7z")) {
            extract7z(archive, destination);
        } else {
            if (name.endsWith(".rar")) {
                extractRar(archive, destination);
                return;
            }
            throw new IOException("Unsupported archive format: " + archive.getName());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void extractZip(File zipFile, File destination) throws Throwable {
        ZipFile zf = ((ZipFile.Builder) ZipFile.builder().setFile(zipFile)).get();
        try {
            Enumeration<ZipArchiveEntry> entries = zf.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                File out = new File(destination, entry.getName());
                if (entry.isDirectory()) {
                    out.mkdirs();
                } else {
                    out.getParentFile().mkdirs();
                    InputStream is = zf.getInputStream(entry);
                    try {
                        FileOutputStream os = new FileOutputStream(out);
                        try {
                            IOUtils.copyTo(is, os);
                            os.close();
                            if (is != null) {
                                is.close();
                            }
                        } catch (Throwable th) {
                            try {
                                os.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                        }
                        throw th3;
                    }
                }
            }
            if (zf != null) {
                zf.close();
            }
        } catch (Throwable th5) {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Throwable th6) {
                    th5.addSuppressed(th6);
                }
            }
            throw th5;
        }
    }

    /* JADX WARN: Code duplicated, block: B:39:0x005b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    public static void extract7z(File sevenZFile, File destination) throws IOException {
        SevenZFile zf = ((SevenZFile.Builder) SevenZFile.builder().setFile(sevenZFile)).get();
        while (true) {
            try {
                SevenZArchiveEntry entry = zf.getNextEntry();
                if (entry == null) {
                    break;
                }
                File out = new File(destination, entry.getName());
                if (entry.isDirectory()) {
                    out.mkdirs();
                } else {
                    out.getParentFile().mkdirs();
                    FileOutputStream os = new FileOutputStream(out);
                    try {
                        byte[] buffer = new byte[8192];
                        while (true) {
                            int len = zf.read(buffer);
                            if (len <= 0) {
                                break;
                            } else {
                                os.write(buffer, 0, len);
                            }
                            if (zf != null) {
                                try {
                                    zf.close();
                                } catch (Throwable th) {
                                    th.addSuppressed(th);
                                }
                            }
                            throw th;
                        }
                        os.close();
                    } catch (Throwable th2) {
                        try {
                            os.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                        throw th2;
                    }
                }
            } catch (Throwable th4) {
                if (zf != null) {
                    zf.close();
                }
                throw th4;
            }
        }
        if (zf != null) {
            zf.close();
        }
    }

    public static void extractRar(File rarFile, File destination) throws IOException {
        try {
            Archive archive = new Archive(rarFile);
            while (true) {
                try {
                    FileHeader fh = archive.nextFileHeader();
                    if (fh != null) {
                        String fileName = fh.getFileName();
                        File out = new File(destination, fileName.replace(org.apache.commons.io.IOUtils.DIR_SEPARATOR_WINDOWS, org.apache.commons.io.IOUtils.DIR_SEPARATOR_UNIX));
                        if (fh.isDirectory()) {
                            out.mkdirs();
                        } else {
                            out.getParentFile().mkdirs();
                            FileOutputStream os = new FileOutputStream(out);
                            try {
                                archive.extractFile(fh, os);
                                os.close();
                            } catch (Throwable th) {
                                try {
                                    os.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        }
                    } else {
                        archive.close();
                        return;
                    }
                } catch (Throwable th3) {
                    try {
                        archive.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
                throw new IOException("Failed to extract RAR file: " + rarFile.getAbsolutePath(), e);
            }
        } catch (RarException e) {
            throw new IOException("Failed to extract RAR file: " + rarFile.getAbsolutePath(), e);
        }
    }
}
