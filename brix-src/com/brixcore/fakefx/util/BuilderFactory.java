package com.brixcore.fakefx.util;

/* JADX INFO: loaded from: classes2.dex */
@FunctionalInterface
public interface BuilderFactory {
    Builder<?> getBuilder(Class<?> cls);
}
