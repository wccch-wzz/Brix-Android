package com.brixcore.download.liteloader;

import com.brixcore.download.DownloadProvider;
import com.brixcore.download.RemoteVersion;
import com.brixcore.download.VersionList;
import com.brixcore.util.io.HttpRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.commons.compress.java.util.jar.Pack200;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderVersionList extends VersionList<LiteLoaderRemoteVersion> {
    public static final String LITELOADER_LIST = "https://dl.liteloader.com/versions/versions.json";
    private static final String SNAPSHOT_FILE = "https://repo.mumfrey.com/content/repositories/snapshots/com/mumfrey/liteloader/%s-SNAPSHOT/liteloader-%s-%s-%s-release.jar";
    private static final String SNAPSHOT_METADATA = "https://repo.mumfrey.com/content/repositories/snapshots/com/mumfrey/liteloader/%s-SNAPSHOT/maven-metadata.xml";
    private final DownloadProvider downloadProvider;

    public LiteLoaderVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return true;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync(final String gameVersion) {
        return HttpRequest.GET(this.downloadProvider.injectURL(LITELOADER_LIST)).getJsonAsync(LiteLoaderVersionsRoot.class).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.liteloader.LiteLoaderVersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) throws IOException {
                this.f$0.lambda$refreshAsync$0(gameVersion, (LiteLoaderVersionsRoot) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0(String str, LiteLoaderVersionsRoot liteLoaderVersionsRoot) throws IOException {
        LiteLoaderGameVersions liteLoaderGameVersions = liteLoaderVersionsRoot.getVersions().get(str);
        if (liteLoaderGameVersions == null) {
            return;
        }
        LiteLoaderRemoteVersion liteLoaderRemoteVersionLoadSnapshotVersion = null;
        if (liteLoaderGameVersions.getSnapshots() != null) {
            try {
                liteLoaderRemoteVersionLoadSnapshotVersion = loadSnapshotVersion(str, liteLoaderGameVersions.getSnapshots().getLiteLoader().get(Pack200.Packer.LATEST));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            if (liteLoaderGameVersions.getRepoitory() != null && liteLoaderGameVersions.getArtifacts() != null) {
                loadArtifactVersion(str, liteLoaderGameVersions.getRepoitory(), liteLoaderGameVersions.getArtifacts());
            }
            if (liteLoaderRemoteVersionLoadSnapshotVersion != null) {
                this.versions.put(str, liteLoaderRemoteVersionLoadSnapshotVersion);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    private void loadArtifactVersion(String str, LiteLoaderRepository liteLoaderRepository, LiteLoaderBranch liteLoaderBranch) {
        for (Map.Entry<String, LiteLoaderVersion> entry : liteLoaderBranch.getLiteLoader().entrySet()) {
            String key = entry.getKey();
            LiteLoaderVersion value = entry.getValue();
            if (!Pack200.Packer.LATEST.equals(key)) {
                this.versions.put(str, new LiteLoaderRemoteVersion(str, value.getVersion(), RemoteVersion.Type.RELEASE, Collections.singletonList(liteLoaderRepository.getUrl() + "com/mumfrey/liteloader/" + str + "/" + value.getFile()), value.getTweakClass(), value.getLibraries()));
            }
        }
    }

    private LiteLoaderRemoteVersion loadSnapshotVersion(String gameVersion, LiteLoaderVersion v) throws IOException {
        String root = HttpRequest.GET(String.format(SNAPSHOT_METADATA, gameVersion)).getString();
        Document document = Jsoup.parseBodyFragment(root);
        String timestamp = ((Elements) Objects.requireNonNull(document.select("timestamp"), "timestamp")).text();
        String buildNumber = ((Elements) Objects.requireNonNull(document.select("buildNumber"), "buildNumber")).text();
        return new LiteLoaderRemoteVersion(gameVersion, timestamp + "-" + buildNumber, RemoteVersion.Type.SNAPSHOT, Collections.singletonList(String.format(SNAPSHOT_FILE, gameVersion, gameVersion, timestamp, buildNumber)), v.getTweakClass(), v.getLibraries());
    }
}
