package com.brixcore.fakefx.reflect;

import java.lang.reflect.Constructor;

/* JADX INFO: loaded from: classes2.dex */
public final class ConstructorUtil {
    private ConstructorUtil() {
    }

    public static Constructor<?> getConstructor(Class<?> cls, Class<?>[] params) throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getConstructor(params);
    }

    public static Constructor<?>[] getConstructors(Class<?> cls) {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getConstructors();
    }
}
