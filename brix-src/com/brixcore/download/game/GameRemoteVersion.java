package com.brixcore.download.game;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.RemoteVersion;
import com.brixcore.game.ReleaseType;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.versioning.GameVersionNumber;
import java.time.Instant;
import java.util.List;

/* JADX INFO: loaded from: classes9.dex */
public final class GameRemoteVersion extends RemoteVersion {
    private final ReleaseType type;

    public GameRemoteVersion(String gameVersion, String selfVersion, List<String> url, ReleaseType type, Instant releaseDate) {
        super(LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId(), gameVersion, selfVersion, releaseDate, getReleaseType(type), url);
        this.type = type;
    }

    public ReleaseType getType() {
        return this.type;
    }

    @Override // com.brixcore.download.RemoteVersion
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new GameInstallTask(dependencyManager, baseVersion, this);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.download.RemoteVersion, java.lang.Comparable
    public int compareTo(RemoteVersion o) {
        if (!(o instanceof GameRemoteVersion)) {
            return 0;
        }
        int dateCompare = o.getReleaseDate().compareTo(getReleaseDate());
        return dateCompare != 0 ? dateCompare : GameVersionNumber.compare(getGameVersion(), o.getGameVersion());
    }

    private static RemoteVersion.Type getReleaseType(ReleaseType type) {
        if (type == null) {
            return RemoteVersion.Type.UNCATEGORIZED;
        }
        switch (type) {
            case RELEASE:
                return RemoteVersion.Type.RELEASE;
            case SNAPSHOT:
                return RemoteVersion.Type.SNAPSHOT;
            case UNKNOWN:
                return RemoteVersion.Type.UNCATEGORIZED;
            case PENDING:
                return RemoteVersion.Type.PENDING;
            case UNOBFUSCATED:
                return RemoteVersion.Type.UNOBFUSCATED;
            default:
                return RemoteVersion.Type.OLD;
        }
    }
}
