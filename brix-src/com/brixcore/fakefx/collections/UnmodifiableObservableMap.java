package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class UnmodifiableObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
    private final ObservableMap<K, V> backingMap;
    private Set<Map.Entry<K, V>> entryset;
    private Set<K> keyset;
    private final MapChangeListener<K, V> listener = new MapChangeListener() { // from class: com.brixcore.fakefx.collections.UnmodifiableObservableMap$$ExternalSyntheticLambda0
        @Override // com.brixcore.fakefx.collections.MapChangeListener
        public final void onChanged(MapChangeListener.Change change) {
            this.f$0.lambda$new$0(change);
        }
    };
    private MapListenerHelper<K, V> listenerHelper;
    private Collection<V> values;

    public UnmodifiableObservableMap(ObservableMap<K, V> map) {
        this.backingMap = map;
        this.backingMap.addListener(new WeakMapChangeListener(this.listener));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(MapChangeListener.Change c) {
        callObservers(new MapAdapterChange(this, c));
    }

    private void callObservers(MapChangeListener.Change<? extends K, ? extends V> c) {
        MapListenerHelper.fireValueChangedEvent(this.listenerHelper, c);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableMap
    public void addListener(MapChangeListener<? super K, ? super V> observer) {
        this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, observer);
    }

    @Override // com.brixcore.fakefx.collections.ObservableMap
    public void removeListener(MapChangeListener<? super K, ? super V> observer) {
        this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, observer);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.backingMap.size();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        return this.backingMap.containsKey(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        return this.backingMap.containsValue(value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        return this.backingMap.get(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        if (this.keyset == null) {
            this.keyset = Collections.unmodifiableSet(this.backingMap.keySet());
        }
        return this.keyset;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.backingMap.values());
        }
        return this.values;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entryset == null) {
            this.entryset = Collections.unmodifiableMap(this.backingMap).entrySet();
        }
        return this.entryset;
    }
}
