package com.brixcore.download.cleanroom;

import com.brixcore.download.DownloadProvider;
import com.brixcore.download.VersionList;
import com.brixcore.task.GetTask;
import com.brixcore.util.Lang;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes5.dex */
public final class CleanroomVersionList extends VersionList<CleanroomRemoteVersion> {
    private static final String INSTALLER_URL = "https://hmcl.glavo.site/metadata/cleanroom/files/cleanroom-%s-installer.jar";
    private static final String LOADER_LIST_URL = "https://hmcl.glavo.site/metadata/cleanroom/index.json";
    private final DownloadProvider downloadProvider;

    public CleanroomVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return false;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.completedFuture(null).thenApplyAsync(Lang.wrap(new ExceptionalFunction() { // from class: com.brixcore.download.cleanroom.CleanroomVersionList$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$refreshAsync$0((Void) obj);
            }
        })).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.cleanroom.CleanroomVersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$1((CleanroomVersionList.ReleaseResult[]) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ReleaseResult[] lambda$refreshAsync$0(Void unused) throws Exception {
        GetTask task = new GetTask(this.downloadProvider.injectURLWithCandidates(LOADER_LIST_URL));
        task.execute();
        String result = task.getResult();
        return (ReleaseResult[]) JsonUtils.GSON.fromJson(result, ReleaseResult[].class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$1(ReleaseResult[] releaseResultArr) {
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            for (ReleaseResult releaseResult : releaseResultArr) {
                this.versions.put("1.12.2", new CleanroomRemoteVersion("1.12.2", releaseResult.name, Instant.parse(releaseResult.created_at), Collections.singletonList(String.format(INSTALLER_URL, releaseResult.name))));
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class ReleaseResult {
        String created_at;
        String name;

        private ReleaseResult() {
        }
    }
}
