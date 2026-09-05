package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableStringValue;
import com.brixcore.fakefx.util.StringConverter;
import java.text.Format;

/* JADX INFO: loaded from: classes4.dex */
public abstract class StringProperty extends ReadOnlyStringProperty implements Property<String>, WritableStringValue {
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(String v) {
        set(v);
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bindBidirectional(Property<String> other) {
        Bindings.bindBidirectional(this, other);
    }

    public void bindBidirectional(Property<?> other, Format format) {
        Bindings.bindBidirectional(this, other, format);
    }

    public <T> void bindBidirectional(Property<T> other, StringConverter<T> converter) {
        Bindings.bindBidirectional(this, other, converter);
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbindBidirectional(Property<String> other) {
        Bindings.unbindBidirectional((Property) this, (Property) other);
    }

    public void unbindBidirectional(Object other) {
        Bindings.unbindBidirectional(this, other);
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyStringProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("StringProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }
}
