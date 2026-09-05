package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableFloatValue;
import com.brixcore.fakefx.binding.BidirectionalBinding;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public abstract class FloatProperty extends ReadOnlyFloatProperty implements Property<Number>, WritableFloatValue {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(Number v) {
        if (v == null) {
            set(0.0f);
        } else {
            set(v.floatValue());
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyFloatProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("FloatProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static FloatProperty floatProperty(final Property<Float> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new FloatPropertyBase() { // from class: com.brixcore.fakefx.beans.property.FloatProperty.1
            {
                BidirectionalBinding.bindNumber((FloatProperty) this, (Property<Float>) property);
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyFloatProperty, com.brixcore.fakefx.beans.binding.FloatExpression
    public ObjectProperty<Float> asObject() {
        return new ObjectPropertyBase<Float>() { // from class: com.brixcore.fakefx.beans.property.FloatProperty.2
            {
                BidirectionalBinding.bindNumber((Property<Float>) this, FloatProperty.this);
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public Object getBean() {
                return null;
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public String getName() {
                return FloatProperty.this.getName();
            }
        };
    }
}
