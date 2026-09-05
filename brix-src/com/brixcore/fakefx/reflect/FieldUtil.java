package com.brixcore.fakefx.reflect;

import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes2.dex */
public final class FieldUtil {
    private FieldUtil() {
    }

    public static Field getField(Class<?> cls, String name) throws NoSuchFieldException {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getField(name);
    }
}
