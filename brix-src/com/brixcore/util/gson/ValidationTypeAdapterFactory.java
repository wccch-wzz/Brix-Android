package com.brixcore.util.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes8.dex */
public final class ValidationTypeAdapterFactory implements TypeAdapterFactory {
    public static final ValidationTypeAdapterFactory INSTANCE = new ValidationTypeAdapterFactory();

    @Override // com.google.gson.TypeAdapterFactory
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> tt) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, tt);
        return new TypeAdapter<T>() { // from class: com.brixcore.util.gson.ValidationTypeAdapterFactory.1
            @Override // com.google.gson.TypeAdapter
            public void write(JsonWriter writer, T t) throws IOException {
                if (t instanceof Validation) {
                    try {
                        ((Validation) t).validate();
                    } catch (TolerableValidationException e) {
                        delegate.write(writer, null);
                        return;
                    }
                }
                delegate.write(writer, t);
            }

            @Override // com.google.gson.TypeAdapter
            public T read(JsonReader jsonReader) throws IOException {
                T t = (T) delegate.read(jsonReader);
                if (t instanceof Validation) {
                    try {
                        ((Validation) t).validate();
                    } catch (TolerableValidationException e) {
                        return null;
                    }
                }
                return t;
            }
        };
    }
}
