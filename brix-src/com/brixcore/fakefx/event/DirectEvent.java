package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public class DirectEvent extends Event {
    public static final EventType<DirectEvent> DIRECT = new EventType<>(Event.ANY, "DIRECT");
    private static final long serialVersionUID = 20121107;
    private final Event originalEvent;

    public DirectEvent(Event originalEvent) {
        this(originalEvent, null, null);
    }

    public DirectEvent(Event originalEvent, Object source, EventTarget target) {
        super(source, target, DIRECT);
        this.originalEvent = originalEvent;
    }

    public Event getOriginalEvent() {
        return this.originalEvent;
    }
}
