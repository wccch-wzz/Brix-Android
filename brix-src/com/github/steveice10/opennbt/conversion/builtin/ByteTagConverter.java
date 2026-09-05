package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;

/* JADX INFO: loaded from: classes.dex */
public class ByteTagConverter implements TagConverter<ByteTag, Byte> {
    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public Byte convert(ByteTag tag) {
        return tag.getValue();
    }

    @Override // com.github.steveice10.opennbt.conversion.TagConverter
    public ByteTag convert(String name, Byte value) {
        return new ByteTag(name, value.byteValue());
    }
}
