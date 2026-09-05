package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableDoubleValue;
import com.brixcore.fakefx.beans.value.ObservableFloatValue;
import com.brixcore.fakefx.beans.value.ObservableIntegerValue;
import com.brixcore.fakefx.beans.value.ObservableLongValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.binding.StringFormatter;
import java.util.Locale;

/* JADX INFO: loaded from: classes16.dex */
public abstract class NumberExpressionBase implements NumberExpression {
    public static <S extends Number> NumberExpressionBase numberExpression(ObservableNumberValue value) {
        ObservableNumberValue observableNumberValueLongExpression;
        if (value == null) {
            throw new NullPointerException("Value must be specified.");
        }
        if (value instanceof NumberExpressionBase) {
            observableNumberValueLongExpression = value;
        } else if (value instanceof ObservableIntegerValue) {
            observableNumberValueLongExpression = IntegerExpression.integerExpression((ObservableIntegerValue) value);
        } else if (value instanceof ObservableDoubleValue) {
            observableNumberValueLongExpression = DoubleExpression.doubleExpression((ObservableDoubleValue) value);
        } else if (value instanceof ObservableFloatValue) {
            observableNumberValueLongExpression = FloatExpression.floatExpression((ObservableFloatValue) value);
        } else if (value instanceof ObservableLongValue) {
            observableNumberValueLongExpression = LongExpression.longExpression((ObservableLongValue) value);
        } else {
            observableNumberValueLongExpression = null;
        }
        NumberExpressionBase result = (NumberExpressionBase) observableNumberValueLongExpression;
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Unsupported Type");
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public NumberBinding add(ObservableNumberValue other) {
        return Bindings.add(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public NumberBinding subtract(ObservableNumberValue other) {
        return Bindings.subtract(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public NumberBinding multiply(ObservableNumberValue other) {
        return Bindings.multiply(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public NumberBinding divide(ObservableNumberValue other) {
        return Bindings.divide(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(ObservableNumberValue other) {
        return Bindings.equal(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(ObservableNumberValue other, double epsilon) {
        return Bindings.equal(this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(double other, double epsilon) {
        return Bindings.equal(this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(float other, double epsilon) {
        return Bindings.equal((ObservableNumberValue) this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(long other) {
        return Bindings.equal(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(long other, double epsilon) {
        return Bindings.equal((ObservableNumberValue) this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(int other) {
        return Bindings.equal((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isEqualTo(int other, double epsilon) {
        return Bindings.equal((ObservableNumberValue) this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(ObservableNumberValue other) {
        return Bindings.notEqual(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(ObservableNumberValue other, double epsilon) {
        return Bindings.notEqual(this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(double other, double epsilon) {
        return Bindings.notEqual(this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(float other, double epsilon) {
        return Bindings.notEqual((ObservableNumberValue) this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(long other) {
        return Bindings.notEqual(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(long other, double epsilon) {
        return Bindings.notEqual((ObservableNumberValue) this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(int other) {
        return Bindings.notEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding isNotEqualTo(int other, double epsilon) {
        return Bindings.notEqual((ObservableNumberValue) this, other, epsilon);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThan(ObservableNumberValue other) {
        return Bindings.greaterThan(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThan(double other) {
        return Bindings.greaterThan(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThan(float other) {
        return Bindings.greaterThan((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThan(long other) {
        return Bindings.greaterThan((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThan(int other) {
        return Bindings.greaterThan((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThan(ObservableNumberValue other) {
        return Bindings.lessThan(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThan(double other) {
        return Bindings.lessThan(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThan(float other) {
        return Bindings.lessThan((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThan(long other) {
        return Bindings.lessThan((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThan(int other) {
        return Bindings.lessThan((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThanOrEqualTo(ObservableNumberValue other) {
        return Bindings.greaterThanOrEqual(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThanOrEqualTo(double other) {
        return Bindings.greaterThanOrEqual(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThanOrEqualTo(float other) {
        return Bindings.greaterThanOrEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThanOrEqualTo(long other) {
        return Bindings.greaterThanOrEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding greaterThanOrEqualTo(int other) {
        return Bindings.greaterThanOrEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThanOrEqualTo(ObservableNumberValue other) {
        return Bindings.lessThanOrEqual(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThanOrEqualTo(double other) {
        return Bindings.lessThanOrEqual(this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThanOrEqualTo(float other) {
        return Bindings.lessThanOrEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThanOrEqualTo(long other) {
        return Bindings.lessThanOrEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public BooleanBinding lessThanOrEqualTo(int other) {
        return Bindings.lessThanOrEqual((ObservableNumberValue) this, other);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public StringBinding asString(String format) {
        return (StringBinding) Bindings.format(format, this);
    }

    @Override // com.brixcore.fakefx.beans.binding.NumberExpression
    public StringBinding asString(Locale locale, String format) {
        return (StringBinding) Bindings.format(locale, format, this);
    }
}
