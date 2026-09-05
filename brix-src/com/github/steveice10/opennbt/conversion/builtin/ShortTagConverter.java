package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;

/* JADX INFO: loaded from: classes.dex */
public class ShortTagConverter implements TagConverter<ShortTag, Short> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public Short convert(ShortTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public ShortTag convert(String name, Short value) {
        return new ShortTag(name, value.shortValue());
    }
}
