package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.binding.DoubleBinding;
import com.brixcore.fakefx.beans.binding.ObjectExpression;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableDoubleValue;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes4.dex */
public abstract class DoublePropertyBase extends DoubleProperty {
    private double value;
    private ObservableDoubleValue observable = null;
    private InvalidationListener listener = null;
    private boolean valid = true;
    private ExpressionHelper<Number> helper = null;

    @Override // com.brixcore.fakefx.beans.property.DoubleProperty, com.brixcore.fakefx.beans.property.ReadOnlyDoubleProperty, com.brixcore.fakefx.beans.binding.DoubleExpression
    public /* bridge */ /* synthetic */ ObjectExpression asObject() {
        return super.asObject();
    }

    @Override // com.brixcore.fakefx.beans.property.DoubleProperty, com.brixcore.fakefx.beans.property.ReadOnlyDoubleProperty, com.brixcore.fakefx.beans.binding.DoubleExpression
    public /* bridge */ /* synthetic */ ReadOnlyObjectProperty asObject() {
        return super.asObject();
    }

    @Override // com.brixcore.fakefx.beans.property.DoubleProperty, com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    public /* bridge */ /* synthetic */ void setValue(Number number) {
        super.setValue(number);
    }

    public DoublePropertyBase() {
    }

    public DoublePropertyBase(double initialValue) {
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
    public void addListener(ChangeListener<? super Number> listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super Number> listener) {
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

    @Override // com.brixcore.fakefx.beans.value.ObservableDoubleValue
    public double get() {
        this.valid = true;
        return this.observable == null ? this.value : this.observable.get();
    }

    @Override // com.brixcore.fakefx.beans.value.WritableDoubleValue
    public void set(double newValue) {
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
    public void bind(final ObservableValue<? extends Number> rawObservable) {
        ObservableDoubleValue newObservable;
        if (rawObservable == null) {
            throw new NullPointerException("Cannot bind to null");
        }
        if (rawObservable instanceof ObservableDoubleValue) {
            newObservable = (ObservableDoubleValue) rawObservable;
        } else if (rawObservable instanceof ObservableNumberValue) {
            final ObservableNumberValue numberValue = (ObservableNumberValue) rawObservable;
            newObservable = new ValueWrapper(rawObservable) { // from class: com.brixcore.fakefx.beans.property.DoublePropertyBase.1
                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    return numberValue.doubleValue();
                }
            };
        } else {
            newObservable = new ValueWrapper(rawObservable) { // from class: com.brixcore.fakefx.beans.property.DoublePropertyBase.2
                @Override // com.brixcore.fakefx.beans.binding.DoubleBinding
                protected double computeValue() {
                    Number value = (Number) rawObservable.getValue2();
                    if (value == null) {
                        return 0.0d;
                    }
                    return value.doubleValue();
                }
            };
        }
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

    @Override // com.brixcore.fakefx.beans.property.DoubleProperty, com.brixcore.fakefx.beans.property.ReadOnlyDoubleProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("DoubleProperty [");
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
        private final WeakReference<DoublePropertyBase> wref;

        public Listener(DoublePropertyBase ref) {
            this.wref = new WeakReference<>(ref);
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            DoublePropertyBase ref = this.wref.get();
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

    private abstract class ValueWrapper extends DoubleBinding {
        private ObservableValue<? extends Number> observable;

        public ValueWrapper(ObservableValue<? extends Number> observable) {
            this.observable = observable;
            bind(observable);
        }

        @Override // com.brixcore.fakefx.beans.binding.DoubleBinding, com.brixcore.fakefx.beans.binding.Binding
        public void dispose() {
            unbind(this.observable);
        }
    }
}
