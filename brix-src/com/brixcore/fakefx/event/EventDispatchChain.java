package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public interface EventDispatchChain {
    EventDispatchChain append(EventDispatcher eventDispatcher);

    Event dispatchEvent(Event event);

    EventDispatchChain prepend(EventDispatcher eventDispatcher);
}
