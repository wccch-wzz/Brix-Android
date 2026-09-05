package com.brixcore.download.liteloader;

import com.brixcore.download.BMCLAPIDownloadProvider;
import com.brixcore.download.RemoteVersion;
import com.brixcore.download.VersionList;
import com.brixcore.util.Pair;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.NetworkUtils;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderBMCLVersionList extends VersionList<LiteLoaderRemoteVersion> {
    private final BMCLAPIDownloadProvider downloadProvider;

    public LiteLoaderBMCLVersionList(BMCLAPIDownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class LiteLoaderBMCLVersion {
        private final LiteLoaderVersion build;
        private final String version;

        public LiteLoaderBMCLVersion(LiteLoaderVersion build, String version) {
            this.build = build;
            this.version = version;
        }
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        throw new UnsupportedOperationException();
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync(final String gameVersion) {
        return HttpRequest.GET(this.downloadProvider.injectURL("https://bmclapi2.bangbang93.com/liteloader/list"), Pair.pair("mcversion", gameVersion)).getJsonAsync(LiteLoaderBMCLVersion.class).thenAccept(new Consumer() { // from class: com.brixcore.download.liteloader.LiteLoaderBMCLVersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) throws Throwable {
                this.f$0.lambda$refreshAsync$0(gameVersion, (LiteLoaderBMCLVersionList.LiteLoaderBMCLVersion) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0(String str, LiteLoaderBMCLVersion liteLoaderBMCLVersion) throws Throwable {
        Throwable th;
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            try {
                this.versions.put(str, new LiteLoaderRemoteVersion(str, liteLoaderBMCLVersion.version, RemoteVersion.Type.UNCATEGORIZED, Collections.singletonList(NetworkUtils.withQuery(this.downloadProvider.injectURL("https://bmclapi2.bangbang93.com/liteloader/download"), Collections.singletonMap("version", liteLoaderBMCLVersion.version))), liteLoaderBMCLVersion.build.getTweakClass(), liteLoaderBMCLVersion.build.getLibraries()));
                this.lock.writeLock().unlock();
            } catch (Throwable th2) {
                th = th2;
                this.lock.writeLock().unlock();
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }
}
