package com.brixcore.util.fakefx;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;

/* JADX INFO: loaded from: classes15.dex */
class ReferenceHolder implements InvalidationListener {
    private Object ref;

    public ReferenceHolder(Object ref) {
        this.ref = ref;
    }

    @Override // com.brixcore.fakefx.beans.InvalidationListener
    public void invalidated(Observable observable) {
    }
}
