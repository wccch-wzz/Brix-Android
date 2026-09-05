package com.brixcore.download;

import com.brixcore.util.Logging;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes14.dex */
public class MultipleSourceVersionList extends VersionList<RemoteVersion> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final VersionList<?>[] backends;

    MultipleSourceVersionList(VersionList<?>[] backends) {
        this.backends = backends;
        if (backends.length < 1) {
            throw new AssertionError();
        }
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        for (VersionList<?> backend : this.backends) {
            if (!backend.hasType()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> loadAsync() {
        throw new UnsupportedOperationException("MultipleSourceVersionList does not support loading the entire remote version list.");
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        throw new UnsupportedOperationException("MultipleSourceVersionList does not support loading the entire remote version list.");
    }

    private CompletableFuture<?> refreshAsync(final String gameVersion, final int sourceIndex) {
        final VersionList<?> versionList = this.backends[sourceIndex];
        final CompletableFuture<Void> future = versionList.refreshAsync(gameVersion).thenRunAsync(new Runnable() { // from class: com.brixcore.download.MultipleSourceVersionList$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$refreshAsync$0(gameVersion, versionList);
            }
        });
        if (sourceIndex == this.backends.length - 1) {
            return future;
        }
        return future.handle(new BiFunction() { // from class: com.brixcore.download.MultipleSourceVersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return this.f$0.lambda$refreshAsync$1(future, gameVersion, sourceIndex, (Void) obj, (Throwable) obj2);
            }
        }).thenCompose((Function<? super U, ? extends CompletionStage<U>>) new Function() { // from class: com.brixcore.download.MultipleSourceVersionList$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return MultipleSourceVersionList.lambda$refreshAsync$2((CompletableFuture) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0(String str, VersionList versionList) {
        this.lock.writeLock().lock();
        try {
            this.versions.putAll(str, (Collection<? extends T>) versionList.getVersions(str));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$refreshAsync$1(CompletableFuture future, String gameVersion, int sourceIndex, Void ignore, Throwable e) {
        if (e == null) {
            return future;
        }
        Logging.LOG.log(Level.WARNING, "Failed to fetch versions list and try to fetch from other source", e);
        return refreshAsync(gameVersion, sourceIndex + 1);
    }

    static /* synthetic */ CompletionStage lambda$refreshAsync$2(CompletableFuture it) {
        return it;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync(String gameVersion) {
        this.versions.clear(gameVersion);
        return refreshAsync(gameVersion, 0);
    }
}
