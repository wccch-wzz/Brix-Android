package com.brixcore.auth;

import com.brixcore.game.Arguments;
import com.brixcore.game.LaunchOptions;
import java.io.IOException;
import java.util.UUID;

/* JADX INFO: loaded from: classes8.dex */
public class AuthInfo {
    public static final String USER_TYPE_LEGACY = "legacy";
    public static final String USER_TYPE_MOJANG = "mojang";
    public static final String USER_TYPE_MSA = "msa";
    private final String accessToken;
    private final String userProperties;
    private final String userType;
    private final String username;
    private final UUID uuid;

    public AuthInfo(String username, UUID uuid, String accessToken, String userType, String userProperties) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.userType = userType;
        this.userProperties = userProperties;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getUserType() {
        return this.userType;
    }

    public String getUserProperties() {
        return this.userProperties;
    }

    public Arguments getLaunchArguments(LaunchOptions options) throws IOException {
        return null;
    }
}
