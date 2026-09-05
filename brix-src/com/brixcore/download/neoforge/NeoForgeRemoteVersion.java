package com.brixcore.download.neoforge;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public class NeoForgeRemoteVersion extends RemoteVersion {
    public NeoForgeRemoteVersion(String gameVersion, String selfVersion, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId(), gameVersion, selfVersion, null, getType(selfVersion), urls);
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new NeoForgeInstallTask(dependencyManager, baseVersion, this);
    }

    private static RemoteVersion.Type getType(String version) {
        return version.contains("beta") ? RemoteVersion.Type.SNAPSHOT : RemoteVersion.Type.RELEASE;
    }

    public static String normalize(String version) {
        if (version.startsWith("1.20.1-")) {
            if (version.startsWith("forge-", "1.20.1-".length())) {
                return version.substring("1.20.1-forge-".length());
            }
            return version.substring("1.20.1-".length());
        }
        return version;
    }
}
