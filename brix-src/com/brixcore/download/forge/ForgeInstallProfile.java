package com.brixcore.download.forge;

import com.brixcore.game.Version;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeInstallProfile implements Validation {

    @SerializedName("install")
    private final ForgeInstall install;

    @SerializedName("versionInfo")
    private final Version versionInfo;

    public ForgeInstallProfile(ForgeInstall install, Version versionInfo) {
        this.install = install;
        this.versionInfo = versionInfo;
    }

    public ForgeInstall getInstall() {
        return this.install;
    }

    public Version getVersionInfo() {
        return this.versionInfo;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.install == null) {
            throw new JsonParseException("InstallProfile install cannot be null");
        }
        if (this.versionInfo == null) {
            throw new JsonParseException("InstallProfile versionInfo cannot be null");
        }
    }
}
