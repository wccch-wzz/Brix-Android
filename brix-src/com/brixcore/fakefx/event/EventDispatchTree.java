package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public interface EventDispatchTree extends EventDispatchChain {
    @Override // com.brixcore.fakefx.event.EventDispatchChain
    EventDispatchTree append(EventDispatcher eventDispatcher);

    EventDispatchTree createTree();

    EventDispatchTree mergeTree(EventDispatchTree eventDispatchTree);

    @Override // com.brixcore.fakefx.event.EventDispatchChain
    EventDispatchTree prepend(EventDispatcher eventDispatcher);
}
