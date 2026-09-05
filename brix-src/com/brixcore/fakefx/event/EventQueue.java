package com.brixcore.fakefx.event;

import java.util.ArrayDeque;
import java.util.Queue;

/* JADX INFO: loaded from: classes5.dex */
public final class EventQueue {
    private boolean inLoop;
    private Queue<Event> queue = new ArrayDeque();

    public void postEvent(Event event) {
        this.queue.add(event);
    }

    public void fire() {
        if (this.inLoop) {
            return;
        }
        this.inLoop = true;
        while (!this.queue.isEmpty()) {
            try {
                Event top = this.queue.remove();
                Event.fireEvent(top.getTarget(), top);
            } catch (Throwable th) {
                this.inLoop = false;
                throw th;
            }
        }
        this.inLoop = false;
    }
}
