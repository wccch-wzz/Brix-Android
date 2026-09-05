package com.brixcore.util.fakefx;

import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.binding.Bindings;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.util.Lang;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes15.dex */
public abstract class BindingMapping<T, U> extends ObjectBinding<U> {
    protected final ObservableValue<? extends T> predecessor;

    public static <T> BindingMapping<?, T> of(ObservableValue<T> property) {
        if (property instanceof BindingMapping) {
            return (BindingMapping) property;
        }
        return new SimpleBinding(property);
    }

    public static <S extends Observable, T> BindingMapping<?, T> of(final S watched, final Function<S, T> mapper) {
        return of(Bindings.createObjectBinding(new Callable() { // from class: com.brixcore.util.fakefx.BindingMapping$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return mapper.apply(watched);
            }
        }, watched));
    }

    public BindingMapping(ObservableValue<? extends T> predecessor) {
        this.predecessor = (ObservableValue) Objects.requireNonNull(predecessor);
        bind(predecessor);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public <V> BindingMapping<?, V> map(Function<? super U, ? extends V> mapper) {
        return new MappedBinding(this, mapper);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public <V> BindingMapping<?, V> flatMap(Function<? super U, ? extends ObservableValue<? extends V>> mapper) {
        return flatMap(mapper, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <V> BindingMapping<?, V> flatMap(Function<? super U, ? extends ObservableValue<? extends V>> function, Supplier<? extends V> nullAlternative) {
        return new FlatMappedBinding(map((Function) function), nullAlternative);
    }

    public <V> BindingMapping<?, V> asyncMap(Function<U, CompletableFuture<V>> mapper, V initial) {
        return new AsyncMappedBinding(this, mapper, initial);
    }

    private static class SimpleBinding<T> extends BindingMapping<T, T> {
        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue flatMap(Function function) {
            return super.flatMap(function);
        }

        public SimpleBinding(ObservableValue<T> predecessor) {
            super(predecessor);
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected T computeValue() {
            return this.predecessor.getValue2();
        }

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public <V> BindingMapping<?, V> map(Function<? super T, ? extends V> mapper) {
            return new MappedBinding(this.predecessor, mapper);
        }

        @Override // com.brixcore.util.fakefx.BindingMapping
        public <V> BindingMapping<?, V> asyncMap(Function<T, CompletableFuture<V>> mapper, V initial) {
            return new AsyncMappedBinding(this.predecessor, mapper, initial);
        }
    }

    private static class MappedBinding<T, U> extends BindingMapping<T, U> {
        private final Function<? super T, ? extends U> mapper;

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue flatMap(Function function) {
            return super.flatMap(function);
        }

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue map(Function function) {
            return super.map(function);
        }

        public MappedBinding(ObservableValue<? extends T> predecessor, Function<? super T, ? extends U> mapper) {
            super(predecessor);
            this.mapper = mapper;
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected U computeValue() {
            return this.mapper.apply(this.predecessor.getValue2());
        }
    }

    private static class FlatMappedBinding<T extends ObservableValue<? extends U>, U> extends BindingMapping<T, U> {
        private T lastObservable;
        private final Supplier<? extends U> nullAlternative;

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue flatMap(Function function) {
            return super.flatMap(function);
        }

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue map(Function function) {
            return super.map(function);
        }

        public FlatMappedBinding(ObservableValue<? extends T> predecessor, Supplier<? extends U> nullAlternative) {
            super(predecessor);
            this.lastObservable = null;
            this.nullAlternative = nullAlternative;
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected U computeValue() {
            T value2 = this.predecessor.getValue2();
            if (value2 != this.lastObservable) {
                if (this.lastObservable != null) {
                    unbind(this.lastObservable);
                }
                if (value2 != null) {
                    bind(value2);
                }
                this.lastObservable = value2;
            }
            if (value2 == null) {
                if (this.nullAlternative == null) {
                    throw new NullPointerException();
                }
                return this.nullAlternative.get();
            }
            return (U) value2.getValue2();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class AsyncMappedBinding<T, U> extends BindingMapping<T, U> {
        private boolean computing;
        private T computingPrev;
        private boolean initialized;
        private final Function<? super T, ? extends CompletableFuture<? extends U>> mapper;
        private T prev;
        private U value;

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue flatMap(Function function) {
            return super.flatMap(function);
        }

        @Override // com.brixcore.util.fakefx.BindingMapping, com.brixcore.fakefx.beans.value.ObservableValue
        public /* bridge */ /* synthetic */ ObservableValue map(Function function) {
            return super.map(function);
        }

        public AsyncMappedBinding(ObservableValue<? extends T> predecessor, Function<? super T, ? extends CompletableFuture<? extends U>> mapper, U initial) {
            super(predecessor);
            this.initialized = false;
            this.computing = false;
            this.value = initial;
            this.mapper = mapper;
        }

        private void tryUpdateValue(final T currentPrev) {
            synchronized (this) {
                if ((this.initialized && Objects.equals(this.prev, currentPrev)) || isComputing(currentPrev)) {
                    return;
                }
                this.computing = true;
                this.computingPrev = currentPrev;
                try {
                    CompletableFuture<? extends U> task = (CompletableFuture) Objects.requireNonNull(this.mapper.apply(currentPrev));
                    task.handle((BiFunction<? super Object, Throwable, ? extends U>) new BiFunction() { // from class: com.brixcore.util.fakefx.BindingMapping$AsyncMappedBinding$$ExternalSyntheticLambda0
                        @Override // java.util.function.BiFunction
                        public final Object apply(Object obj, Object obj2) {
                            return this.f$0.lambda$tryUpdateValue$0(currentPrev, obj, (Throwable) obj2);
                        }
                    });
                } catch (Throwable e) {
                    valueUpdateFailed(currentPrev);
                    throw e;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ Object lambda$tryUpdateValue$0(Object obj, Object obj2, Throwable e) {
            if (e == null) {
                valueUpdate(obj, obj2);
                invalidate();
                return null;
            }
            Lang.handleUncaughtException(e);
            valueUpdateFailed(obj);
            return null;
        }

        private void valueUpdate(T currentPrev, U computed) {
            synchronized (this) {
                if (isComputing(currentPrev)) {
                    this.computing = false;
                    this.computingPrev = null;
                    this.prev = currentPrev;
                    this.value = computed;
                    this.initialized = true;
                }
            }
        }

        private void valueUpdateFailed(T currentPrev) {
            synchronized (this) {
                if (isComputing(currentPrev)) {
                    this.computing = false;
                    this.computingPrev = null;
                }
            }
        }

        private boolean isComputing(T prev) {
            return this.computing && Objects.equals(prev, this.computingPrev);
        }

        @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
        protected U computeValue() {
            tryUpdateValue(this.predecessor.getValue2());
            return this.value;
        }
    }
}
