package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;

/* JADX INFO: loaded from: classes5.dex */
public class DefaultStringConverter extends StringConverter<String> {
    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(String value) {
        return value != null ? value : "";
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String fromString(String value) {
        return value;
    }
}
