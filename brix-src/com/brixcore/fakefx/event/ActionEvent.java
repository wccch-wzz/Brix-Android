package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public class ActionEvent extends Event {
    public static final EventType<ActionEvent> ACTION = new EventType<>(Event.ANY, "ACTION");
    public static final EventType<ActionEvent> ANY = ACTION;
    private static final long serialVersionUID = 20121107;

    public ActionEvent() {
        super(ACTION);
    }

    public ActionEvent(Object source, EventTarget target) {
        super(source, target, ACTION);
    }

    @Override // com.brixcore.fakefx.event.Event
    public ActionEvent copyFor(Object newSource, EventTarget newTarget) {
        return (ActionEvent) super.copyFor(newSource, newTarget);
    }

    @Override // com.brixcore.fakefx.event.Event
    public EventType<? extends ActionEvent> getEventType() {
        return super.getEventType();
    }
}
