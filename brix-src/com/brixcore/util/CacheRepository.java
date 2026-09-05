package com.brixcore.util;

import com.brixcore.util.function.ExceptionalSupplier;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.IOUtils;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes11.dex */
public class CacheRepository {
    public static final String SHA1 = "SHA-1";
    private static CacheRepository instance = new CacheRepository();
    private Path cacheDirectory;
    private Path commonDirectory;
    private Map<String, ETagItem> index;
    private Path indexFile;
    private final Map<String, Storage> storages = new HashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void changeDirectory(Path commonDir) {
        this.commonDirectory = commonDir;
        this.cacheDirectory = commonDir.resolve("cache");
        this.indexFile = this.cacheDirectory.resolve("etag.json");
        this.lock.writeLock().lock();
        try {
            try {
                for (Storage storage : this.storages.values()) {
                    storage.changeDirectory(this.cacheDirectory);
                }
                if (Files.isRegularFile(this.indexFile, new LinkOption[0])) {
                    ETagIndex raw = (ETagIndex) JsonUtils.GSON.fromJson(FileUtils.readText(this.indexFile), ETagIndex.class);
                    if (raw == null) {
                        this.index = new HashMap();
                    } else {
                        this.index = joinETagIndexes(raw.eTag);
                    }
                } else {
                    this.index = new HashMap();
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read index file", (Throwable) e);
                this.index = new HashMap();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Path getCommonDirectory() {
        return this.commonDirectory;
    }

    public Path getCacheDirectory() {
        return this.cacheDirectory;
    }

    public Storage getStorage(String key) {
        this.lock.readLock().lock();
        try {
            return this.storages.computeIfAbsent(key, new Function() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda6
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return new CacheRepository.Storage((String) obj);
                }
            });
        } finally {
            this.lock.readLock().unlock();
        }
    }

    protected Path getFile(String algorithm, String hash) {
        return getCacheDirectory().resolve(algorithm).resolve(hash.substring(0, 2)).resolve(hash);
    }

    protected boolean fileExists(String algorithm, String hash) {
        if (hash == null) {
            return false;
        }
        Path file = getFile(algorithm, hash);
        if (!Files.exists(file, new LinkOption[0])) {
            return false;
        }
        try {
            return DigestUtils.digestToString(algorithm, file).equalsIgnoreCase(hash);
        } catch (IOException e) {
            return false;
        }
    }

    public void tryCacheFile(Path path, String algorithm, String hash) throws IOException {
        Path cache = getFile(algorithm, hash);
        if (Files.isRegularFile(cache, new LinkOption[0])) {
            return;
        }
        FileUtils.copyFile(path, cache);
    }

    /* JADX INFO: renamed from: cacheFile, reason: merged with bridge method [inline-methods] */
    public Path lambda$checkExistentFile$0(Path path, String algorithm, String hash) throws IOException {
        Path cache = getFile(algorithm, hash);
        FileUtils.copyFile(path, cache);
        return cache;
    }

    public Optional<Path> checkExistentFile(final Path original, final String algorithm, final String hash) {
        if (fileExists(algorithm, hash)) {
            return Optional.of(getFile(algorithm, hash));
        }
        if (original != null && Files.exists(original, new LinkOption[0])) {
            if (hash != null) {
                try {
                    String checksum = DigestUtils.digestToString(algorithm, original);
                    if (checksum.equalsIgnoreCase(hash)) {
                        return Optional.of(restore(original, new ExceptionalSupplier() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda7
                            @Override // com.brixcore.util.function.ExceptionalSupplier
                            public final Object get() {
                                return this.f$0.lambda$checkExistentFile$0(original, algorithm, hash);
                            }
                        }));
                    }
                } catch (IOException e) {
                }
            } else {
                return Optional.of(original);
            }
        }
        return Optional.empty();
    }

    protected Path restore(Path original, ExceptionalSupplier<Path, ? extends IOException> cacheSupplier) throws Exception {
        Path cache = cacheSupplier.get();
        Files.delete(original);
        Files.createLink(original, cache);
        return cache;
    }

    public Path getCachedRemoteFile(URLConnection conn) throws IOException {
        String url = conn.getURL().toString();
        this.lock.readLock().lock();
        try {
            ETagItem eTagItem = this.index.get(url);
            this.lock.readLock().unlock();
            if (eTagItem == null) {
                throw new IOException("Cannot find the URL");
            }
            if (StringUtils.isBlank(eTagItem.hash) || !fileExists(SHA1, eTagItem.hash)) {
                throw new FileNotFoundException();
            }
            Path file = getFile(SHA1, eTagItem.hash);
            if (Files.getLastModifiedTime(file, new LinkOption[0]).toMillis() != eTagItem.localLastModified) {
                String hash = DigestUtils.digestToString(SHA1, file);
                if (!Objects.equals(hash, eTagItem.hash)) {
                    throw new IOException("This file is modified");
                }
            }
            return file;
        } catch (Throwable th) {
            this.lock.readLock().unlock();
            throw th;
        }
    }

    public void removeRemoteEntry(URLConnection conn) {
        String url = conn.getURL().toString();
        this.lock.readLock().lock();
        try {
            this.index.remove(url);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void injectConnection(URLConnection conn) {
        String url = conn.getURL().toString();
        this.lock.readLock().lock();
        try {
            ETagItem eTagItem = this.index.get(url);
            this.lock.readLock().unlock();
            if (eTagItem != null && eTagItem.eTag != null) {
                conn.setRequestProperty("If-None-Match", eTagItem.eTag);
            }
        } catch (Throwable th) {
            this.lock.readLock().unlock();
            throw th;
        }
    }

    public void cacheRemoteFile(final Path downloaded, URLConnection conn) throws IOException {
        cacheData(new ExceptionalSupplier() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda1
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$cacheRemoteFile$1(downloaded);
            }
        }, conn);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CacheResult lambda$cacheRemoteFile$1(Path downloaded) throws IOException {
        String hash = DigestUtils.digestToString(SHA1, downloaded);
        Path cached = lambda$checkExistentFile$0(downloaded, SHA1, hash);
        return new CacheResult(hash, cached);
    }

    public void cacheText(String text, URLConnection conn) throws IOException {
        cacheBytes(text.getBytes(StandardCharsets.UTF_8), conn);
    }

    public void cacheBytes(final byte[] bytes, URLConnection conn) throws IOException {
        cacheData(new ExceptionalSupplier() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$cacheBytes$2(bytes);
            }
        }, conn);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CacheResult lambda$cacheBytes$2(byte[] bytes) throws IOException {
        String hash = DigestUtils.digestToString(SHA1, bytes);
        Path cached = getFile(SHA1, hash);
        FileUtils.writeBytes(cached, bytes);
        return new CacheResult(hash, cached);
    }

    public synchronized void cacheData(ExceptionalSupplier<CacheResult, IOException> cacheSupplier, URLConnection conn) throws IOException {
        String eTag = conn.getHeaderField("ETag");
        if (eTag == null) {
            return;
        }
        String url = conn.getURL().toString();
        String lastModified = conn.getHeaderField("Last-Modified");
        CacheResult cacheResult = cacheSupplier.get();
        ETagItem eTagItem = new ETagItem(url, eTag, cacheResult.hash, Files.getLastModifiedTime(cacheResult.cachedFile, new LinkOption[0]).toMillis(), lastModified);
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            this.index.compute(eTagItem.url, updateEntity(eTagItem));
            saveETagIndex();
            writeLock.unlock();
        } catch (Throwable th) {
            writeLock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CacheResult {
        public Path cachedFile;
        public String hash;

        public CacheResult(String hash, Path cachedFile) {
            this.hash = hash;
            this.cachedFile = cachedFile;
        }
    }

    private BiFunction<String, ETagItem, ETagItem> updateEntity(final ETagItem newItem) {
        return new BiFunction() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda8
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return this.f$0.lambda$updateEntity$3(newItem, (String) obj, (CacheRepository.ETagItem) obj2);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ETagItem lambda$updateEntity$3(ETagItem newItem, String key, ETagItem oldItem) {
        if (oldItem == null) {
            return newItem;
        }
        if (oldItem.compareTo(newItem) < 0) {
            Path cached = getFile(SHA1, oldItem.hash);
            try {
                Files.deleteIfExists(cached);
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Cannot delete old file");
            }
            return newItem;
        }
        return oldItem;
    }

    @SafeVarargs
    private final Map<String, ETagItem> joinETagIndexes(Collection<ETagItem>... indexes) {
        final Map<String, ETagItem> eTags = new ConcurrentHashMap<>();
        Stream<ETagItem> stream = (Stream) Arrays.stream(indexes).filter(new Predicate() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Collection) obj);
            }
        }).map(new Function() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Collection) obj).stream();
            }
        }).reduce(Stream.empty(), new BinaryOperator() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda4
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Stream.concat((Stream) obj, (Stream) obj2);
            }
        });
        stream.forEach(new Consumer() { // from class: com.brixcore.util.CacheRepository$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$joinETagIndexes$4(eTags, (CacheRepository.ETagItem) obj);
            }
        });
        return eTags;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$joinETagIndexes$4(Map eTags, ETagItem eTag) {
        eTags.compute(eTag.url, updateEntity(eTag));
    }

    public void saveETagIndex() throws IOException {
        FileChannel channel = FileChannel.open(this.indexFile, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        try {
            FileLock lock = channel.lock();
            try {
                ETagIndex indexOnDisk = (ETagIndex) JsonUtils.fromMaybeMalformedJson(new String(IOUtils.readFullyWithoutClosing(Channels.newInputStream(channel)), StandardCharsets.UTF_8), ETagIndex.class);
                Collection<ETagItem>[] collectionArr = new Collection[2];
                collectionArr[0] = indexOnDisk == null ? null : indexOnDisk.eTag;
                collectionArr[1] = this.index.values();
                Map<String, ETagItem> newIndex = joinETagIndexes(collectionArr);
                channel.truncate(0L);
                ByteBuffer writeTo = ByteBuffer.wrap(JsonUtils.GSON.toJson(new ETagIndex(newIndex.values())).getBytes(StandardCharsets.UTF_8));
                while (writeTo.hasRemaining()) {
                    if (channel.write(writeTo) == 0) {
                        throw new IOException("No value is written");
                    }
                }
                this.index = newIndex;
                lock.release();
                if (channel != null) {
                    channel.close();
                }
            } catch (Throwable th) {
                lock.release();
                throw th;
            }
        } catch (Throwable th2) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
            }
            throw th2;
        }
    }

    private static final class ETagIndex {
        private final Collection<ETagItem> eTag;

        public ETagIndex() {
            this.eTag = new HashSet();
        }

        public ETagIndex(Collection<ETagItem> eTags) {
            this.eTag = new HashSet(eTags);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class ETagItem {
        private final String eTag;
        private final String hash;

        @SerializedName("local")
        private final long localLastModified;

        @SerializedName("remote")
        private final String remoteLastModified;
        private final String url;

        public ETagItem() {
            this(null, null, null, 0L, null);
        }

        public ETagItem(String url, String eTag, String hash, long localLastModified, String remoteLastModified) {
            this.url = url;
            this.eTag = eTag;
            this.hash = hash;
            this.localLastModified = localLastModified;
            this.remoteLastModified = remoteLastModified;
        }

        public int compareTo(final ETagItem other) {
            if (!this.url.equals(other.url)) {
                throw new IllegalArgumentException();
            }
            ZonedDateTime thisTime = (ZonedDateTime) Lang.ignoringException(new ExceptionalSupplier() { // from class: com.brixcore.util.CacheRepository$ETagItem$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.function.ExceptionalSupplier
                public final Object get() {
                    return this.f$0.lambda$compareTo$0();
                }
            }, null);
            ZonedDateTime otherTime = (ZonedDateTime) Lang.ignoringException(new ExceptionalSupplier() { // from class: com.brixcore.util.CacheRepository$ETagItem$$ExternalSyntheticLambda1
                @Override // com.brixcore.util.function.ExceptionalSupplier
                public final Object get() {
                    return ZonedDateTime.parse(this.f$0.remoteLastModified, DateTimeFormatter.RFC_1123_DATE_TIME);
                }
            }, null);
            if (thisTime == null && otherTime == null) {
                return 0;
            }
            if (thisTime == null) {
                return -1;
            }
            if (otherTime == null) {
                return 1;
            }
            return thisTime.compareTo((ChronoZonedDateTime<?>) otherTime);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ZonedDateTime lambda$compareTo$0() throws Exception {
            return ZonedDateTime.parse(this.remoteLastModified, DateTimeFormatter.RFC_1123_DATE_TIME);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ETagItem eTagItem = (ETagItem) o;
            if (this.localLastModified == eTagItem.localLastModified && Objects.equals(this.url, eTagItem.url) && Objects.equals(this.eTag, eTagItem.eTag) && Objects.equals(this.hash, eTagItem.hash) && Objects.equals(this.remoteLastModified, eTagItem.remoteLastModified)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.url, this.eTag, this.hash, Long.valueOf(this.localLastModified), this.remoteLastModified);
        }
    }

    public static final class Storage {
        private Path indexFile;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final String name;
        private Map<String, Object> storage;

        public Storage(String name) {
            this.name = name;
        }

        public Object getEntry(String key) {
            this.lock.readLock().lock();
            try {
                return this.storage.get(key);
            } finally {
                this.lock.readLock().unlock();
            }
        }

        public void putEntry(String key, Object value) {
            this.lock.writeLock().lock();
            try {
                this.storage.put(key, value);
                saveToFile();
            } finally {
                this.lock.writeLock().unlock();
            }
        }

        private void joinEntries(Map<String, Object> storage) {
            this.storage.putAll(storage);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void changeDirectory(Path cacheDirectory) {
            this.lock.writeLock().lock();
            try {
                try {
                    this.indexFile = cacheDirectory.resolve(this.name + ".json");
                    if (Files.isRegularFile(this.indexFile, new LinkOption[0])) {
                        joinEntries((Map) JsonUtils.fromNonNullJson(FileUtils.readText(this.indexFile), new TypeToken<Map<String, Object>>() { // from class: com.brixcore.util.CacheRepository.Storage.1
                        }.getType()));
                    }
                } finally {
                    this.lock.writeLock().unlock();
                }
            } catch (JsonParseException | IOException e) {
                Logging.LOG.log(Level.WARNING, "Unable to read storage {" + this.name + "} file");
            }
        }

        public void saveToFile() {
            try {
                FileChannel channel = FileChannel.open(this.indexFile, StandardOpenOption.READ, StandardOpenOption.WRITE);
                try {
                    FileLock lock = channel.lock();
                    try {
                        Map<String, Object> indexOnDisk = (Map) JsonUtils.fromMaybeMalformedJson(new String(IOUtils.readFullyWithoutClosing(Channels.newInputStream(channel)), StandardCharsets.UTF_8), new TypeToken<Map<String, Object>>() { // from class: com.brixcore.util.CacheRepository.Storage.2
                        }.getType());
                        if (indexOnDisk == null) {
                            indexOnDisk = new HashMap();
                        }
                        indexOnDisk.putAll(this.storage);
                        channel.truncate(0L);
                        channel.write(ByteBuffer.wrap(JsonUtils.GSON.toJson(this.storage).getBytes(StandardCharsets.UTF_8)));
                        this.storage = indexOnDisk;
                        lock.release();
                        if (channel != null) {
                            channel.close();
                        }
                    } catch (Throwable th) {
                        lock.release();
                        throw th;
                    }
                } catch (Throwable th2) {
                    if (channel != null) {
                        try {
                            channel.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Unable to write storage {" + this.name + "} file");
            }
        }
    }

    public static CacheRepository getInstance() {
        return instance;
    }

    public static void setInstance(CacheRepository instance2) {
        instance = instance2;
    }
}
