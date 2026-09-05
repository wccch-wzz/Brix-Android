package com.brixcore.mod;

import java.util.HashSet;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class LocalMod {
    private final String id;
    private final ModLoaderType modLoaderType;
    private final HashSet<LocalModFile> files = new HashSet<>();
    private final HashSet<LocalModFile> oldFiles = new HashSet<>();

    public LocalMod(String id, ModLoaderType modLoaderType) {
        this.id = id;
        this.modLoaderType = modLoaderType;
    }

    public String getId() {
        return this.id;
    }

    public ModLoaderType getModLoaderType() {
        return this.modLoaderType;
    }

    public HashSet<LocalModFile> getFiles() {
        return this.files;
    }

    public HashSet<LocalModFile> getOldFiles() {
        return this.oldFiles;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocalMod localMod = (LocalMod) o;
        if (Objects.equals(this.id, localMod.id) && this.modLoaderType == localMod.modLoaderType) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.modLoaderType);
    }
}
