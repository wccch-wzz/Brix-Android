package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public abstract class CompositeEventDispatcher extends BasicEventDispatcher {
    public abstract BasicEventDispatcher getFirstDispatcher();

    public abstract BasicEventDispatcher getLastDispatcher();

    @Override // com.brixcore.fakefx.event.BasicEventDispatcher
    public final Event dispatchCapturingEvent(Event event) {
        for (BasicEventDispatcher childDispatcher = getFirstDispatcher(); childDispatcher != null; childDispatcher = childDispatcher.getNextDispatcher()) {
            event = childDispatcher.dispatchCapturingEvent(event);
            if (event.isConsumed()) {
                break;
            }
        }
        return event;
    }

    @Override // com.brixcore.fakefx.event.BasicEventDispatcher
    public final Event dispatchBubblingEvent(Event event) {
        for (BasicEventDispatcher childDispatcher = getLastDispatcher(); childDispatcher != null; childDispatcher = childDispatcher.getPreviousDispatcher()) {
            event = childDispatcher.dispatchBubblingEvent(event);
            if (event.isConsumed()) {
                break;
            }
        }
        return event;
    }
}
