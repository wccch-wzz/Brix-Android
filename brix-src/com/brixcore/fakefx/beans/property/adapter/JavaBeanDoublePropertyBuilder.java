package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanDoublePropertyBuilder {
    private final JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();

    private JavaBeanDoublePropertyBuilder() {
    }

    public static JavaBeanDoublePropertyBuilder create() {
        return new JavaBeanDoublePropertyBuilder();
    }

    public JavaBeanDoubleProperty build() throws NoSuchMethodException {
        PropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!Double.TYPE.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
            throw new IllegalArgumentException("Not a double property");
        }
        return new JavaBeanDoubleProperty(descriptor, this.helper.getBean());
    }

    public JavaBeanDoublePropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public JavaBeanDoublePropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public JavaBeanDoublePropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public JavaBeanDoublePropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public JavaBeanDoublePropertyBuilder setter(String setter) {
        this.helper.setterName(setter);
        return this;
    }

    public JavaBeanDoublePropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }

    public JavaBeanDoublePropertyBuilder setter(Method setter) {
        this.helper.setter(setter);
        return this;
    }
}
