package com.brixcore.util.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes8.dex */
public final class JsonTypeAdapterFactory implements TypeAdapterFactory {
    public static final JsonTypeAdapterFactory INSTANCE = new JsonTypeAdapterFactory();

    private <T> TypeAdapter<T> createForJsonType(Gson gson, final TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        final JsonType jsonType = (JsonType) rawType.getDeclaredAnnotation(JsonType.class);
        if (jsonType == null) {
            return null;
        }
        JsonSubtype[] subtypes = jsonType.subtypes();
        final Map<String, TypeAdapter<?>> labelTypeAdapterMap = new HashMap<>();
        final Map<Class<?>, TypeAdapter<?>> classTypeAdapterMap = new HashMap<>();
        final Map<Class<?>, JsonSubtype> classJsonSubtypeMap = new HashMap<>();
        for (JsonSubtype subtype : subtypes) {
            TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, TypeToken.get((Class) subtype.clazz()));
            labelTypeAdapterMap.put(subtype.name(), delegateAdapter);
            classTypeAdapterMap.put(subtype.clazz(), delegateAdapter);
            classJsonSubtypeMap.put(subtype.clazz(), subtype);
        }
        return new TypeAdapter<T>() { // from class: com.brixcore.util.gson.JsonTypeAdapterFactory.1
            @Override // com.google.gson.TypeAdapter
            public void write(JsonWriter out, T value) throws IOException {
                Class<?> type2 = value.getClass();
                TypeAdapter<T> delegate = (TypeAdapter) classTypeAdapterMap.get(type2);
                if (delegate == null) {
                    throw new JsonParseException("Cannot serialize " + type2.getName() + ". Please check your @JsonType configuration");
                }
                JsonSubtype subtype2 = (JsonSubtype) classJsonSubtypeMap.get(type2);
                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                if (jsonObject.has(jsonType.property())) {
                    throw new JsonParseException("Cannot serialize " + type2.getName() + ". Because it has already defined a field named '" + jsonType.property() + "'");
                }
                jsonObject.add(jsonType.property(), new JsonPrimitive(subtype2.name()));
                Streams.write(jsonObject, out);
            }

            @Override // com.google.gson.TypeAdapter
            public T read(JsonReader in) {
                JsonElement jsonElement = Streams.parse(in);
                JsonElement typeLabelElement = jsonElement.getAsJsonObject().get(jsonType.property());
                if (typeLabelElement == null) {
                    throw new JsonParseException("Cannot deserialize " + type + ". Because it does not define a field named '" + jsonType.property() + "'");
                }
                String typeLabel = typeLabelElement.getAsString();
                TypeAdapter<T> delegate = (TypeAdapter) labelTypeAdapterMap.get(typeLabel);
                if (delegate == null) {
                    throw new JsonParseException("Cannot deserialize " + type + " with subtype '" + typeLabel + "'");
                }
                return delegate.fromJsonTree(jsonElement);
            }
        };
    }

    private <T> TypeAdapter<T> createForJsonSubtype(Gson gson, TypeToken<T> type) {
        final JsonType jsonType;
        Class<? super T> rawType = type.getRawType();
        if (rawType.getSuperclass() == null || (jsonType = (JsonType) rawType.getSuperclass().getDeclaredAnnotation(JsonType.class)) == null) {
            return null;
        }
        JsonSubtype jsonSubtype = null;
        for (JsonSubtype subtype : jsonType.subtypes()) {
            if (subtype.clazz() == rawType) {
                jsonSubtype = subtype;
            }
        }
        if (jsonSubtype == null) {
            return null;
        }
        final JsonSubtype subtype2 = jsonSubtype;
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() { // from class: com.brixcore.util.gson.JsonTypeAdapterFactory.2
            @Override // com.google.gson.TypeAdapter
            public void write(JsonWriter out, T value) throws IOException {
                Class<?> type2 = value.getClass();
                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                if (jsonObject.has(jsonType.property())) {
                    throw new JsonParseException("Cannot serialize " + type2.getName() + ". Because it has already defined a field named '" + jsonType.property() + "'");
                }
                jsonObject.add(jsonType.property(), new JsonPrimitive(subtype2.name()));
                Streams.write(jsonObject, out);
            }

            @Override // com.google.gson.TypeAdapter
            public T read(JsonReader jsonReader) throws IOException {
                return (T) delegate.read(jsonReader);
            }
        };
    }

    @Override // com.google.gson.TypeAdapterFactory
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> typeAdapter = createForJsonType(gson, type);
        if (typeAdapter == null) {
            return createForJsonSubtype(gson, type);
        }
        return typeAdapter;
    }
}
