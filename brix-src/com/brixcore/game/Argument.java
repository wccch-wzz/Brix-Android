package com.brixcore.game;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
@JsonAdapter(Deserializer.class)
public interface Argument extends Cloneable {
    List<String> toString(Map<String, String> map, Map<String, Boolean> map2);

    public static class Deserializer implements JsonDeserializer<Argument> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.JsonDeserializer
        public Argument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return new StringArgument(json.getAsString());
            }
            return (Argument) context.deserialize(json, RuledArgument.class);
        }
    }
}
