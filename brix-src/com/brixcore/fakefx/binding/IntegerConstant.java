package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableIntegerValue;

/* JADX INFO: loaded from: classes6.dex */
public final class IntegerConstant implements ObservableIntegerValue {
    private final int value;

    private IntegerConstant(int value) {
        this.value = value;
    }

    public static IntegerConstant valueOf(int value) {
        return new IntegerConstant(value);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableIntegerValue
    public int get() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Integer.valueOf(this.value);
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
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public long longValue() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public float floatValue() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public double doubleValue() {
        return this.value;
    }
}
