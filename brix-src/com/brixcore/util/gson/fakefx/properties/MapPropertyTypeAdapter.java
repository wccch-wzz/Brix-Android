package com.brixcore.util.gson.fakefx.properties;

import com.brixcore.fakefx.beans.property.MapProperty;
import com.brixcore.fakefx.beans.property.SimpleMapProperty;
import com.brixcore.fakefx.collections.ObservableMap;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes9.dex */
public class MapPropertyTypeAdapter<K, V> extends PropertyTypeAdapter<ObservableMap<K, V>, MapProperty<K, V>> {
    public MapPropertyTypeAdapter(TypeAdapter<ObservableMap<K, V>> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.PropertyTypeAdapter
    public MapProperty<K, V> createProperty(ObservableMap<K, V> deserializedValue) {
        return new SimpleMapProperty(deserializedValue);
    }
}
