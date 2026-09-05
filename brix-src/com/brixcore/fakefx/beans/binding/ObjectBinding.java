package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.BindingHelperObserver;
import com.brixcore.fakefx.binding.ExpressionHelper;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class ObjectBinding<T> extends ObjectExpression<T> implements Binding<T> {
    private BindingHelperObserver observer;
    private T value;
    private boolean valid = false;
    private ExpressionHelper<T> helper = null;

    protected abstract T computeValue();

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super T> listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super T> listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    protected final void bind(Observable... dependencies) {
        if (dependencies != null && dependencies.length > 0) {
            if (this.observer == null) {
                this.observer = new BindingHelperObserver(this);
            }
            for (Observable dep : dependencies) {
                dep.addListener(this.observer);
            }
        }
    }

    protected final void unbind(Observable... dependencies) {
        if (this.observer != null) {
            for (Observable dep : dependencies) {
                dep.removeListener(this.observer);
            }
            this.observer = null;
        }
    }

    public void dispose() {
    }

    public ObservableList<?> getDependencies() {
        return FXCollections.emptyObservableList();
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
    public final T get() {
        if (!this.valid) {
            T computed = computeValue();
            if (!allowValidation()) {
                return computed;
            }
            this.value = computed;
            this.valid = true;
        }
        return this.value;
    }

    protected void onInvalidating() {
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public final void invalidate() {
        if (this.valid) {
            this.valid = false;
            onInvalidating();
            ExpressionHelper.fireValueChangedEvent(this.helper);
            if (!this.valid) {
                this.value = null;
            }
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public final boolean isValid() {
        return this.valid;
    }

    protected final boolean isObserved() {
        return this.helper != null;
    }

    protected boolean allowValidation() {
        return true;
    }

    public String toString() {
        return this.valid ? "ObjectBinding [value: " + get() + "]" : "ObjectBinding [invalid]";
    }
}
