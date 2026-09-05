package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;

/* JADX INFO: loaded from: classes.dex */
public class DoubleArrayTagConverter implements TagConverter<DoubleArrayTag, double[]> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public double[] convert(DoubleArrayTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public DoubleArrayTag convert(String name, double[] value) {
        return new DoubleArrayTag(name, value);
    }
}
