package com.brixcore.util.gson.fakefx.properties.primitives;

import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.property.SimpleBooleanProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes16.dex */
public class BooleanPropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Boolean, BooleanProperty> {
    public BooleanPropertyTypeAdapter(TypeAdapter<Boolean> delegate, boolean throwOnNullProperty, boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public Boolean extractPrimitiveValue(BooleanProperty property) {
        return Boolean.valueOf(property.get());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public BooleanProperty createDefaultProperty() {
        return new SimpleBooleanProperty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public BooleanProperty wrapNonNullPrimitiveValue(Boolean deserializedValue) {
        return new SimpleBooleanProperty(deserializedValue.booleanValue());
    }
}
