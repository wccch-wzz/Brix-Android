package com.brixcore.util.fakefx;

import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.util.function.ExceptionalFunction;
import java.lang.Exception;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

/* JADX INFO: loaded from: classes15.dex */
public class ObservableOptionalCache<K, V, E extends Exception> {
    private final ObservableCache<K, Optional<V>, E> backed;

    public ObservableOptionalCache(ExceptionalFunction<K, Optional<V>, E> source, BiConsumer<K, Throwable> exceptionHandler, Executor executor) {
        this.backed = new ObservableCache<>(source, exceptionHandler, Optional.empty(), executor);
    }

    static /* synthetic */ Optional lambda$getImmediately$0(Optional it) {
        return it;
    }

    public Optional<V> getImmediately(K k) {
        return (Optional<V>) this.backed.getImmediately(k).flatMap(new Function() { // from class: com.brixcore.util.fakefx.ObservableOptionalCache$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ObservableOptionalCache.lambda$getImmediately$0((Optional) obj);
            }
        });
    }

    public void put(K key, V value) {
        this.backed.put(key, Optional.of(value));
    }

    public Optional<V> get(K key) {
        return this.backed.get(key);
    }

    public Optional<V> getDirectly(K key) throws Exception {
        return this.backed.getDirectly(key);
    }

    public ObjectBinding<Optional<V>> binding(K key) {
        return this.backed.binding(key);
    }

    public ObjectBinding<Optional<V>> binding(K key, boolean quiet) {
        return this.backed.binding(key, quiet);
    }

    public void invalidate(K key) {
        this.backed.invalidate(key);
    }
}
