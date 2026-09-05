package com.brixcore.download.game;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.function.ExceptionalRunnable;
import com.brixcore.util.gson.JsonUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes9.dex */
public class GameInstallTask extends Task<Version> {
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final DefaultDependencyManager dependencyManager;
    private final VersionJsonDownloadTask downloadTask;
    private final DefaultGameRepository gameRepository;
    private final GameRemoteVersion remote;
    private final Version version;

    public GameInstallTask(DefaultDependencyManager dependencyManager, Version version, GameRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.gameRepository = dependencyManager.getGameRepository();
        this.version = version;
        this.remote = remoteVersion;
        this.downloadTask = new VersionJsonDownloadTask(remoteVersion.getGameVersion(), dependencyManager);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return Collections.singleton(this.downloadTask);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public boolean isRelyingOnDependencies() {
        return false;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        Version patch = ((Version) JsonUtils.fromNonNullJson(this.downloadTask.getResult(), Version.class)).setId(LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId()).setVersion(this.remote.getGameVersion()).setJar(null).setPriority(0);
        setResult(patch);
        Version version = new Version(this.version.getId()).addPatch(patch);
        this.dependencies.add(Task.allOf((Task<?>[]) new Task[]{new GameDownloadTask(this.dependencyManager, this.remote.getGameVersion(), version), Task.allOf((Task<?>[]) new Task[]{new GameAssetDownloadTask(this.dependencyManager, version, true, true), new GameLibrariesTask(this.dependencyManager, version, true)}).withRunAsync(new ExceptionalRunnable() { // from class: com.brixcore.download.game.GameInstallTask$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() {
                GameInstallTask.lambda$execute$0();
            }
        })}).thenComposeAsync(this.gameRepository.saveAsync(version)));
    }

    static /* synthetic */ void lambda$execute$0() throws RuntimeException {
    }
}
