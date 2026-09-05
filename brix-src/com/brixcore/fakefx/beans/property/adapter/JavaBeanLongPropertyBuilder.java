package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanLongPropertyBuilder {
    private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();

    private JavaBeanLongPropertyBuilder() {
    }

    public static JavaBeanLongPropertyBuilder create() {
        return new JavaBeanLongPropertyBuilder();
    }

    public JavaBeanLongProperty build() throws NoSuchMethodException {
        PropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!Long.TYPE.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
            throw new IllegalArgumentException("Not a long property");
        }
        return new JavaBeanLongProperty(descriptor, this.helper.getBean());
    }

    public JavaBeanLongPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public JavaBeanLongPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public JavaBeanLongPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public JavaBeanLongPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public JavaBeanLongPropertyBuilder setter(String setter) {
        this.helper.setterName(setter);
        return this;
    }

    public JavaBeanLongPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }

    public JavaBeanLongPropertyBuilder setter(Method setter) {
        this.helper.setter(setter);
        return this;
    }
}
