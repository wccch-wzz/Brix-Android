package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public class RedirectedEvent extends Event {
    public static final EventType<RedirectedEvent> REDIRECTED = new EventType<>(Event.ANY, "REDIRECTED");
    private static final long serialVersionUID = 20121107;
    private final Event originalEvent;

    public RedirectedEvent(Event originalEvent) {
        this(originalEvent, null, null);
    }

    public RedirectedEvent(Event originalEvent, Object source, EventTarget target) {
        super(source, target, REDIRECTED);
        this.originalEvent = originalEvent;
    }

    public Event getOriginalEvent() {
        return this.originalEvent;
    }
}
