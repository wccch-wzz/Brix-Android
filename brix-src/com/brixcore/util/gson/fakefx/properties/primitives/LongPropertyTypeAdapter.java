package com.brixcore.util.gson.fakefx.properties.primitives;

import com.brixcore.fakefx.beans.property.LongProperty;
import com.brixcore.fakefx.beans.property.SimpleLongProperty;
import com.google.gson.TypeAdapter;

/* JADX INFO: loaded from: classes16.dex */
public class LongPropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Long, LongProperty> {
    public LongPropertyTypeAdapter(TypeAdapter<Long> delegate, boolean throwOnNullProperty, boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public Long extractPrimitiveValue(LongProperty property) {
        return Long.valueOf(property.get());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public LongProperty createDefaultProperty() {
        return new SimpleLongProperty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.brixcore.util.gson.fakefx.properties.primitives.PrimitivePropertyTypeAdapter
    public LongProperty wrapNonNullPrimitiveValue(Long deserializedValue) {
        return new SimpleLongProperty(deserializedValue.longValue());
    }
}
