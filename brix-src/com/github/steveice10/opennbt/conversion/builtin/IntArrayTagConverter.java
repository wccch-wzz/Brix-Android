package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;

/* JADX INFO: loaded from: classes.dex */
public class IntArrayTagConverter implements TagConverter<IntArrayTag, int[]> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public int[] convert(IntArrayTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public IntArrayTag convert(String name, int[] value) {
        return new IntArrayTag(name, value);
    }
}
