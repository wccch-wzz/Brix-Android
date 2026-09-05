package com.brixcore.task;

import java.util.EventListener;

/* JADX INFO: loaded from: classes7.dex */
public abstract class TaskListener implements EventListener {
    public void onStart() {
    }

    public void onReady(Task<?> task) {
    }

    public void onRunning(Task<?> task) {
    }

    public void onFinished(Task<?> task) {
    }

    public void onFailed(Task<?> task, Throwable throwable) {
        onFinished(task);
    }

    public void onStop(boolean success, TaskExecutor executor) {
    }

    public void onPropertiesUpdate(Task<?> task) {
    }
}
