package com.brixcore.fakefx.beans;

import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes15.dex */
public final class WeakInvalidationListener implements InvalidationListener, WeakListener {
    private final WeakReference<InvalidationListener> ref;

    public WeakInvalidationListener(@NamedArg("listener") InvalidationListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener must be specified.");
        }
        this.ref = new WeakReference<>(listener);
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return this.ref.get() == null;
    }

    @Override // com.brixcore.fakefx.beans.InvalidationListener
    public void invalidated(Observable observable) {
        InvalidationListener listener = this.ref.get();
        if (listener != null) {
            listener.invalidated(observable);
        } else {
            observable.removeListener(this);
        }
    }
}
