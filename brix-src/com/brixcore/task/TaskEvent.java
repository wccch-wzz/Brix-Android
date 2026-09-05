package com.brixcore.task;

import com.brixcore.event.Event;

/* JADX INFO: loaded from: classes7.dex */
public class TaskEvent extends Event {
    private final boolean failed;
    private final Task<?> task;

    public TaskEvent(Object source, Task<?> task, boolean failed) {
        super(source);
        this.task = task;
        this.failed = failed;
    }

    public Task<?> getTask() {
        return this.task;
    }

    public boolean isFailed() {
        return this.failed;
    }
}
