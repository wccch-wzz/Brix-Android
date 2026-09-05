package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.beans.property.ReadOnlyProperty;

/* JADX INFO: loaded from: classes15.dex */
public interface ReadOnlyJavaBeanProperty<T> extends ReadOnlyProperty<T> {
    void dispose();

    void fireValueChangedEvent();
}
