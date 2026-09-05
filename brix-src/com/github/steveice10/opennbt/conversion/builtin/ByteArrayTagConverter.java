package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;

/* JADX INFO: loaded from: classes.dex */
public class ByteArrayTagConverter implements TagConverter<ByteArrayTag, byte[]> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public byte[] convert(ByteArrayTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public ByteArrayTag convert(String name, byte[] value) {
        return new ByteArrayTag(name, value);
    }
}
