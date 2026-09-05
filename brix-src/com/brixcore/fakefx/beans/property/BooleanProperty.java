package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableBooleanValue;
import com.brixcore.fakefx.binding.BidirectionalBinding;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BooleanProperty extends ReadOnlyBooleanProperty implements Property<Boolean>, WritableBooleanValue {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(Boolean v) {
        if (v == null) {
            set(false);
        } else {
            set(v.booleanValue());
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bindBidirectional(Property<Boolean> other) {
        Bindings.bindBidirectional(this, other);
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbindBidirectional(Property<Boolean> other) {
        Bindings.unbindBidirectional((Property) this, (Property) other);
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("BooleanProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static BooleanProperty booleanProperty(final Property<Boolean> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return property instanceof BooleanProperty ? (BooleanProperty) property : new BooleanPropertyBase() { // from class: com.brixcore.fakefx.beans.property.BooleanProperty.1
            {
                BidirectionalBinding.bind(this, property);
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public Object getBean() {
                return null;
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public String getName() {
                return property.getName();
            }
        };
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty, com.brixcore.fakefx.beans.binding.BooleanExpression
    public ObjectProperty<Boolean> asObject() {
        return new ObjectPropertyBase<Boolean>() { // from class: com.brixcore.fakefx.beans.property.BooleanProperty.2
            {
                BidirectionalBinding.bind(this, BooleanProperty.this);
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public Object getBean() {
                return null;
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public String getName() {
                return BooleanProperty.this.getName();
            }
        };
    }
}
