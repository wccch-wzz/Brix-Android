package com.brixcore.mod.curse;

import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseManifestMinecraft implements Validation {

    @SerializedName("version")
    private final String gameVersion;

    @SerializedName("modLoaders")
    private final List<CurseManifestModLoader> modLoaders;

    public CurseManifestMinecraft() {
        this.gameVersion = "";
        this.modLoaders = Collections.emptyList();
    }

    public CurseManifestMinecraft(String gameVersion, List<CurseManifestModLoader> modLoaders) {
        this.gameVersion = gameVersion;
        this.modLoaders = new ArrayList(modLoaders);
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public List<CurseManifestModLoader> getModLoaders() {
        return Collections.unmodifiableList(this.modLoaders);
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(this.gameVersion)) {
            throw new JsonParseException("CurseForge Manifest.gameVersion cannot be blank.");
        }
    }
}
