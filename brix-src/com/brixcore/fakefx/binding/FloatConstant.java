package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableFloatValue;

/* JADX INFO: loaded from: classes6.dex */
public final class FloatConstant implements ObservableFloatValue {
    private final float value;

    private FloatConstant(float value) {
        this.value = value;
    }

    public static FloatConstant valueOf(float value) {
        return new FloatConstant(value);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableFloatValue
    public float get() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Float.valueOf(this.value);
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
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public double doubleValue() {
        return this.value;
    }
}
