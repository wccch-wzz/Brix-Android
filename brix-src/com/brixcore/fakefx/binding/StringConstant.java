package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.binding.StringExpression;
import com.brixcore.fakefx.beans.value.ChangeListener;

/* JADX INFO: loaded from: classes6.dex */
public class StringConstant extends StringExpression {
    private final String value;

    private StringConstant(String value) {
        this.value = value;
    }

    public static StringConstant valueOf(String value) {
        return new StringConstant(value);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableObjectValue
    public String get() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.binding.StringExpression, com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public String getValue2() {
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super String> observer) {
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener observer) {
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super String> observer) {
    }
}
