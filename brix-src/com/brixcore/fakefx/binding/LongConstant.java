package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableLongValue;

/* JADX INFO: loaded from: classes6.dex */
public final class LongConstant implements ObservableLongValue {
    private final long value;

    private LongConstant(long value) {
        this.value = value;
    }

    public static LongConstant valueOf(long value) {
        return new LongConstant(value);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableLongValue
    public long get() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public Number getValue2() {
        return Long.valueOf(this.value);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super Number> observer) {
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super Number> observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableNumberValue
    public int intValue() {
        return (int) this.value;
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
