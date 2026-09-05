package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.LongTag;

/* JADX INFO: loaded from: classes.dex */
public class LongTagConverter implements TagConverter<LongTag, Long> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public Long convert(LongTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public LongTag convert(String name, Long value) {
        return new LongTag(name, value.longValue());
    }
}
