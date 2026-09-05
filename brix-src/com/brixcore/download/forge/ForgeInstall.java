package com.brixcore.download.forge;

import com.brixcore.game.Artifact;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeInstall {
    private final String filePath;
    private final String logo;
    private final String minecraft;
    private final String mirrorList;
    private final Artifact path;
    private final String profileName;
    private final String target;
    private final String version;
    private final String welcome;

    public ForgeInstall() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public ForgeInstall(String profileName, String target, Artifact path, String version, String filePath, String welcome, String minecraft, String mirrorList, String logo) {
        this.profileName = profileName;
        this.target = target;
        this.path = path;
        this.version = version;
        this.filePath = filePath;
        this.welcome = welcome;
        this.minecraft = minecraft;
        this.mirrorList = mirrorList;
        this.logo = logo;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public String getTarget() {
        return this.target;
    }

    public Artifact getPath() {
        return this.path;
    }

    public String getVersion() {
        return this.version;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getWelcome() {
        return this.welcome;
    }

    public String getMinecraft() {
        return this.minecraft;
    }

    public String getMirrorList() {
        return this.mirrorList;
    }

    public String getLogo() {
        return this.logo;
    }
}
