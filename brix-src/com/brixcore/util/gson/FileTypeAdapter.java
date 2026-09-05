package com.brixcore.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.File;
import java.lang.reflect.Type;

/* JADX INFO: loaded from: classes8.dex */
public final class FileTypeAdapter implements JsonSerializer<File>, JsonDeserializer<File> {
    public static final FileTypeAdapter INSTANCE = new FileTypeAdapter();

    private FileTypeAdapter() {
    }

    @Override // com.google.gson.JsonSerializer
    public JsonElement serialize(File t, Type type, JsonSerializationContext jsc) {
        if (t == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(t.getPath());
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.gson.JsonDeserializer
    public File deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        if (je == null) {
            return null;
        }
        return new File(je.getAsString());
    }
}
