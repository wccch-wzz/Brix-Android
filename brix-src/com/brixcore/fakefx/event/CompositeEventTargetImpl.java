package com.brixcore.fakefx.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes5.dex */
public class CompositeEventTargetImpl implements CompositeEventTarget {
    private final Set<EventTarget> eventTargets;

    public CompositeEventTargetImpl(EventTarget... eventTargets) {
        Set<EventTarget> mutableSet = new HashSet<>(eventTargets.length);
        mutableSet.addAll(Arrays.asList(eventTargets));
        this.eventTargets = Collections.unmodifiableSet(mutableSet);
    }

    @Override // com.brixcore.fakefx.event.CompositeEventTarget
    public Set<EventTarget> getTargets() {
        return this.eventTargets;
    }

    @Override // com.brixcore.fakefx.event.CompositeEventTarget
    public boolean containsTarget(EventTarget target) {
        return this.eventTargets.contains(target);
    }

    @Override // com.brixcore.fakefx.event.EventTarget
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        EventDispatchTree eventDispatchTree = (EventDispatchTree) tail;
        for (EventTarget eventTarget : this.eventTargets) {
            EventDispatchTree targetDispatchTree = eventDispatchTree.createTree();
            eventDispatchTree = eventDispatchTree.mergeTree((EventDispatchTree) eventTarget.buildEventDispatchChain(targetDispatchTree));
        }
        return eventDispatchTree;
    }
}
