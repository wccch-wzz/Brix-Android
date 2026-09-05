package com.github.steveice10.opennbt.conversion;

import com.github.steveice10.opennbt.tag.builtin.Tag;

/* JADX INFO: loaded from: classes.dex */
public interface TagConverter<T extends Tag, V> {
    T convert(String str, V v);

    V convert(T t);
}
