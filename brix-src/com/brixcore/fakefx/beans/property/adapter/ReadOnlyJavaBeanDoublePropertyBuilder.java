package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class ReadOnlyJavaBeanDoublePropertyBuilder {
    private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();

    private ReadOnlyJavaBeanDoublePropertyBuilder() {
    }

    public static ReadOnlyJavaBeanDoublePropertyBuilder create() {
        return new ReadOnlyJavaBeanDoublePropertyBuilder();
    }

    public ReadOnlyJavaBeanDoubleProperty build() throws NoSuchMethodException {
        ReadOnlyPropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!Double.TYPE.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
            throw new IllegalArgumentException("Not a double property");
        }
        return new ReadOnlyJavaBeanDoubleProperty(descriptor, this.helper.getBean());
    }

    public ReadOnlyJavaBeanDoublePropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public ReadOnlyJavaBeanDoublePropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public ReadOnlyJavaBeanDoublePropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public ReadOnlyJavaBeanDoublePropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public ReadOnlyJavaBeanDoublePropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }
}
