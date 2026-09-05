package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.binding.Binding;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes6.dex */
public class BindingHelperObserver implements InvalidationListener, WeakListener {
    private final WeakReference<Binding<?>> ref;

    public BindingHelperObserver(Binding<?> binding) {
        if (binding == null) {
            throw new NullPointerException("Binding has to be specified.");
        }
        this.ref = new WeakReference<>(binding);
    }

    @Override // com.brixcore.fakefx.beans.InvalidationListener
    public void invalidated(Observable observable) {
        Binding<?> binding = this.ref.get();
        if (binding == null) {
            observable.removeListener(this);
        } else {
            binding.invalidate();
        }
    }

    @Override // com.brixcore.fakefx.beans.WeakListener
    public boolean wasGarbageCollected() {
        return this.ref.get() == null;
    }
}
