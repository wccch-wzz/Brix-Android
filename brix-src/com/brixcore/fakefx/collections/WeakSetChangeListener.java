package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.WeakListener;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public final class WeakSetChangeListener<E> implements SetChangeListener<E>, WeakListener {
    private final WeakReference<SetChangeListener<E>> ref;

    public WeakSetChangeListener(@NamedArg("listener") SetChangeListener<E> listener) {
        if (listener == null) {
            throw new NullPointerException("Listener must be specified.");
        }
        this.ref = new WeakReference<>(listener);
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return this.ref.get() == null;
    }

    @Override // com.brixcore.fakefx.collections.SetChangeListener
    public void onChanged(SetChangeListener.Change<? extends E> change) {
        SetChangeListener<E> listener = this.ref.get();
        if (listener != null) {
            listener.onChanged(change);
        } else {
            change.getSet().removeListener(this);
        }
    }
}
