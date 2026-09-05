package com.brixcore.fakefx.event;

import java.util.Set;

/* JADX INFO: loaded from: classes5.dex */
public interface CompositeEventTarget extends EventTarget {
    boolean containsTarget(EventTarget eventTarget);

    Set<EventTarget> getTargets();
}
