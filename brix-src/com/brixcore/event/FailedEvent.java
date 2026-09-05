package com.brixcore.event;

/* JADX INFO: loaded from: classes11.dex */
public class FailedEvent<T> extends Event {
    private final int failedTime;
    private T newResult;

    public FailedEvent(Object source, int failedTime, T newResult) {
        super(source);
        this.failedTime = failedTime;
        this.newResult = newResult;
    }

    public int getFailedTime() {
        return this.failedTime;
    }

    public T getNewResult() {
        return this.newResult;
    }

    public void setNewResult(T newResult) {
        this.newResult = newResult;
    }
}
