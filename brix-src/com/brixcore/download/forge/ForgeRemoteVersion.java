package com.brixcore.download.forge;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import java.time.Instant;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class ForgeRemoteVersion extends RemoteVersion {
    public ForgeRemoteVersion(String gameVersion, String selfVersion, Instant releaseDate, List<String> url) {
        super(LibraryAnalyzer.LibraryType.FORGE.getPatchId(), gameVersion, selfVersion, releaseDate, url);
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new ForgeInstallTask(dependencyManager, baseVersion, this);
    }
}
