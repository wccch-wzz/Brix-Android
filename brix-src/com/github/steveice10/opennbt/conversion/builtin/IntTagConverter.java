package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.IntTag;

/* JADX INFO: loaded from: classes.dex */
public class IntTagConverter implements TagConverter<IntTag, Integer> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public Integer convert(IntTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public IntTag convert(String name, Integer value) {
        return new IntTag(name, value.intValue());
    }
}
