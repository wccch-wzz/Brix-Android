package com.brixcore.download;

import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.versioning.VersionNumber;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes14.dex */
public class RemoteVersion implements Comparable<RemoteVersion> {
    private final String gameVersion;
    private final String libraryId;
    private final Instant releaseDate;
    private final String selfVersion;
    private final Type type;
    private final List<String> urls;

    public enum Type {
        UNCATEGORIZED,
        RELEASE,
        SNAPSHOT,
        OLD,
        PENDING,
        UNOBFUSCATED
    }

    public RemoteVersion(String libraryId, String gameVersion, String selfVersion, Instant releaseDate, List<String> urls) {
        this(libraryId, gameVersion, selfVersion, releaseDate, Type.UNCATEGORIZED, urls);
    }

    public RemoteVersion(String libraryId, String gameVersion, String selfVersion, Instant releaseDate, Type type, List<String> urls) {
        this.libraryId = (String) Objects.requireNonNull(libraryId);
        this.gameVersion = (String) Objects.requireNonNull(gameVersion);
        this.selfVersion = (String) Objects.requireNonNull(selfVersion);
        this.releaseDate = releaseDate;
        this.urls = (List) Objects.requireNonNull(urls);
        this.type = (Type) Objects.requireNonNull(type);
    }

    public String getLibraryId() {
        return this.libraryId;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getSelfVersion() {
        return this.selfVersion;
    }

    public String getFullVersion() {
        return getSelfVersion();
    }

    public Instant getReleaseDate() {
        return this.releaseDate;
    }

    public List<String> getUrls() {
        return this.urls;
    }

    public Type getVersionType() {
        return this.type;
    }

    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        throw new UnsupportedOperationException(this + " cannot be installed yet");
    }

    public boolean equals(Object obj) {
        return (obj instanceof RemoteVersion) && Objects.equals(this.selfVersion, ((RemoteVersion) obj).selfVersion);
    }

    public int hashCode() {
        return this.selfVersion.hashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("selfVersion", this.selfVersion).append("gameVersion", this.gameVersion).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(RemoteVersion o) {
        return VersionNumber.asVersion(o.selfVersion).compareTo(VersionNumber.asVersion(this.selfVersion));
    }
}
