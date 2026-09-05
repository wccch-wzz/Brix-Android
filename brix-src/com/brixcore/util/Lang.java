package com.brixcore.util;

import com.brixcore.util.function.ExceptionalBiConsumer;
import com.brixcore.util.function.ExceptionalConsumer;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.function.ExceptionalRunnable;
import com.brixcore.util.function.ExceptionalSupplier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes11.dex */
public final class Lang {
    public static final Function<Throwable, Void> handleUncaught = new Function() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda10
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Lang.lambda$static$10((Throwable) obj);
        }
    };
    private static Timer timer;

    private Lang() {
    }

    public static <T> T requireNonNullElse(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> T requireNonNullElseGet(T value, Supplier<? extends T> defaultValue) {
        return value != null ? value : defaultValue.get();
    }

    public static <T, U> U requireNonNullElseGet(T value, Function<? super T, ? extends U> mapper, Supplier<? extends U> defaultValue) {
        return value != null ? mapper.apply(value) : defaultValue.get();
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs) {
        return mapOf(Arrays.asList(pairs));
    }

    public static <K, V> Map<K, V> mapOf(Iterable<Pair<K, V>> pairs) {
        Map<K, V> map = new LinkedHashMap<>();
        for (Pair<K, V> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    }

    @SafeVarargs
    public static <T> List<T> immutableListOf(T... elements) {
        return Collections.unmodifiableList(Arrays.asList(elements));
    }

    public static <T extends Comparable<T>> T clamp(T min, T val, T max) {
        if (val.compareTo(min) < 0) {
            return min;
        }
        return val.compareTo(max) > 0 ? max : val;
    }

    public static double clamp(double min, double val, double max) {
        return Math.max(min, Math.min(val, max));
    }

    public static int clamp(int min, int val, int max) {
        return Math.max(min, Math.min(val, max));
    }

    public static boolean test(ExceptionalRunnable<?> r) {
        try {
            r.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <E extends Exception> boolean test(ExceptionalSupplier<Boolean, E> r) {
        try {
            return r.get().booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> T ignoringException(ExceptionalSupplier<T, ?> exceptionalSupplier) {
        return (T) ignoringException(exceptionalSupplier, null);
    }

    public static <T> T ignoringException(ExceptionalSupplier<T, ?> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static <V> Optional<V> tryCast(Object obj, Class<V> clazz) {
        if (clazz.isInstance(obj)) {
            return Optional.of(clazz.cast(obj));
        }
        return Optional.empty();
    }

    public static <T> T getOrDefault(List<T> a, int index, T defaultValue) {
        return (index < 0 || index >= a.size()) ? defaultValue : a.get(index);
    }

    public static <T> T merge(T t, T t2, BinaryOperator<T> binaryOperator) {
        if (t == null) {
            return t2;
        }
        return t2 == null ? t : (T) binaryOperator.apply(t, t2);
    }

    public static <T> List<T> removingDuplicates(List<T> list) {
        LinkedHashSet<T> set = new LinkedHashSet<>(list.size());
        set.addAll(list);
        return new ArrayList(set);
    }

    public static <T> List<T> merge(Collection<? extends T> a, Collection<? extends T> b) {
        List<T> result = new ArrayList<>();
        if (a != null) {
            result.addAll(a);
        }
        if (b != null) {
            result.addAll(b);
        }
        return result;
    }

    public static <T> List<T> copyList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return new ArrayList(list);
    }

    public static void executeDelayed(final Runnable runnable, final TimeUnit timeUnit, final long timeout, boolean isDaemon) {
        thread(new Runnable() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                Lang.lambda$executeDelayed$0(timeUnit, timeout, runnable);
            }
        }, null, isDaemon);
    }

    static /* synthetic */ void lambda$executeDelayed$0(TimeUnit timeUnit, long timeout, Runnable runnable) {
        try {
            timeUnit.sleep(timeout);
            runnable.run();
        } catch (InterruptedException e) {
        }
    }

    public static Thread thread(Runnable runnable) {
        return thread(runnable, null);
    }

    public static Thread thread(Runnable runnable, String name) {
        return thread(runnable, name, false);
    }

    public static Thread thread(Runnable runnable, String name, boolean isDaemon) {
        Thread thread = new Thread(runnable);
        if (isDaemon) {
            thread.setDaemon(true);
        }
        if (name != null) {
            thread.setName(name);
        }
        thread.start();
        return thread;
    }

    public static ThreadPoolExecutor threadPool(final String name, final boolean daemon, int threads, long timeout, TimeUnit timeunit) {
        final AtomicInteger counter = new AtomicInteger(1);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(threads, threads, timeout, timeunit, new LinkedBlockingQueue(), new ThreadFactory() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return Lang.lambda$threadPool$1(name, counter, daemon, runnable);
            }
        });
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }

    static /* synthetic */ Thread lambda$threadPool$1(String name, AtomicInteger counter, boolean daemon, Runnable r) {
        Thread t = new Thread(r, name + "-" + counter.getAndIncrement());
        t.setDaemon(daemon);
        return t;
    }

    public static int parseInt(Object string, int defaultValue) {
        try {
            return Integer.parseInt(string.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Integer toIntOrNull(Object string) {
        if (string == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(string.toString()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double toDoubleOrNull(Object string) {
        if (string == null) {
            return null;
        }
        try {
            return Double.valueOf(Double.parseDouble(string.toString()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SafeVarargs
    public static <T> T nonNull(T... t) {
        for (T a : t) {
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    public static <T> T apply(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static void rethrow(Throwable e) {
        if (e == null) {
            return;
        }
        if ((e instanceof ExecutionException) || (e instanceof CompletionException)) {
            rethrow(e.getCause());
        } else {
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new CompletionException(e);
        }
    }

    public static Runnable wrap(final ExceptionalRunnable<?> runnable) {
        return new Runnable() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                Lang.lambda$wrap$2(runnable);
            }
        };
    }

    static /* synthetic */ void lambda$wrap$2(ExceptionalRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            rethrow(e);
        }
    }

    public static <T> Supplier<T> wrap(final ExceptionalSupplier<T, ?> supplier) {
        return new Supplier() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return Lang.lambda$wrap$3(supplier);
            }
        };
    }

    static /* synthetic */ Object lambda$wrap$3(ExceptionalSupplier supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            rethrow(e);
            throw new InternalError("Unreachable code");
        }
    }

    public static <T, R> Function<T, R> wrap(final ExceptionalFunction<T, R, ?> fn) {
        return new Function() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Lang.lambda$wrap$4(fn, obj);
            }
        };
    }

    static /* synthetic */ Object lambda$wrap$4(ExceptionalFunction fn, Object t) {
        try {
            return fn.apply(t);
        } catch (Exception e) {
            rethrow(e);
            throw new InternalError("Unreachable code");
        }
    }

    public static <T> Consumer<T> wrapConsumer(final ExceptionalConsumer<T, ?> fn) {
        return new Consumer() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Lang.lambda$wrapConsumer$5(fn, obj);
            }
        };
    }

    static /* synthetic */ void lambda$wrapConsumer$5(ExceptionalConsumer fn, Object t) {
        try {
            fn.accept(t);
        } catch (Exception e) {
            rethrow(e);
        }
    }

    public static <T, E> BiConsumer<T, E> wrap(final ExceptionalBiConsumer<T, E, ?> fn) {
        return new BiConsumer() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Lang.lambda$wrap$6(fn, obj, obj2);
            }
        };
    }

    static /* synthetic */ void lambda$wrap$6(ExceptionalBiConsumer fn, Object t, Object e) {
        try {
            fn.accept(t, e);
        } catch (Exception ex) {
            rethrow(ex);
        }
    }

    @SafeVarargs
    public static <T> Consumer<T> compose(final Consumer<T>... consumers) {
        return new Consumer() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda13
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Lang.lambda$compose$7(consumers, obj);
            }
        };
    }

    static /* synthetic */ void lambda$compose$7(Consumer[] consumers, Object t) {
        for (Consumer consumer : consumers) {
            consumer.accept(t);
        }
    }

    public static <T> Stream<T> toStream(Optional<T> optional) {
        return (Stream) optional.map(new Function() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Stream.of(obj);
            }
        }).orElseGet(new Supplier() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda12
            @Override // java.util.function.Supplier
            public final Object get() {
                return Stream.empty();
            }
        });
    }

    public static <T> Iterable<T> toIterable(final Enumeration<T> enumeration) {
        if (enumeration == null) {
            throw new NullPointerException();
        }
        return new Iterable() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda7
            @Override // java.lang.Iterable
            public final Iterator iterator() {
                return Lang.lambda$toIterable$8(enumeration);
            }
        };
    }

    static /* synthetic */ Iterator lambda$toIterable$8(final Enumeration enumeration) {
        return new Iterator<T>() { // from class: com.brixcore.util.Lang.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override // java.util.Iterator
            public T next() {
                return (T) enumeration.nextElement();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterable<T> toIterable(final Stream<T> stream) {
        Objects.requireNonNull(stream);
        return new Iterable() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda3
            @Override // java.lang.Iterable
            public final Iterator iterator() {
                return stream.iterator();
            }
        };
    }

    static /* synthetic */ Iterator lambda$toIterable$9(Iterator iterator) {
        return iterator;
    }

    public static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
        return new Iterable() { // from class: com.brixcore.util.Lang$$ExternalSyntheticLambda4
            @Override // java.lang.Iterable
            public final Iterator iterator() {
                return Lang.lambda$toIterable$9(iterator);
            }
        };
    }

    public static <T, U> void forEachZipped(Iterable<T> i1, Iterable<U> i2, BiConsumer<T, U> action) {
        Iterator<T> it1 = i1.iterator();
        Iterator<U> it2 = i2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            action.accept(it1.next(), it2.next());
        }
    }

    public static synchronized Timer getTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        return timer;
    }

    public static synchronized TimerTask setTimeout(final Runnable runnable, long delayMs) {
        TimerTask task;
        task = new TimerTask() { // from class: com.brixcore.util.Lang.2
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                runnable.run();
            }
        };
        getTimer().schedule(task, delayMs);
        return task;
    }

    public static Throwable resolveException(Throwable e) {
        if ((e instanceof ExecutionException) || (e instanceof CompletionException)) {
            return resolveException(e.getCause());
        }
        return e;
    }

    static /* synthetic */ Void lambda$static$10(Throwable e) {
        handleUncaughtException(e);
        return null;
    }

    public static <R> R handleUncaughtException(Throwable e) {
        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        return null;
    }
}
