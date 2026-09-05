package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public interface Binding<T> extends ObservableValue<T> {
    void dispose();

    ObservableList<?> getDependencies();

    void invalidate();

    boolean isValid();
}
