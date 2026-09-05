package com.brixcore.util.gson;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

/* JADX INFO: loaded from: classes8.dex */
public abstract class GsonSerializerHelper<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    protected static void add(JsonObject object, String property, JsonElement value) {
        if (value == null) {
            return;
        }
        object.add(property, value);
    }
}
