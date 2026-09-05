package com.brixcore.util;

import java.util.function.Consumer;

/* JADX INFO: loaded from: classes11.dex */
@FunctionalInterface
public interface FutureCallback<T> {
    void call(T t, Runnable runnable, Consumer<String> consumer);
}
