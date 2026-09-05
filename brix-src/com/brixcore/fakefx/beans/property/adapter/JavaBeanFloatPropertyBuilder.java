package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanFloatPropertyBuilder {
    private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();

    private JavaBeanFloatPropertyBuilder() {
    }

    public static JavaBeanFloatPropertyBuilder create() {
        return new JavaBeanFloatPropertyBuilder();
    }

    public JavaBeanFloatProperty build() throws NoSuchMethodException {
        PropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!Float.TYPE.equals(descriptor.getType()) && !Number.class.isAssignableFrom(descriptor.getType())) {
            throw new IllegalArgumentException("Not a float property");
        }
        return new JavaBeanFloatProperty(descriptor, this.helper.getBean());
    }

    public JavaBeanFloatPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public JavaBeanFloatPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public JavaBeanFloatPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public JavaBeanFloatPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public JavaBeanFloatPropertyBuilder setter(String setter) {
        this.helper.setterName(setter);
        return this;
    }

    public JavaBeanFloatPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }

    public JavaBeanFloatPropertyBuilder setter(Method setter) {
        this.helper.setter(setter);
        return this;
    }
}
