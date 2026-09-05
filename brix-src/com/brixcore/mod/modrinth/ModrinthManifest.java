package com.brixcore.mod.modrinth;

import com.brixcore.mod.ModpackManifest;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes14.dex */
public class ModrinthManifest implements ModpackManifest, Validation {
    private final Map<String, String> dependencies;
    private final List<File> files;
    private final int formatVersion;
    private final String game;
    private final String name;
    private final String summary;
    private final String versionId;

    public ModrinthManifest(String game, int formatVersion, String versionId, String name, String summary, List<File> files, Map<String, String> dependencies) {
        this.game = game;
        this.formatVersion = formatVersion;
        this.versionId = versionId;
        this.name = name;
        this.summary = summary;
        this.files = files;
        this.dependencies = dependencies;
    }

    public String getGame() {
        return this.game;
    }

    public int getFormatVersion() {
        return this.formatVersion;
    }

    public String getVersionId() {
        return this.versionId;
    }

    public String getName() {
        return this.name;
    }

    public String getSummary() {
        return this.summary == null ? "" : this.summary;
    }

    public List<File> getFiles() {
        return this.files;
    }

    public Map<String, String> getDependencies() {
        return this.dependencies;
    }

    public String getGameVersion() {
        return this.dependencies.get("minecraft");
    }

    @Override // com.brixcore.mod.ModpackManifest
    public ModpackProvider getProvider() {
        return ModrinthModpackProvider.INSTANCE;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        if (this.dependencies == null || this.dependencies.get("minecraft") == null) {
            throw new JsonParseException("missing Modrinth.dependencies.minecraft");
        }
    }

    public static class File {
        private final List<URL> downloads;
        private final Map<String, String> env;
        private final int fileSize;
        private final Map<String, String> hashes;
        private final String path;

        public File(String path, Map<String, String> hashes, Map<String, String> env, List<URL> downloads, int fileSize) {
            this.path = path;
            this.hashes = hashes;
            this.env = env;
            this.downloads = downloads;
            this.fileSize = fileSize;
        }

        public String getPath() {
            return this.path;
        }

        public Map<String, String> getHashes() {
            return this.hashes;
        }

        public Map<String, String> getEnv() {
            return this.env;
        }

        public List<URL> getDownloads() {
            return this.downloads;
        }

        public int getFileSize() {
            return this.fileSize;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            File file = (File) o;
            if (this.fileSize == file.fileSize && this.path.equals(file.path) && this.hashes.equals(file.hashes) && Objects.equals(this.env, file.env) && this.downloads.equals(file.downloads)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.path, this.hashes, this.env, this.downloads, Integer.valueOf(this.fileSize));
        }
    }
}
