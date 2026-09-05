package com.brixcore.download.game;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes9.dex */
public final class GameDownloadTask extends Task<Void> {
    private final List<Task<?>> dependencies = new ArrayList();
    private final DefaultDependencyManager dependencyManager;
    private final String gameVersion;
    private final Version version;

    public GameDownloadTask(DefaultDependencyManager dependencyManager, String gameVersion, Version version) {
        this.dependencyManager = dependencyManager;
        this.gameVersion = gameVersion;
        this.version = version.resolve(dependencyManager.getGameRepository());
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() {
        File jar = this.dependencyManager.getGameRepository().getVersionJar(this.version);
        FileDownloadTask task = new FileDownloadTask(this.dependencyManager.getDownloadProvider().injectURLWithCandidates(this.version.getDownloadInfo().getUrl()), jar, FileDownloadTask.IntegrityCheck.of(CacheRepository.SHA1, this.version.getDownloadInfo().getSha1()));
        task.setCaching(true);
        task.setCacheRepository(this.dependencyManager.getCacheRepository());
        if (this.gameVersion != null) {
            task.setCandidate(this.dependencyManager.getCacheRepository().getCommonDirectory().resolve("jars").resolve(this.gameVersion + ".jar"));
        }
        this.dependencies.add(task);
    }
}
