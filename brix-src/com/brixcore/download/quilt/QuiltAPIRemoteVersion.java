package com.brixcore.download.quilt;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.Version;
import com.brixcore.mod.RemoteMod;
import com.brixcore.task.Task;
import java.time.Instant;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public class QuiltAPIRemoteVersion extends RemoteVersion {
    private final String fullVersion;
    private final RemoteMod.Version version;

    QuiltAPIRemoteVersion(String gameVersion, String selfVersion, String fullVersion, Instant datePublished, RemoteMod.Version version, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.QUILT_API.getPatchId(), gameVersion, selfVersion, datePublished, urls);
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
        return new QuiltAPIInstallTask(dependencyManager, baseVersion, this);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.download.RemoteVersion, java.lang.Comparable
    public int compareTo(RemoteVersion o) {
        if (o instanceof QuiltAPIRemoteVersion) {
            return -getReleaseDate().compareTo(o.getReleaseDate());
        }
        return 0;
    }
}
