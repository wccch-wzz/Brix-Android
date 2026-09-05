package com.brixcore.download.fabric;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.mod.RemoteMod;
import com.brixcore.task.Task;
import java.time.Instant;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class FabricAPIRemoteVersion extends RemoteVersion {
    private final String fullVersion;
    private final RemoteMod.Version version;

    FabricAPIRemoteVersion(String gameVersion, String selfVersion, String fullVersion, Instant datePublished, RemoteMod.Version version, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.FABRIC_API.getPatchId(), gameVersion, selfVersion, datePublished, urls);
        this.fullVersion = fullVersion;
        this.version = version;
    }

    @Override // com.brixcore.download.RemoteVersion
    public String getFullVersion() {
        return this.fullVersion;
    }

    public RemoteMod.Version getVersion() {
        return this.version;
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new FabricAPIInstallTask(dependencyManager, baseVersion, this);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.download.RemoteVersion, java.lang.Comparable
    public int compareTo(RemoteVersion o) {
        if (o instanceof FabricAPIRemoteVersion) {
            return -getReleaseDate().compareTo(o.getReleaseDate());
        }
        return 0;
    }
}
