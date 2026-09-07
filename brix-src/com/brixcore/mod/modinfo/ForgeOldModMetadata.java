package com.brixcore.mod.modinfo;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes10.dex */
public final class ForgeOldModMetadata {
    private final String author;
    private final String[] authorList;
    private final String[] authors;
    private final String credits;
    private final String description;
    private final String logoFile;
    private final String mcversion;

    @SerializedName("modid")
    private final String modId;
    private final String name;
    private final String updateUrl;
    private final String url;
    private final String version;

    public ForgeOldModMetadata() {
        this("", "", "", "", "", "", "", "", "", "", new String[0], new String[0]);
    }

    public ForgeOldModMetadata(String modId, String name, String description, String author, String version, String logoFile, String mcversion, String url, String updateUrl, String credits, String[] authorList, String[] authors) {
        this.modId = modId;
        this.name = name;
        this.description = description;
        this.author = author;
        this.version = version;
        this.logoFile = logoFile;
        this.mcversion = mcversion;
        this.url = url;
        this.updateUrl = updateUrl;
        this.credits = credits;
        this.authorList = authorList;
        this.authors = authors;
    }

    public String getModId() {
        return this.modId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getVersion() {
        return this.version;
    }

    public String getLogoFile() {
        return this.logoFile;
    }

    public String getGameVersion() {
        return this.mcversion;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUpdateUrl() {
        return this.updateUrl;
    }

    public String getCredits() {
        return this.credits;
    }

    public String[] getAuthorList() {
        return this.authorList;
    }

    public String[] getAuthors() {
        return this.authors;
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0145 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.brixcore.mod.LocalModFile fromFile(com.brixcore.mod.ModManager r15, java.nio.file.Path r16, java.nio.file.FileSystem r17) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 375
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.brixcore.mod.modinfo.ForgeOldModMetadata.fromFile(com.brixcore.mod.ModManager, java.nio.file.Path, java.nio.file.FileSystem):com.brixcore.mod.LocalModFile");
    }
}
