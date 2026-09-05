package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableIntegerValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class IntegerExpression extends NumberExpressionBase implements ObservableIntegerValue {
    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public int intValue() {
        return get();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public long longValue() {
        return get();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public float floatValue() {
        return get();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public double doubleValue() {
        return get();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Integer.valueOf(get());
    }

    public static IntegerExpression integerExpression(final ObservableIntegerValue value) {
        if (value != null) {
            return value instanceof IntegerExpression ? (IntegerExpression) value : new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.IntegerExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
                protected int computeValue() {
                    return value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableIntegerValue> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    public static <T extends Number> IntegerExpression integerExpression(final ObservableValue<T> value) {
        if (value != null) {
            return value instanceof IntegerExpression ? (IntegerExpression) value : new IntegerBinding() { // from class: com.brixcore.fakefx.beans.binding.IntegerExpression.2
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.IntegerBinding
                protected int computeValue() {
                    Number number = (Number) value.getValue2();
                    if (number == null) {
                        return 0;
                    }
                    return number.intValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.IntegerBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<T>> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public IntegerBinding negate() {
        return (IntegerBinding) Bindings.negate(this);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding add(double other) {
        return Bindings.add(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding add(float other) {
        return (FloatBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public LongBinding add(long other) {
        return (LongBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public IntegerBinding add(int other) {
        return (IntegerBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding subtract(double other) {
        return Bindings.subtract(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding subtract(float other) {
        return (FloatBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public LongBinding subtract(long other) {
        return (LongBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public IntegerBinding subtract(int other) {
        return (IntegerBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding multiply(double other) {
        return Bindings.multiply(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding multiply(float other) {
        return (FloatBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public LongBinding multiply(long other) {
        return (LongBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public IntegerBinding multiply(int other) {
        return (IntegerBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding divide(double other) {
        return Bindings.divide(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding divide(float other) {
        return (FloatBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public LongBinding divide(long other) {
        return (LongBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public IntegerBinding divide(int other) {
        return (IntegerBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    public ObjectExpression<Integer> asObject() {
        return new ObjectBinding<Integer>() { // from class: com.brixcore.fakefx.beans.binding.IntegerExpression.3
            {
                bind(IntegerExpression.this);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                unbind(IntegerExpression.this);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            public Integer computeValue() {
                return IntegerExpression.this.getValue2();
            }
        };
    }
}
