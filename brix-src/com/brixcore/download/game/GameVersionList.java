package com.brixcore.download.game;

import com.brixcore.download.DownloadProvider;
import com.brixcore.download.VersionList;
import com.brixcore.util.Logging;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.HttpRequest;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes9.dex */
public final class GameVersionList extends VersionList<GameRemoteVersion> {
    private final DownloadProvider downloadProvider;

    public GameVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return true;
    }

    @Override // com.brixcore.download.VersionList
    protected Collection<GameRemoteVersion> getVersionsImpl(String gameVersion) {
        return this.versions.values();
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return HttpRequest.GET(this.downloadProvider.getVersionListURL()).getJsonAsync(GameRemoteVersions.class).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.game.GameVersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$0((GameRemoteVersions) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0(GameRemoteVersions gameRemoteVersions) {
        GameRemoteVersions gameRemoteVersions2 = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(GameVersionList.class.getResourceAsStream("/assets/game/unlisted-versions.json"));
            try {
                gameRemoteVersions2 = (GameRemoteVersions) JsonUtils.GSON.fromJson((Reader) inputStreamReader, GameRemoteVersions.class);
                inputStreamReader.close();
            } catch (Throwable th) {
                try {
                    inputStreamReader.close();
                    throw th;
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                    throw th;
                }
            }
        } catch (Throwable th3) {
            Logging.LOG.log(Level.WARNING, "Failed to load unlisted versions", th3);
        }
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            if (gameRemoteVersions2 != null) {
                for (GameRemoteVersionInfo gameRemoteVersionInfo : gameRemoteVersions2.getVersions()) {
                    this.versions.put(gameRemoteVersionInfo.getGameVersion(), new GameRemoteVersion(gameRemoteVersionInfo.getGameVersion(), gameRemoteVersionInfo.getGameVersion(), Collections.singletonList(gameRemoteVersionInfo.getUrl()), gameRemoteVersionInfo.getType(), gameRemoteVersionInfo.getReleaseTime()));
                }
            }
            for (GameRemoteVersionInfo gameRemoteVersionInfo2 : gameRemoteVersions.getVersions()) {
                this.versions.put(gameRemoteVersionInfo2.getGameVersion(), new GameRemoteVersion(gameRemoteVersionInfo2.getGameVersion(), gameRemoteVersionInfo2.getGameVersion(), Collections.singletonList(gameRemoteVersionInfo2.getUrl()), gameRemoteVersionInfo2.getType(), gameRemoteVersionInfo2.getReleaseTime()));
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
