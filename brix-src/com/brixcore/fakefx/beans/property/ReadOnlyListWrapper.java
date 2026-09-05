package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlyListWrapper<E> extends SimpleListProperty<E> {
    private ReadOnlyListWrapper<E>.ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlyListWrapper() {
    }

    public ReadOnlyListWrapper(ObservableList<E> initialValue) {
        super(initialValue);
    }

    public ReadOnlyListWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlyListWrapper(Object bean, String name, ObservableList<E> initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlyListProperty<E> getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.ListPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    @Override // com.brixcore.fakefx.beans.property.ListPropertyBase
    protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> change) {
        super.fireValueChangedEvent(change);
        if (this.readOnlyProperty != null) {
            change.reset();
            this.readOnlyProperty.fireValueChangedEvent(change);
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyListPropertyBase<E> {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public ObservableList<E> get() {
            return ReadOnlyListWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlyListWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyListWrapper.this.getName();
        }

        @Override // com.brixcore.fakefx.beans.binding.ListExpression
        public ReadOnlyIntegerProperty sizeProperty() {
            return ReadOnlyListWrapper.this.sizeProperty();
        }

        @Override // com.brixcore.fakefx.beans.binding.ListExpression
        public ReadOnlyBooleanProperty emptyProperty() {
            return ReadOnlyListWrapper.this.emptyProperty();
        }
    }
}
