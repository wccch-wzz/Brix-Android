package com.brixcore.mod.curse;

import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseManifestModLoader implements Validation {

    @SerializedName("id")
    private final String id;

    @SerializedName("primary")
    private final boolean primary;

    public CurseManifestModLoader() {
        this("", false);
    }

    public CurseManifestModLoader(String id, boolean primary) {
        this.id = id;
        this.primary = primary;
    }

    public String getId() {
        return this.id;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(this.id)) {
            throw new JsonParseException("Curse Forge modpack manifest Mod loader id cannot be blank.");
        }
    }
}
