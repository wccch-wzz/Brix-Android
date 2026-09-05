package com.brixcore.fakefx.event;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.event.Event;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes5.dex */
public final class WeakEventHandler<T extends Event> implements EventHandler<T> {
    private final WeakReference<EventHandler<T>> weakRef;

    public WeakEventHandler(@NamedArg("eventHandler") EventHandler<T> eventHandler) {
        this.weakRef = new WeakReference<>(eventHandler);
    }

    public boolean wasGarbageCollected() {
        return this.weakRef.get() == null;
    }

    @Override // com.brixcore.fakefx.event.EventHandler
    public void handle(T event) {
        EventHandler<T> eventHandler = this.weakRef.get();
        if (eventHandler != null) {
            eventHandler.handle(event);
        }
    }

    void clear() {
        this.weakRef.clear();
    }
}
