package com.brixcore.fakefx.event;

import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes5.dex */
public final class EventUtil {
    private static final EventDispatchChainImpl eventDispatchChain = new EventDispatchChainImpl();
    private static final AtomicBoolean eventDispatchChainInUse = new AtomicBoolean();

    public static Event fireEvent(EventTarget eventTarget, Event event) {
        if (event.getTarget() != eventTarget) {
            event = event.copyFor(event.getSource(), eventTarget);
        }
        if (eventDispatchChainInUse.getAndSet(true)) {
            return fireEventImpl(new EventDispatchChainImpl(), eventTarget, event);
        }
        try {
            return fireEventImpl(eventDispatchChain, eventTarget, event);
        } finally {
            eventDispatchChain.reset();
            eventDispatchChainInUse.set(false);
        }
    }

    public static Event fireEvent(Event event, EventTarget... eventTargets) {
        return fireEventImpl(new EventDispatchTreeImpl(), new CompositeEventTargetImpl(eventTargets), event);
    }

    private static Event fireEventImpl(EventDispatchChain eventDispatchChain2, EventTarget eventTarget, Event event) {
        EventDispatchChain targetDispatchChain = eventTarget.buildEventDispatchChain(eventDispatchChain2);
        return targetDispatchChain.dispatchEvent(event);
    }
}
