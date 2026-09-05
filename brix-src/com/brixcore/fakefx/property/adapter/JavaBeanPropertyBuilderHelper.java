package com.brixcore.fakefx.property.adapter;

import com.brixcore.fakefx.reflect.ReflectUtil;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes7.dex */
public class JavaBeanPropertyBuilderHelper {
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";
    private static final String SET_PREFIX = "set";
    private Object bean;
    private Class<?> beanClass;
    private PropertyDescriptor descriptor;
    private Method getter;
    private String getterName;
    private String propertyName;
    private Method setter;
    private String setterName;

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
                this.beanClass = newClass;
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

    public void setterName(String setterName) {
        String str = this.setterName;
        if (setterName == null) {
            if (str == null) {
                return;
            }
        } else if (setterName.equals(str)) {
            return;
        }
        this.setterName = setterName;
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

    public void setter(Method setter) {
        Method method = this.setter;
        if (setter == null) {
            if (method == null) {
                return;
            }
        } else if (setter.equals(method)) {
            return;
        }
        this.setter = setter;
        this.descriptor = null;
    }

    public PropertyDescriptor getDescriptor() throws NoSuchMethodException {
        if (this.descriptor == null) {
            if (this.propertyName == null) {
                throw new NullPointerException("Property name has to be specified");
            }
            if (this.propertyName.isEmpty()) {
                throw new IllegalArgumentException("Property name cannot be empty");
            }
            String capitalizedName = ReadOnlyPropertyDescriptor.capitalizedName(this.propertyName);
            Method getterMethod = this.getter;
            if (getterMethod == null) {
                if (this.getterName == null || this.getterName.isEmpty()) {
                    try {
                        getterMethod = this.beanClass.getMethod(IS_PREFIX + capitalizedName, new Class[0]);
                    } catch (NoSuchMethodException e) {
                        getterMethod = this.beanClass.getMethod(GET_PREFIX + capitalizedName, new Class[0]);
                    }
                } else {
                    getterMethod = this.beanClass.getMethod(this.getterName, new Class[0]);
                }
            }
            Method setterMethod = this.setter;
            if (setterMethod == null) {
                Class<?> type = getterMethod.getReturnType();
                if (this.setterName != null && !this.setterName.isEmpty()) {
                    setterMethod = this.beanClass.getMethod(this.setterName, type);
                } else {
                    setterMethod = this.beanClass.getMethod(SET_PREFIX + capitalizedName, type);
                }
            }
            this.descriptor = new PropertyDescriptor(this.propertyName, this.beanClass, getterMethod, setterMethod);
        }
        return this.descriptor;
    }
}
