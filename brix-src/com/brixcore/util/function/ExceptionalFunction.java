package com.brixcore.util.function;

import java.lang.Exception;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalFunction<T, R, E extends Exception> {
    R apply(T t) throws Exception;

    static <T, E extends RuntimeException> ExceptionalFunction<T, T, E> identity() {
        return new ExceptionalFunction() { // from class: com.brixcore.util.function.ExceptionalFunction$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return ExceptionalFunction.lambda$identity$0(obj);
            }
        };
    }

    static /* synthetic */ Object lambda$identity$0(Object t) throws RuntimeException {
        return t;
    }
}
