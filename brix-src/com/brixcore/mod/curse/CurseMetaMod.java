package com.brixcore.mod.curse;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseMetaMod {

    @SerializedName(alternate = {"downloadUrl"}, value = "DownloadURL")
    private final String downloadURL;

    @SerializedName(alternate = {"fileName"}, value = "FileName")
    private final String fileName;

    @SerializedName("FileNameOnDisk")
    private final String fileNameOnDisk;

    @SerializedName(alternate = {"id"}, value = "Id")
    private final int id;

    public CurseMetaMod() {
        this(0, "", "", "");
    }

    public CurseMetaMod(int id, String fileName, String fileNameOnDisk, String downloadURL) {
        this.id = id;
        this.fileName = fileName;
        this.fileNameOnDisk = fileNameOnDisk;
        this.downloadURL = downloadURL;
    }

    public int getId() {
        return this.id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileNameOnDisk() {
        return this.fileNameOnDisk;
    }

    public String getDownloadURL() {
        return this.downloadURL;
    }
}
