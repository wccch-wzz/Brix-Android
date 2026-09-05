package com.brixcore.fakefx.beans.property;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlyIntegerWrapper extends SimpleIntegerProperty {
    private ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlyIntegerWrapper() {
    }

    public ReadOnlyIntegerWrapper(int initialValue) {
        super(initialValue);
    }

    public ReadOnlyIntegerWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlyIntegerWrapper(Object bean, String name, int initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlyIntegerProperty getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.IntegerPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyIntegerPropertyBase {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableIntegerValue
        public int get() {
            return ReadOnlyIntegerWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlyIntegerWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyIntegerWrapper.this.getName();
        }
    }
}
