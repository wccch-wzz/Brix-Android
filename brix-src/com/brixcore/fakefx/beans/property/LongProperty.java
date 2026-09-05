package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.value.WritableLongValue;
import com.brixcore.fakefx.binding.BidirectionalBinding;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public abstract class LongProperty extends ReadOnlyLongProperty implements Property<Number>, WritableLongValue {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void setValue(Number v) {
        if (v == null) {
            set(0L);
        } else {
            set(v.longValue());
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyLongProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("LongProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    public static LongProperty longProperty(final Property<Long> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new LongPropertyBase() { // from class: com.brixcore.fakefx.beans.property.LongProperty.1
            {
                BidirectionalBinding.bindNumber((LongProperty) this, (Property<Long>) property);
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

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyLongProperty, com.brixcore.fakefx.beans.binding.LongExpression
    public ObjectProperty<Long> asObject() {
        return new ObjectPropertyBase<Long>() { // from class: com.brixcore.fakefx.beans.property.LongProperty.2
            {
                BidirectionalBinding.bindNumber((Property<Long>) this, LongProperty.this);
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public Object getBean() {
                return null;
            }

            @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
            public String getName() {
                return LongProperty.this.getName();
            }
        };
    }
}
