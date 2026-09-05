package com.brixcore.util.function;

import java.lang.Exception;
import java.util.Objects;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalBiConsumer<T, U, E extends Exception> {
    void accept(T t, U u) throws Exception;

    default ExceptionalBiConsumer<T, U, ?> andThen(final ExceptionalBiConsumer<? super T, ? super U, ?> after) {
        Objects.requireNonNull(after);
        return new ExceptionalBiConsumer() { // from class: com.brixcore.util.function.ExceptionalBiConsumer$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalBiConsumer
            public final void accept(Object obj, Object obj2) throws Exception {
                this.f$0.lambda$andThen$0(after, obj, obj2);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* synthetic */ default void lambda$andThen$0(ExceptionalBiConsumer after, Object obj, Object obj2) throws Exception {
        accept(obj, obj2);
        after.accept(obj, obj2);
    }
}
