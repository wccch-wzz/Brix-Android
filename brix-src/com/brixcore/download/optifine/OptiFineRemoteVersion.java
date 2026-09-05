package com.brixcore.download.optifine;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import java.util.List;

/* JADX INFO: loaded from: classes11.dex */
public class OptiFineRemoteVersion extends RemoteVersion {
    public OptiFineRemoteVersion(String gameVersion, String selfVersion, List<String> urls, boolean snapshot) {
        super(LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId(), gameVersion, selfVersion, null, snapshot ? RemoteVersion.Type.SNAPSHOT : RemoteVersion.Type.RELEASE, urls);
    }

    @Override // com.brixcore.download.RemoteVersion
    public String getFullVersion() {
        return getGameVersion() + "_" + getSelfVersion();
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new OptiFineInstallTask(dependencyManager, baseVersion, this);
    }
}
