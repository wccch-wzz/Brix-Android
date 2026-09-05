package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.collections.ObservableArray;

/* JADX INFO: loaded from: classes3.dex */
public interface ObservableArray<T extends ObservableArray<T>> extends Observable {
    void addListener(ArrayChangeListener<T> arrayChangeListener);

    void clear();

    void ensureCapacity(int i);

    void removeListener(ArrayChangeListener<T> arrayChangeListener);

    void resize(int i);

    int size();

    void trimToSize();
}
