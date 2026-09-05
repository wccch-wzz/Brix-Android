package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanObjectPropertyBuilder<T> {
    private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();

    private JavaBeanObjectPropertyBuilder() {
    }

    public static JavaBeanObjectPropertyBuilder create() {
        return new JavaBeanObjectPropertyBuilder();
    }

    public JavaBeanObjectProperty<T> build() throws NoSuchMethodException {
        PropertyDescriptor descriptor = this.helper.getDescriptor();
        return new JavaBeanObjectProperty<>(descriptor, this.helper.getBean());
    }

    public JavaBeanObjectPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public JavaBeanObjectPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public JavaBeanObjectPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public JavaBeanObjectPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public JavaBeanObjectPropertyBuilder setter(String setter) {
        this.helper.setterName(setter);
        return this;
    }

    public JavaBeanObjectPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }

    public JavaBeanObjectPropertyBuilder setter(Method setter) {
        this.helper.setter(setter);
        return this;
    }
}
