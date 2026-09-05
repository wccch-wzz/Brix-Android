package com.brixcore.event;

import com.brixcore.util.Logging;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/* JADX INFO: loaded from: classes11.dex */
public final class EventBus {
    public static final EventBus EVENT_BUS = new EventBus();
    private final ConcurrentHashMap<Class<?>, EventManager<?>> events = new ConcurrentHashMap<>();

    static /* synthetic */ EventManager lambda$channel$0(Class ignored) {
        return new EventManager();
    }

    public <T extends Event> EventManager<T> channel(Class<T> clazz) {
        return (EventManager) this.events.computeIfAbsent(clazz, new Function() { // from class: com.brixcore.event.EventBus$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return EventBus.lambda$channel$0((Class) obj);
            }
        });
    }

    public Event.Result fireEvent(Event obj) {
        Logging.LOG.info(obj + " gets fired");
        return channel(obj.getClass()).fireEvent(obj);
    }
}
