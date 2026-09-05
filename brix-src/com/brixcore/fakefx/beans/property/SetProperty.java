package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableSetValue;
import com.brixcore.fakefx.collections.ObservableSet;

/* JADX INFO: loaded from: classes4.dex */
public abstract class SetProperty<E> extends ReadOnlySetProperty<E> implements Property<ObservableSet<E>>, WritableSetValue<E> {
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(ObservableSet<E> v) {
        set(v);
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bindBidirectional(Property<ObservableSet<E>> other) {
        Bindings.bindBidirectional(this, other);
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbindBidirectional(Property<ObservableSet<E>> other) {
        Bindings.unbindBidirectional((Property) this, (Property) other);
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlySetProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("SetProperty [");
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
