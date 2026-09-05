package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class ReadOnlyJavaBeanIntegerPropertyBuilder {
    private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();

    private ReadOnlyJavaBeanIntegerPropertyBuilder() {
    }

    public static ReadOnlyJavaBeanIntegerPropertyBuilder create() {
        return new ReadOnlyJavaBeanIntegerPropertyBuilder();
    }

    public ReadOnlyJavaBeanIntegerProperty build() throws NoSuchMethodException {
        ReadOnlyPropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!Integer.TYPE.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
            throw new IllegalArgumentException("Not an int property");
        }
        return new ReadOnlyJavaBeanIntegerProperty(descriptor, this.helper.getBean());
    }

    public ReadOnlyJavaBeanIntegerPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public ReadOnlyJavaBeanIntegerPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public ReadOnlyJavaBeanIntegerPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public ReadOnlyJavaBeanIntegerPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public ReadOnlyJavaBeanIntegerPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }
}
