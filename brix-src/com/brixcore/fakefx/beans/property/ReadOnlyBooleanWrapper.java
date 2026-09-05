package com.brixcore.fakefx.beans.property;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlyBooleanWrapper extends SimpleBooleanProperty {
    private ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlyBooleanWrapper() {
    }

    public ReadOnlyBooleanWrapper(boolean initialValue) {
        super(initialValue);
    }

    public ReadOnlyBooleanWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlyBooleanWrapper(Object bean, String name, boolean initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlyBooleanProperty getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.BooleanPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyBooleanPropertyBase {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableBooleanValue
        public boolean get() {
            return ReadOnlyBooleanWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlyBooleanWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyBooleanWrapper.this.getName();
        }
    }
}
