package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import java.util.Objects;
import java.util.function.Function;

/* JADX INFO: loaded from: classes6.dex */
public class MappedBinding<S, T> extends LazyObjectBinding<T> {
    private final Function<? super S, ? extends T> mapper;
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

    public MappedBinding(ObservableValue<S> source, Function<? super S, ? extends T> mapper) {
        this.source = (ObservableValue) Objects.requireNonNull(source, "source cannot be null");
        this.mapper = (Function) Objects.requireNonNull(mapper, "mapper cannot be null");
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
    protected T computeValue() {
        S value = this.source.getValue2();
        if (value == null) {
            return null;
        }
        return this.mapper.apply(value);
    }

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding
    protected Subscription observeSources() {
        return Subscription.subscribeInvalidations(this.source, new Runnable() { // from class: com.brixcore.fakefx.binding.MappedBinding$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.invalidate();
            }
        });
    }
}
