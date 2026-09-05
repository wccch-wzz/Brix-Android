package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.LongArrayTag;

/* JADX INFO: loaded from: classes.dex */
public class LongArrayTagConverter implements TagConverter<LongArrayTag, long[]> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public long[] convert(LongArrayTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public LongArrayTag convert(String name, long[] value) {
        return new LongArrayTag(name, value);
    }
}
