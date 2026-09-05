package com.brixcore.util.gson.fakefx.properties;

import com.brixcore.fakefx.beans.property.SimpleStringProperty;
import com.brixcore.fakefx.beans.property.StringProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes9.dex */
public class StringPropertyTypeAdapter extends PropertyTypeAdapter<String, StringProperty> {
    public StringPropertyTypeAdapter(TypeAdapter<String> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.PropertyTypeAdapter
    public StringProperty createProperty(String deserializedValue) {
        return new SimpleStringProperty(deserializedValue);
    }
}
