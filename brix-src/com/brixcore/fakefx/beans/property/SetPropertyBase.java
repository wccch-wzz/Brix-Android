package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.SetExpressionHelper;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.collections.SetChangeListener;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes4.dex */
public abstract class SetPropertyBase<E> extends SetProperty<E> {
    private SetPropertyBase<E>.EmptyProperty empty0;
    private SetPropertyBase<E>.SizeProperty size0;
    private ObservableSet<E> value;
    private final SetChangeListener<E> setChangeListener = new SetChangeListener() { // from class: com.brixcore.fakefx.beans.property.SetPropertyBase$$ExternalSyntheticLambda0
        @Override // com.brixcore.fakefx.collections.SetChangeListener
        public final void onChanged(SetChangeListener.Change change) {
            this.f$0.lambda$new$0(change);
        }
    };
    private ObservableValue<? extends ObservableSet<E>> observable = null;
    private InvalidationListener listener = null;
    private boolean valid = true;
    private SetExpressionHelper<E> helper = null;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(SetChangeListener.Change change) {
        invalidateProperties();
        invalidated();
        fireValueChangedEvent(change);
    }

    public SetPropertyBase() {
    }

    public SetPropertyBase(ObservableSet<E> initialValue) {
        this.value = initialValue;
        if (initialValue != null) {
            initialValue.addListener(this.setChangeListener);
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.SetExpression
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
            return SetPropertyBase.this.size();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return SetPropertyBase.this;
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

    @Override // com.brixcore.fakefx.beans.binding.SetExpression
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
            return SetPropertyBase.this.isEmpty();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return SetPropertyBase.this;
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
        this.helper = SetExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = SetExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
        this.helper = SetExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super ObservableSet<E>> listener) {
        this.helper = SetExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableSet
    public void addListener(SetChangeListener<? super E> listener) {
        this.helper = SetExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableSet
    public void removeListener(SetChangeListener<? super E> listener) {
        this.helper = SetExpressionHelper.removeListener(this.helper, listener);
    }

    protected void fireValueChangedEvent() {
        SetExpressionHelper.fireValueChangedEvent(this.helper);
    }

    protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> change) {
        SetExpressionHelper.fireValueChangedEvent(this.helper, change);
    }

    private void invalidateProperties() {
        if (this.size0 != null) {
            this.size0.fireValueChangedEvent();
        }
        if (this.empty0 != null) {
            this.empty0.fireValueChangedEvent();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markInvalid(ObservableSet<E> oldValue) {
        if (this.valid) {
            if (oldValue != null) {
                oldValue.removeListener(this.setChangeListener);
            }
            this.valid = false;
            invalidateProperties();
            invalidated();
            fireValueChangedEvent();
        }
    }

    protected void invalidated() {
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
    public ObservableSet<E> get() {
        if (!this.valid) {
            this.value = this.observable == null ? this.value : this.observable.getValue2();
            this.valid = true;
            if (this.value != null) {
                this.value.addListener((SetChangeListener<? super E>) this.setChangeListener);
            }
        }
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.WritableObjectValue
    public void set(ObservableSet<E> newValue) {
        if (isBound()) {
            throw new RuntimeException(((getBean() == null || getName() == null) ? "" : getBean().getClass().getSimpleName() + "." + getName() + " : ") + "A bound value cannot be set.");
        }
        if (this.value != newValue) {
            ObservableSet<E> oldValue = this.value;
            this.value = newValue;
            markInvalid(oldValue);
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public boolean isBound() {
        return this.observable != null;
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bind(ObservableValue<? extends ObservableSet<E>> newObservable) {
        if (newObservable == null) {
            throw new NullPointerException("Cannot bind to null");
        }
        if (newObservable != this.observable) {
            unbind();
            this.observable = newObservable;
            if (this.listener == null) {
                this.listener = new Listener(this);
            }
            this.observable.addListener(this.listener);
            markInvalid(this.value);
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbind() {
        if (this.observable != null) {
            this.value = this.observable.getValue2();
            this.observable.removeListener(this.listener);
            this.observable = null;
        }
    }

    @Override // com.brixcore.fakefx.beans.property.SetProperty, com.brixcore.fakefx.beans.property.ReadOnlySetProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("SetProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if (name != null && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        if (isBound()) {
            result.append("bound, ");
            if (this.valid) {
                result.append("value: ").append(get());
            } else {
                result.append("invalid");
            }
        } else {
            result.append("value: ").append(get());
        }
        result.append("]");
        return result.toString();
    }

    private static class Listener<E> implements InvalidationListener, WeakListener {
        private final WeakReference<SetPropertyBase<E>> wref;

        public Listener(SetPropertyBase<E> ref) {
            this.wref = new WeakReference<>(ref);
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            SetPropertyBase<E> ref = this.wref.get();
            if (ref == null) {
                observable.removeListener(this);
            } else {
                ref.markInvalid(((SetPropertyBase) ref).value);
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.wref.get() == null;
        }
    }
}
