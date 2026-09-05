package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.beans.property.ReadOnlyLongPropertyBase;
import com.brixcore.fakefx.property.MethodHelper;
import com.brixcore.fakefx.property.adapter.Disposer;
import com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor;
import com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor.ReadOnlyListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

/* JADX INFO: loaded from: classes15.dex */
public final class ReadOnlyJavaBeanLongProperty extends ReadOnlyLongPropertyBase implements ReadOnlyJavaBeanProperty<Number> {
    private final AccessControlContext acc = AccessController.getContext();
    private final ReadOnlyPropertyDescriptor descriptor;
    private final ReadOnlyPropertyDescriptor.ReadOnlyListener<Number> listener;

    ReadOnlyJavaBeanLongProperty(ReadOnlyPropertyDescriptor descriptor, Object bean) {
        this.descriptor = descriptor;
        Objects.requireNonNull(descriptor);
        this.listener = descriptor.new ReadOnlyListener<>(bean, this);
        descriptor.addListener(this.listener);
        Disposer.addRecord(this, new DescriptorListenerCleaner(descriptor, this.listener));
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableLongValue
    public long get() {
        return ((Long) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanLongProperty$$ExternalSyntheticLambda0
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return this.f$0.lambda$get$0();
            }
        }, this.acc)).longValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Long lambda$get$0() {
        try {
            return Long.valueOf(((Number) MethodHelper.invoke(this.descriptor.getGetter(), getBean(), null)).longValue());
        } catch (IllegalAccessException e) {
            throw new UndeclaredThrowableException(e);
        } catch (InvocationTargetException e2) {
            throw new UndeclaredThrowableException(e2);
        }
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
    public Object getBean() {
        return this.listener.getBean();
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
    public String getName() {
        return this.descriptor.getName();
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyLongPropertyBase, com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanProperty
    public void fireValueChangedEvent() {
        super.fireValueChangedEvent();
    }

    @Override // com.brixcore.fakefx.beans.property.adapter.ReadOnlyJavaBeanProperty
    public void dispose() {
        this.descriptor.removeListener(this.listener);
    }
}
