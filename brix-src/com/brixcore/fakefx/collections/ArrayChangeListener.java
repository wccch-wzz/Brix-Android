package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.collections.ObservableArray;

/* JADX INFO: loaded from: classes3.dex */
public interface ArrayChangeListener<T extends ObservableArray<T>> {
    void onChanged(T t, boolean z, int i, int i2);
}
