package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;

/* JADX INFO: loaded from: classes.dex */
public class FloatTagConverter implements TagConverter<FloatTag, Float> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public Float convert(FloatTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public FloatTag convert(String name, Float value) {
        return new FloatTag(name, value.floatValue());
    }
}
