package com.brixcore.util.gson.fakefx.properties;

import com.brixcore.fakefx.beans.property.Property;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes9.dex */
public abstract class PropertyTypeAdapter<I, P extends Property<? extends I>> extends TypeAdapter<P> {
    private final TypeAdapter<I> delegate;
    private final boolean throwOnNullProperty;

    protected abstract P createProperty(I i);

    PropertyTypeAdapter(TypeAdapter<I> innerValueTypeAdapter, boolean throwOnNullProperty) {
        this.delegate = innerValueTypeAdapter;
        this.throwOnNullProperty = throwOnNullProperty;
    }

    @Override // com.google.gson.TypeAdapter
    public void write(JsonWriter jsonWriter, P p) throws IOException {
        if (p == null) {
            if (this.throwOnNullProperty) {
                throw new NullPropertyException();
            }
            jsonWriter.nullValue();
            return;
        }
        this.delegate.write(jsonWriter, (I) p.getValue2());
    }

    @Override // com.google.gson.TypeAdapter
    public P read(JsonReader jsonReader) throws IOException {
        return (P) createProperty(this.delegate.read(jsonReader));
    }
}
