package com.brixcore.util.fakefx;

import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.util.function.ExceptionalFunction;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes15.dex */
public class ObservableCache<K, V, E extends Exception> {
    private final BiConsumer<K, Throwable> exceptionHandler;
    private final Executor executor;
    private final V fallbackValue;
    private final ExceptionalFunction<K, V, E> source;
    private final ObservableHelper observable = new ObservableHelper();
    private final Map<K, V> cache = new HashMap();
    private final Map<K, CompletableFuture<V>> pendings = new HashMap();
    private final Map<K, Boolean> invalidated = new HashMap();

    public ObservableCache(ExceptionalFunction<K, V, E> source, BiConsumer<K, Throwable> exceptionHandler, V fallbackValue, Executor executor) {
        this.source = source;
        this.exceptionHandler = exceptionHandler;
        this.fallbackValue = fallbackValue;
        this.executor = executor;
    }

    public Optional<V> getImmediately(K key) {
        Optional<V> optionalOfNullable;
        synchronized (this) {
            optionalOfNullable = Optional.ofNullable(this.cache.get(key));
        }
        return optionalOfNullable;
    }

    public void put(K key, V value) {
        synchronized (this) {
            this.cache.put(key, value);
            this.invalidated.remove(key);
        }
        this.observable.invalidate();
    }

    private CompletableFuture<V> query(final K key, Executor executor) {
        synchronized (this) {
            CompletableFuture<V> prev = this.pendings.get(key);
            if (prev != null) {
                return prev;
            }
            final CompletableFuture<V> future = new CompletableFuture<>();
            this.pendings.put(key, future);
            executor.execute(new Runnable() { // from class: com.brixcore.util.fakefx.ObservableCache$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$query$0(key, future);
                }
            });
            return future;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$query$0(Object key, CompletableFuture future) {
        try {
            V result = this.source.apply(key);
            synchronized (this) {
                this.cache.put(key, result);
                this.invalidated.remove(key);
                this.pendings.remove(key, future);
            }
            future.complete(result);
            this.observable.invalidate();
        } catch (Throwable ex) {
            synchronized (this) {
                this.pendings.remove(key);
                this.exceptionHandler.accept(key, ex);
                future.completeExceptionally(ex);
            }
        }
    }

    public V get(K key) {
        synchronized (this) {
            V cached = this.cache.get(key);
            if (cached != null && !this.invalidated.containsKey(key)) {
                return cached;
            }
            try {
                return query(key, new Executor() { // from class: com.brixcore.util.fakefx.ObservableCache$$ExternalSyntheticLambda2
                    @Override // java.util.concurrent.Executor
                    public final void execute(Runnable runnable) {
                        runnable.run();
                    }
                }).join();
            } catch (CancellationException | CompletionException e) {
                if (cached == null) {
                    return this.fallbackValue;
                }
                return cached;
            }
        }
    }

    public V getDirectly(K key) throws Exception {
        V result = this.source.apply(key);
        put(key, result);
        return result;
    }

    public ObjectBinding<V> binding(K key) {
        return binding(key, false);
    }

    public ObjectBinding<V> binding(final K key, final boolean quiet) {
        return Bindings.createObjectBinding(new Callable() { // from class: com.brixcore.util.fakefx.ObservableCache$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return this.f$0.lambda$binding$1(key, quiet);
            }
        }, this.observable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ Object lambda$binding$1(Object obj, boolean quiet) throws Exception {
        V result;
        boolean refresh;
        synchronized (this) {
            result = this.cache.get(obj);
            if (result == null) {
                result = this.fallbackValue;
                refresh = true;
            } else {
                refresh = this.invalidated.containsKey(obj);
            }
        }
        if (!quiet && refresh) {
            query(obj, this.executor);
        }
        return result;
    }

    public void invalidate(K key) {
        synchronized (this) {
            if (this.cache.containsKey(key)) {
                this.invalidated.put(key, Boolean.TRUE);
            }
        }
        this.observable.invalidate();
    }
}
