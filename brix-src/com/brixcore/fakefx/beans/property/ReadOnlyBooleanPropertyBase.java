package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.binding.ObjectExpression;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.ExpressionHelper;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyBooleanPropertyBase extends ReadOnlyBooleanProperty {
    ExpressionHelper<Boolean> helper;

    @Override // com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty, com.brixcore.fakefx.beans.binding.BooleanExpression
    public /* bridge */ /* synthetic */ ObjectExpression asObject() {
        return super.asObject();
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
}
