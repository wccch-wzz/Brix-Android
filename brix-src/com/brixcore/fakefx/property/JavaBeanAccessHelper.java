package com.brixcore.fakefx.property;

import com.brixcore.fakefx.beans.property.ReadOnlyObjectProperty;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanAccessHelper {
    private static Method JAVA_BEAN_QUICK_ACCESSOR_CREATE_RO;
    private static boolean initialized;

    private JavaBeanAccessHelper() {
    }

    public static <T> ReadOnlyObjectProperty<T> createReadOnlyJavaBeanProperty(Object bean, String propertyName) throws NoSuchMethodException {
        init();
        if (JAVA_BEAN_QUICK_ACCESSOR_CREATE_RO == null) {
            throw new UnsupportedOperationException("Java beans are not supported.");
        }
        try {
            return (ReadOnlyObjectProperty) JAVA_BEAN_QUICK_ACCESSOR_CREATE_RO.invoke(null, bean, propertyName);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Java beans are not supported.");
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof NoSuchMethodException) {
                throw ((NoSuchMethodException) ex.getCause());
            }
            throw new UnsupportedOperationException("Java beans are not supported.");
        }
    }

    private static void init() {
        if (!initialized) {
            try {
                JAVA_BEAN_QUICK_ACCESSOR_CREATE_RO = Class.forName("com.sun.javafx.property.adapter.JavaBeanQuickAccessor", true, JavaBeanAccessHelper.class.getClassLoader()).getDeclaredMethod("createReadOnlyJavaBeanObjectProperty", Object.class, String.class);
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e2) {
            }
            initialized = true;
        }
    }
}
