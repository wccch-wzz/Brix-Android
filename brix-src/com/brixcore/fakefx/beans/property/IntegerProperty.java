package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableIntegerValue;
import com.brixcore.fakefx.binding.BidirectionalBinding;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public abstract class IntegerProperty extends ReadOnlyIntegerProperty implements Property<Number>, WritableIntegerValue {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(Number v) {
        if (v == null) {
            set(0);
        } else {
            set(v.intValue());
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bindBidirectional(Property<Number> other) {
        Bindings.bindBidirectional(this, other);
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbindBidirectional(Property<Number> other) {
        Bindings.unbindBidirectional((Property) this, (Property) other);
    }

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("IntegerProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static IntegerProperty integerProperty(final Property<Integer> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new IntegerPropertyBase() { // from class: com.brixcore.fakefx.beans.property.IntegerProperty.1
            {
                BidirectionalBinding.bindNumber((IntegerProperty) this, (Property<Integer>) property);
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty, com.brixcore.fakefx.beans.binding.IntegerExpression
    public ObjectProperty<Integer> asObject() {
        return new ObjectPropertyBase<Integer>() { // from class: com.brixcore.fakefx.beans.property.IntegerProperty.2
            {
                BidirectionalBinding.bindNumber((Property<Integer>) this, IntegerProperty.this);
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public Object getBean() {
                return null;
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public String getName() {
                return IntegerProperty.this.getName();
            }
        };
    }
}
