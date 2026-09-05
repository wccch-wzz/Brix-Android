package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import java.util.Objects;
import java.util.function.Function;

/* JADX INFO: loaded from: classes6.dex */
public class FlatMappedBinding<S, T> extends LazyObjectBinding<T> {
    private ObservableValue<? extends T> indirectSource;
    private Subscription indirectSourceSubscription = Subscription.EMPTY;
    private final Function<? super S, ? extends ObservableValue<? extends T>> mapper;
    private final ObservableValue<S> source;

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding, com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.Observable
    public /* bridge */ /* synthetic */ void addListener(InvalidationListener invalidationListener) {
        super.addListener(invalidationListener);
    }

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding, com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.value.ObservableValue
    public /* bridge */ /* synthetic */ void addListener(ChangeListener changeListener) {
        super.addListener(changeListener);
    }

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding, com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.Observable
    public /* bridge */ /* synthetic */ void removeListener(InvalidationListener invalidationListener) {
        super.removeListener(invalidationListener);
    }

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding, com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.value.ObservableValue
    public /* bridge */ /* synthetic */ void removeListener(ChangeListener changeListener) {
        super.removeListener(changeListener);
    }

    public FlatMappedBinding(ObservableValue<S> source, Function<? super S, ? extends ObservableValue<? extends T>> mapper) {
        this.source = (ObservableValue) Objects.requireNonNull(source, "source cannot be null");
        this.mapper = (Function) Objects.requireNonNull(mapper, "mapper cannot be null");
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
    protected T computeValue() {
        S value = this.source.getValue2();
        ObservableValue<? extends T> newIndirectSource = value == null ? null : this.mapper.apply(value);
        if (isObserved() && this.indirectSource != newIndirectSource) {
            this.indirectSourceSubscription.unsubscribe();
            this.indirectSourceSubscription = newIndirectSource == null ? Subscription.EMPTY : Subscription.subscribeInvalidations(newIndirectSource, new Runnable() { // from class: com.brixcore.fakefx.binding.FlatMappedBinding$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.invalidate();
                }
            });
            this.indirectSource = newIndirectSource;
        }
        if (newIndirectSource == null) {
            return null;
        }
        return newIndirectSource.getValue2();
    }

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding
    protected Subscription observeSources() {
        final Subscription subscription = Subscription.subscribeInvalidations(this.source, new Runnable() { // from class: com.brixcore.fakefx.binding.FlatMappedBinding$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.invalidateAll();
            }
        });
        return new Subscription() { // from class: com.brixcore.fakefx.binding.FlatMappedBinding$$ExternalSyntheticLambda1
            @Override // com.brixcore.fakefx.binding.Subscription
            public final void unsubscribe() {
                this.f$0.lambda$observeSources$0(subscription);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$observeSources$0(Subscription subscription) {
        subscription.unsubscribe();
        unsubscribeIndirectSource();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateAll() {
        unsubscribeIndirectSource();
        invalidate();
    }

    private void unsubscribeIndirectSource() {
        this.indirectSourceSubscription.unsubscribe();
        this.indirectSourceSubscription = Subscription.EMPTY;
        this.indirectSource = null;
    }
}
