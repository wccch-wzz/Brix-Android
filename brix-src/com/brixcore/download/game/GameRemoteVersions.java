package com.brixcore.download.game;

import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
import org.apache.commons.compress.java.util.jar.Pack200;

/* JADX INFO: loaded from: classes9.dex */
public final class GameRemoteVersions implements Validation {

    @SerializedName(Pack200.Packer.LATEST)
    private final GameRemoteLatestVersions latest;

    @SerializedName("versions")
    private final List<GameRemoteVersionInfo> versions;

    public GameRemoteVersions() {
        this(Collections.emptyList(), null);
    }

    public GameRemoteVersions(List<GameRemoteVersionInfo> versions, GameRemoteLatestVersions latest) {
        this.versions = versions;
        this.latest = latest;
    }

    public GameRemoteLatestVersions getLatest() {
        return this.latest;
    }

    public List<GameRemoteVersionInfo> getVersions() {
        return this.versions;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.versions == null) {
            throw new JsonParseException("GameRemoteVersions.versions cannot be null");
        }
    }
}
