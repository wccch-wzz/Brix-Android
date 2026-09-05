package com.brixcore.fakefx.collections;

/* JADX INFO: loaded from: classes3.dex */
public class MapAdapterChange<K, V> extends MapChangeListener.Change<K, V> {
    private final MapChangeListener.Change<? extends K, ? extends V> change;

    public MapAdapterChange(ObservableMap<K, V> map, MapChangeListener.Change<? extends K, ? extends V> change) {
        super(map);
        this.change = change;
    }

    @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
    public boolean wasAdded() {
        return this.change.wasAdded();
    }

    @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
    public boolean wasRemoved() {
        return this.change.wasRemoved();
    }

    @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
    public K getKey() {
        return this.change.getKey();
    }

    @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
    public V getValueAdded() {
        return this.change.getValueAdded();
    }

    @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
    public V getValueRemoved() {
        return this.change.getValueRemoved();
    }

    public String toString() {
        return this.change.toString();
    }
}
