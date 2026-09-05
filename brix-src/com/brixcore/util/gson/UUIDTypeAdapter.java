package com.brixcore.util.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes8.dex */
public final class UUIDTypeAdapter extends TypeAdapter<UUID> {
    public static final UUIDTypeAdapter INSTANCE = new UUIDTypeAdapter();
    private static final Pattern regex = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    @Override // com.google.gson.TypeAdapter
    public void write(JsonWriter writer, UUID value) throws IOException {
        writer.value(value == null ? null : fromUUID(value));
    }

    @Override // com.google.gson.TypeAdapter
    public UUID read(JsonReader reader) throws IOException {
        try {
            return fromString(reader.nextString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("UUID malformed");
        }
    }

    public static String fromUUID(UUID value) {
        return value.toString().replace("-", "");
    }

    public static UUID fromString(String input) {
        return UUID.fromString(regex.matcher(input).replaceFirst("$1-$2-$3-$4-$5"));
    }
}
