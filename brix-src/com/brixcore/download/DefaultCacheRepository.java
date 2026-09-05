package com.brixcore.download;

import com.brixcore.download.game.LibraryDownloadTask;
import com.brixcore.game.Library;
import com.brixcore.game.LibraryDownloadInfo;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Logging;
import com.brixcore.util.function.ExceptionalSupplier;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.FileUtils;
import com.brixcore.utils.BrixPath;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes14.dex */
public class DefaultCacheRepository extends CacheRepository {
    private Index index;
    private Path indexFile;
    private Path librariesDir;
    private final ReadWriteLock lock;

    public DefaultCacheRepository() {
        this(new File(BrixPath.CACHE_DIR).toPath());
    }

    public DefaultCacheRepository(Path commonDirectory) {
        this.lock = new ReentrantReadWriteLock();
        this.index = null;
        changeDirectory(commonDirectory);
    }

    @Override // com.brixcore.util.CacheRepository
    public void changeDirectory(Path commonDir) {
        super.changeDirectory(commonDir);
        this.librariesDir = commonDir.resolve("libraries");
        this.indexFile = getCacheDirectory().resolve("index.json");
        this.lock.writeLock().lock();
        try {
            try {
                if (Files.isRegularFile(this.indexFile, new LinkOption[0])) {
                    this.index = (Index) JsonUtils.fromNonNullJson(FileUtils.readText(this.indexFile), Index.class);
                } else {
                    this.index = new Index();
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read index file", (Throwable) e);
                this.index = new Index();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void tryCacheLibrary(final Library library, Path jar) {
        this.lock.readLock().lock();
        try {
            if (this.index.getLibraries().stream().anyMatch(new Predicate() { // from class: com.brixcore.download.DefaultCacheRepository$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return library.getName().equals(((DefaultCacheRepository.LibraryIndex) obj).getName());
                }
            })) {
                this.lock.readLock().unlock();
                return;
            }
            this.lock.readLock().unlock();
            try {
                LibraryDownloadInfo info = library.getDownload();
                String hash = info.getSha1();
                if (hash != null) {
                    String checksum = DigestUtils.digestToString(CacheRepository.SHA1, jar);
                    if (hash.equalsIgnoreCase(checksum)) {
                        cacheLibrary(library, jar, false);
                    }
                } else if (library.getChecksums() != null && !library.getChecksums().isEmpty() && LibraryDownloadTask.checksumValid(jar, library.getChecksums())) {
                    cacheLibrary(library, jar, true);
                }
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Unable to calc hash value of file " + jar, (Throwable) e);
            }
        } catch (Throwable th) {
            this.lock.readLock().unlock();
            throw th;
        }
    }

    public Optional<Path> getLibrary(final Library library) {
        LibraryDownloadInfo info = library.getDownload();
        String hash = info.getSha1();
        if (fileExists(CacheRepository.SHA1, hash)) {
            return Optional.of(getFile(CacheRepository.SHA1, hash));
        }
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            List<LibraryIndex> libraries = (List) this.index.getLibraries().stream().filter(new Predicate() { // from class: com.brixcore.download.DefaultCacheRepository$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((DefaultCacheRepository.LibraryIndex) obj).getName().equals(library.getName());
                }
            }).collect(Collectors.toList());
            for (LibraryIndex libIndex : libraries) {
                if (fileExists(CacheRepository.SHA1, libIndex.getHash())) {
                    Path file = getFile(CacheRepository.SHA1, libIndex.getHash());
                    if (libIndex.getType().equalsIgnoreCase(LibraryIndex.TYPE_FORGE) && LibraryDownloadTask.checksumValid(file, library.getChecksums())) {
                        Optional<Path> optionalOf = Optional.of(file);
                        readLock.unlock();
                        return optionalOf;
                    }
                }
            }
            readLock.unlock();
            final Path jar = this.librariesDir.resolve(info.getPath());
            if (Files.exists(jar, new LinkOption[0])) {
                try {
                    if (hash != null) {
                        String checksum = DigestUtils.digestToString(CacheRepository.SHA1, jar);
                        if (hash.equalsIgnoreCase(checksum)) {
                            return Optional.of(restore(jar, new ExceptionalSupplier() { // from class: com.brixcore.download.DefaultCacheRepository$$ExternalSyntheticLambda1
                                @Override // com.brixcore.util.function.ExceptionalSupplier
                                public final Object get() {
                                    return this.f$0.lambda$getLibrary$2(library, jar);
                                }
                            }));
                        }
                    } else if (library.getChecksums() != null && !library.getChecksums().isEmpty()) {
                        if (LibraryDownloadTask.checksumValid(jar, library.getChecksums())) {
                            return Optional.of(restore(jar, new ExceptionalSupplier() { // from class: com.brixcore.download.DefaultCacheRepository$$ExternalSyntheticLambda2
                                @Override // com.brixcore.util.function.ExceptionalSupplier
                                public final Object get() {
                                    return this.f$0.lambda$getLibrary$3(library, jar);
                                }
                            }));
                        }
                    } else {
                        return Optional.of(jar);
                    }
                } catch (IOException e) {
                }
            }
            return Optional.empty();
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Path lambda$getLibrary$2(Library library, Path jar) throws IOException {
        return cacheLibrary(library, jar, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Path lambda$getLibrary$3(Library library, Path jar) throws IOException {
        return cacheLibrary(library, jar, true);
    }

    public Path cacheLibrary(Library library, Path path, boolean forge) throws IOException {
        String hash = library.getDownload().getSha1();
        if (hash == null) {
            hash = DigestUtils.digestToString(CacheRepository.SHA1, path);
        }
        Path cache = getFile(CacheRepository.SHA1, hash);
        FileUtils.copyFile(path, cache);
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            LibraryIndex libIndex = new LibraryIndex(library.getName(), hash, forge ? LibraryIndex.TYPE_FORGE : "jar");
            this.index.getLibraries().add(libIndex);
            saveIndex();
            return cache;
        } finally {
            writeLock.unlock();
        }
    }

    private void saveIndex() {
        if (this.indexFile == null || this.index == null) {
            return;
        }
        try {
            FileUtils.writeText(this.indexFile, JsonUtils.GSON.toJson(this.index));
        } catch (IOException e) {
            Logging.LOG.log(Level.SEVERE, "Unable to save index.json", (Throwable) e);
        }
    }

    private static final class Index implements Validation {
        private final Set<LibraryIndex> libraries;

        public Index() {
            this(new HashSet());
        }

        public Index(Set<LibraryIndex> libraries) {
            this.libraries = (Set) Objects.requireNonNull(libraries);
        }

        public Set<LibraryIndex> getLibraries() {
            return this.libraries;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            if (this.libraries == null) {
                throw new JsonParseException("Index.libraries cannot be null");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class LibraryIndex implements Validation {
        public static final String TYPE_FORGE = "forge";
        public static final String TYPE_JAR = "jar";
        private final String hash;
        private final String name;
        private final String type;

        public LibraryIndex() {
            this("", "", "");
        }

        public LibraryIndex(String name, String hash, String type) {
            this.name = name;
            this.hash = hash;
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public String getHash() {
            return this.hash;
        }

        public String getType() {
            return this.type;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            if (this.name == null || this.hash == null || this.type == null) {
                throw new JsonParseException("Index.LibraryIndex.* cannot be null");
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LibraryIndex that = (LibraryIndex) o;
            if (Objects.equals(this.name, that.name) && Objects.equals(this.hash, that.hash) && Objects.equals(this.type, that.type)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.name, this.hash, this.type);
        }
    }
}
