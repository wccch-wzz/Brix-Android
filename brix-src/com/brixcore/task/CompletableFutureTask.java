package com.brixcore.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/* JADX INFO: loaded from: classes7.dex */
public abstract class CompletableFutureTask<T> extends Task<T> {

    public static class CustomException extends RuntimeException {
    }

    public abstract CompletableFuture<T> getFuture(TaskCompletableFuture taskCompletableFuture);

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
    }

    protected static Throwable resolveException(Throwable e) {
        if ((e instanceof ExecutionException) || (e instanceof CompletionException)) {
            return resolveException(e.getCause());
        }
        return e;
    }

    protected static CompletableFuture<Void> breakable(CompletableFuture<?> future) {
        return future.thenApplyAsync(new Function() { // from class: com.brixcore.task.CompletableFutureTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return CompletableFutureTask.lambda$breakable$0(obj);
            }
        }).exceptionally((Function<Throwable, ? extends U>) new Function() { // from class: com.brixcore.task.CompletableFutureTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return CompletableFutureTask.lambda$breakable$1((Throwable) obj);
            }
        });
    }

    static /* synthetic */ Void lambda$breakable$0(Object unused1) {
        return null;
    }

    static /* synthetic */ Void lambda$breakable$1(Throwable throwable) {
        if (resolveException(throwable) instanceof CustomException) {
            return null;
        }
        throw new CompletionException(throwable);
    }
}
