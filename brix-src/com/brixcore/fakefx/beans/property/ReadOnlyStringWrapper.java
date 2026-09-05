package com.brixcore.fakefx.beans.property;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlyStringWrapper extends SimpleStringProperty {
    private ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlyStringWrapper() {
    }

    public ReadOnlyStringWrapper(String initialValue) {
        super(initialValue);
    }

    public ReadOnlyStringWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlyStringWrapper(Object bean, String name, String initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlyStringProperty getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.StringPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyStringPropertyBase {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
        public String get() {
            return ReadOnlyStringWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlyStringWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyStringWrapper.this.getName();
        }
    }
}
