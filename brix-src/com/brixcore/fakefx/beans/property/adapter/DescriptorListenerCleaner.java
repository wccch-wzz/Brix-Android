package com.brixcore.fakefx.beans.property.adapter;

import com.brixcore.fakefx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes15.dex */
class DescriptorListenerCleaner implements Runnable {
    private final WeakReference<ReadOnlyPropertyDescriptor.ReadOnlyListener<?>> lRef;
    private final ReadOnlyPropertyDescriptor pd;

    DescriptorListenerCleaner(ReadOnlyPropertyDescriptor pd, ReadOnlyPropertyDescriptor.ReadOnlyListener<?> l) {
        this.pd = pd;
        this.lRef = new WeakReference<>(l);
    }

    @Override // java.lang.Runnable
    public void run() {
        ReadOnlyPropertyDescriptor.ReadOnlyListener<?> l = this.lRef.get();
        if (l != null) {
            this.pd.removeListener(l);
        }
    }
}
