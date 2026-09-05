package com.brixcore.util.gson.fakefx.properties.primitives;

import com.brixcore.fakefx.beans.property.FloatProperty;
import com.brixcore.fakefx.beans.property.SimpleFloatProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes16.dex */
public class FloatPropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Float, FloatProperty> {
    public FloatPropertyTypeAdapter(TypeAdapter<Float> delegate, boolean throwOnNullProperty, boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public Float extractPrimitiveValue(FloatProperty property) {
        return Float.valueOf(property.get());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public FloatProperty createDefaultProperty() {
        return new SimpleFloatProperty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public FloatProperty wrapNonNullPrimitiveValue(Float deserializedValue) {
        return new SimpleFloatProperty(deserializedValue.floatValue());
    }
}
