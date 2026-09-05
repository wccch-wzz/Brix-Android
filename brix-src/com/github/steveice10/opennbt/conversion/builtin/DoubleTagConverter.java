package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;

/* JADX INFO: loaded from: classes.dex */
public class DoubleTagConverter implements TagConverter<DoubleTag, Double> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public Double convert(DoubleTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public DoubleTag convert(String name, Double value) {
        return new DoubleTag(name, value.doubleValue());
    }
}
