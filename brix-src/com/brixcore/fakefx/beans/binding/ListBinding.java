package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyBooleanPropertyBase;
import com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyIntegerPropertyBase;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.BindingHelperObserver;
import com.brixcore.fakefx.binding.ListExpressionHelper;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class ListBinding<E> extends ListExpression<E> implements Binding<ObservableList<E>> {
    private ListBinding<E>.EmptyProperty empty0;
    private BindingHelperObserver observer;
    private ListBinding<E>.SizeProperty size0;
    private ObservableList<E> value;
    private final ListChangeListener<E> listChangeListener = new ListChangeListener<E>() { // from class: com.brixcore.fakefx.beans.binding.ListBinding.1
        @Override // com.brixcore.fakefx.collections.ListChangeListener
        public void onChanged(ListChangeListener.Change<? extends E> change) {
            ListBinding.this.invalidateProperties();
            ListBinding.this.onInvalidating();
            ListExpressionHelper.fireValueChangedEvent(ListBinding.this.helper, change);
        }
    };
    private boolean valid = false;
    private ListExpressionHelper<E> helper = null;

    protected abstract ObservableList<E> computeValue();

    @Override // com.brixcore.fakefx.beans.binding.ListExpression, com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public /* bridge */ /* synthetic */ Object getValue2() {
        return super.getValue2();
    }

    @Override // com.brixcore.fakefx.beans.binding.ListExpression
    public ReadOnlyIntegerProperty sizeProperty() {
        if (this.size0 == null) {
            this.size0 = new SizeProperty();
        }
        return this.size0;
    }

    private class SizeProperty extends ReadOnlyIntegerPropertyBase {
        private SizeProperty() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableIntegerValue
        public int get() {
            return ListBinding.this.size();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ListBinding.this;
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return "size";
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyIntegerPropertyBase
        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.ListExpression
    public ReadOnlyBooleanProperty emptyProperty() {
        if (this.empty0 == null) {
            this.empty0 = new EmptyProperty();
        }
        return this.empty0;
    }

    private class EmptyProperty extends ReadOnlyBooleanPropertyBase {
        private EmptyProperty() {
        }

        @Override // com.brixcore.fakefx.beans.value.ObservableBooleanValue
        public boolean get() {
            return ListBinding.this.isEmpty();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return ListBinding.this;
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public String getName() {
            return "empty";
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyBooleanPropertyBase
        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = ListExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = ListExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super ObservableList<E>> listener) {
        this.helper = ListExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super ObservableList<E>> listener) {
        this.helper = ListExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public void addListener(ListChangeListener<? super E> listener) {
        this.helper = ListExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public void removeListener(ListChangeListener<? super E> listener) {
        this.helper = ListExpressionHelper.removeListener(this.helper, listener);
    }

    protected final void bind(Observable... dependencies) {
        if (dependencies != null && dependencies.length > 0) {
            if (this.observer == null) {
                this.observer = new BindingHelperObserver(this);
            }
            for (Observable dep : dependencies) {
                if (dep != null) {
                    dep.addListener(this.observer);
                }
            }
        }
    }

    protected final void unbind(Observable... dependencies) {
        if (this.observer != null) {
            for (Observable dep : dependencies) {
                if (dep != null) {
                    dep.removeListener(this.observer);
                }
            }
            this.observer = null;
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public void dispose() {
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public ObservableList<?> getDependencies() {
        return FXCollections.emptyObservableList();
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
    public final ObservableList<E> get() {
        if (!this.valid) {
            this.value = computeValue();
            this.valid = true;
            if (this.value != null) {
                this.value.addListener((ListChangeListener<? super E>) this.listChangeListener);
            }
        }
        return this.value;
    }

    protected void onInvalidating() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateProperties() {
        if (this.size0 != null) {
            this.size0.fireValueChangedEvent();
        }
        if (this.empty0 != null) {
            this.empty0.fireValueChangedEvent();
        }
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    @Override // com.brixcore.fakefx.beans.binding.Binding
    public final void invalidate() {
        if (this.valid) {
            if (this.value != null) {
                this.value.removeListener((ListChangeListener<? super E>) this.listChangeListener);
            }
            this.valid = false;
            invalidateProperties();
            onInvalidating();
            ListExpressionHelper.fireValueChangedEvent(this.helper);
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public final boolean isValid() {
        return this.valid;
    }

    public String toString() {
        return this.valid ? "ListBinding [value: " + get() + "]" : "ListBinding [invalid]";
    }
}
