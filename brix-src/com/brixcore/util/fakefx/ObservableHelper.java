package com.brixcore.util.fakefx;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes15.dex */
public class ObservableHelper implements Observable, InvalidationListener {
    private List<InvalidationListener> listeners;
    private Observable source;

    public ObservableHelper() {
        this.listeners = new CopyOnWriteArrayList();
        this.source = this;
    }

    public ObservableHelper(Observable source) {
        this.listeners = new CopyOnWriteArrayList();
        this.source = source;
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.listeners.add(listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.listeners.remove(listener);
    }

    public void invalidate() {
        this.listeners.forEach(new Consumer() { // from class: com.brixcore.util.fakefx.ObservableHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$invalidate$0((InvalidationListener) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$invalidate$0(InvalidationListener it) {
        if (it == null) {
            this.listeners.remove((Object) null);
        } else {
            it.invalidated(this.source);
        }
    }

    @Override // com.brixcore.fakefx.beans.InvalidationListener
    public void invalidated(Observable observable) {
        invalidate();
    }

    public void receiveUpdatesFrom(Observable observable) {
        observable.removeListener(this);
        observable.addListener(this);
    }
}
