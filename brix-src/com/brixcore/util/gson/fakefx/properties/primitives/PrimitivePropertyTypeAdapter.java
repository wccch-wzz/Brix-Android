package com.brixcore.util.gson.fakefx.properties.primitives;

import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.util.gson.fakefx.properties.NullPropertyException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes16.dex */
public abstract class PrimitivePropertyTypeAdapter<I, P extends Property<?>> extends TypeAdapter<P> {
    private final boolean crashOnNullValue;
    private final TypeAdapter<I> delegate;
    private final boolean throwOnNullProperty;

    protected abstract P createDefaultProperty();

    protected abstract I extractPrimitiveValue(P p);

    protected abstract P wrapNonNullPrimitiveValue(I i);

    public PrimitivePropertyTypeAdapter(TypeAdapter<I> innerValueTypeAdapter, boolean throwOnNullProperty, boolean crashOnNullValue) {
        this.delegate = innerValueTypeAdapter;
        this.throwOnNullProperty = throwOnNullProperty;
        this.crashOnNullValue = crashOnNullValue;
    }

    @Override // com.google.gson.TypeAdapter
    public void write(JsonWriter out, P property) throws IOException {
        if (property == null) {
            if (this.throwOnNullProperty) {
                throw new NullPropertyException();
            }
            out.nullValue();
            return;
        }
        this.delegate.write(out, extractPrimitiveValue(property));
    }

    @Override // com.google.gson.TypeAdapter
    public P read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            if (this.crashOnNullValue) {
                throw new NullPrimitiveException(jsonReader.getPath());
            }
            return (P) createDefaultProperty();
        }
        return (P) wrapNonNullPrimitiveValue(this.delegate.read(jsonReader));
    }
}
