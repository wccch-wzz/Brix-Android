package com.brixcore.util.function;

import java.lang.Exception;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalConsumer<T, E extends Exception> {
    void accept(T t) throws Exception;

    static <T, E extends Exception> ExceptionalConsumer<T, E> fromRunnable(final ExceptionalRunnable<E> exceptionalRunnable) {
        return (ExceptionalConsumer<T, E>) new ExceptionalConsumer<T, E>() { // from class: com.brixcore.util.function.ExceptionalConsumer.1
            @Override // com.brixcore.util.function.ExceptionalConsumer
            public void accept(T o) throws Exception {
                exceptionalRunnable.run();
            }

            public String toString() {
                return exceptionalRunnable.toString();
            }
        };
    }

    static <T> ExceptionalConsumer<T, ?> empty() {
        return new ExceptionalConsumer() { // from class: com.brixcore.util.function.ExceptionalConsumer$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalConsumer
            public final void accept(Object obj) throws Exception {
                ExceptionalConsumer.lambda$empty$0(obj);
            }
        };
    }

    static /* synthetic */ void lambda$empty$0(Object s) throws Exception {
    }
}
