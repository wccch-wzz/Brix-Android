package com.brixcore.download.fabric;

import com.brixcore.download.DownloadProvider;
import com.brixcore.download.VersionList;
import com.brixcore.mod.RemoteMod;
import com.brixcore.mod.modrinth.ModrinthRemoteModRepository;
import com.brixcore.util.Lang;
import com.brixcore.util.function.ExceptionalRunnable;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: classes3.dex */
public class FabricAPIVersionList extends VersionList<FabricAPIRemoteVersion> {
    private final DownloadProvider downloadProvider;

    public FabricAPIVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return false;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.download.fabric.FabricAPIVersionList$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() throws Exception {
                this.f$0.lambda$refreshAsync$0();
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0() throws Exception {
        for (RemoteMod.Version version : Lang.toIterable(ModrinthRemoteModRepository.MODS.getRemoteVersionsById("P7dR8mSH"))) {
            for (String str : version.getGameVersions()) {
                this.versions.put(str, new FabricAPIRemoteVersion(str, version.getVersion(), version.getName(), version.getDatePublished(), version, Collections.singletonList(version.getFile().getUrl())));
            }
        }
    }
}
