package com.brixcore.fakefx.collections;

/* JADX INFO: loaded from: classes3.dex */
@FunctionalInterface
public interface MapChangeListener<K, V> {
    void onChanged(Change<? extends K, ? extends V> change);

    public static abstract class Change<K, V> {
        private final ObservableMap<K, V> map;

        public abstract K getKey();

        public abstract V getValueAdded();

        public abstract V getValueRemoved();

        public abstract boolean wasAdded();

        public abstract boolean wasRemoved();

        public Change(ObservableMap<K, V> map) {
            this.map = map;
        }

        public ObservableMap<K, V> getMap() {
            return this.map;
        }
    }
}
