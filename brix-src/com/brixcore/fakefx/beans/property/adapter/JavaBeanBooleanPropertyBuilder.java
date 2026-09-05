package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanBooleanPropertyBuilder {
    private final JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();

    private JavaBeanBooleanPropertyBuilder() {
    }

    public static JavaBeanBooleanPropertyBuilder create() {
        return new JavaBeanBooleanPropertyBuilder();
    }

    public JavaBeanBooleanProperty build() throws NoSuchMethodException {
        PropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!Boolean.TYPE.equals(descriptor.getType()) && !Boolean.class.equals(descriptor.getType())) {
            throw new IllegalArgumentException("Not a boolean property");
        }
        return new JavaBeanBooleanProperty(descriptor, this.helper.getBean());
    }

    public JavaBeanBooleanPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public JavaBeanBooleanPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public JavaBeanBooleanPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public JavaBeanBooleanPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public JavaBeanBooleanPropertyBuilder setter(String setter) {
        this.helper.setterName(setter);
        return this;
    }

    public JavaBeanBooleanPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }

    public JavaBeanBooleanPropertyBuilder setter(Method setter) {
        this.helper.setter(setter);
        return this;
    }
}
