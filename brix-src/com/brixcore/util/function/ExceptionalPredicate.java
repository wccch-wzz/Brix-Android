package com.brixcore.util.function;

import java.lang.Exception;

/* JADX INFO: loaded from: classes6.dex */
public interface ExceptionalPredicate<T, E extends Exception> {
    boolean test(T t) throws Exception;
}
