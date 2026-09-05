package com.brixcore.util.gson.fakefx.properties;

import com.brixcore.fakefx.beans.property.ListProperty;
import com.brixcore.fakefx.beans.property.SimpleListProperty;
import com.brixcore.fakefx.collections.ObservableList;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes9.dex */
public class ListPropertyTypeAdapter<T> extends PropertyTypeAdapter<ObservableList<T>, ListProperty<T>> {
    public ListPropertyTypeAdapter(TypeAdapter<ObservableList<T>> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.PropertyTypeAdapter
    public ListProperty<T> createProperty(ObservableList<T> deserializedValue) {
        return new SimpleListProperty(deserializedValue);
    }
}
