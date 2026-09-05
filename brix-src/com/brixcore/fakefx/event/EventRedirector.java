package com.brixcore.fakefx.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes5.dex */
public class EventRedirector extends BasicEventDispatcher {
    private final Object eventSource;
    private final List<EventDispatcher> eventDispatchers = new CopyOnWriteArrayList();
    private final EventDispatchChainImpl eventDispatchChain = new EventDispatchChainImpl();

    public EventRedirector(Object eventSource) {
        this.eventSource = eventSource;
    }

    protected void handleRedirectedEvent(Object eventSource, Event event) {
    }

    public final void addEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatchers.add(eventDispatcher);
    }

    public final void removeEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatchers.remove(eventDispatcher);
    }

    @Override // com.brixcore.fakefx.event.BasicEventDispatcher
    public final Event dispatchCapturingEvent(Event event) {
        EventType<?> eventType = event.getEventType();
        if (eventType == DirectEvent.DIRECT) {
            return ((DirectEvent) event).getOriginalEvent();
        }
        redirectEvent(event);
        if (eventType == RedirectedEvent.REDIRECTED) {
            handleRedirectedEvent(event.getSource(), ((RedirectedEvent) event).getOriginalEvent());
            return event;
        }
        return event;
    }

    private void redirectEvent(Event event) {
        RedirectedEvent redirectedEvent;
        if (!this.eventDispatchers.isEmpty()) {
            if (event.getEventType() == RedirectedEvent.REDIRECTED) {
                redirectedEvent = (RedirectedEvent) event;
            } else {
                redirectedEvent = new RedirectedEvent(event, this.eventSource, null);
            }
            for (EventDispatcher eventDispatcher : this.eventDispatchers) {
                this.eventDispatchChain.reset();
                eventDispatcher.dispatchEvent(redirectedEvent, this.eventDispatchChain);
            }
        }
    }
}
