package com.brixcore.fakefx.beans.value;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.WeakListener;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public final class WeakChangeListener<T> implements ChangeListener<T>, WeakListener {
    private final WeakReference<ChangeListener<T>> ref;

    public WeakChangeListener(@NamedArg("listener") ChangeListener<T> listener) {
        if (listener == null) {
            throw new NullPointerException("Listener must be specified.");
        }
        this.ref = new WeakReference<>(listener);
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return this.ref.get() == null;
    }

    @Override // com.brixcore.fakefx.beans.value.ChangeListener
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        ChangeListener<T> listener = this.ref.get();
        if (listener != null) {
            listener.changed(observable, oldValue, newValue);
        } else {
            observable.removeListener(this);
        }
    }
}
