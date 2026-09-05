package com.brixcore.mod;

import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class ModpackConfiguration<T> implements Validation {
    private final T manifest;
    private final String name;
    private final List<FileInformation> overrides;
    private final String type;
    private final String version;

    public ModpackConfiguration() {
        this(null, null, "", null, Collections.emptyList());
    }

    public ModpackConfiguration(T manifest, String type, String name, String version, List<FileInformation> overrides) {
        this.manifest = manifest;
        this.type = type;
        this.name = name;
        this.version = version;
        this.overrides = new ArrayList(overrides);
    }

    public T getManifest() {
        return this.manifest;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public ModpackConfiguration<T> setManifest(T manifest) {
        return new ModpackConfiguration<>(manifest, this.type, this.name, this.version, this.overrides);
    }

    public ModpackConfiguration<T> setOverrides(List<FileInformation> overrides) {
        return new ModpackConfiguration<>(this.manifest, this.type, this.name, this.version, overrides);
    }

    public ModpackConfiguration<T> setVersion(String version) {
        return new ModpackConfiguration<>(this.manifest, this.type, this.name, version, this.overrides);
    }

    public List<FileInformation> getOverrides() {
        return Collections.unmodifiableList(this.overrides);
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.manifest == null) {
            throw new JsonParseException("MinecraftInstanceConfiguration missing `manifest`");
        }
        if (this.type == null) {
            throw new JsonParseException("MinecraftInstanceConfiguration missing `type`");
        }
    }

    public static class FileInformation implements Validation {
        private final String downloadURL;
        private final String hash;
        private final String path;

        public FileInformation() {
            this(null, null);
        }

        public FileInformation(String path, String hash) {
            this(path, hash, null);
        }

        public FileInformation(String path, String hash, String downloadURL) {
            this.path = path;
            this.hash = hash;
            this.downloadURL = downloadURL;
        }

        public String getPath() {
            return this.path;
        }

        public String getDownloadURL() {
            return this.downloadURL;
        }

        public String getHash() {
            return this.hash;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException {
            if (this.path == null) {
                throw new JsonParseException("FileInformation missing `path`.");
            }
            if (this.hash == null) {
                throw new JsonParseException("FileInformation missing file hash code.");
            }
        }
    }
}
