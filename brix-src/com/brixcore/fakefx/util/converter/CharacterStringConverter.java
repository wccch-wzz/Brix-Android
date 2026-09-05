package com.brixcore.fakefx.util.converter;

import com.brixcore.fakefx.util.StringConverter;

/* JADX INFO: loaded from: classes5.dex */
public class CharacterStringConverter extends StringConverter<Character> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.util.StringConverter
    public Character fromString(String value) {
        if (value == null) {
            return null;
        }
        String value2 = value.trim();
        if (value2.length() < 1) {
            return null;
        }
        return Character.valueOf(value2.charAt(0));
    }

    @Override // com.brixcore.fakefx.util.StringConverter
    public String toString(Character value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
