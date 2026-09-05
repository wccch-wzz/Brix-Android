package com.brixcore.fakefx.beans.property;

/* JADX INFO: loaded from: classes4.dex */
public class ReadOnlyFloatWrapper extends SimpleFloatProperty {
    private ReadOnlyPropertyImpl readOnlyProperty;

    public ReadOnlyFloatWrapper() {
    }

    public ReadOnlyFloatWrapper(float initialValue) {
        super(initialValue);
    }

    public ReadOnlyFloatWrapper(Object bean, String name) {
        super(bean, name);
    }

    public ReadOnlyFloatWrapper(Object bean, String name, float initialValue) {
        super(bean, name, initialValue);
    }

    public ReadOnlyFloatProperty getReadOnlyProperty() {
        if (this.readOnlyProperty == null) {
            this.readOnlyProperty = new ReadOnlyPropertyImpl();
        }
        return this.readOnlyProperty;
    }

    @Override // com.brixcore.fakefx.beans.property.FloatPropertyBase
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
        if (this.readOnlyProperty != null) {
            this.readOnlyProperty.fireValueChangedEvent();
        }
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyFloatPropertyBase {
        private ReadOnlyPropertyImpl() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableFloatValue
        public float get() {
            return ReadOnlyFloatWrapper.this.get();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ReadOnlyFloatWrapper.this.getBean();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return ReadOnlyFloatWrapper.this.getName();
        }
    }
}
