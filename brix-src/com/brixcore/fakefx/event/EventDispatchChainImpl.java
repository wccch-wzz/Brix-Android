package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public class EventDispatchChainImpl implements EventDispatchChain {
    private static final int CAPACITY_GROWTH_FACTOR = 8;
    private int activeCount;
    private EventDispatcher[] dispatchers;
    private int headIndex;
    private int[] nextLinks;
    private int reservedCount;
    private int tailIndex;

    public void reset() {
        for (int i = 0; i < this.reservedCount; i++) {
            this.dispatchers[i] = null;
        }
        this.reservedCount = 0;
        this.activeCount = 0;
        this.headIndex = 0;
        this.tailIndex = 0;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchChain
    public EventDispatchChain append(EventDispatcher eventDispatcher) {
        ensureCapacity(this.reservedCount + 1);
        if (this.activeCount == 0) {
            insertFirst(eventDispatcher);
            return this;
        }
        this.dispatchers[this.reservedCount] = eventDispatcher;
        this.nextLinks[this.tailIndex] = this.reservedCount;
        this.tailIndex = this.reservedCount;
        this.activeCount++;
        this.reservedCount++;
        return this;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchChain
    public EventDispatchChain prepend(EventDispatcher eventDispatcher) {
        ensureCapacity(this.reservedCount + 1);
        if (this.activeCount == 0) {
            insertFirst(eventDispatcher);
            return this;
        }
        this.dispatchers[this.reservedCount] = eventDispatcher;
        this.nextLinks[this.reservedCount] = this.headIndex;
        this.headIndex = this.reservedCount;
        this.activeCount++;
        this.reservedCount++;
        return this;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchChain
    public Event dispatchEvent(Event event) {
        if (this.activeCount == 0) {
            return event;
        }
        int savedHeadIndex = this.headIndex;
        int savedTailIndex = this.tailIndex;
        int savedActiveCount = this.activeCount;
        int savedReservedCount = this.reservedCount;
        EventDispatcher nextEventDispatcher = this.dispatchers[this.headIndex];
        this.headIndex = this.nextLinks[this.headIndex];
        this.activeCount--;
        Event returnEvent = nextEventDispatcher.dispatchEvent(event, this);
        this.headIndex = savedHeadIndex;
        this.tailIndex = savedTailIndex;
        this.activeCount = savedActiveCount;
        this.reservedCount = savedReservedCount;
        return returnEvent;
    }

    private void insertFirst(EventDispatcher eventDispatcher) {
        this.dispatchers[this.reservedCount] = eventDispatcher;
        this.headIndex = this.reservedCount;
        this.tailIndex = this.reservedCount;
        this.activeCount = 1;
        this.reservedCount++;
    }

    private void ensureCapacity(int size) {
        int newCapacity = ((size + 8) - 1) & (-8);
        if (newCapacity == 0) {
            return;
        }
        if (this.dispatchers == null || this.dispatchers.length < newCapacity) {
            EventDispatcher[] newDispatchers = new EventDispatcher[newCapacity];
            int[] newLinks = new int[newCapacity];
            if (this.reservedCount > 0) {
                System.arraycopy(this.dispatchers, 0, newDispatchers, 0, this.reservedCount);
                System.arraycopy(this.nextLinks, 0, newLinks, 0, this.reservedCount);
            }
            this.dispatchers = newDispatchers;
            this.nextLinks = newLinks;
        }
    }
}
