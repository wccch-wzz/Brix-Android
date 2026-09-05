package com.brixcore.fakefx.event;

import com.brixcore.fakefx.event.Event;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes5.dex */
public final class EventType<T extends Event> implements Serializable {
    public static final EventType<Event> ROOT = new EventType<>("EVENT", (EventType) null);
    private final String name;
    private WeakHashMap<EventType<? extends T>, Void> subTypes;
    private final EventType<? super T> superType;

    @Deprecated
    public EventType() {
        this(ROOT, (String) null);
    }

    public EventType(String name) {
        this(ROOT, name);
    }

    public EventType(EventType<? super T> superType) {
        this(superType, (String) null);
    }

    public EventType(EventType<? super T> superType, String name) {
        if (superType == null) {
            throw new NullPointerException("Event super type must not be null!");
        }
        this.superType = superType;
        this.name = name;
        superType.register(this);
    }

    EventType(String name, EventType<? super T> superType) {
        this.superType = superType;
        this.name = name;
        if (superType != null) {
            if (superType.subTypes != null) {
                Iterator<EventType<? extends T>> it = superType.subTypes.keySet().iterator();
                while (it.hasNext()) {
                    EventType<? extends T> next = it.next();
                    if ((name == null && next.name == null) || (name != null && name.equals(next.name))) {
                        it.remove();
                    }
                }
            }
            superType.register(this);
        }
    }

    public final EventType<? super T> getSuperType() {
        return this.superType;
    }

    public final String getName() {
        return this.name;
    }

    public String toString() {
        return this.name != null ? this.name : super.toString();
    }

    private void register(EventType<? extends T> subType) {
        if (this.subTypes == null) {
            this.subTypes = new WeakHashMap<>();
        }
        for (EventType<? extends T> t : this.subTypes.keySet()) {
            if ((t.name == null && subType.name == null) || (t.name != null && t.name.equals(subType.name))) {
                throw new IllegalArgumentException("EventType \"" + subType + "\"with parent \"" + subType.getSuperType() + "\" already exists");
            }
        }
        this.subTypes.put(subType, null);
    }

    private Object writeReplace() throws ObjectStreamException {
        Deque<String> path = new LinkedList<>();
        for (EventType eventType = this; eventType != ROOT; eventType = eventType.superType) {
            path.addFirst(eventType.name);
        }
        return new EventTypeSerialization(new ArrayList(path));
    }

    static class EventTypeSerialization implements Serializable {
        private List<String> path;

        public EventTypeSerialization(List<String> path) {
            this.path = path;
        }

        private Object readResolve() throws ObjectStreamException {
            EventType<Event> eventType = EventType.ROOT;
            for (int i = 0; i < this.path.size(); i++) {
                String p = this.path.get(i);
                if (((EventType) eventType).subTypes != null) {
                    EventType<Event> eventTypeFindSubType = findSubType(((EventType) eventType).subTypes.keySet(), p);
                    if (eventTypeFindSubType == null) {
                        throw new InvalidObjectException("Cannot find event type \"" + p + "\" (of " + eventType + ")");
                    }
                    eventType = eventTypeFindSubType;
                } else {
                    throw new InvalidObjectException("Cannot find event type \"" + p + "\" (of " + eventType + ")");
                }
            }
            return eventType;
        }

        private EventType findSubType(Set<EventType> subTypes, String name) {
            for (EventType t : subTypes) {
                if ((t.name == null && name == null) || (t.name != null && t.name.equals(name))) {
                    return t;
                }
            }
            return null;
        }
    }
}
