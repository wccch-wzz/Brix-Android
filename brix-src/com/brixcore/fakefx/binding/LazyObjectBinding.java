package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.binding.ObjectBinding;
import com.brixcore.fakefx.beans.value.ChangeListener;

/* JADX INFO: loaded from: classes6.dex */
abstract class LazyObjectBinding<T> extends ObjectBinding<T> {
    private Subscription subscription;
    private boolean wasObserved;

    protected abstract Subscription observeSources();

    LazyObjectBinding() {
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super T> listener) {
        super.addListener(listener);
        updateSubscriptionAfterAdd();
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super T> listener) {
        super.removeListener(listener);
        updateSubscriptionAfterRemove();
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        super.addListener(listener);
        updateSubscriptionAfterAdd();
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding, com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        super.removeListener(listener);
        updateSubscriptionAfterRemove();
    }

    @Override // com.brixcore.fakefx.beans.binding.ObjectBinding
    protected boolean allowValidation() {
        return isObserved();
    }

    private void updateSubscriptionAfterAdd() {
        if (!this.wasObserved) {
            this.subscription = observeSources();
            get();
            this.wasObserved = true;
        }
    }

    private void updateSubscriptionAfterRemove() {
        if (this.wasObserved && !isObserved()) {
            this.subscription.unsubscribe();
            this.subscription = null;
            invalidate();
            this.wasObserved = false;
        }
    }
}
