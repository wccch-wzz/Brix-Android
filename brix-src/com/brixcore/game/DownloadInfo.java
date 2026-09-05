package com.brixcore.game;

import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.StringUtils;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.file.Path;

/* JADX INFO: loaded from: classes2.dex */
public class DownloadInfo implements Validation {

    @SerializedName("sha1")
    private final String sha1;

    @SerializedName("size")
    private final int size;

    @SerializedName("url")
    private final String url;

    public DownloadInfo() {
        this("");
    }

    public DownloadInfo(String url) {
        this(url, null);
    }

    public DownloadInfo(String url, String sha1) {
        this(url, sha1, 0);
    }

    public DownloadInfo(String url, String sha1, int size) {
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    public String getUrl() {
        return this.url;
    }

    public String getSha1() {
        if ("invalid".equals(this.sha1)) {
            return null;
        }
        return this.sha1;
    }

    public int getSize() {
        return this.size;
    }

    public String toString() {
        return new ToStringBuilder(this).append("url", this.url).append("sha1", this.sha1).append("size", Integer.valueOf(this.size)).toString();
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        if (StringUtils.isBlank(this.url)) {
            throw new TolerableValidationException();
        }
    }

    public boolean validateChecksum(Path file, boolean defaultValue) throws IOException {
        return getSha1() == null ? defaultValue : DigestUtils.digestToString(CacheRepository.SHA1, file).equalsIgnoreCase(getSha1());
    }
}
