package com.brixcore.fakefx.property.adapter;

import com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;

/* JADX INFO: loaded from: classes7.dex */
public final class JavaBeanQuickAccessor {
    private JavaBeanQuickAccessor() {
    }

    public static <T> ReadOnlyJavaBeanObjectProperty<T> createReadOnlyJavaBeanObjectProperty(Object bean, String name) throws NoSuchMethodException {
        return ReadOnlyJavaBeanObjectPropertyBuilder.create().bean(bean).name(name).build();
    }
}
