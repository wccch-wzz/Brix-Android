package com.brixcore.fakefx.property;

import com.brixcore.fakefx.beans.property.ReadOnlyProperty;
import com.brixcore.fakefx.reflect.ReflectUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/* JADX INFO: loaded from: classes15.dex */
public final class PropertyReference<T> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Class<?> clazz;
    private Method getter;
    private String name;
    private Method propertyGetter;
    private boolean reflected = false;
    private Method setter;
    private Class<?> type;

    public PropertyReference(Class<?> clazz, String name) {
        if (name == null) {
            throw new NullPointerException("Name must be specified");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("Name must be specified");
        }
        if (clazz == null) {
            throw new NullPointerException("Class must be specified");
        }
        ReflectUtil.checkPackageAccess(clazz);
        this.name = name;
        this.clazz = clazz;
    }

    public boolean isWritable() {
        reflect();
        return this.setter != null;
    }

    public boolean isReadable() {
        reflect();
        return this.getter != null;
    }

    public boolean hasProperty() {
        reflect();
        return this.propertyGetter != null;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getContainingClass() {
        return this.clazz;
    }

    public Class<?> getType() {
        reflect();
        return this.type;
    }

    public void set(Object bean, T value) {
        if (!isWritable()) {
            throw new IllegalStateException("Cannot write to readonly property " + this.name);
        }
        if (this.setter == null) {
            throw new AssertionError();
        }
        try {
            MethodHelper.invoke(this.setter, bean, new Object[]{value});
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public T get(Object obj) {
        if (!isReadable()) {
            throw new IllegalStateException("Cannot read from unreadable property " + this.name);
        }
        if (this.getter == null) {
            throw new AssertionError();
        }
        try {
            return (T) MethodHelper.invoke(this.getter, obj, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ReadOnlyProperty<T> getProperty(Object bean) {
        if (!hasProperty()) {
            throw new IllegalStateException("Cannot get property " + this.name);
        }
        if (this.propertyGetter == null) {
            throw new AssertionError();
        }
        try {
            return (ReadOnlyProperty) MethodHelper.invoke(this.propertyGetter, bean, null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toString() {
        return this.name;
    }

    private void reflect() {
        String properName;
        if (!this.reflected) {
            this.reflected = true;
            try {
                if (this.name.length() == 1) {
                    properName = this.name.substring(0, 1).toUpperCase();
                } else {
                    properName = Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
                }
                this.type = null;
                String getterName = "get" + properName;
                try {
                    Method m = this.clazz.getMethod(getterName, new Class[0]);
                    if (Modifier.isPublic(m.getModifiers())) {
                        this.getter = m;
                    }
                } catch (NoSuchMethodException e) {
                }
                if (this.getter == null) {
                    String getterName2 = "is" + properName;
                    try {
                        Method m2 = this.clazz.getMethod(getterName2, new Class[0]);
                        if (Modifier.isPublic(m2.getModifiers())) {
                            this.getter = m2;
                        }
                    } catch (NoSuchMethodException e2) {
                    }
                }
                String setterName = "set" + properName;
                if (this.getter != null) {
                    this.type = this.getter.getReturnType();
                    try {
                        Method m3 = this.clazz.getMethod(setterName, this.type);
                        if (Modifier.isPublic(m3.getModifiers())) {
                            this.setter = m3;
                        }
                    } catch (NoSuchMethodException e3) {
                    }
                } else {
                    Method[] methods = this.clazz.getMethods();
                    for (Method m4 : methods) {
                        Class<?>[] parameters = m4.getParameterTypes();
                        if (setterName.equals(m4.getName()) && parameters.length == 1 && Modifier.isPublic(m4.getModifiers())) {
                            this.setter = m4;
                            this.type = parameters[0];
                            break;
                        }
                    }
                }
                String propertyGetterName = this.name + "Property";
                try {
                    Method m5 = this.clazz.getMethod(propertyGetterName, new Class[0]);
                    if (Modifier.isPublic(m5.getModifiers())) {
                        this.propertyGetter = m5;
                    } else {
                        this.propertyGetter = null;
                    }
                } catch (NoSuchMethodException e4) {
                }
            } catch (RuntimeException e5) {
                System.err.println("Failed to introspect property " + this.name);
            }
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PropertyReference)) {
            return false;
        }
        PropertyReference<?> other = (PropertyReference) obj;
        if (this.name == other.name || (this.name != null && this.name.equals(other.name))) {
            return this.clazz == other.clazz || (this.clazz != null && this.clazz.equals(other.clazz));
        }
        return false;
    }

    public int hashCode() {
        int hash = (5 * 97) + (this.name != null ? this.name.hashCode() : 0);
        return (hash * 97) + (this.clazz != null ? this.clazz.hashCode() : 0);
    }
}
