package com.brixcore.download.game;

import com.brixcore.download.AbstractDependencyManager;
import com.brixcore.download.DefaultCacheRepository;
import com.brixcore.game.Library;
import com.brixcore.task.DownloadException;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Logging;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes9.dex */
public class LibraryDownloadTask extends Task<Void> {
    protected final DefaultCacheRepository cacheRepository;
    private boolean cached = false;
    protected final AbstractDependencyManager dependencyManager;
    protected final Path jar;
    protected final Library library;
    private final Library originalLibrary;
    private FileDownloadTask task;
    protected final String url;

    public LibraryDownloadTask(AbstractDependencyManager dependencyManager, Path file, Library library) {
        this.dependencyManager = dependencyManager;
        this.originalLibrary = library;
        setSignificance(Task.TaskSignificance.MODERATE);
        library = library.is("net.minecraftforge", DefaultCacheRepository.LibraryIndex.TYPE_FORGE) ? library.setClassifier("universal") : library;
        this.library = library;
        this.cacheRepository = dependencyManager.getCacheRepository();
        this.url = library.getDownload().getUrl();
        this.jar = file;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.cached ? Collections.emptyList() : Collections.singleton(this.task);
    }

    @Override // com.brixcore.task.Task
    public boolean isRelyingOnDependents() {
        return false;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        if (!this.cached && !isDependentsSucceeded()) {
            Exception t = this.task.getException();
            if (t instanceof DownloadException) {
                throw new LibraryDownloadException(this.library, t.getCause());
            }
            if (t instanceof CancellationException) {
                throw new CancellationException();
            }
            throw new LibraryDownloadException(this.library, t);
        }
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() {
        Optional<Path> libPath = this.cacheRepository.getLibrary(this.originalLibrary);
        if (libPath.isPresent()) {
            try {
                FileUtils.copyFile(libPath.get(), this.jar);
                this.cached = true;
                return;
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Failed to copy file from cache", (Throwable) e);
            }
        }
        List<URL> uris = this.dependencyManager.getDownloadProvider().injectURLWithCandidates(this.url);
        this.task = new FileDownloadTask(uris, this.jar.toFile(), this.library.getDownload().getSha1() != null ? new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, this.library.getDownload().getSha1()) : null);
        this.task.setCacheRepository(this.cacheRepository);
        this.task.setCaching(true);
        this.task.addIntegrityCheckHandler(FileDownloadTask.ZIP_INTEGRITY_CHECK_HANDLER);
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        if (!this.cached) {
            try {
                this.cacheRepository.cacheLibrary(this.library, this.jar, false);
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Failed to cache downloaded library " + this.library, (Throwable) e);
            }
        }
    }

    public static boolean checksumValid(Path libPath, List<String> checksums) {
        if (checksums == null) {
            return true;
        }
        try {
            if (checksums.isEmpty()) {
                return true;
            }
            byte[] fileData = Files.readAllBytes(libPath);
            boolean valid = checksums.contains(DigestUtils.digestToString(CacheRepository.SHA1, fileData));
            if (!valid && FileUtils.getName(libPath).endsWith(".jar")) {
                return validateJar(fileData, checksums);
            }
            return valid;
        } catch (IOException e) {
            Logging.LOG.warning("Failed to validate " + libPath + StringUtils.LF + e);
            return false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean validateJar(byte[] data, List<String> checksums) throws IOException {
        char c;
        HashMap<String, String> files = new HashMap<>();
        String[] hashes = null;
        JarInputStream jar = new JarInputStream(new ByteArrayInputStream(data));
        for (JarEntry entry = jar.getNextJarEntry(); entry != null; entry = jar.getNextJarEntry()) {
            byte[] eData = IOUtils.readFully(jar);
            if (entry.getName().equals("checksums.sha1")) {
                hashes = new String(eData, StandardCharsets.UTF_8).split(StringUtils.LF);
            }
            if (!entry.isDirectory()) {
                files.put(entry.getName(), DigestUtils.digestToString(CacheRepository.SHA1, eData));
            }
        }
        jar.close();
        char c2 = 0;
        if (hashes == null) {
            return false;
        }
        boolean failed = !checksums.contains(files.get("checksums.sha1"));
        if (failed) {
            c = 0;
        } else {
            int length = hashes.length;
            int i = 0;
            while (i < length) {
                String hash = hashes[i];
                if (hash.trim().isEmpty() || !hash.contains(StringUtils.SPACE)) {
                    c = c2;
                } else {
                    String[] e = hash.split(StringUtils.SPACE);
                    String validChecksum = e[c2];
                    String target = hash.substring(validChecksum.length() + 1);
                    String checksum = files.get(target);
                    c = c2;
                    if (!files.containsKey(target) || checksum == null) {
                        Logging.LOG.warning("    " + target + " : missing");
                        failed = true;
                    } else if (!checksum.equals(validChecksum)) {
                        Logging.LOG.warning("    " + target + " : failed (" + checksum + ", " + validChecksum + ")");
                        failed = true;
                    }
                }
                i++;
                c2 = c;
            }
            c = c2;
        }
        if (failed) {
            return c;
        }
        return true;
    }
}
