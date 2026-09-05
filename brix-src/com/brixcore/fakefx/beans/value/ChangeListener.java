package com.brixcore.fakefx.beans.value;

/* JADX INFO: loaded from: classes2.dex */
@FunctionalInterface
public interface ChangeListener<T> {
    void changed(ObservableValue<? extends T> observableValue, T t, T t2);
}
