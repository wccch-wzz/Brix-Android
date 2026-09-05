package com.brixcore.download.game;

import com.brixcore.game.ReleaseType;
import com.brixcore.util.Constants;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.time.Instant;

/* JADX INFO: loaded from: classes9.dex */
public final class GameRemoteVersionInfo implements Validation {

    @SerializedName("id")
    private final String gameVersion;

    @SerializedName("releaseTime")
    private final Instant releaseTime;

    @SerializedName("time")
    private final Instant time;

    @SerializedName("type")
    private final ReleaseType type;

    @SerializedName("url")
    private final String url;

    public GameRemoteVersionInfo() {
        this("", Instant.now(), Instant.now(), ReleaseType.UNKNOWN);
    }

    public GameRemoteVersionInfo(String gameVersion, Instant time, Instant releaseTime, ReleaseType type) {
        this(gameVersion, time, releaseTime, type, Constants.DEFAULT_LIBRARY_URL + gameVersion + "/" + gameVersion + ".json");
    }

    public GameRemoteVersionInfo(String gameVersion, Instant time, Instant releaseTime, ReleaseType type, String url) {
        this.gameVersion = gameVersion;
        this.time = time;
        this.releaseTime = releaseTime;
        this.type = type;
        this.url = url;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public Instant getTime() {
        return this.time;
    }

    public Instant getReleaseTime() {
        return this.releaseTime;
    }

    public ReleaseType getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(this.gameVersion)) {
            throw new JsonParseException("GameRemoteVersion id cannot be blank");
        }
        if (StringUtils.isBlank(this.url)) {
            throw new JsonParseException("GameRemoteVersion url cannot be blank");
        }
    }
}
