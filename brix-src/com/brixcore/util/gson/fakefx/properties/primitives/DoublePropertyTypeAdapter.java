package com.brixcore.util.gson.fakefx.properties.primitives;

import com.brixcore.fakefx.beans.property.DoubleProperty;
import com.brixcore.fakefx.beans.property.SimpleDoubleProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes16.dex */
public class DoublePropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Double, DoubleProperty> {
    public DoublePropertyTypeAdapter(TypeAdapter<Double> delegate, boolean throwOnNullProperty, boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public Double extractPrimitiveValue(DoubleProperty property) {
        return Double.valueOf(property.get());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public DoubleProperty createDefaultProperty() {
        return new SimpleDoubleProperty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public DoubleProperty wrapNonNullPrimitiveValue(Double deserializedValue) {
        return new SimpleDoubleProperty(deserializedValue.doubleValue());
    }
}
