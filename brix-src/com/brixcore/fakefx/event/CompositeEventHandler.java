package com.brixcore.fakefx.event;

import com.brixcore.fakefx.event.Event;

/* JADX INFO: loaded from: classes5.dex */
public final class CompositeEventHandler<T extends Event> {
    private EventHandler<? super T> eventHandler;
    private EventProcessorRecord<T> firstRecord;
    private EventProcessorRecord<T> lastRecord;

    public void setEventHandler(EventHandler<? super T> eventHandler) {
        this.eventHandler = eventHandler;
    }

    public EventHandler<? super T> getEventHandler() {
        return this.eventHandler;
    }

    public void addEventHandler(EventHandler<? super T> eventHandler) {
        if (find(eventHandler, false) == null) {
            append(this.lastRecord, createEventHandlerRecord(eventHandler));
        }
    }

    public void removeEventHandler(EventHandler<? super T> eventHandler) {
        EventProcessorRecord<T> record = find(eventHandler, false);
        if (record != null) {
            remove(record);
        }
    }

    public void addEventFilter(EventHandler<? super T> eventFilter) {
        if (find(eventFilter, true) == null) {
            append(this.lastRecord, createEventFilterRecord(eventFilter));
        }
    }

    public void removeEventFilter(EventHandler<? super T> eventFilter) {
        EventProcessorRecord<T> record = find(eventFilter, true);
        if (record != null) {
            remove(record);
        }
    }

    public void dispatchBubblingEvent(Event event) {
        for (EventProcessorRecord<T> record = this.firstRecord; record != null; record = ((EventProcessorRecord) record).nextRecord) {
            if (record.isDisconnected()) {
                remove(record);
            } else {
                record.handleBubblingEvent(event);
            }
        }
        if (this.eventHandler != null) {
            this.eventHandler.handle(event);
        }
    }

    public void dispatchCapturingEvent(Event event) {
        for (EventProcessorRecord<T> record = this.firstRecord; record != null; record = ((EventProcessorRecord) record).nextRecord) {
            if (record.isDisconnected()) {
                remove(record);
            } else {
                record.handleCapturingEvent(event);
            }
        }
    }

    public boolean hasFilter() {
        return find(true);
    }

    public boolean hasHandler() {
        if (getEventHandler() != null) {
            return true;
        }
        return find(false);
    }

    boolean containsHandler(EventHandler<? super T> eventHandler) {
        return find(eventHandler, false) != null;
    }

    boolean containsFilter(EventHandler<? super T> eventFilter) {
        return find(eventFilter, true) != null;
    }

    private EventProcessorRecord<T> createEventHandlerRecord(EventHandler<? super T> eventHandler) {
        if (eventHandler instanceof WeakEventHandler) {
            return new WeakEventHandlerRecord((WeakEventHandler) eventHandler);
        }
        return new NormalEventHandlerRecord(eventHandler);
    }

    private EventProcessorRecord<T> createEventFilterRecord(EventHandler<? super T> eventFilter) {
        if (eventFilter instanceof WeakEventHandler) {
            return new WeakEventFilterRecord((WeakEventHandler) eventFilter);
        }
        return new NormalEventFilterRecord(eventFilter);
    }

    private void remove(EventProcessorRecord<T> record) {
        EventProcessorRecord<T> prevRecord = ((EventProcessorRecord) record).prevRecord;
        EventProcessorRecord<T> nextRecord = ((EventProcessorRecord) record).nextRecord;
        if (prevRecord != null) {
            ((EventProcessorRecord) prevRecord).nextRecord = nextRecord;
        } else {
            this.firstRecord = nextRecord;
        }
        if (nextRecord != null) {
            ((EventProcessorRecord) nextRecord).prevRecord = prevRecord;
        } else {
            this.lastRecord = prevRecord;
        }
    }

    private void append(EventProcessorRecord<T> prevRecord, EventProcessorRecord<T> newRecord) {
        EventProcessorRecord<T> nextRecord;
        if (prevRecord != null) {
            nextRecord = ((EventProcessorRecord) prevRecord).nextRecord;
            ((EventProcessorRecord) prevRecord).nextRecord = newRecord;
        } else {
            nextRecord = this.firstRecord;
            this.firstRecord = newRecord;
        }
        if (nextRecord != null) {
            ((EventProcessorRecord) nextRecord).prevRecord = newRecord;
        } else {
            this.lastRecord = newRecord;
        }
        ((EventProcessorRecord) newRecord).prevRecord = prevRecord;
        ((EventProcessorRecord) newRecord).nextRecord = nextRecord;
    }

