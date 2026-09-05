package com.brixcore.util.function;

import java.lang.Exception;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalSupplier<R, E extends Exception> {
    R get() throws Exception;

    default Callable<R> toCallable() {
        return new Callable() { // from class: com.brixcore.util.function.ExceptionalSupplier$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return this.f$0.get();
            }
        };
    }
}
