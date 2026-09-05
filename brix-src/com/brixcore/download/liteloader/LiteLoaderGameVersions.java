package com.brixcore.download.liteloader;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderGameVersions {

    @SerializedName("artefacts")
    private final LiteLoaderBranch artifacts;

    @SerializedName("repo")
    private final LiteLoaderRepository repoitory;

    @SerializedName("snapshots")
    private final LiteLoaderBranch snapshots;

    public LiteLoaderGameVersions() {
        this(null, null, null);
    }

    public LiteLoaderGameVersions(LiteLoaderRepository repoitory, LiteLoaderBranch artifacts, LiteLoaderBranch snapshots) {
        this.repoitory = repoitory;
        this.artifacts = artifacts;
        this.snapshots = snapshots;
    }

    public LiteLoaderRepository getRepoitory() {
        return this.repoitory;
    }

    public LiteLoaderBranch getArtifacts() {
        return this.artifacts;
    }

    public LiteLoaderBranch getSnapshots() {
        return this.snapshots;
    }
}
