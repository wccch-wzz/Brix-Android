package com.brixcore.auth.yggdrasil;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyMapSerializer implements JsonSerializer<Map<String, String>>, JsonDeserializer<Map<String, String>> {
    @Override // com.google.gson.JsonDeserializer
    public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, String> result = new LinkedHashMap<>();
        for (JsonElement element : json.getAsJsonArray()) {
            if (element instanceof JsonObject) {
                JsonObject object = (JsonObject) element;
                result.put(object.get("name").getAsString(), object.get("value").getAsString());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    @Override // com.google.gson.JsonSerializer
    public JsonElement serialize(Map<String, String> src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonArray result = new JsonArray();
        src.forEach(new BiConsumer() { // from class: com.brixcore.auth.yggdrasil.PropertyMapSerializer$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PropertyMapSerializer.lambda$serialize$0(result, (String) obj, (String) obj2);
            }
        });
        return result;
    }

    static /* synthetic */ void lambda$serialize$0(JsonArray result, String k, String v) {
        JsonObject object = new JsonObject();
        object.addProperty("name", k);
        object.addProperty("value", v);
        result.add(object);
    }
}
