package com.brixcore.util.gson.fakefx.properties;

import com.brixcore.fakefx.beans.property.SetProperty;
import com.brixcore.fakefx.beans.property.SimpleSetProperty;
import com.brixcore.fakefx.collections.ObservableSet;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes9.dex */
public class SetPropertyTypeAdapter<T> extends PropertyTypeAdapter<ObservableSet<T>, SetProperty<T>> {
    public SetPropertyTypeAdapter(TypeAdapter<ObservableSet<T>> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.PropertyTypeAdapter
    public SetProperty<T> createProperty(ObservableSet<T> deserializedValue) {
        return new SimpleSetProperty(deserializedValue);
    }
}
