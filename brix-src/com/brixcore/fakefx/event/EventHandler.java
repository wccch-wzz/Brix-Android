package com.brixcore.fakefx.event;

import com.brixcore.fakefx.event.Event;
import java.util.EventListener;

/* JADX INFO: loaded from: classes5.dex */
@FunctionalInterface
public interface EventHandler<T extends Event> extends EventListener {
    void handle(T t);
}
