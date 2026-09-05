package com.brixcore.util.gson.fakefx.properties.primitives;

import com.brixcore.fakefx.beans.property.IntegerProperty;
import com.brixcore.fakefx.beans.property.SimpleIntegerProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes16.dex */
public class IntegerPropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Integer, IntegerProperty> {
    public IntegerPropertyTypeAdapter(TypeAdapter<Integer> delegate, boolean throwOnNullProperty, boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public Integer extractPrimitiveValue(IntegerProperty property) {
        return property.getValue2();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public IntegerProperty createDefaultProperty() {
        return new SimpleIntegerProperty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public IntegerProperty wrapNonNullPrimitiveValue(Integer deserializedValue) {
        return new SimpleIntegerProperty(deserializedValue.intValue());
    }
}
