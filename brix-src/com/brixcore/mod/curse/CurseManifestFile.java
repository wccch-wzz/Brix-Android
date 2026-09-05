package com.brixcore.mod.curse;

import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.net.URL;
import java.util.Objects;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseManifestFile implements Validation {

    @SerializedName("fileID")
    private final int fileID;

    @SerializedName("fileName")
    private final String fileName;

    @SerializedName("projectID")
    private final int projectID;

    @SerializedName("required")
    private final boolean required;

    @SerializedName("url")
    private final String url;

    public CurseManifestFile() {
        this(0, 0, null, null, true);
    }

    public CurseManifestFile(int projectID, int fileID, String fileName, String url, boolean required) {
        this.projectID = projectID;
        this.fileID = fileID;
        this.fileName = fileName;
        this.url = url;
        this.required = required;
    }

    public int getProjectID() {
        return this.projectID;
    }

    public int getFileID() {
        return this.fileID;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isRequired() {
        return this.required;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.projectID == 0 || this.fileID == 0) {
            throw new JsonParseException("Missing Project ID or File ID.");
        }
    }

    public URL getUrl() {
        if (this.url == null) {
            if (this.fileName != null) {
                return NetworkUtils.toURL(NetworkUtils.encodeLocation(String.format("https://edge.forgecdn.net/files/%d/%d/%s", Integer.valueOf(this.fileID / 1000), Integer.valueOf(this.fileID % 1000), this.fileName)));
            }
            return null;
        }
        return NetworkUtils.toURL(NetworkUtils.encodeLocation(this.url));
    }

    public CurseManifestFile withFileName(String fileName) {
        return new CurseManifestFile(this.projectID, this.fileID, fileName, this.url, this.required);
    }

    public CurseManifestFile withURL(String url) {
        return new CurseManifestFile(this.projectID, this.fileID, this.fileName, url, this.required);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurseManifestFile that = (CurseManifestFile) o;
        if (this.projectID == that.projectID && this.fileID == that.fileID) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.projectID), Integer.valueOf(this.fileID));
    }
}
