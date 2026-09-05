package com.brixcore.download.forge;

import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeVersion implements Validation {
    private final String branch;
    private final int build;
    private final String[][] files;
    private final String jobver;
    private final String mcversion;
    private final String modified;
    private final String version;

    public ForgeVersion() {
        this(null, null, null, null, 0, null, null);
    }

    public ForgeVersion(String branch, String mcversion, String jobver, String version, int build, String modified, String[][] files) {
        this.branch = branch;
        this.mcversion = mcversion;
        this.jobver = jobver;
        this.version = version;
        this.build = build;
        this.modified = modified;
        this.files = files;
    }

    public String getBranch() {
        return this.branch;
    }

    public String getGameVersion() {
        return this.mcversion;
    }

    public String getJobver() {
        return this.jobver;
    }

    public String getVersion() {
        return this.version;
    }

    public int getBuild() {
        return this.build;
    }

    public String getModified() {
        return this.modified;
    }

    public String[][] getFiles() {
        return this.files;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.files == null) {
            throw new JsonParseException("ForgeVersion files cannot be null");
        }
        if (this.version == null) {
            throw new JsonParseException("ForgeVersion version cannot be null");
        }
        if (this.mcversion == null) {
            throw new JsonParseException("ForgeVersion mcversion cannot be null");
        }
    }
}
