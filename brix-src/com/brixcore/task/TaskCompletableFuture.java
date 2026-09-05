package com.brixcore.task;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/* JADX INFO: loaded from: classes7.dex */
public interface TaskCompletableFuture {
    CompletableFuture<?> all(Collection<Task<?>> collection);

    <T> CompletableFuture<T> one(Task<T> task);
}
