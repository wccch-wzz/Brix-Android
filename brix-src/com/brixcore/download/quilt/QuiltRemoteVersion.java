package com.brixcore.download.quilt;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public class QuiltRemoteVersion extends RemoteVersion {
    QuiltRemoteVersion(String gameVersion, String selfVersion, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.QUILT.getPatchId(), gameVersion, selfVersion, null, urls);
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new QuiltInstallTask(dependencyManager, baseVersion, this);
    }
}
