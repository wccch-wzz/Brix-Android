package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.StringTag;

/* JADX INFO: loaded from: classes.dex */
public class StringTagConverter implements TagConverter<StringTag, String> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public String convert(StringTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public StringTag convert(String name, String value) {
        return new StringTag(name, value);
    }
}
