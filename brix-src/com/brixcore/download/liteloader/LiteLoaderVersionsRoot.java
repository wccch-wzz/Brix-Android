package com.brixcore.download.liteloader;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.Map;

/* JADX INFO: loaded from: classes14.dex */
public final class LiteLoaderVersionsRoot {

    @SerializedName("meta")
    private final LiteLoaderVersionsMeta meta;

    @SerializedName("versions")
    private final Map<String, LiteLoaderGameVersions> versions;

    public LiteLoaderVersionsRoot() {
        this(Collections.emptyMap(), null);
    }

    public LiteLoaderVersionsRoot(Map<String, LiteLoaderGameVersions> versions, LiteLoaderVersionsMeta meta) {
        this.versions = versions;
        this.meta = meta;
    }

    public Map<String, LiteLoaderGameVersions> getVersions() {
        return Collections.unmodifiableMap(this.versions);
    }

    public LiteLoaderVersionsMeta getMeta() {
        return this.meta;
    }
}
