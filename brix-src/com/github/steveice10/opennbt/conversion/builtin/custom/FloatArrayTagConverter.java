package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.custom.FloatArrayTag;

/* JADX INFO: loaded from: classes.dex */
public class FloatArrayTagConverter implements TagConverter<FloatArrayTag, float[]> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public float[] convert(FloatArrayTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public FloatArrayTag convert(String name, float[] value) {
        return new FloatArrayTag(name, value);
    }
}
