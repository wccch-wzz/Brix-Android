package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.binding.BooleanBinding;
import com.brixcore.fakefx.beans.binding.ObjectExpression;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableBooleanValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes4.dex */
public abstract class BooleanPropertyBase extends BooleanProperty {
    private boolean value;
    private ObservableBooleanValue observable = null;
    private InvalidationListener listener = null;
    private boolean valid = true;
    private ExpressionHelper<Boolean> helper = null;

    @Override // com.brixcore.fakefx.beans.property.BooleanProperty, com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty, com.brixcore.fakefx.beans.binding.BooleanExpression
    public /* bridge */ /* synthetic */ ObjectExpression asObject() {
        return super.asObject();
    }

    @Override // com.brixcore.fakefx.beans.property.BooleanProperty, com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty, com.brixcore.fakefx.beans.binding.BooleanExpression
    public /* bridge */ /* synthetic */ ReadOnlyObjectProperty asObject() {
        return super.asObject();
    }

    @Override // com.brixcore.fakefx.beans.property.BooleanProperty, com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public /* bridge */ /* synthetic */ void setValue(Boolean bool) {
        super.setValue(bool);
    }

    public BooleanPropertyBase() {
    }

    public BooleanPropertyBase(boolean initialValue) {
        this.value = initialValue;
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super Boolean> listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super Boolean> listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    protected void fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(this.helper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markInvalid() {
        if (this.valid) {
            this.valid = false;
            invalidated();
            fireValueChangedEvent();
        }
    }

    protected void invalidated() {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableBooleanValue
    public boolean get() {
        this.valid = true;
        return this.observable == null ? this.value : this.observable.get();
    }

    @Override // com.brixcore.fakefx.beans.value.WritableBooleanValue
    public void set(boolean newValue) {
        if (isBound()) {
            throw new RuntimeException(((getBean() == null || getName() == null) ? "" : getBean().getClass().getSimpleName() + "." + getName() + " : ") + "A bound value cannot be set.");
        }
        if (this.value != newValue) {
            this.value = newValue;
            markInvalid();
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public boolean isBound() {
        return this.observable != null;
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bind(ObservableValue<? extends Boolean> rawObservable) {
        if (rawObservable == null) {
            throw new NullPointerException("Cannot bind to null");
        }
        ObservableBooleanValue newObservable = rawObservable instanceof ObservableBooleanValue ? (ObservableBooleanValue) rawObservable : new ValueWrapper(rawObservable);
        if (!newObservable.equals(this.observable)) {
            unbind();
            this.observable = newObservable;
            if (this.listener == null) {
                this.listener = new Listener(this);
            }
            this.observable.addListener(this.listener);
            markInvalid();
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void unbind() {
        if (this.observable != null) {
            this.value = this.observable.get();
            this.observable.removeListener(this.listener);
            if (this.observable instanceof ValueWrapper) {
                ((ValueWrapper) this.observable).dispose();
            }
            this.observable = null;
        }
    }

    @Override // com.brixcore.fakefx.beans.property.BooleanProperty, com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("BooleanProperty [");
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

    private static class Listener implements InvalidationListener, WeakListener {
        private final WeakReference<BooleanPropertyBase> wref;

        public Listener(BooleanPropertyBase ref) {
            this.wref = new WeakReference<>(ref);
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            BooleanPropertyBase ref = this.wref.get();
            if (ref == null) {
                observable.removeListener(this);
            } else {
                ref.markInvalid();
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.wref.get() == null;
        }
    }

    private class ValueWrapper extends BooleanBinding {
        private ObservableValue<? extends Boolean> observable;

        public ValueWrapper(ObservableValue<? extends Boolean> observable) {
            this.observable = observable;
            bind(observable);
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding
        protected boolean computeValue() {
            Boolean value = this.observable.getValue2();
            if (value == null) {
                return false;
            }
            return value.booleanValue();
        }

        @Override // com.brixcore.fakefx.beans.binding.BooleanBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            unbind(this.observable);
        }
    }
}
