package com.brixcore.download.game;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.RemoteVersion;
import com.brixcore.download.VersionList;
import com.brixcore.task.GetTask;
import com.brixcore.task.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes9.dex */
public final class VersionJsonDownloadTask extends Task<String> {
    private final DefaultDependencyManager dependencyManager;
    private final String gameVersion;
    private final VersionList<?> gameVersionList;
    private final List<Task<?>> dependents = new ArrayList(1);
    private final List<Task<?>> dependencies = new ArrayList(1);

    public VersionJsonDownloadTask(String gameVersion, DefaultDependencyManager dependencyManager) {
        this.gameVersion = gameVersion;
        this.dependencyManager = dependencyManager;
        this.gameVersionList = dependencyManager.getVersionList("game");
        this.dependents.add(Task.fromCompletableFuture(this.gameVersionList.loadAsync(gameVersion)));
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws IOException {
        RemoteVersion remoteVersion = (RemoteVersion) this.gameVersionList.getVersion(this.gameVersion, this.gameVersion).orElseThrow(new Supplier() { // from class: com.brixcore.download.game.VersionJsonDownloadTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.lambda$execute$0();
            }
        });
        this.dependencies.add(new GetTask(this.dependencyManager.getDownloadProvider().injectURLsWithCandidates(remoteVersion.getUrls())).storeTo(new Consumer() { // from class: com.brixcore.download.game.VersionJsonDownloadTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.setResult((String) obj);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ IOException lambda$execute$0() {
        return new IOException("Cannot find specific version " + this.gameVersion + " in remote repository");
    }
}
