package com.brixcore.util.function;

import java.lang.Exception;
import java.util.Objects;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalBiFunction<T, U, R, E extends Exception> {
    R apply(T t, U u) throws Exception;

    default <V> ExceptionalBiFunction<T, U, V, ?> andThen(final ExceptionalFunction<? super R, ? extends V, ?> after) {
        Objects.requireNonNull(after);
        return new ExceptionalBiFunction() { // from class: com.brixcore.util.function.ExceptionalBiFunction$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalBiFunction
            public final Object apply(Object obj, Object obj2) {
                return this.f$0.lambda$andThen$0(after, obj, obj2);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* synthetic */ default Object lambda$andThen$0(ExceptionalFunction after, Object obj, Object obj2) throws Exception {
        return after.apply(apply(obj, obj2));
    }
}
