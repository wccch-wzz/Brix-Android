package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableObjectValue;

/* JADX INFO: loaded from: classes6.dex */
public class ObjectConstant<T> implements ObservableObjectValue<T> {
    private final T value;

    private ObjectConstant(T value) {
        this.value = value;
    }

    public static <T> ObjectConstant<T> valueOf(T value) {
        return new ObjectConstant<>(value);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
    public T get() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public T getValue2() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super T> observer) {
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super T> observer) {
    }
}
