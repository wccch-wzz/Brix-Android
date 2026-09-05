package com.brixcore.download.liteloader;

import com.google.gson.annotations.SerializedName;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderVersionsMeta {

    @SerializedName("authors")
    private final String authors;

    @SerializedName("description")
    private final String description;

    @SerializedName("url")
    private final String url;

    public LiteLoaderVersionsMeta() {
        this("", "", "");
    }

    public LiteLoaderVersionsMeta(String description, String authors, String url) {
        this.description = description;
        this.authors = authors;
        this.url = url;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthors() {
        return this.authors;
    }

    public String getUrl() {
        return this.url;
    }
}
