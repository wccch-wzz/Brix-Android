package com.brixcore.fakefx.beans.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.property.ReadOnlyBooleanProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyIntegerProperty;
import com.brixcore.fakefx.beans.value.ObservableMapValue;
import com.brixcore.fakefx.beans.value.ObservableValue;
import com.brixcore.fakefx.binding.StringFormatter;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes16.dex */
public abstract class MapExpression<K, V> implements ObservableMapValue<K, V> {
    private static final ObservableMap EMPTY_MAP = new EmptyObservableMap();

    public abstract ReadOnlyBooleanProperty emptyProperty();

    public abstract ReadOnlyIntegerProperty sizeProperty();

    private static class EmptyObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
        private EmptyObservableMap() {
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void addListener(MapChangeListener<? super K, ? super V> mapChangeListener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void removeListener(MapChangeListener<? super K, ? super V> mapChangeListener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
        }
    }

    @Override // com.brixcore.fakefx.beans.value.ObservableValue
    /* JADX INFO: renamed from: getValue */
    public ObservableMap<K, V> getValue2() {
        return get();
    }

    public static <K, V> MapExpression<K, V> mapExpression(final ObservableMapValue<K, V> value) {
        if (value != null) {
            return value instanceof MapExpression ? (MapExpression) value : new MapBinding<K, V>() { // from class: com.brixcore.fakefx.beans.binding.MapExpression.1
                {
                    super.bind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.MapBinding, com.brixcore.fakefx.beans.binding.Binding
                public void dispose() {
                    super.unbind(value);
                }

                @Override // com.brixcore.fakefx.beans.binding.MapBinding
                protected ObservableMap<K, V> computeValue() {
                    return value.get();
                }

                @Override // com.brixcore.fakefx.beans.binding.MapBinding, com.brixcore.fakefx.beans.binding.Binding
                public ObservableList<?> getDependencies() {
                    return FXCollections.singletonObservableList(value);
                }
            };
        }
        throw new NullPointerException("Map must be specified.");
    }

    public int getSize() {
        return size();
    }

    public ObjectBinding<V> valueAt(K key) {
        return Bindings.valueAt(this, key);
    }

    public ObjectBinding<V> valueAt(ObservableValue<K> key) {
        return Bindings.valueAt((ObservableMap) this, (ObservableValue) key);
    }

    public BooleanBinding isEqualTo(ObservableMap<?, ?> other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(ObservableMap<?, ?> other) {
        return Bindings.notEqual(this, other);
    }

    public BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    public BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    @Override // java.util.Map
    public int size() {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.size() : map.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.isEmpty() : map.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.containsKey(obj) : map.containsKey(obj);
    }

    @Override // java.util.Map
    public boolean containsValue(Object obj) {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.containsValue(obj) : map.containsValue(obj);
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.put(key, value) : map.put(key, value);
    }

    @Override // java.util.Map
    public V remove(Object obj) {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.remove(obj) : map.remove(obj);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> elements) {
        ObservableMap<K, V> map = get();
        if (map == null) {
            EMPTY_MAP.putAll(elements);
        } else {
            map.putAll(elements);
        }
    }

    @Override // java.util.Map
    public void clear() {
        ObservableMap<K, V> map = get();
        if (map == null) {
            EMPTY_MAP.clear();
        } else {
            map.clear();
        }
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.keySet() : map.keySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.values() : map.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.entrySet() : map.entrySet();
    }

    @Override // java.util.Map
    public V get(Object key) {
        ObservableMap<K, V> map = get();
        return map == null ? EMPTY_MAP.get(key) : map.get(key);
    }
}
