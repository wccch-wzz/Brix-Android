package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import java.util.Objects;

/* JADX INFO: loaded from: classes6.dex */
public class OrElseBinding<T> extends LazyObjectBinding<T> {
    private final T constant;
    private final ObservableValue<T> source;

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

    public OrElseBinding(ObservableValue<T> source, T constant) {
        this.source = (ObservableValue) Objects.requireNonNull(source, "source cannot be null");
        this.constant = constant;
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
    protected T computeValue() {
        T value = this.source.getValue2();
        return value == null ? this.constant : value;
    }

    @Override // com.brixcore.fakefx.binding.LazyObjectBinding
    protected Subscription observeSources() {
        return Subscription.subscribeInvalidations(this.source, new Runnable() { // from class: com.brixcore.fakefx.binding.OrElseBinding$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.invalidate();
            }
        });
    }
}
