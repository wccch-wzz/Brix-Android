package com.brixcore.download.liteloader;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderRepository {

    @SerializedName("classifier")
    private final String classifier;

    @SerializedName("stream")
    private final String stream;

    @SerializedName("type")
    private final String type;

    @SerializedName("url")
    private final String url;

    public LiteLoaderRepository() {
        this("", "", "", "");
    }

    public LiteLoaderRepository(String stream, String type, String url, String classifier) {
        this.stream = stream;
        this.type = type;
        this.url = url;
        this.classifier = classifier;
    }

    public String getStream() {
        return this.stream;
    }

    public String getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public String getClassifier() {
        return this.classifier;
    }
}
