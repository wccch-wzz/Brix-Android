package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.beans.value.WritableValue;

/* JADX INFO: loaded from: classes4.dex */
public interface Property<T> extends ReadOnlyProperty<T>, WritableValue<T> {
    void bind(ObservableValue<? extends T> observableValue);

    void bindBidirectional(Property<T> property);

    boolean isBound();

    void unbind();

    void unbindBidirectional(Property<T> property);
}
