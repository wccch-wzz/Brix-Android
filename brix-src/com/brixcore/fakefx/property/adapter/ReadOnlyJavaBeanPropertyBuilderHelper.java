package com.brixcore.fakefx.property.adapter;

import com.brixcore.fakefx.reflect.ReflectUtil;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes7.dex */
public class ReadOnlyJavaBeanPropertyBuilderHelper {
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";
    private Object bean;
    private Class<?> beanClass;
    private ReadOnlyPropertyDescriptor descriptor;
    private Method getter;
    private String getterName;
    private String propertyName;

    public void name(String propertyName) {
        String str = this.propertyName;
        if (propertyName == null) {
            if (str == null) {
                return;
            }
        } else if (propertyName.equals(str)) {
            return;
        }
        this.propertyName = propertyName;
        this.descriptor = null;
    }

    public void beanClass(Class<?> beanClass) {
        Class<?> cls = this.beanClass;
        if (beanClass == null) {
            if (cls == null) {
                return;
            }
        } else if (beanClass.equals(cls)) {
            return;
        }
        ReflectUtil.checkPackageAccess(beanClass);
        this.beanClass = beanClass;
        this.descriptor = null;
    }

    public void bean(Object bean) {
        this.bean = bean;
        if (bean != null) {
            Class<?> newClass = bean.getClass();
            if (this.beanClass == null || !this.beanClass.isAssignableFrom(newClass)) {
                ReflectUtil.checkPackageAccess(newClass);
                this.beanClass = bean.getClass();
                this.descriptor = null;
            }
        }
    }

    public Object getBean() {
        return this.bean;
    }

    public void getterName(String getterName) {
        String str = this.getterName;
        if (getterName == null) {
            if (str == null) {
                return;
            }
        } else if (getterName.equals(str)) {
            return;
        }
        this.getterName = getterName;
        this.descriptor = null;
    }

    public void getter(Method getter) {
        Method method = this.getter;
        if (getter == null) {
            if (method == null) {
                return;
            }
        } else if (getter.equals(method)) {
            return;
        }
        this.getter = getter;
        this.descriptor = null;
    }

    public ReadOnlyPropertyDescriptor getDescriptor() throws NoSuchMethodException {
        if (this.descriptor == null) {
            if (this.propertyName == null || this.bean == null) {
                throw new NullPointerException("Bean and property name have to be specified");
            }
            if (this.propertyName.isEmpty()) {
                throw new IllegalArgumentException("Property name cannot be empty");
            }
            String capitalizedName = ReadOnlyPropertyDescriptor.capitalizedName(this.propertyName);
            if (this.getter == null) {
                if (this.getterName != null && !this.getterName.isEmpty()) {
                    this.getter = this.beanClass.getMethod(this.getterName, new Class[0]);
                } else {
                    try {
                        this.getter = this.beanClass.getMethod(IS_PREFIX + capitalizedName, new Class[0]);
                    } catch (NoSuchMethodException e) {
                        this.getter = this.beanClass.getMethod(GET_PREFIX + capitalizedName, new Class[0]);
                    }
                }
            }
            this.descriptor = new ReadOnlyPropertyDescriptor(this.propertyName, this.beanClass, this.getter);
        }
        return this.descriptor;
    }
}
