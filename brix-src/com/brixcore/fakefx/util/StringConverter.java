package com.brixcore.fakefx.util;

/* JADX INFO: loaded from: classes2.dex */
public abstract class StringConverter<T> {
    public abstract T fromString(String str);

    public abstract String toString(T t);
}
