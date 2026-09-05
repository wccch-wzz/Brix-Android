package com.brixcore.download.liteloader;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Library;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes14.dex */
public class LiteLoaderRemoteVersion extends RemoteVersion {
    private final Collection<Library> libraries;
    private final String tweakClass;

    LiteLoaderRemoteVersion(String gameVersion, String selfVersion, RemoteVersion.Type type, List<String> urls, String tweakClass, Collection<Library> libraries) {
        super(LibraryAnalyzer.LibraryType.LITELOADER.getPatchId(), gameVersion, selfVersion, null, type, urls);
        this.tweakClass = tweakClass;
        this.libraries = libraries;
    }

    public Collection<Library> getLibraries() {
        return this.libraries;
    }

    public String getTweakClass() {
        return this.tweakClass;
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new LiteLoaderInstallTask(dependencyManager, baseVersion, this);
    }
}
