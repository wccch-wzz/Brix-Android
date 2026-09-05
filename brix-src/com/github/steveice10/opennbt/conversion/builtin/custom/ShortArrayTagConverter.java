package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.custom.ShortArrayTag;

/* JADX INFO: loaded from: classes.dex */
public class ShortArrayTagConverter implements TagConverter<ShortArrayTag, short[]> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public short[] convert(ShortArrayTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public ShortArrayTag convert(String name, short[] value) {
        return new ShortArrayTag(name, value);
    }
}
