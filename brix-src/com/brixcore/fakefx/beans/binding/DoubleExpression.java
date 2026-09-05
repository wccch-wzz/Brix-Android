package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableDoubleValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class DoubleExpression extends NumberExpressionBase implements ObservableDoubleValue {
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
        return (float) get();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public double doubleValue() {
        return get();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Double.valueOf(get());
    }

    public static DoubleExpression doubleExpression(final ObservableDoubleValue value) {
        if (value != null) {
            return value instanceof DoubleExpression ? (DoubleExpression) value : new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.DoubleExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableDoubleValue> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    public static <T extends Number> DoubleExpression doubleExpression(final ObservableValue<T> value) {
        if (value != null) {
            return value instanceof DoubleExpression ? (DoubleExpression) value : new DoubleBinding() { // from class: com.brixcore.fakefx.beans.binding.DoubleExpression.2
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    Number number = (Number) value.getValue2();
                    if (number == null) {
                        return 0.0d;
                    }
                    return number.doubleValue();
                }

                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<ObservableValue<T>> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Value must be specified.");
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding negate() {
        return (DoubleBinding) Bindings.negate(this);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding add(ObservableNumberValue other) {
        return (DoubleBinding) Bindings.add(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding add(double other) {
        return Bindings.add(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding add(float other) {
        return (DoubleBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding add(long other) {
        return (DoubleBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding add(int other) {
        return (DoubleBinding) Bindings.add((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding subtract(ObservableNumberValue other) {
        return (DoubleBinding) Bindings.subtract(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding subtract(double other) {
        return Bindings.subtract(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding subtract(float other) {
        return (DoubleBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding subtract(long other) {
        return (DoubleBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding subtract(int other) {
        return (DoubleBinding) Bindings.subtract((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding multiply(ObservableNumberValue other) {
        return (DoubleBinding) Bindings.multiply(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding multiply(double other) {
        return Bindings.multiply(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding multiply(float other) {
        return (DoubleBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding multiply(long other) {
        return (DoubleBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding multiply(int other) {
        return (DoubleBinding) Bindings.multiply((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding divide(ObservableNumberValue other) {
        return (DoubleBinding) Bindings.divide(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding divide(double other) {
        return Bindings.divide(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding divide(float other) {
        return (DoubleBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding divide(long other) {
        return (DoubleBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public DoubleBinding divide(int other) {
        return (DoubleBinding) Bindings.divide((ObservableNumberValue) this, other);
    }

    public ObjectExpression<Double> asObject() {
        return new ObjectBinding<Double>() { // from class: com.brixcore.fakefx.beans.binding.DoubleExpression.3
            {
                bind(DoubleExpression.this);
            }

            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.binding.Binding
            public void dispose() {
                unbind(DoubleExpression.this);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
            public Double computeValue() {
                return DoubleExpression.this.getValue2();
            }
        };
    }
}
