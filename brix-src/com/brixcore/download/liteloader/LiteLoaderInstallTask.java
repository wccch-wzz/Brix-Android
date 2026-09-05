package com.brixcore.download.liteloader;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.Arguments;
import com.brixcore.game.Artifact;
import com.brixcore.game.LibrariesDownloadInfo;
import com.brixcore.game.Library;
import com.brixcore.game.LibraryDownloadInfo;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.Lang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderInstallTask extends Task<Version> {
    private final DefaultDependencyManager dependencyManager;
    private final LiteLoaderRemoteVersion remote;
    private final Version version;
    private final List<Task<?>> dependents = new ArrayList();
    private final List<Task<?>> dependencies = new ArrayList(1);

    public LiteLoaderInstallTask(DefaultDependencyManager dependencyManager, Version version, LiteLoaderRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remote = remoteVersion;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() {
        Library library = new Library(new Artifact("com.mumfrey", "liteloader", this.remote.getSelfVersion()), "http://dl.liteloader.com/versions/", new LibrariesDownloadInfo(new LibraryDownloadInfo(null, this.remote.getUrls().get(0))));
        setResult(new Version(LibraryAnalyzer.LibraryType.LITELOADER.getPatchId(), this.remote.getSelfVersion(), 60000, new Arguments().addGameArguments("--tweakClass", LibraryAnalyzer.LITELOADER_TWEAKER), LibraryAnalyzer.LAUNCH_WRAPPER_MAIN, Lang.merge(this.remote.getLibraries(), Collections.singleton(library))).setLogging(Collections.emptyMap()));
        this.dependencies.add(this.dependencyManager.checkLibraryCompletionAsync(getResult(), true));
    }
}
