package com.brixcore.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes8.dex */
public final class JsonUtils {
    public static final Gson GSON = defaultGsonBuilder().create();
    public static final Gson GSON_SIMPLE = new GsonBuilder().setPrettyPrinting().create();
    public static final Gson UGLY_GSON = new GsonBuilder().registerTypeAdapterFactory(JsonTypeAdapterFactory.INSTANCE).registerTypeAdapterFactory(ValidationTypeAdapterFactory.INSTANCE).registerTypeAdapterFactory(LowerCaseEnumTypeAdapterFactory.INSTANCE).create();

    private JsonUtils() {
    }

    public static <T> TypeToken<List<T>> listTypeOf(Class<T> cls) {
        return (TypeToken<List<T>>) TypeToken.getParameterized(List.class, cls);
    }

    public static <T> TypeToken<List<T>> listTypeOf(TypeToken<T> typeToken) {
        return (TypeToken<List<T>>) TypeToken.getParameterized(List.class, typeToken.getType());
    }

    public static <K, V> TypeToken<Map<K, V>> mapTypeOf(Class<K> cls, Class<V> cls2) {
        return (TypeToken<Map<K, V>>) TypeToken.getParameterized(Map.class, cls, cls2);
    }

    public static <K, V> TypeToken<Map<K, V>> mapTypeOf(Class<K> cls, TypeToken<V> typeToken) {
        return (TypeToken<Map<K, V>>) TypeToken.getParameterized(Map.class, cls, typeToken.getType());
    }

    public static <T> T fromJsonFully(InputStream inputStream, Class<T> cls) throws JsonParseException, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try {
            T t = (T) GSON.fromJson((Reader) inputStreamReader, (Class) cls);
            inputStreamReader.close();
            return t;
        } catch (Throwable th) {
            try {
                inputStreamReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T> T fromJsonFully(InputStream inputStream, Type type) throws JsonParseException, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try {
            T t = (T) GSON.fromJson(inputStreamReader, type);
            inputStreamReader.close();
            return t;
        } catch (Throwable th) {
            try {
                inputStreamReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T> T fromNonNullJson(String str, Class<T> cls) throws JsonParseException {
        T t = (T) GSON.fromJson(str, (Class) cls);
        if (t == null) {
            throw new JsonParseException("Json object cannot be null.");
        }
        return t;
    }

    public static <T> T fromNonNullJson(String str, TypeToken<T> typeToken) throws JsonParseException {
        T t = (T) GSON.fromJson(str, typeToken);
        if (t == null) {
            throw new JsonParseException("Json object cannot be null.");
        }
        return t;
    }

    public static <T> T fromNonNullJson(String str, Type type) throws JsonParseException {
        T t = (T) GSON.fromJson(str, type);
        if (t == null) {
            throw new JsonParseException("Json object cannot be null.");
        }
        return t;
    }

    public static <T> T fromNonNullJsonFully(InputStream inputStream, Class<T> cls) throws JsonParseException, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try {
            T t = (T) GSON.fromJson((Reader) inputStreamReader, (Class) cls);
            if (t == null) {
                throw new JsonParseException("Json object cannot be null.");
            }
            inputStreamReader.close();
            return t;
        } catch (Throwable th) {
            try {
                inputStreamReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T> T fromNonNullJsonFully(InputStream inputStream, Type type) throws JsonParseException, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try {
            T t = (T) GSON.fromJson(inputStreamReader, type);
            if (t == null) {
                throw new JsonParseException("Json object cannot be null.");
            }
            inputStreamReader.close();
            return t;
        } catch (Throwable th) {
            try {
                inputStreamReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T> T fromMaybeMalformedJson(String str, Class<T> cls) throws JsonParseException {
        try {
            return (T) GSON.fromJson(str, (Class) cls);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static <T> T fromMaybeMalformedJson(String str, Type type) throws JsonParseException {
        try {
            return (T) GSON.fromJson(str, type);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static <T> T fromJsonFile(Path path, Class<T> cls) throws IOException {
        return (T) fromJsonFile(path, TypeToken.get((Class) cls));
    }

    public static <T> T fromJsonFile(Path path, TypeToken<T> typeToken) throws IOException {
        BufferedReader bufferedReaderNewBufferedReader = Files.newBufferedReader(path);
        try {
            T t = (T) GSON.fromJson(bufferedReaderNewBufferedReader, typeToken.getType());
            if (bufferedReaderNewBufferedReader != null) {
                bufferedReaderNewBufferedReader.close();
            }
            return t;
        } catch (Throwable th) {
            if (bufferedReaderNewBufferedReader != null) {
                try {
                    bufferedReaderNewBufferedReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static GsonBuilder defaultGsonBuilder() {
        return new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().registerTypeAdapter(Instant.class, InstantTypeAdapter.INSTANCE).registerTypeAdapter(UUID.class, UUIDTypeAdapter.INSTANCE).registerTypeAdapter(File.class, FileTypeAdapter.INSTANCE).registerTypeAdapterFactory(ValidationTypeAdapterFactory.INSTANCE).registerTypeAdapterFactory(LowerCaseEnumTypeAdapterFactory.INSTANCE).registerTypeAdapterFactory(JsonTypeAdapterFactory.INSTANCE);
    }
}
