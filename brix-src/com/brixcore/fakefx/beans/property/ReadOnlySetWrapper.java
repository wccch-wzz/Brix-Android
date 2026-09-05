package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.collections.SetChangeListener;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlySetWrapper<E> extends SimpleSetProperty<E> {
    private ReadOnlySetWrapper<E>.ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlySetWrapper() {
    }

    public ReadOnlySetWrapper(ObservableSet<E> initialValue) {
        super(initialValue);
    }

    public ReadOnlySetWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlySetWrapper(Object bean, String name, ObservableSet<E> initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlySetProperty<E> getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.SetPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    @Override // com.brixcore.fakefx.beans.property.SetPropertyBase
    protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> change) {
        super.fireValueChangedEvent(change);
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent(change);
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlySetPropertyBase<E> {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public ObservableSet<E> get() {
            return ReadOnlySetWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlySetWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlySetWrapper.this.getName();
        }

        @Override // com.brixcore.fakefx.beans.binding.SetExpression
        public ReadOnlyIntegerProperty sizeProperty() {
            return ReadOnlySetWrapper.this.sizeProperty();
        }

        @Override // com.brixcore.fakefx.beans.binding.SetExpression
        public ReadOnlyBooleanProperty emptyProperty() {
            return ReadOnlySetWrapper.this.emptyProperty();
        }
    }
}
