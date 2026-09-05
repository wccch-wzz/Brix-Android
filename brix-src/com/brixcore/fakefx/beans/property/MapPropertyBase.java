package com.brixcore.fakefx.beans.property;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.MapExpressionHelper;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableMap;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes4.dex */
public abstract class MapPropertyBase<K, V> extends MapProperty<K, V> {
    private MapPropertyBase<K, V>.EmptyProperty empty0;
    private MapPropertyBase<K, V>.SizeProperty size0;
    private ObservableMap<K, V> value;
    private final MapChangeListener<K, V> mapChangeListener = new MapChangeListener() { // from class: com.brixcore.fakefx.beans.property.MapPropertyBase$$ExternalSyntheticLambda0
        @Override // com.brixcore.fakefx.collections.MapChangeListener
        public final void onChanged(MapChangeListener.Change change) {
            this.f$0.lambda$new$0(change);
        }
    };
    private ObservableValue<? extends ObservableMap<K, V>> observable = null;
    private InvalidationListener listener = null;
    private boolean valid = true;
    private MapExpressionHelper<K, V> helper = null;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(MapChangeListener.Change change) {
        invalidateProperties();
        invalidated();
        fireValueChangedEvent(change);
    }

    public MapPropertyBase() {
    }

    public MapPropertyBase(ObservableMap<K, V> initialValue) {
        this.value = initialValue;
        if (initialValue != null) {
            initialValue.addListener(this.mapChangeListener);
        }
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
            return MapPropertyBase.this.size();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return MapPropertyBase.this;
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
            return MapPropertyBase.this.isEmpty();
        }

        @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
        public Object getBean() {
            return MapPropertyBase.this;
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

    protected void fireValueChangedEvent() {
        MapExpressionHelper.fireValueChangedEvent(this.helper);
    }

    protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
        MapExpressionHelper.fireValueChangedEvent(this.helper, change);
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
    public void markInvalid(ObservableMap<K, V> oldValue) {
        if (this.valid) {
            if (oldValue != null) {
                oldValue.removeListener(this.mapChangeListener);
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
    public ObservableMap<K, V> get() {
        if (!this.valid) {
            this.value = this.observable == null ? this.value : this.observable.getValue2();
            this.valid = true;
            if (this.value != null) {
                this.value.addListener((MapChangeListener<? super K, ? super V>) this.mapChangeListener);
            }
        }
        return this.value;
    }

    @Override // com.brixcore.fakefx.beans.value.WritableObjectValue
    public void set(ObservableMap<K, V> newValue) {
        if (isBound()) {
            throw new RuntimeException(((getBean() == null || getName() == null) ? "" : getBean().getClass().getSimpleName() + "." + getName() + " : ") + "A bound value cannot be set.");
        }
        if (this.value != newValue) {
            ObservableMap<K, V> oldValue = this.value;
            this.value = newValue;
            markInvalid(oldValue);
        }
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public boolean isBound() {
        return this.observable != null;
    }

    @Override // com.brixcore.fakefx.beans.property.Property
    public void bind(ObservableValue<? extends ObservableMap<K, V>> newObservable) {
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

    @Override // com.brixcore.fakefx.beans.property.MapProperty, com.brixcore.fakefx.beans.property.ReadOnlyMapProperty
    public String toString() {
        Object bean = getBean();
        String name = getName();
        StringBuilder result = new StringBuilder("MapProperty [");
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

    private static class Listener<K, V> implements InvalidationListener, WeakListener {
        private final WeakReference<MapPropertyBase<K, V>> wref;

        public Listener(MapPropertyBase<K, V> ref) {
            this.wref = new WeakReference<>(ref);
        }

        @Override // com.brixcore.fakefx.beans.InvalidationListener
        public void invalidated(Observable observable) {
            MapPropertyBase<K, V> ref = this.wref.get();
            if (ref == null) {
                observable.removeListener(this);
            } else {
                ref.markInvalid(((MapPropertyBase) ref).value);
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.wref.get() == null;
        }
    }
}
