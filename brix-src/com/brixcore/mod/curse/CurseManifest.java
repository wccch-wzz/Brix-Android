package com.brixcore.mod.curse;

import com.brixcore.mod.ModpackManifest;
import com.brixcore.mod.ModpackProvider;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseManifest implements ModpackManifest {
    public static final String MINECRAFT_MODPACK = "minecraftModpack";

    @SerializedName("author")
    private final String author;

    @SerializedName("files")
    private final List<CurseManifestFile> files;

    @SerializedName("manifestType")
    private final String manifestType;

    @SerializedName("manifestVersion")
    private final int manifestVersion;

    @SerializedName("minecraft")
    private final CurseManifestMinecraft minecraft;

    @SerializedName("name")
    private final String name;

    @SerializedName("overrides")
    private final String overrides;

    @SerializedName("version")
    private final String version;

    public CurseManifest() {
        this("minecraftModpack", 1, "", "1.0", "", "overrides", new CurseManifestMinecraft(), Collections.emptyList());
    }

    public CurseManifest(String manifestType, int manifestVersion, String name, String version, String author, String overrides, CurseManifestMinecraft minecraft, List<CurseManifestFile> files) {
        this.manifestType = manifestType;
        this.manifestVersion = manifestVersion;
        this.name = name;
        this.version = version;
        this.author = author;
        this.overrides = overrides;
        this.minecraft = minecraft;
        this.files = files;
    }

    public String getManifestType() {
        return this.manifestType;
    }

    public int getManifestVersion() {
        return this.manifestVersion;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getOverrides() {
        return this.overrides;
    }

    public CurseManifestMinecraft getMinecraft() {
        return this.minecraft;
    }

    public List<CurseManifestFile> getFiles() {
        return this.files;
    }

    public CurseManifest setFiles(List<CurseManifestFile> files) {
        return new CurseManifest(this.manifestType, this.manifestVersion, this.name, this.version, this.author, this.overrides, this.minecraft, files);
    }

    @Override // com.brixcore.mod.ModpackManifest
    public ModpackProvider getProvider() {
        return CurseModpackProvider.INSTANCE;
    }
}
