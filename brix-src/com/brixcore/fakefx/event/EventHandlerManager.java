package com.brixcore.fakefx.event;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes5.dex */
public class EventHandlerManager extends BasicEventDispatcher {
    private final Map<EventType<? extends Event>, CompositeEventHandler<? extends Event>> eventHandlerMap = new HashMap();
    private final Object eventSource;

    public EventHandlerManager(Object eventSource) {
        this.eventSource = eventSource;
    }

    public final <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        validateEventType(eventType);
        validateEventHandler(eventHandler);
        CompositeEventHandler<T> compositeEventHandler = createGetCompositeEventHandler(eventType);
        compositeEventHandler.addEventHandler(eventHandler);
    }

    public final <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        validateEventType(eventType);
        validateEventHandler(eventHandler);
        CompositeEventHandler<? extends Event> compositeEventHandler = this.eventHandlerMap.get(eventType);
        if (compositeEventHandler != null) {
            compositeEventHandler.removeEventHandler(eventHandler);
        }
    }

    public final <T extends Event> void addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
        validateEventType(eventType);
        validateEventFilter(eventFilter);
        CompositeEventHandler<T> compositeEventHandler = createGetCompositeEventHandler(eventType);
        compositeEventHandler.addEventFilter(eventFilter);
    }

    public final <T extends Event> void removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
        validateEventType(eventType);
        validateEventFilter(eventFilter);
        CompositeEventHandler<? extends Event> compositeEventHandler = this.eventHandlerMap.get(eventType);
        if (compositeEventHandler != null) {
            compositeEventHandler.removeEventFilter(eventFilter);
        }
    }

    public final <T extends Event> void setEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        validateEventType(eventType);
        CompositeEventHandler<? extends Event> compositeEventHandler = this.eventHandlerMap.get(eventType);
        if (compositeEventHandler == null) {
            if (eventHandler == null) {
                return;
            }
            compositeEventHandler = new CompositeEventHandler<>();
            this.eventHandlerMap.put(eventType, compositeEventHandler);
        }
        compositeEventHandler.setEventHandler(eventHandler);
    }

    public final <T extends Event> EventHandler<? super T> getEventHandler(EventType<T> eventType) {
        CompositeEventHandler<? extends Event> compositeEventHandler = this.eventHandlerMap.get(eventType);
        if (compositeEventHandler != null) {
            return compositeEventHandler.getEventHandler();
        }
        return null;
    }

    @Override // com.brixcore.fakefx.event.BasicEventDispatcher
    public final Event dispatchCapturingEvent(Event event) {
        EventType<? extends Event> eventType = event.getEventType();
        do {
            event = dispatchCapturingEvent(eventType, event);
            eventType = eventType.getSuperType();
        } while (eventType != null);
        return event;
    }

    @Override // com.brixcore.fakefx.event.BasicEventDispatcher
    public final Event dispatchBubblingEvent(Event event) {
        EventType<? extends Event> eventType = event.getEventType();
        do {
            event = dispatchBubblingEvent(eventType, event);
            eventType = eventType.getSuperType();
        } while (eventType != null);
        return event;
    }

    private <T extends Event> CompositeEventHandler<T> createGetCompositeEventHandler(EventType<T> eventType) {
        CompositeEventHandler<T> compositeEventHandler = (CompositeEventHandler) this.eventHandlerMap.get(eventType);
        if (compositeEventHandler == null) {
            CompositeEventHandler<T> compositeEventHandler2 = new CompositeEventHandler<>();
            this.eventHandlerMap.put(eventType, compositeEventHandler2);
            return compositeEventHandler2;
        }
        return compositeEventHandler;
    }

    protected Object getEventSource() {
        return this.eventSource;
    }

    private Event dispatchCapturingEvent(EventType<? extends Event> handlerType, Event event) {
        CompositeEventHandler<? extends Event> compositeEventHandler = this.eventHandlerMap.get(handlerType);
        if (compositeEventHandler != null && compositeEventHandler.hasFilter()) {
            Event event2 = fixEventSource(event, this.eventSource);
            compositeEventHandler.dispatchCapturingEvent(event2);
            return event2;
        }
        return event;
    }

    private Event dispatchBubblingEvent(EventType<? extends Event> handlerType, Event event) {
        CompositeEventHandler<? extends Event> compositeEventHandler = this.eventHandlerMap.get(handlerType);
        if (compositeEventHandler != null && compositeEventHandler.hasHandler()) {
            Event event2 = fixEventSource(event, this.eventSource);
            compositeEventHandler.dispatchBubblingEvent(event2);
            return event2;
        }
        return event;
    }

    private static Event fixEventSource(Event event, Object eventSource) {
        if (event.getSource() != eventSource) {
            return event.copyFor(eventSource, event.getTarget());
        }
        return event;
    }

    private static void validateEventType(EventType<?> eventType) {
        if (eventType == null) {
            throw new NullPointerException("Event type must not be null");
        }
    }

    private static void validateEventHandler(EventHandler<?> eventHandler) {
        if (eventHandler == null) {
            throw new NullPointerException("Event handler must not be null");
        }
    }

    private static void validateEventFilter(EventHandler<?> eventFilter) {
        if (eventFilter == null) {
            throw new NullPointerException("Event filter must not be null");
        }
    }
}
