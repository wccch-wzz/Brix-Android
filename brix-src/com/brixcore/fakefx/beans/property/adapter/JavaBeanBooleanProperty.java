package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.ExpressionHelper;
import com.brixcore.fakefx.property.MethodHelper;
import com.brixcore.fakefx.property.adapter.Disposer;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor;
import com.brixcore.fakefx.property.adapter.PropertyDescriptor.Listener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

/* JADX INFO: loaded from: classes15.dex */
public final class JavaBeanBooleanProperty extends BooleanProperty implements JavaBeanProperty<Boolean> {
    private final PropertyDescriptor descriptor;
    private final PropertyDescriptor.Listener<Boolean> listener;
    private ObservableValue<? extends Boolean> observable = null;
    private ExpressionHelper<Boolean> helper = null;
    private final AccessControlContext acc = AccessController.getContext();

    JavaBeanBooleanProperty(PropertyDescriptor descriptor, Object bean) {
        this.descriptor = descriptor;
        Objects.requireNonNull(descriptor);
        this.listener = descriptor.new Listener<>(bean, this);
        descriptor.addListener(this.listener);
        Disposer.addRecord(this, new DescriptorListenerCleaner(descriptor, this.listener));
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableBooleanValue
    public boolean get() {
        return ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.beans.property.adapter.JavaBeanBooleanProperty$$ExternalSyntheticLambda0
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return this.f$0.lambda$get$0();
            }
        }, this.acc)).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$get$0() {
        try {
            return (Boolean) MethodHelper.invoke(this.descriptor.getGetter(), getBean(), null);
        } catch (IllegalAccessException e) {
            throw new UndeclaredThrowableException(e);
        } catch (InvocationTargetException e2) {
            throw new UndeclaredThrowableException(e2);
        }
    }

    @Override // com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void set(final boolean value) {
        if (isBound()) {
            throw new RuntimeException("A bound value cannot be set.");
        }
        AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.beans.property.adapter.JavaBeanBooleanProperty$$ExternalSyntheticLambda1
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return this.f$0.lambda$set$1(value);
            }
        }, this.acc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Void lambda$set$1(boolean value) {
        try {
            MethodHelper.invoke(this.descriptor.getSetter(), getBean(), new Object[]{Boolean.valueOf(value)});
            ExpressionHelper.fireValueChangedEvent(this.helper);
            return null;
        } catch (IllegalAccessException e) {
            throw new UndeclaredThrowableException(e);
        } catch (InvocationTargetException e2) {
            throw new UndeclaredThrowableException(e2);
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bind(ObservableValue<? extends Boolean> observable) {
        if (observable == null) {
            throw new NullPointerException("Cannot bind to null");
        }
        if (!observable.equals(this.observable)) {
            unbind();
            set(observable.getValue2().booleanValue());
            this.observable = observable;
            this.observable.addListener(this.listener);
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbind() {
        if (this.observable != null) {
            this.observable.removeListener(this.listener);
            this.observable = null;
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public boolean isBound() {
        return this.observable != null;
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
    public Object getBean() {
        return this.listener.getBean();
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
    public String getName() {
        return this.descriptor.getName();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super Boolean> listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super Boolean> listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanProperty
    public void fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(this.helper);
    }

    @Override // com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanProperty
    public void dispose() {
        this.descriptor.removeListener(this.listener);
    }

    @Override // com.brixcore.fakefx.beans.property.BooleanProperty, com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("BooleanProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        if (isBound()) {
            result.append("bound, ");
        }
        result.append("value: ").append(get());
        result.append("]");
        return result.toString();
    }
}
