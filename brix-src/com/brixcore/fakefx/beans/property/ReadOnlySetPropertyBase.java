package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.SetExpressionHelper;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.collections.SetChangeListener;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlySetPropertyBase<E> extends ReadOnlySetProperty<E> {
    private SetExpressionHelper<E> helper;

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
}
