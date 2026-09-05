package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableNumberValue;
import com.brixcore.fakefx.binding.BindingHelperObserver;
import com.brixcore.fakefx.binding.ExpressionHelper;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;

/* JADX INFO: loaded from: classes16.dex */
public abstract class DoubleBinding extends DoubleExpression implements NumberBinding {
    private ExpressionHelper<Number> helper = null;
    private BindingHelperObserver observer;
    private boolean valid;
    private double value;

    protected abstract double computeValue();

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding add(double d) {
        return super.add(d);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding add(float f) {
        return super.add(f);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding add(int i) {
        return super.add(i);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding add(long j) {
        return super.add(j);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding add(ObservableNumberValue observableNumberValue) {
        return super.add(observableNumberValue);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding divide(double d) {
        return super.divide(d);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding divide(float f) {
        return super.divide(f);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding divide(int i) {
        return super.divide(i);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding divide(long j) {
        return super.divide(j);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding divide(ObservableNumberValue observableNumberValue) {
        return super.divide(observableNumberValue);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public /* bridge */ /* synthetic */ Number getValue2() {
        return super.getValue2();
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding multiply(double d) {
        return super.multiply(d);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding multiply(float f) {
        return super.multiply(f);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding multiply(int i) {
        return super.multiply(i);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding multiply(long j) {
        return super.multiply(j);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding multiply(ObservableNumberValue observableNumberValue) {
        return super.multiply(observableNumberValue);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding negate() {
        return super.negate();
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding subtract(double d) {
        return super.subtract(d);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding subtract(float f) {
        return super.subtract(f);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding subtract(int i) {
        return super.subtract(i);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding subtract(long j) {
        return super.subtract(j);
    }

    @Override // com.brixcore.fakefx.beans.binding.DoubleExpression, com.brixcore.fakefx.beans.binding.NumberExpressionBase, com.brixcore.fakefx.beans.binding.NumberExpression
    public /* bridge */ /* synthetic */ NumberBinding subtract(ObservableNumberValue observableNumberValue) {
        return super.subtract(observableNumberValue);
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

    @Override // com.brixcore.fakefx.beans.value.ObservableDoubleValue
    public final double get() {
        if (!this.valid) {
            this.value = computeValue();
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
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public final boolean isValid() {
        return this.valid;
    }

    public String toString() {
        return this.valid ? "DoubleBinding [value: " + get() + "]" : "DoubleBinding [invalid]";
    }
}
