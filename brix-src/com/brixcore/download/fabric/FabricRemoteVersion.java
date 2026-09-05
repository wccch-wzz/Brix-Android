package com.brixcore.download.fabric;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class FabricRemoteVersion extends RemoteVersion {
    FabricRemoteVersion(String gameVersion, String selfVersion, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.FABRIC.getPatchId(), gameVersion, selfVersion, null, urls);
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new FabricInstallTask(dependencyManager, baseVersion, this);
    }
}
