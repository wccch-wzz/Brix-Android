package com.brixcore.util.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes8.dex */
public final class LowerCaseEnumTypeAdapterFactory implements TypeAdapterFactory {
    public static final LowerCaseEnumTypeAdapterFactory INSTANCE = new LowerCaseEnumTypeAdapterFactory();

    @Override // com.google.gson.TypeAdapterFactory
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> tt) {
        Class<? super T> rawType = tt.getRawType();
        if (!rawType.isEnum()) {
            return null;
        }
        final HashMap map = new HashMap();
        for (Object constant : rawType.getEnumConstants()) {
            map.put(toLowercase(constant), constant);
        }
        return new TypeAdapter<T>() { // from class: com.brixcore.util.gson.LowerCaseEnumTypeAdapterFactory.1
            @Override // com.google.gson.TypeAdapter
            public void write(JsonWriter writer, T t) throws IOException {
                if (t == null) {
                    writer.nullValue();
                } else {
                    writer.value(LowerCaseEnumTypeAdapterFactory.toLowercase(t));
                }
            }

            @Override // com.google.gson.TypeAdapter
            public T read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                return (T) map.get(jsonReader.nextString().toLowerCase(Locale.ROOT));
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String toLowercase(Object o) {
        return o.toString().toLowerCase(Locale.ROOT);
    }
}
