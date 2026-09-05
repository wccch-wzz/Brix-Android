package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.value.ObservableValue;

/* JADX INFO: loaded from: classes4.dex */
public interface ReadOnlyProperty<T> extends ObservableValue<T> {
    Object getBean();

    String getName();
}
