package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes15.dex */
public final class ReadOnlyJavaBeanStringPropertyBuilder {
    private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();

    private ReadOnlyJavaBeanStringPropertyBuilder() {
    }

    public static ReadOnlyJavaBeanStringPropertyBuilder create() {
        return new ReadOnlyJavaBeanStringPropertyBuilder();
    }

    public ReadOnlyJavaBeanStringProperty build() throws NoSuchMethodException {
        ReadOnlyPropertyDescriptor descriptor = this.helper.getDescriptor();
        if (!String.class.equals(descriptor.getType())) {
            throw new IllegalArgumentException("Not a String property");
        }
        return new ReadOnlyJavaBeanStringProperty(descriptor, this.helper.getBean());
    }

    public ReadOnlyJavaBeanStringPropertyBuilder name(String name) {
        this.helper.name(name);
        return this;
    }

    public ReadOnlyJavaBeanStringPropertyBuilder bean(Object bean) {
        this.helper.bean(bean);
        return this;
    }

    public ReadOnlyJavaBeanStringPropertyBuilder beanClass(Class<?> beanClass) {
        this.helper.beanClass(beanClass);
        return this;
    }

    public ReadOnlyJavaBeanStringPropertyBuilder getter(String getter) {
        this.helper.getterName(getter);
        return this;
    }

    public ReadOnlyJavaBeanStringPropertyBuilder getter(Method getter) {
        this.helper.getter(getter);
        return this;
    }
}
