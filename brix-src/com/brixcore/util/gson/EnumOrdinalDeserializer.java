package com.brixcore.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.lang.Enum;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes8.dex */
public final class EnumOrdinalDeserializer<T extends Enum<T>> implements JsonDeserializer<T> {
    private final Map<String, T> mapping = new HashMap();

    public EnumOrdinalDeserializer(Class<T> enumClass) {
        for (T constant : enumClass.getEnumConstants()) {
            this.mapping.put(String.valueOf(constant.ordinal()), constant);
            String name = constant.name();
            try {
                SerializedName annotation = (SerializedName) enumClass.getField(name).getAnnotation(SerializedName.class);
                if (annotation != null) {
                    name = annotation.value();
                    for (String alternate : annotation.alternate()) {
                        this.mapping.put(alternate, constant);
                    }
                }
                this.mapping.put(name, constant);
            } catch (NoSuchFieldException e) {
                throw new AssertionError(e);
            }
        }
    }

    @Override // com.google.gson.JsonDeserializer
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return this.mapping.get(json.getAsString());
    }
}
