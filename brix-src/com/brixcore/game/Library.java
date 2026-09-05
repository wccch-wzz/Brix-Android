package com.brixcore.game;

import com.brixcore.util.Constants;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.platform.OperatingSystem;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/* JADX INFO: loaded from: classes2.dex */
public class Library implements Comparable<Library>, Validation {

    @SerializedName("name")
    private final Artifact artifact;
    private final List<String> checksums;
    private final LibrariesDownloadInfo downloads;
    private final ExtractRules extract;

    @SerializedName(alternate = {"MMC-filename"}, value = "filename")
    private final String fileName;

    @SerializedName(alternate = {"MMC-hint"}, value = "hint")
    private final String hint;
    private final Map<OperatingSystem, String> natives;
    private final List<CompatibilityRule> rules;
    private final String url;

    public Library(Artifact artifact) {
        this(artifact, null, null);
    }

    public Library(Artifact artifact, String url, LibrariesDownloadInfo downloads) {
        this(artifact, url, downloads, null, null, null, null, null, null);
    }

    public Library(Artifact artifact, String url, LibrariesDownloadInfo downloads, List<String> checksums, ExtractRules extract, Map<OperatingSystem, String> natives, List<CompatibilityRule> rules, String hint, String filename) {
        this.artifact = artifact;
        this.url = url;
        this.downloads = downloads;
        this.extract = extract;
        this.natives = natives;
        this.rules = rules;
        this.checksums = checksums;
        this.hint = hint;
        this.fileName = filename;
    }

    public String getGroupId() {
        return this.artifact.getGroup();
    }

    public String getArtifactId() {
        return this.artifact.getName();
    }

    public String getName() {
        return this.artifact.toString();
    }

    public String getVersion() {
        return this.artifact.getVersion();
    }

    public String getClassifier() {
        if (this.artifact.getClassifier() == null) {
            return null;
        }
        return this.artifact.getClassifier();
    }

    public ExtractRules getExtract() {
        return this.extract == null ? ExtractRules.EMPTY : this.extract;
    }

    public boolean appliesToCurrentEnvironment() {
        return CompatibilityRule.appliesToCurrentEnvironment(this.rules);
    }

    public boolean isNative() {
        return this.natives != null && appliesToCurrentEnvironment();
    }

    protected LibraryDownloadInfo getRawDownloadInfo() {
        if (this.downloads != null) {
            if (isNative()) {
                return this.downloads.getClassifiers().get(getClassifier());
            }
            return this.downloads.getArtifact();
        }
        return null;
    }

    public String getPath() {
        LibraryDownloadInfo temp = getRawDownloadInfo();
        if (temp != null && temp.getPath() != null) {
            return temp.getPath();
        }
        return this.artifact.setClassifier(getClassifier()).getPath();
    }

    public LibraryDownloadInfo getDownload() {
        LibraryDownloadInfo temp = getRawDownloadInfo();
        String path = getPath();
        return new LibraryDownloadInfo(path, (String) Optional.ofNullable(temp).map(new Function() { // from class: com.brixcore.game.Library$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((LibraryDownloadInfo) obj).getUrl();
            }
        }).orElse(((String) Optional.ofNullable(this.url).orElse(Constants.DEFAULT_LIBRARY_URL)) + path), temp != null ? temp.getSha1() : null, temp != null ? temp.getSize() : 0);
    }

    public boolean hasDownloadURL() {
        LibraryDownloadInfo temp = getRawDownloadInfo();
        if (temp != null) {
            return temp.getUrl() != null;
        }
        return this.url != null;
    }

    public List<String> getChecksums() {
        return this.checksums;
    }

    public List<CompatibilityRule> getRules() {
        return this.rules;
    }

    public String getHint() {
        return this.hint;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean is(String groupId, String artifactId) {
        return getGroupId().equals(groupId) && getArtifactId().equals(artifactId);
    }

    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(Library o) {
        if (getName().compareTo(o.getName()) == 0) {
            return Boolean.compare(isNative(), o.isNative());
        }
        return getName().compareTo(o.getName());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Library)) {
            return false;
        }
        Library other = (Library) obj;
        return getName().equals(other.getName()) && isNative() == other.isNative();
    }

    public int hashCode() {
        return Objects.hash(getName(), Boolean.valueOf(isNative()));
    }

    public Library setClassifier(String classifier) {
        return new Library(this.artifact.setClassifier(classifier), this.url, this.downloads, this.checksums, this.extract, this.natives, this.rules, this.hint, this.fileName);
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        if (this.artifact == null) {
            throw new JsonParseException("Library.name cannot be null");
        }
    }
}
