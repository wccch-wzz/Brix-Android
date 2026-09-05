package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.MapExpressionHelper;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableMap;

/* JADX INFO: loaded from: classes4.dex */
public abstract class ReadOnlyMapPropertyBase<K, V> extends ReadOnlyMapProperty<K, V> {
    private MapExpressionHelper<K, V> helper;

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = MapExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = MapExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
        this.helper = MapExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    public void removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
        this.helper = MapExpressionHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableMap
    public void addListener(MapChangeListener<? super K, ? super V> listener) {
        this.helper = MapExpressionHelper.addListener(this.helper, this, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableMap
    public void removeListener(MapChangeListener<? super K, ? super V> listener) {
        this.helper = MapExpressionHelper.removeListener(this.helper, listener);
    }

    protected void fireValueChangedEvent() {
        MapExpressionHelper.fireValueChangedEvent(this.helper);
    }

    protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
        MapExpressionHelper.fireValueChangedEvent(this.helper, change);
    }
}
