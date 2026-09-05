package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableFloatValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class FloatExpression extends NumberExpressionBase implements ObservableFloatValue {
    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public int intValue() {
        return (int) get();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public long longValue() {
        return (long) get();
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
        return Float.valueOf(get());
    }

    public static FloatExpression floatExpression(final ObservableFloatValue value) {
        if (value != null) {
            return value instanceof FloatExpression ? (FloatExpression) value : new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.FloatExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    return value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableFloatValue> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    public static <T extends Number> FloatExpression floatExpression(final ObservableValue<T> value) {
        if (value != null) {
            return value instanceof FloatExpression ? (FloatExpression) value : new FloatBinding() { // from class: com.brixcore.fakefx.beans.binding.FloatExpression.2
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding
                protected float computeValue() {
                    Number number = (Number) value.getValue2();
                    if (number == null) {
                        return 0.0f;
                    }
                    return number.floatValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.FloatBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<T>> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding negate() {
        return (FloatBinding) Bindings.negate(this);
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
    public FloatBinding add(long other) {
        return (FloatBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding add(int other) {
        return (FloatBinding) Bindings.add((ObservableNumberValue) this, other);
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
    public FloatBinding subtract(long other) {
        return (FloatBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding subtract(int other) {
        return (FloatBinding) Bindings.subtract((ObservableNumberValue) this, other);
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
    public FloatBinding multiply(long other) {
        return (FloatBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding multiply(int other) {
        return (FloatBinding) Bindings.multiply((ObservableNumberValue) this, other);
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
    public FloatBinding divide(long other) {
        return (FloatBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public FloatBinding divide(int other) {
        return (FloatBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    public ObjectExpression<Float> asObject() {
        return new ObjectBinding<Float>() { // from class: com.brixcore.fakefx.beans.binding.FloatExpression.3
            {
                bind(FloatExpression.this);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                unbind(FloatExpression.this);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            public Float computeValue() {
                return FloatExpression.this.getValue2();
            }
        };
    }
}
