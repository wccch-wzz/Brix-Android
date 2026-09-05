package com.brixcore.event;

import com.brixcore.event.Event;
import com.brixcore.util.SimpleMultimap;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes11.dex */
public final class EventManager<T extends Event> {
    private final SimpleMultimap<EventPriority, Consumer<T>, CopyOnWriteArraySet<Consumer<T>>> handlers = new SimpleMultimap<>(new Supplier() { // from class: com.brixcore.event.EventManager$$ExternalSyntheticLambda2
        @Override // java.util.function.Supplier
        public final Object get() {
            return EventManager.lambda$new$0();
        }
    }, new Supplier() { // from class: com.brixcore.event.EventManager$$ExternalSyntheticLambda3
        @Override // java.util.function.Supplier
        public final Object get() {
            return EventManager.$r8$lambda$h72ocSWVDQTRC74gf0FU8L0NlTc();
        }
    });

    public static /* synthetic */ CopyOnWriteArraySet $r8$lambda$h72ocSWVDQTRC74gf0FU8L0NlTc() {
        return new CopyOnWriteArraySet();
    }

    static /* synthetic */ Map lambda$new$0() {
        return new EnumMap(EventPriority.class);
    }

    public Consumer<T> registerWeak(Consumer<T> consumer) {
        register(new WeakListener(consumer));
        return consumer;
    }

    public Consumer<T> registerWeak(Consumer<T> consumer, EventPriority priority) {
        register(new WeakListener(consumer), priority);
        return consumer;
    }

    public void register(Consumer<T> consumer) {
        register(consumer, EventPriority.NORMAL);
    }

    public synchronized void register(Consumer<T> consumer, EventPriority priority) {
        if (!((CopyOnWriteArraySet) this.handlers.get(priority)).contains(consumer)) {
            this.handlers.put(priority, consumer);
        }
    }

    public void register(final Runnable runnable) {
        register(new Consumer() { // from class: com.brixcore.event.EventManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                runnable.run();
            }
        });
    }

    public void register(final Runnable runnable, EventPriority priority) {
        register(new Consumer() { // from class: com.brixcore.event.EventManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                runnable.run();
            }
        }, priority);
    }

    public synchronized Event.Result fireEvent(T event) {
        for (EventPriority priority : EventPriority.values()) {
            for (Consumer<T> handler : (CopyOnWriteArraySet) this.handlers.get(priority)) {
                handler.accept(event);
            }
        }
        if (event.hasResult()) {
            return event.getResult();
        }
        return Event.Result.DEFAULT;
    }

    public synchronized void unregister(Consumer<T> consumer) {
        this.handlers.removeValue(consumer);
    }

    private class WeakListener implements Consumer<T> {
        private final WeakReference<Consumer<T>> ref;

        public WeakListener(Consumer<T> listener) {
            this.ref = new WeakReference<>(listener);
        }

        @Override // java.util.function.Consumer
        public void accept(T t) {
            Consumer<T> listener = this.ref.get();
            if (listener == null) {
                EventManager.this.unregister(this);
            } else {
                listener.accept(t);
            }
        }
    }
}
