package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableMap;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlyMapWrapper<K, V> extends SimpleMapProperty<K, V> {
    private ReadOnlyMapWrapper<K, V>.ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlyMapWrapper() {
    }

    public ReadOnlyMapWrapper(ObservableMap<K, V> initialValue) {
        super(initialValue);
    }

    public ReadOnlyMapWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlyMapWrapper(Object bean, String name, ObservableMap<K, V> initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlyMapProperty<K, V> getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.MapPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    @Override // com.brixcore.fakefx.beans.property.MapPropertyBase
    protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
        super.fireValueChangedEvent(change);
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent(change);
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyMapPropertyBase<K, V> {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public ObservableMap<K, V> get() {
            return ReadOnlyMapWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlyMapWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyMapWrapper.this.getName();
        }

        @Override // com.brixcore.fakefx.beans.binding.MapExpression
        public ReadOnlyIntegerProperty sizeProperty() {
            return ReadOnlyMapWrapper.this.sizeProperty();
        }

        @Override // com.brixcore.fakefx.beans.binding.MapExpression
        public ReadOnlyBooleanProperty emptyProperty() {
            return ReadOnlyMapWrapper.this.emptyProperty();
        }
    }
}
