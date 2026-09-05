package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.WeakListener;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public final class WeakListChangeListener<E> implements ListChangeListener<E>, WeakListener {
    private final WeakReference<ListChangeListener<E>> ref;

    public WeakListChangeListener(@NamedArg("listener") ListChangeListener<E> listener) {
        if (listener == null) {
            throw new NullPointerException("Listener must be specified.");
        }
        this.ref = new WeakReference<>(listener);
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return this.ref.get() == null;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener
    public void onChanged(ListChangeListener.Change<? extends E> change) {
        ListChangeListener<E> listener = this.ref.get();
        if (listener != null) {
            listener.onChanged(change);
        } else {
            change.getList().removeListener(this);
        }
    }
}
