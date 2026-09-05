package com.brixcore.util.gson.fakefx.properties;

import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.fakefx.beans.property.SimpleObjectProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes9.dex */
public class ObjectPropertyTypeAdapter<T> extends PropertyTypeAdapter<T, Property<T>> {
    public ObjectPropertyTypeAdapter(TypeAdapter<T> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    @Override // com.brixcore.util.gson.fakefx.properties.PropertyTypeAdapter
    protected Property<T> createProperty(T deserializedValue) {
        return new SimpleObjectProperty(deserializedValue);
    }
}
