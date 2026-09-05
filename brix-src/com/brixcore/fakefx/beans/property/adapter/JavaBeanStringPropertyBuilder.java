package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.JavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanStringPropertyBuilder {
    private JavaBeanPropertyBuilderHelper helper = new JavaBeanPropertyBuilderHelper();

    private JavaBeanStringPropertyBuilder() {
    }

    public static JavaBeanStringPropertyBuilder create() {
        return new JavaBeanStringPropertyBuilder();
    }

    public JavaBeanStringProperty build() throws NoSuchMethodException {
        PropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!String.class.equals(descriptor.getType())) {
            throw new IllegalArgumentException("Not a String property");
        }
        return new JavaBeanStringProperty(descriptor, this.helper.getBean());
    }

    public JavaBeanStringPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public JavaBeanStringPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public JavaBeanStringPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public JavaBeanStringPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public JavaBeanStringPropertyBuilder setter(String setter) {
        this.helper.setterName(setter);
        return this;
    }

    public JavaBeanStringPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }

    public JavaBeanStringPropertyBuilder setter(Method setter) {
        this.helper.setter(setter);
        return this;
    }
}
