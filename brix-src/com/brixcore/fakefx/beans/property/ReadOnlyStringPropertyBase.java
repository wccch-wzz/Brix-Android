package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.ExpressionHelper;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyStringPropertyBase extends ReadOnlyStringProperty {
    ExpressionHelper<String> helper;

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super String> listener) {
        this.helper = ExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super String> listener) {
        this.helper = ExpressionHelper.removeListener(this.helper, listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(this.helper);
    }
}
