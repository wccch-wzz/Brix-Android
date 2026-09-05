package com.brixcore.fakefx.property.adapter;

import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import com.brixcore.fakefx.reflect.ReflectUtil;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/* JADX INFO: loaded from: classes7.dex */
public class ReadOnlyPropertyDescriptor {
    private static final String ADD_LISTENER_METHOD_NAME = "addPropertyChangeListener";
    private static final int ADD_LISTENER_TAKES_NAME = 1;
    private static final String ADD_PREFIX = "add";
    private static final String REMOVE_LISTENER_METHOD_NAME = "removePropertyChangeListener";
    private static final int REMOVE_LISTENER_TAKES_NAME = 2;
    private static final String REMOVE_PREFIX = "remove";
    private static final String SUFFIX = "Listener";
    private final Method addChangeListener;
    protected final Class<?> beanClass;
    private final int flags;
    private final Method getter;
    protected final String name;
    private final Method removeChangeListener;
    private final Class<?> type;

    public String getName() {
        return this.name;
    }

    public Method getGetter() {
        return this.getter;
    }

    public Class<?> getType() {
        return this.type;
    }

    public ReadOnlyPropertyDescriptor(String propertyName, Class<?> beanClass, Method getter) throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(beanClass);
        this.name = propertyName;
        this.beanClass = beanClass;
        this.getter = getter;
        this.type = getter.getReturnType();
        Method tmpAddChangeListener = null;
        Method tmpRemoveChangeListener = null;
        int tmpFlags = 0;
        try {
            String methodName = ADD_PREFIX + capitalizedName(this.name) + SUFFIX;
            tmpAddChangeListener = beanClass.getMethod(methodName, PropertyChangeListener.class);
        } catch (NoSuchMethodException e) {
            try {
                tmpAddChangeListener = beanClass.getMethod(ADD_LISTENER_METHOD_NAME, String.class, PropertyChangeListener.class);
                tmpFlags = 0 | 1;
            } catch (NoSuchMethodException e2) {
                try {
                    tmpAddChangeListener = beanClass.getMethod(ADD_LISTENER_METHOD_NAME, PropertyChangeListener.class);
                } catch (NoSuchMethodException e3) {
                }
            }
        }
        try {
            String methodName2 = REMOVE_PREFIX + capitalizedName(this.name) + SUFFIX;
            tmpRemoveChangeListener = beanClass.getMethod(methodName2, PropertyChangeListener.class);
        } catch (NoSuchMethodException e4) {
            try {
                tmpRemoveChangeListener = beanClass.getMethod(REMOVE_LISTENER_METHOD_NAME, String.class, PropertyChangeListener.class);
                tmpFlags |= 2;
            } catch (NoSuchMethodException e5) {
                try {
                    tmpRemoveChangeListener = beanClass.getMethod(REMOVE_LISTENER_METHOD_NAME, PropertyChangeListener.class);
                } catch (NoSuchMethodException e6) {
                }
            }
        }
        this.addChangeListener = tmpAddChangeListener;
        this.removeChangeListener = tmpRemoveChangeListener;
        this.flags = tmpFlags;
    }

    public static String capitalizedName(String name) {
        return (name == null || name.length() == 0) ? name : name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    public void addListener(ReadOnlyListener listener) {
        if (this.addChangeListener != null) {
            try {
                if ((this.flags & 1) > 0) {
                    this.addChangeListener.invoke(listener.getBean(), this.name, listener);
                } else {
                    this.addChangeListener.invoke(listener.getBean(), listener);
                }
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
    }

    public void removeListener(ReadOnlyListener listener) {
        if (this.removeChangeListener != null) {
            try {
                if ((this.flags & 2) > 0) {
                    this.removeChangeListener.invoke(listener.getBean(), this.name, listener);
                } else {
                    this.removeChangeListener.invoke(listener.getBean(), listener);
                }
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
    }

    public class ReadOnlyListener<T> implements PropertyChangeListener, WeakListener {
        protected final Object bean;
        private final WeakReference<ReadOnlyJavaBeanProperty<T>> propertyRef;

        public Object getBean() {
            return this.bean;
        }

        public ReadOnlyListener(Object bean, ReadOnlyJavaBeanProperty<T> property) {
            this.bean = bean;
            this.propertyRef = new WeakReference<>(property);
        }

        protected ReadOnlyJavaBeanProperty<T> checkRef() {
            ReadOnlyJavaBeanProperty<T> result = this.propertyRef.get();
            if (result == null) {
                ReadOnlyPropertyDescriptor.this.removeListener(this);
            }
            return result;
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
            ReadOnlyJavaBeanProperty<T> property;
            if (this.bean.equals(propertyChangeEvent.getSource()) && ReadOnlyPropertyDescriptor.this.name.equals(propertyChangeEvent.getPropertyName()) && (property = checkRef()) != null) {
                property.fireValueChangedEvent();
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return checkRef() == null;
        }
    }
}
