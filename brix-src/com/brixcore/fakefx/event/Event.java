package com.brixcore.fakefx.event;

import com.brixcore.fakefx.beans.NamedArg;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.EventObject;

/* JADX INFO: loaded from: classes5.dex */
public class Event extends EventObject implements Cloneable {
    private static final long serialVersionUID = 20121107;
    protected boolean consumed;
    protected EventType<? extends Event> eventType;
    protected transient EventTarget target;
    public static final EventTarget NULL_SOURCE_TARGET = new EventTarget() { // from class: com.brixcore.fakefx.event.Event$$ExternalSyntheticLambda0
        @Override // com.brixcore.fakefx.event.EventTarget
        public final EventDispatchChain buildEventDispatchChain(EventDispatchChain eventDispatchChain) {
            return Event.lambda$static$0(eventDispatchChain);
        }
    };
    public static final EventType<Event> ANY = EventType.ROOT;

    static /* synthetic */ EventDispatchChain lambda$static$0(EventDispatchChain tail) {
        return tail;
    }

    public Event(@NamedArg("eventType") EventType<? extends Event> eventType) {
        this(null, null, eventType);
    }

    public Event(@NamedArg("source") Object source, @NamedArg("target") EventTarget target, @NamedArg("eventType") EventType<? extends Event> eventType) {
        super(source != null ? source : NULL_SOURCE_TARGET);
        this.target = target != null ? target : NULL_SOURCE_TARGET;
        this.eventType = eventType;
    }

    public EventTarget getTarget() {
        return this.target;
    }

    public EventType<? extends Event> getEventType() {
        return this.eventType;
    }

    public Event copyFor(Object newSource, EventTarget newTarget) {
        Event newEvent = (Event) clone();
        newEvent.source = newSource != null ? newSource : NULL_SOURCE_TARGET;
        newEvent.target = newTarget != null ? newTarget : NULL_SOURCE_TARGET;
        newEvent.consumed = false;
        return newEvent;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Can't clone Event");
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.source = NULL_SOURCE_TARGET;
        this.target = NULL_SOURCE_TARGET;
    }

    public static void fireEvent(EventTarget eventTarget, Event event) {
        if (eventTarget == null) {
            throw new NullPointerException("Event target must not be null!");
        }
        if (event == null) {
            throw new NullPointerException("Event must not be null!");
        }
        EventUtil.fireEvent(eventTarget, event);
    }
}
