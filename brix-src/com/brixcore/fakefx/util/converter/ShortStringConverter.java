package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;

/* JADX INFO: loaded from: classes5.dex */
public class ShortStringConverter extends StringConverter<Short> {
    @Override // com.brixcore.fakefx.util.StringConverter
    public Short fromString(String text) {
        if (text == null) {
            return null;
        }
        String text2 = text.trim();
        if (text2.length() < 1) {
            return null;
        }
        return Short.valueOf(text2);
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(Short value) {
        if (value == null) {
            return "";
        }
        return Short.toString(value.shortValue());
    }
}
