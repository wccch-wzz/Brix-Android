package com.brixcore.util.function;

import java.lang.Exception;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalRunnable<E extends Exception> {
    void run() throws Exception;

    default Callable<Void> toCallable() {
        return new Callable() { // from class: com.brixcore.util.function.ExceptionalRunnable$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return this.f$0.lambda$toCallable$0();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* synthetic */ default Void lambda$toCallable$0() throws Exception {
        run();
        return null;
    }
}
