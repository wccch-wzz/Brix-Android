package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.collections.ObservableArray;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ObservableArrayBase<T extends ObservableArray<T>> implements ObservableArray<T> {
    private ArrayListenerHelper<T> listenerHelper;

    @Override // com.brixcore.fakefx.beans.Observable
    public final void addListener(InvalidationListener listener) {
        this.listenerHelper = ArrayListenerHelper.addListener(this.listenerHelper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public final void removeListener(InvalidationListener listener) {
        this.listenerHelper = ArrayListenerHelper.removeListener(this.listenerHelper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableArray
    public final void addListener(ArrayChangeListener<T> listener) {
        this.listenerHelper = ArrayListenerHelper.addListener(this.listenerHelper, this, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableArray
    public final void removeListener(ArrayChangeListener<T> listener) {
        this.listenerHelper = ArrayListenerHelper.removeListener(this.listenerHelper, listener);
    }

    protected final void fireChange(boolean sizeChanged, int from, int to) {
        ArrayListenerHelper.fireValueChangedEvent(this.listenerHelper, sizeChanged, from, to);
    }
}
