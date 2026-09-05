package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableDoubleValue;

/* JADX INFO: loaded from: classes6.dex */
public final class DoubleConstant implements ObservableDoubleValue {
    private final double value;

    private DoubleConstant(double value) {
        this.value = value;
    }

    public static DoubleConstant valueOf(double value) {
        return new DoubleConstant(value);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableDoubleValue
    public double get() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue, reason: merged with bridge method [inline-methods] */
    public Number getValue2() {
        return Double.valueOf(this.value);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super Number> listener) {
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super Number> listener) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public int intValue() {
        return (int) this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public long longValue() {
        return (long) this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public float floatValue() {
        return (float) this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public double doubleValue() {
        return this.value;
    }
}
