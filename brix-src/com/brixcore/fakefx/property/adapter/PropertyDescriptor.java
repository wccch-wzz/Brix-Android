package com.brixcore.fakefx.property.adapter;

import com.brixcore.fakefx.beans.property.Property;
import com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanProperty;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.property.MethodHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes7.dex */
public class PropertyDescriptor extends ReadOnlyPropertyDescriptor {
    private static final String ADD_PREFIX = "add";
    private static final String ADD_VETOABLE_LISTENER_METHOD_NAME = "addVetoableChangeListener";
    private static final int ADD_VETOABLE_LISTENER_TAKES_NAME = 1;
    private static final String REMOVE_PREFIX = "remove";
    private static final String REMOVE_VETOABLE_LISTENER_METHOD_NAME = "removeVetoableChangeListener";
    private static final int REMOVE_VETOABLE_LISTENER_TAKES_NAME = 2;
    private static final String SUFFIX = "Listener";
    private final Method addVetoListener;
    private final int flags;
    private final Method removeVetoListener;
    private final Method setter;

    public Method getSetter() {
        return this.setter;
    }

    public PropertyDescriptor(String propertyName, Class<?> beanClass, Method getter, Method setter) throws NoSuchMethodException {
        super(propertyName, beanClass, getter);
        this.setter = setter;
        Method tmpAddVetoListener = null;
        Method tmpRemoveVetoListener = null;
        int tmpFlags = 0;
        String addMethodName = ADD_PREFIX + capitalizedName(this.name) + SUFFIX;
        try {
            tmpAddVetoListener = beanClass.getMethod(addMethodName, VetoableChangeListener.class);
        } catch (NoSuchMethodException e) {
            try {
                tmpAddVetoListener = beanClass.getMethod(ADD_VETOABLE_LISTENER_METHOD_NAME, String.class, VetoableChangeListener.class);
                tmpFlags = 0 | 1;
            } catch (NoSuchMethodException e2) {
                try {
                    tmpAddVetoListener = beanClass.getMethod(ADD_VETOABLE_LISTENER_METHOD_NAME, VetoableChangeListener.class);
                } catch (NoSuchMethodException e3) {
                }
            }
        }
        String removeMethodName = REMOVE_PREFIX + capitalizedName(this.name) + SUFFIX;
        try {
            tmpRemoveVetoListener = beanClass.getMethod(removeMethodName, VetoableChangeListener.class);
        } catch (NoSuchMethodException e4) {
            try {
                tmpRemoveVetoListener = beanClass.getMethod(REMOVE_VETOABLE_LISTENER_METHOD_NAME, String.class, VetoableChangeListener.class);
                tmpFlags |= 2;
            } catch (NoSuchMethodException e5) {
                try {
                    tmpRemoveVetoListener = beanClass.getMethod(REMOVE_VETOABLE_LISTENER_METHOD_NAME, VetoableChangeListener.class);
                } catch (NoSuchMethodException e6) {
                }
            }
        }
        this.addVetoListener = tmpAddVetoListener;
        this.removeVetoListener = tmpRemoveVetoListener;
        this.flags = tmpFlags;
    }

    @Override // com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor
    public void addListener(ReadOnlyPropertyDescriptor.ReadOnlyListener listener) {
        super.addListener(listener);
        if (this.addVetoListener != null) {
            try {
                if ((this.flags & 1) > 0) {
                    this.addVetoListener.invoke(listener.getBean(), this.name, listener);
                } else {
                    this.addVetoListener.invoke(listener.getBean(), listener);
                }
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
    }

    @Override // com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor
    public void removeListener(ReadOnlyPropertyDescriptor.ReadOnlyListener listener) {
        super.removeListener(listener);
        if (this.removeVetoListener != null) {
            try {
                if ((this.flags & 2) > 0) {
                    this.removeVetoListener.invoke(listener.getBean(), this.name, listener);
                } else {
                    this.removeVetoListener.invoke(listener.getBean(), listener);
                }
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
    }

    public class Listener<T> extends ReadOnlyPropertyDescriptor.ReadOnlyListener<T> implements ChangeListener<T>, VetoableChangeListener {
        private boolean updating;

        public Listener(Object bean, ReadOnlyJavaBeanProperty<T> property) {
            super(bean, property);
        }

        @Override // com.brixcore.fakefx.beans.value.ChangeListener
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            ReadOnlyJavaBeanProperty<T> property = checkRef();
            if (property == null) {
                observable.removeListener(this);
                return;
            }
            if (!this.updating) {
                this.updating = true;
                try {
                    MethodHelper.invoke(PropertyDescriptor.this.setter, this.bean, new Object[]{newValue});
                    property.fireValueChangedEvent();
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e2) {
                } finally {
                    this.updating = false;
                }
            }
        }

        @Override // com.brixcore.fakefx.property.adapter.VetoableChangeListener
        public void vetoableChange(PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
            if (this.bean.equals(propertyChangeEvent.getSource()) && PropertyDescriptor.this.name.equals(propertyChangeEvent.getPropertyName())) {
                ReadOnlyJavaBeanProperty<T> property = checkRef();
                if ((property instanceof Property) && ((Property) property).isBound() && !this.updating) {
                    throw new PropertyVetoException("A bound value cannot be set.", propertyChangeEvent);
                }
            }
        }
    }
}
