package com.brixcore.game;

import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.TolerableValidationException;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes2.dex */
public class IdDownloadInfo extends DownloadInfo {

    @SerializedName("id")
    private final String id;

    public IdDownloadInfo() {
        this("", "");
    }

    public IdDownloadInfo(String id, String url) {
        this(id, url, null);
    }

    public IdDownloadInfo(String id, String url, String sha1) {
        this(id, url, sha1, 0);
    }

    public IdDownloadInfo(String id, String url, String sha1, int size) {
        super(url, sha1, size);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override // com.brixcore.game.DownloadInfo, com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        super.validate();
        if (StringUtils.isBlank(this.id)) {
            throw new JsonParseException("IdDownloadInfo id can not be null");
        }
    }
}
