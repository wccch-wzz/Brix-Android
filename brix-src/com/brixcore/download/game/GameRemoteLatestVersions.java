package com.brixcore.download.game;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes9.dex */
public final class GameRemoteLatestVersions {

    @SerializedName("release")
    private final String release;

    @SerializedName("snapshot")
    private final String snapshot;

    public GameRemoteLatestVersions() {
        this(null, null);
    }

    public GameRemoteLatestVersions(String snapshot, String release) {
        this.snapshot = snapshot;
        this.release = release;
    }

    public String getRelease() {
        return this.release;
    }

    public String getSnapshot() {
        return this.snapshot;
    }
}
