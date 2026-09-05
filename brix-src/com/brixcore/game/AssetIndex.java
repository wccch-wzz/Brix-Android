package com.brixcore.game;

import com.brixcore.util.ToStringBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public final class AssetIndex {

    @SerializedName("map_to_resources")
    private final boolean mapToResources;

    @SerializedName("objects")
    private final Map<String, AssetObject> objects;

    @SerializedName("virtual")
    private final boolean virtual;

    public AssetIndex() {
        this(false, Collections.emptyMap());
    }

    public AssetIndex(boolean virtual, Map<String, AssetObject> objects) {
        this.mapToResources = virtual;
        this.virtual = virtual;
        this.objects = new HashMap(objects);
    }

    public boolean isVirtual() {
        return this.virtual || this.mapToResources;
    }

    public boolean needMapToResources() {
        return this.mapToResources;
    }

    public Map<String, AssetObject> getObjects() {
        return Collections.unmodifiableMap(this.objects);
    }

    public String toString() {
        return new ToStringBuilder(this).append("virtual", Boolean.valueOf(this.virtual)).append("objects", this.objects).toString();
    }
}
