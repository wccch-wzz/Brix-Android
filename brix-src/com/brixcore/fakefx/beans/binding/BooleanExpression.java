package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableBooleanValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.StringFormatter;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class BooleanExpression implements ObservableBooleanValue {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public Boolean getValue2() {
        return Boolean.valueOf(get());
    }

    public static BooleanExpression booleanExpression(final ObservableBooleanValue value) {
        if (value != null) {
            return value instanceof BooleanExpression ? (BooleanExpression) value : new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.BooleanExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    return value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableBooleanValue> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    public static BooleanExpression booleanExpression(final ObservableValue<Boolean> value) {
        if (value != null) {
            return value instanceof BooleanExpression ? (BooleanExpression) value : new BooleanBinding() { // from class: com.brixcore.fakefx.beans.binding.BooleanExpression.2
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
                protected boolean computeValue() {
                    Boolean val = (Boolean) value.getValue2();
                    if (val == null) {
                        return false;
                    }
                    return val.booleanValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<Boolean>> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    public BooleanBinding and(ObservableBooleanValue other) {
        return Bindings.and(this, other);
    }

    public BooleanBinding or(ObservableBooleanValue other) {
        return Bindings.or(this, other);
    }

    public BooleanBinding not() {
        return Bindings.not(this);
    }

    public BooleanBinding isEqualTo(ObservableBooleanValue other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(ObservableBooleanValue other) {
        return Bindings.notEqual(this, other);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    public ObjectExpression<Boolean> asObject() {
        return new ObjectBinding<Boolean>() { // from class: com.brixcore.fakefx.beans.binding.BooleanExpression.3
            {
                bind(BooleanExpression.this);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                unbind(BooleanExpression.this);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            public Boolean computeValue() {
                return BooleanExpression.this.getValue2();
            }
        };
    }
}
