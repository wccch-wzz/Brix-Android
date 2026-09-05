package com.brixcore.auth.yggdrasil;

import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public final class Texture {
    private final Map<String, String> metadata;
    private final String url;

    public Texture() {
        this(null, null);
    }

    public Texture(String url, Map<String, String> metadata) {
        this.url = url;
        this.metadata = metadata;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }
}
