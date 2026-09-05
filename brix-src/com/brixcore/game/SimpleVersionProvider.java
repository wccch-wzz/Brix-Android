package com.brixcore.game;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleVersionProvider implements VersionProvider {
    protected final Map<String, Version> versionMap = new HashMap();

    @Override // com.brixcore.game.VersionProvider
    public boolean hasVersion(String id) {
        return this.versionMap.containsKey(id);
    }

    @Override // com.brixcore.game.VersionProvider
    public Version getVersion(String id) {
        if (!hasVersion(id)) {
            throw new VersionNotFoundException("Version id " + id + " not found");
        }
        return this.versionMap.get(id);
    }

    public void addVersion(Version version) {
        this.versionMap.put(version.getId(), version);
    }

    public Map<String, Version> getVersionMap() {
        return this.versionMap;
    }
}
