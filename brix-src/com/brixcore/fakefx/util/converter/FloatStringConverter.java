package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;

/* JADX INFO: loaded from: classes5.dex */
public class FloatStringConverter extends StringConverter<Float> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.util.StringConverter
    public Float fromString(String value) {
        if (value == null) {
            return null;
        }
        String value2 = value.trim();
        if (value2.length() < 1) {
            return null;
        }
        return Float.valueOf(value2);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(Float value) {
        if (value == null) {
            return "";
        }
        return Float.toString(value.floatValue());
    }
}
