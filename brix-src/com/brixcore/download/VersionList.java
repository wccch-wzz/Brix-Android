package com.brixcore.download;

import com.brixcore.download.RemoteVersion;
import com.brixcore.util.SimpleMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes14.dex */
public abstract class VersionList<T extends RemoteVersion> {
    protected final SimpleMultimap<String, T, TreeSet<T>> versions = new SimpleMultimap<>(new Supplier() { // from class: com.brixcore.download.VersionList$$ExternalSyntheticLambda2
        @Override // java.util.function.Supplier
        public final Object get() {
            return VersionList.$r8$lambda$zL9zIj_AuRar_VZKE_7we4Hn4rk();
        }
    }, new Supplier() { // from class: com.brixcore.download.VersionList$$ExternalSyntheticLambda3
        @Override // java.util.function.Supplier
        public final Object get() {
            return VersionList.$r8$lambda$4gJ5v9tAfXSpuA52TufZtTtrB6c();
        }
    });
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static /* synthetic */ TreeSet $r8$lambda$4gJ5v9tAfXSpuA52TufZtTtrB6c() {
        return new TreeSet();
    }

    public static /* synthetic */ HashMap $r8$lambda$zL9zIj_AuRar_VZKE_7we4Hn4rk() {
        return new HashMap();
    }

    public abstract boolean hasType();

    public abstract CompletableFuture<?> refreshAsync();

    public boolean isLoaded() {
        return !this.versions.isEmpty();
    }

    public boolean isLoaded(String gameVersion) {
        return !((TreeSet) this.versions.get(gameVersion)).isEmpty();
    }

    public CompletableFuture<?> refreshAsync(String gameVersion) {
        return refreshAsync();
    }

    public CompletableFuture<?> loadAsync() {
        return CompletableFuture.completedFuture(null).thenComposeAsync(new Function() { // from class: com.brixcore.download.VersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$loadAsync$0(obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$loadAsync$0(Object unused) {
        this.lock.readLock().lock();
        try {
            boolean loaded = isLoaded();
            this.lock.readLock().unlock();
            return loaded ? CompletableFuture.completedFuture(null) : refreshAsync();
        } catch (Throwable th) {
            this.lock.readLock().unlock();
            throw th;
        }
    }

    public CompletableFuture<?> loadAsync(final String gameVersion) {
        return CompletableFuture.completedFuture(null).thenComposeAsync(new Function() { // from class: com.brixcore.download.VersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$loadAsync$1(gameVersion, obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$loadAsync$1(String gameVersion, Object unused) {
        this.lock.readLock().lock();
        try {
            boolean loaded = isLoaded(gameVersion);
            this.lock.readLock().unlock();
            return loaded ? CompletableFuture.completedFuture(null) : refreshAsync(gameVersion);
        } catch (Throwable th) {
            this.lock.readLock().unlock();
            throw th;
        }
    }

    protected Collection<T> getVersionsImpl(String gameVersion) {
        return this.versions.get(gameVersion);
    }

    public final Collection<T> getVersions(String gameVersion) {
        this.lock.readLock().lock();
        try {
            return Collections.unmodifiableCollection(new ArrayList(getVersionsImpl(gameVersion)));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Optional<T> getVersion(String gameVersion, String remoteVersion) {
        this.lock.readLock().lock();
        T result = null;
        try {
            TreeSet<T> remoteVersions = (TreeSet) this.versions.get(gameVersion);
            for (T it : remoteVersions) {
                if (remoteVersion.equals(it.getSelfVersion())) {
                    result = it;
                }
            }
            if (result == null) {
                for (T it2 : remoteVersions) {
                    if (remoteVersion.equals(it2.getFullVersion())) {
                        result = it2;
                    }
                }
            }
            return Optional.ofNullable(result);
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
