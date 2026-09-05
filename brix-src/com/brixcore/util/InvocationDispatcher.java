package com.brixcore.util;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes11.dex */
public final class InvocationDispatcher<T> implements Consumer<T> {
    private final Consumer<T> action;
    private final Executor executor;
    private final AtomicReference<Holder<T>> pendingArg = new AtomicReference<>();

    public static <T> InvocationDispatcher<T> runOn(Executor executor, Consumer<T> action) {
        return new InvocationDispatcher<>(executor, action);
    }

    private InvocationDispatcher(Executor executor, Consumer<T> action) {
        this.executor = executor;
        this.action = action;
    }

    @Override // java.util.function.Consumer
    public void accept(T t) {
        if (this.pendingArg.getAndSet(new Holder<>(t)) == null) {
            this.executor.execute(new Runnable() { // from class: com.brixcore.util.InvocationDispatcher$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$accept$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$accept$0() {
        synchronized (this) {
            this.action.accept(this.pendingArg.getAndSet(null).value);
        }
    }
}
