package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableDoubleValue;
import com.brixcore.fakefx.binding.BidirectionalBinding;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public abstract class DoubleProperty extends ReadOnlyDoubleProperty implements Property<Number>, WritableDoubleValue {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(Number v) {
        if (v == null) {
            set(0.0d);
        } else {
            set(v.doubleValue());
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyDoubleProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("DoubleProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static DoubleProperty doubleProperty(final Property<Double> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new DoublePropertyBase() { // from class: com.brixcore.fakefx.beans.property.DoubleProperty.1
            {
                BidirectionalBinding.bindNumber((DoubleProperty) this, (Property<Double>) property);
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyDoubleProperty, com.brixcore.fakefx.beans.binding.DoubleExpression
    public ObjectProperty<Double> asObject() {
        return new ObjectPropertyBase<Double>() { // from class: com.brixcore.fakefx.beans.property.DoubleProperty.2
            {
                BidirectionalBinding.bindNumber((Property<Double>) this, DoubleProperty.this);
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public Object getBean() {
                return null;
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public String getName() {
                return DoubleProperty.this.getName();
            }
        };
    }
}
