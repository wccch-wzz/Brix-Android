package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyBooleanPropertyBase;
import com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyIntegerPropertyBase;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.binding.BindingHelperObserver;
import com.brixcore.fakefx.binding.MapExpressionHelper;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;

/* JADX INFO: loaded from: classes16.dex */
public abstract class MapBinding<K, V> extends MapExpression<K, V> implements Binding<ObservableMap<K, V>> {
    private MapBinding<K, V>.EmptyProperty empty0;
    private BindingHelperObserver observer;
    private MapBinding<K, V>.SizeProperty size0;
    private ObservableMap<K, V> value;
    private final MapChangeListener<K, V> mapChangeListener = new MapChangeListener<K, V>() { // from class: com.brixcore.fakefx.beans.binding.MapBinding.1
        @Override // com.brixcore.fakefx.collections.MapChangeListener
        public void onChanged(MapChangeListener.Change<? extends K, ? extends V> change) {
            MapBinding.this.invalidateProperties();
            MapBinding.this.onInvalidating();
            MapExpressionHelper.fireValueChangedEvent(MapBinding.this.helper, change);
        }
    };
    private boolean valid = false;
    private MapExpressionHelper<K, V> helper = null;

    protected abstract ObservableMap<K, V> computeValue();

    @Override // com.brixcore.fakefx.beans.binding.MapExpression, com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public /* bridge */ /* synthetic */ Object getValue2() {
        return super.getValue2();
    }

    @Override // com.brixcore.fakefx.beans.binding.MapExpression
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
            return MapBinding.this.size();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return MapBinding.this;
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

    @Override // com.brixcore.fakefx.beans.binding.MapExpression
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
            return MapBinding.this.isEmpty();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return MapBinding.this;
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
    public final ObservableMap<K, V> get() {
        if (!this.valid) {
            this.value = computeValue();
            this.valid = true;
            if (this.value != null) {
                this.value.addListener((MapChangeListener<? super K, ? super V>) this.mapChangeListener);
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
                this.value.removeListener((MapChangeListener<? super K, ? super V>) this.mapChangeListener);
            }
            this.valid = false;
            invalidateProperties();
            onInvalidating();
            MapExpressionHelper.fireValueChangedEvent(this.helper);
        }
    }

    @Override // com.brixcore.fakefx.beans.binding.Binding
    public final boolean isValid() {
        return this.valid;
    }

    public String toString() {
        return this.valid ? "MapBinding [value: " + get() + "]" : "MapBinding [invalid]";
    }
}
