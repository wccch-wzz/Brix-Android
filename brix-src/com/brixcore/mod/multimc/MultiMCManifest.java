package com.brixcore.mod.multimc;

import com.brixcore.util.gson.JsonUtils;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes10.dex */
public final class MultiMCManifest {

    @SerializedName("components")
    private final List<MultiMCManifestComponent> components;

    @SerializedName("formatVersion")
    private final int formatVersion;

    public MultiMCManifest(int formatVersion, List<MultiMCManifestComponent> components) {
        this.formatVersion = formatVersion;
        this.components = components;
    }

    public int getFormatVersion() {
        return this.formatVersion;
    }

    public List<MultiMCManifestComponent> getComponents() {
        return this.components;
    }

    public static MultiMCManifest readMultiMCModpackManifest(ZipFile zipFile, String rootEntryName) throws IOException {
        ZipArchiveEntry mmcPack = zipFile.getEntry(rootEntryName + "mmc-pack.json");
        if (mmcPack == null) {
            return null;
        }
        MultiMCManifest manifest = (MultiMCManifest) JsonUtils.fromNonNullJsonFully(zipFile.getInputStream(mmcPack), MultiMCManifest.class);
        if (manifest.getComponents() == null) {
            throw new IOException("mmc-pack.json malformed.");
        }
        return manifest;
    }

    public static final class MultiMCManifestCachedRequires {

        @SerializedName("equals")
        private final String equalsVersion;

        @SerializedName("suggests")
        private final String suggests;

        @SerializedName("uid")
        private final String uid;

        public MultiMCManifestCachedRequires(String equalsVersion, String uid, String suggests) {
            this.equalsVersion = equalsVersion;
            this.uid = uid;
            this.suggests = suggests;
        }

        public String getEqualsVersion() {
            return this.equalsVersion;
        }

        public String getUid() {
            return this.uid;
        }

        public String getSuggests() {
            return this.suggests;
        }
    }

    public static final class MultiMCManifestComponent {

        @SerializedName("cachedName")
        private final String cachedName;

        @SerializedName("cachedRequires")
        private final List<MultiMCManifestCachedRequires> cachedRequires;

        @SerializedName("cachedVersion")
        private final String cachedVersion;

        @SerializedName("dependencyOnly")
        private final boolean dependencyOnly;

        @SerializedName("important")
        private final boolean important;

        @SerializedName("uid")
        private final String uid;

        @SerializedName("version")
        private final String version;

        public MultiMCManifestComponent(boolean important, boolean dependencyOnly, String uid, String version) {
            this(null, null, null, important, dependencyOnly, uid, version);
        }

        public MultiMCManifestComponent(String cachedName, List<MultiMCManifestCachedRequires> cachedRequires, String cachedVersion, boolean important, boolean dependencyOnly, String uid, String version) {
            this.cachedName = cachedName;
            this.cachedRequires = cachedRequires;
            this.cachedVersion = cachedVersion;
            this.important = important;
            this.dependencyOnly = dependencyOnly;
            this.uid = uid;
            this.version = version;
        }

        public String getCachedName() {
            return this.cachedName;
        }

        public List<MultiMCManifestCachedRequires> getCachedRequires() {
            return this.cachedRequires;
        }

        public String getCachedVersion() {
            return this.cachedVersion;
        }

        public boolean isImportant() {
            return this.important;
        }

        public boolean isDependencyOnly() {
            return this.dependencyOnly;
        }

        public String getUid() {
            return this.uid;
        }

        public String getVersion() {
            return this.version;
        }
    }
}
