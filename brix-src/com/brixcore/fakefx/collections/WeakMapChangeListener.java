package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.WeakListener;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public final class WeakMapChangeListener<K, V> implements MapChangeListener<K, V>, WeakListener {
    private final WeakReference<MapChangeListener<K, V>> ref;

    public WeakMapChangeListener(@NamedArg("listener") MapChangeListener<K, V> listener) {
        if (listener == null) {
            throw new NullPointerException("Listener must be specified.");
        }
        this.ref = new WeakReference<>(listener);
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return this.ref.get() == null;
    }

    @Override // com.brixcore.fakefx.collections.MapChangeListener
    public void onChanged(MapChangeListener.Change<? extends K, ? extends V> change) {
        MapChangeListener<K, V> listener = this.ref.get();
        if (listener != null) {
            listener.onChanged(change);
        } else {
            change.getMap().removeListener(this);
        }
    }
}