    private EventProcessorRecord<T> find(EventHandler<? super T> eventProcessor, boolean isFilter) {
        for (EventProcessorRecord<T> record = this.firstRecord; record != null; record = ((EventProcessorRecord) record).nextRecord) {
            if (record.isDisconnected()) {
                remove(record);
            } else if (record.stores(eventProcessor, isFilter)) {
                return record;
            }
        }
        return null;
    }

    private boolean find(boolean isFilter) {
        for (EventProcessorRecord<T> record = this.firstRecord; record != null; record = ((EventProcessorRecord) record).nextRecord) {
            if (record.isDisconnected()) {
                remove(record);
            } else if (isFilter == record.isFilter()) {
                return true;
            }
        }
        return false;
    }

    private static abstract class EventProcessorRecord<T extends Event> {
        private EventProcessorRecord<T> nextRecord;
        private EventProcessorRecord<T> prevRecord;

        public abstract void handleBubblingEvent(T t);

        public abstract void handleCapturingEvent(T t);

        public abstract boolean isDisconnected();

        public abstract boolean isFilter();

        public abstract boolean stores(EventHandler<? super T> eventHandler, boolean z);

        private EventProcessorRecord() {
        }
    }

    private static final class NormalEventHandlerRecord<T extends Event> extends EventProcessorRecord<T> {
        private final EventHandler<? super T> eventHandler;

        public NormalEventHandlerRecord(EventHandler<? super T> eventHandler) {
            super();
            this.eventHandler = eventHandler;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean stores(EventHandler<? super T> eventProcessor, boolean isFilter) {
            return isFilter == isFilter() && this.eventHandler == eventProcessor;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isFilter() {
            return false;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleBubblingEvent(T event) {
            this.eventHandler.handle(event);
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleCapturingEvent(T event) {
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isDisconnected() {
            return false;
        }
    }

    private static final class WeakEventHandlerRecord<T extends Event> extends EventProcessorRecord<T> {
        private final WeakEventHandler<? super T> weakEventHandler;

        public WeakEventHandlerRecord(WeakEventHandler<? super T> weakEventHandler) {
            super();
            this.weakEventHandler = weakEventHandler;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean stores(EventHandler<? super T> eventProcessor, boolean isFilter) {
            return isFilter == isFilter() && this.weakEventHandler == eventProcessor;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isFilter() {
            return false;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleBubblingEvent(T event) {
            this.weakEventHandler.handle(event);
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleCapturingEvent(T event) {
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isDisconnected() {
            return this.weakEventHandler.wasGarbageCollected();
        }
    }

    private static final class NormalEventFilterRecord<T extends Event> extends EventProcessorRecord<T> {
        private final EventHandler<? super T> eventFilter;

        public NormalEventFilterRecord(EventHandler<? super T> eventFilter) {
            super();
            this.eventFilter = eventFilter;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean stores(EventHandler<? super T> eventProcessor, boolean isFilter) {
            return isFilter == isFilter() && this.eventFilter == eventProcessor;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isFilter() {
            return true;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleBubblingEvent(T event) {
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleCapturingEvent(T event) {
            this.eventFilter.handle(event);
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isDisconnected() {
            return false;
        }
    }

    private static final class WeakEventFilterRecord<T extends Event> extends EventProcessorRecord<T> {
        private final WeakEventHandler<? super T> weakEventFilter;

        public WeakEventFilterRecord(WeakEventHandler<? super T> weakEventFilter) {
            super();
            this.weakEventFilter = weakEventFilter;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean stores(EventHandler<? super T> eventProcessor, boolean isFilter) {
            return isFilter == isFilter() && this.weakEventFilter == eventProcessor;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isFilter() {
            return true;
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleBubblingEvent(T event) {
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public void handleCapturingEvent(T event) {
            this.weakEventFilter.handle(event);
        }

        @Override // com.brixcore.fakefx.event.CompositeEventHandler.EventProcessorRecord
        public boolean isDisconnected() {
            return this.weakEventFilter.wasGarbageCollected();
        }
    }
}
