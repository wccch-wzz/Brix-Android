package com.brixcore.download.fabric;

import com.brixcore.download.DownloadProvider;
import com.brixcore.download.VersionList;
import com.brixcore.util.Lang;
import com.brixcore.util.function.ExceptionalRunnable;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes3.dex */
public final class FabricVersionList extends VersionList<FabricRemoteVersion> {
    private static final String GAME_META_URL = "https://meta.fabricmc.net/v2/versions/game";
    private static final String LOADER_META_URL = "https://meta.fabricmc.net/v2/versions/loader";
    private final DownloadProvider downloadProvider;

    public FabricVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return false;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.download.fabric.FabricVersionList$$ExternalSyntheticLambda1
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() throws Exception {
                this.f$0.lambda$refreshAsync$0();
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0() throws Exception {
        List<String> gameVersions = getGameVersions(GAME_META_URL);
        List<String> gameVersions2 = getGameVersions(LOADER_META_URL);
        this.lock.writeLock().lock();
        try {
            for (String str : gameVersions) {
                for (String str2 : gameVersions2) {
                    this.versions.put(str, new FabricRemoteVersion(str, str2, Collections.singletonList(getLaunchMetaUrl(str, str2))));
                }
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }

    private List<String> getGameVersions(String metaUrl) throws IOException {
        String json = NetworkUtils.doGet(NetworkUtils.toURL(this.downloadProvider.injectURL(metaUrl)));
        return (List) ((ArrayList) JsonUtils.GSON.fromJson(json, new TypeToken<ArrayList<GameVersion>>() { // from class: com.brixcore.download.fabric.FabricVersionList.1
        }.getType())).stream().map(new Function() { // from class: com.brixcore.download.fabric.FabricVersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((FabricVersionList.GameVersion) obj).getVersion();
            }
        }).collect(Collectors.toList());
    }

    private static String getLaunchMetaUrl(String gameVersion, String loaderVersion) {
        return String.format("https://meta.fabricmc.net/v2/versions/loader/%s/%s", gameVersion, loaderVersion);
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class GameVersion {
        private final String maven;
        private final boolean stable;
        private final String version;

        public GameVersion() {
            this("", null, false);
        }

        public GameVersion(String version, String maven, boolean stable) {
            this.version = version;
            this.maven = maven;
            this.stable = stable;
        }

        public String getVersion() {
            return this.version;
        }

        public String getMaven() {
            return this.maven;
        }

        public boolean isStable() {
            return this.stable;
        }
    }
}
