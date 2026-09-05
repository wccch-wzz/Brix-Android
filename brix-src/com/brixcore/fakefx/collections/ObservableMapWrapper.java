package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.util.io.NetworkUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class ObservableMapWrapper<K, V> implements ObservableMap<K, V> {
    private final Map<K, V> backingMap;
    private ObservableMapWrapper<K, V>.ObservableEntrySet entrySet;
    private ObservableMapWrapper<K, V>.ObservableKeySet keySet;
    private MapListenerHelper<K, V> listenerHelper;
    private ObservableMapWrapper<K, V>.ObservableValues values;

    public ObservableMapWrapper(Map<K, V> map) {
        this.backingMap = map;
    }

    private class SimpleChange extends MapChangeListener.Change<K, V> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final V added;
        private final K key;
        private final V old;
        private final boolean wasAdded;
        private final boolean wasRemoved;

        public SimpleChange(K key, V old, V added, boolean wasAdded, boolean wasRemoved) {
            super(ObservableMapWrapper.this);
            if (!wasAdded && !wasRemoved) {
                throw new AssertionError();
            }
            this.key = key;
            this.old = old;
            this.added = added;
            this.wasAdded = wasAdded;
            this.wasRemoved = wasRemoved;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public boolean wasAdded() {
            return this.wasAdded;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public boolean wasRemoved() {
            return this.wasRemoved;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public K getKey() {
            return this.key;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public V getValueAdded() {
            return this.added;
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener.Change
        public V getValueRemoved() {
            return this.old;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (this.wasAdded) {
                if (this.wasRemoved) {
                    builder.append(this.old).append(" replaced by ").append(this.added);
                } else {
                    builder.append(this.added).append(" added");
                }
            } else {
                builder.append(this.old).append(" removed");
            }
            builder.append(" at key ").append(this.key);
            return builder.toString();
        }
    }

    protected void callObservers(MapChangeListener.Change<K, V> change) {
        MapListenerHelper.fireValueChangedEvent(this.listenerHelper, change);
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

    @Override // java.util.Map
    public int size() {
        return this.backingMap.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.backingMap.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.backingMap.containsValue(value);
    }

    @Override // java.util.Map
    public V get(Object key) {
        return this.backingMap.get(key);
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        if (this.backingMap.containsKey(key)) {
            V ret = this.backingMap.put(key, value);
            if ((ret == null && value != null) || (ret != null && !ret.equals(value))) {
                callObservers(new SimpleChange(key, ret, value, true, true));
                return ret;
            }
            return ret;
        }
        V ret2 = this.backingMap.put(key, value);
        callObservers(new SimpleChange(key, ret2, value, true, false));
        return ret2;
    }

    @Override // java.util.Map
    public V remove(Object key) {
        if (!this.backingMap.containsKey(key)) {
            return null;
        }
        V ret = this.backingMap.remove(key);
        callObservers(new SimpleChange(key, ret, null, false, true));
        return ret;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override // java.util.Map
    public void clear() {
        Iterator<Map.Entry<K, V>> i = this.backingMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<K, V> e = i.next();
            K key = e.getKey();
            V val = e.getValue();
            i.remove();
            callObservers(new SimpleChange(key, val, null, false, true));
        }
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new ObservableKeySet();
        }
        return this.keySet;
    }

    @Override // java.util.Map
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new ObservableValues();
        }
        return this.values;
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new ObservableEntrySet();
        }
        return this.entrySet;
    }

    public String toString() {
        return this.backingMap.toString();
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        return this.backingMap.equals(obj);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.backingMap.hashCode();
    }

    private class ObservableKeySet implements Set<K> {
        private ObservableKeySet() {
        }

        @Override // java.util.Set, java.util.Collection
        public int size() {
            return ObservableMapWrapper.this.backingMap.size();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean isEmpty() {
            return ObservableMapWrapper.this.backingMap.isEmpty();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean contains(Object o) {
            return ObservableMapWrapper.this.backingMap.keySet().contains(o);
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new Iterator<K>() { // from class: com.brixcore.fakefx.collections.ObservableMapWrapper.ObservableKeySet.1
                private Iterator<Map.Entry<K, V>> entryIt;
                private K lastKey;
                private V lastValue;

                {
                    this.entryIt = ObservableMapWrapper.this.backingMap.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.entryIt.hasNext();
                }

                @Override // java.util.Iterator
                public K next() {
                    Map.Entry<K, V> last = this.entryIt.next();
                    this.lastKey = last.getKey();
                    this.lastValue = last.getValue();
                    return last.getKey();
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.entryIt.remove();
                    ObservableMapWrapper.this.callObservers(new SimpleChange(this.lastKey, this.lastValue, null, false, true));
                }
            };
        }

        @Override // java.util.Set, java.util.Collection
        public Object[] toArray() {
            return ObservableMapWrapper.this.backingMap.keySet().toArray();
        }

        @Override // java.util.Set, java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            return (T[]) ObservableMapWrapper.this.backingMap.keySet().toArray(tArr);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean add(K e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override // java.util.Set, java.util.Collection
        public boolean remove(Object o) {
            return ObservableMapWrapper.this.remove(o) != null;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return ObservableMapWrapper.this.backingMap.keySet().containsAll(c);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override // java.util.Set, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            return removeRetain(c, false);
        }

        private boolean removeRetain(Collection<?> c, boolean remove) {
            boolean removed = false;
            Iterator<Map.Entry<K, V>> i = ObservableMapWrapper.this.backingMap.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                if (remove == c.contains(e.getKey())) {
                    removed = true;
                    K key = e.getKey();
                    V value = e.getValue();
                    i.remove();
                    ObservableMapWrapper.this.callObservers(new SimpleChange(key, value, null, false, true));
                }
            }
            return removed;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            return removeRetain(c, true);
        }

        @Override // java.util.Set, java.util.Collection
        public void clear() {
            ObservableMapWrapper.this.clear();
        }

        public String toString() {
            return ObservableMapWrapper.this.backingMap.keySet().toString();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean equals(Object obj) {
            return ObservableMapWrapper.this.backingMap.keySet().equals(obj);
        }

        @Override // java.util.Set, java.util.Collection
        public int hashCode() {
            return ObservableMapWrapper.this.backingMap.keySet().hashCode();
        }
    }

    private class ObservableValues implements Collection<V> {
        private ObservableValues() {
        }

        @Override // java.util.Collection
        public int size() {
            return ObservableMapWrapper.this.backingMap.size();
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            return ObservableMapWrapper.this.backingMap.isEmpty();
        }

        @Override // java.util.Collection
        public boolean contains(Object o) {
            return ObservableMapWrapper.this.backingMap.values().contains(o);
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new Iterator<V>() { // from class: com.brixcore.fakefx.collections.ObservableMapWrapper.ObservableValues.1
                private Iterator<Map.Entry<K, V>> entryIt;
                private K lastKey;
                private V lastValue;

                {
                    this.entryIt = ObservableMapWrapper.this.backingMap.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.entryIt.hasNext();
                }

                @Override // java.util.Iterator
                public V next() {
                    Map.Entry<K, V> last = this.entryIt.next();
                    this.lastKey = last.getKey();
                    this.lastValue = last.getValue();
                    return this.lastValue;
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.entryIt.remove();
                    ObservableMapWrapper.this.callObservers(new SimpleChange(this.lastKey, this.lastValue, null, false, true));
                }
            };
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            return ObservableMapWrapper.this.backingMap.values().toArray();
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            return (T[]) ObservableMapWrapper.this.backingMap.values().toArray(tArr);
        }

        @Override // java.util.Collection
        public boolean add(V e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override // java.util.Collection
        public boolean remove(Object o) {
            Iterator<V> i = iterator();
            while (i.hasNext()) {
                if (i.next().equals(o)) {
                    i.remove();
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return ObservableMapWrapper.this.backingMap.values().containsAll(c);
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> c) {
            return removeRetain(c, true);
        }

        private boolean removeRetain(Collection<?> c, boolean remove) {
            boolean removed = false;
            Iterator<Map.Entry<K, V>> i = ObservableMapWrapper.this.backingMap.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                if (remove == c.contains(e.getValue())) {
                    removed = true;
                    K key = e.getKey();
                    V value = e.getValue();
                    i.remove();
                    ObservableMapWrapper.this.callObservers(new SimpleChange(key, value, null, false, true));
                }
            }
            return removed;
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> c) {
            return removeRetain(c, false);
        }

        @Override // java.util.Collection
        public void clear() {
            ObservableMapWrapper.this.clear();
        }

        public String toString() {
            return ObservableMapWrapper.this.backingMap.values().toString();
        }

        @Override // java.util.Collection
        public boolean equals(Object obj) {
            return ObservableMapWrapper.this.backingMap.values().equals(obj);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return ObservableMapWrapper.this.backingMap.values().hashCode();
        }
    }

    private class ObservableEntry implements Map.Entry<K, V> {
        private final Map.Entry<K, V> backingEntry;

        public ObservableEntry(Map.Entry<K, V> backingEntry) {
            this.backingEntry = backingEntry;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.backingEntry.getKey();
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.backingEntry.getValue();
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            V oldValue = this.backingEntry.setValue(value);
            ObservableMapWrapper.this.callObservers(new SimpleChange(getKey(), oldValue, value, true, true));
            return oldValue;
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2) {
                    return true;
                }
                if (v1 != null && v1.equals(v2)) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() != null ? getValue().hashCode() : 0);
        }

        public final String toString() {
            return getKey() + NetworkUtils.NAME_VALUE_SEPARATOR + getValue();
        }
    }

    private class ObservableEntrySet implements Set<Map.Entry<K, V>> {
        private ObservableEntrySet() {
        }

        @Override // java.util.Set, java.util.Collection
        public int size() {
            return ObservableMapWrapper.this.backingMap.size();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean isEmpty() {
            return ObservableMapWrapper.this.backingMap.isEmpty();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean contains(Object o) {
            return ObservableMapWrapper.this.backingMap.entrySet().contains(o);
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() { // from class: com.brixcore.fakefx.collections.ObservableMapWrapper.ObservableEntrySet.1
                private Iterator<Map.Entry<K, V>> backingIt;
                private K lastKey;
                private V lastValue;

                {
                    this.backingIt = ObservableMapWrapper.this.backingMap.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.backingIt.hasNext();
                }

                @Override // java.util.Iterator
                public Map.Entry<K, V> next() {
                    Map.Entry<K, V> last = this.backingIt.next();
                    this.lastKey = last.getKey();
                    this.lastValue = last.getValue();
                    return new ObservableEntry(last);
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.backingIt.remove();
                    ObservableMapWrapper.this.callObservers(new SimpleChange(this.lastKey, this.lastValue, null, false, true));
                }
            };
        }

        @Override // java.util.Set, java.util.Collection
        public Object[] toArray() {
            Object[] array = ObservableMapWrapper.this.backingMap.entrySet().toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = new ObservableEntry((Map.Entry) array[i]);
            }
            return array;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Set, java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            T[] tArr2 = (T[]) ObservableMapWrapper.this.backingMap.entrySet().toArray(tArr);
            for (int i = 0; i < tArr2.length; i++) {
                tArr2[i] = new ObservableEntry((Map.Entry) tArr2[i]);
            }
            return tArr2;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean add(Map.Entry<K, V> e) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override // java.util.Set, java.util.Collection
        public boolean remove(Object o) {
            boolean ret = ObservableMapWrapper.this.backingMap.entrySet().remove(o);
            if (ret) {
                Map.Entry<K, V> entry = (Map.Entry) o;
                ObservableMapWrapper.this.callObservers(new SimpleChange(entry.getKey(), entry.getValue(), null, false, true));
            }
            return ret;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return ObservableMapWrapper.this.backingMap.entrySet().containsAll(c);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override // java.util.Set, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            return removeRetain(c, false);
        }

        private boolean removeRetain(Collection<?> c, boolean remove) {
            boolean removed = false;
            Iterator<Map.Entry<K, V>> i = ObservableMapWrapper.this.backingMap.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                if (remove == c.contains(e)) {
                    removed = true;
                    K key = e.getKey();
                    V value = e.getValue();
                    i.remove();
                    ObservableMapWrapper.this.callObservers(new SimpleChange(key, value, null, false, true));
                }
            }
            return removed;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            return removeRetain(c, true);
        }

        @Override // java.util.Set, java.util.Collection
        public void clear() {
            ObservableMapWrapper.this.clear();
        }

        public String toString() {
            return ObservableMapWrapper.this.backingMap.entrySet().toString();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean equals(Object obj) {
            return ObservableMapWrapper.this.backingMap.entrySet().equals(obj);
        }

        @Override // java.util.Set, java.util.Collection
        public int hashCode() {
            return ObservableMapWrapper.this.backingMap.entrySet().hashCode();
        }
    }
}
