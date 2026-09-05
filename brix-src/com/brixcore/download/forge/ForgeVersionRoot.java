package com.brixcore.download.forge;

import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeVersionRoot implements Validation {
    private final String adfly;
    private final String artifact;
    private final Map<String, int[]> branches;
    private final String homepage;
    private final Map<String, int[]> mcversion;
    private final String name;
    private final Map<Integer, ForgeVersion> number;
    private final Map<String, Integer> promos;
    private final String webpath;

    public ForgeVersionRoot() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public ForgeVersionRoot(String artifact, String webpath, String adfly, String homepage, String name, Map<String, int[]> branches, Map<String, int[]> mcversion, Map<String, Integer> promos, Map<Integer, ForgeVersion> number) {
        this.artifact = artifact;
        this.webpath = webpath;
        this.adfly = adfly;
        this.homepage = homepage;
        this.name = name;
        this.branches = branches;
        this.mcversion = mcversion;
        this.promos = promos;
        this.number = number;
    }

    public String getArtifact() {
        return this.artifact;
    }

    public String getWebPath() {
        return this.webpath;
    }

    public String getAdfly() {
        return this.adfly;
    }

    public String getHomePage() {
        return this.homepage;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, int[]> getBranches() {
        return this.branches;
    }

    public Map<String, int[]> getGameVersions() {
        return this.mcversion;
    }

    public Map<String, Integer> getPromos() {
        return this.promos;
    }

    public Map<Integer, ForgeVersion> getNumber() {
        return this.number;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.number == null) {
            throw new JsonParseException("ForgeVersionRoot number cannot be null");
        }
        if (this.mcversion == null) {
            throw new JsonParseException("ForgeVersionRoot mcversion cannot be null");
        }
    }
}
