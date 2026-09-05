package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.collections.SetChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes6.dex */
public class ContentBinding {
    private static void checkParameters(Object property1, Object property2) {
        if (property1 == null || property2 == null) {
            throw new NullPointerException("Both parameters must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind object to itself");
        }
    }

    public static <E> Object bind(List<E> list1, ObservableList<? extends E> list2) {
        checkParameters(list1, list2);
        ListContentBinding<E> contentBinding = new ListContentBinding<>(list1);
        if (list1 instanceof ObservableList) {
            ((ObservableList) list1).setAll(list2);
        } else {
            list1.clear();
            list1.addAll(list2);
        }
        list2.removeListener(contentBinding);
        list2.addListener(contentBinding);
        return contentBinding;
    }

    public static <E> Object bind(Set<E> set1, ObservableSet<? extends E> set2) {
        checkParameters(set1, set2);
        SetContentBinding<E> contentBinding = new SetContentBinding<>(set1);
        set1.clear();
        set1.addAll(set2);
        set2.removeListener(contentBinding);
        set2.addListener(contentBinding);
        return contentBinding;
    }

    public static <K, V> Object bind(Map<K, V> map1, ObservableMap<? extends K, ? extends V> map2) {
        checkParameters(map1, map2);
        MapContentBinding<K, V> contentBinding = new MapContentBinding<>(map1);
        map1.clear();
        map1.putAll(map2);
        map2.removeListener(contentBinding);
        map2.addListener(contentBinding);
        return contentBinding;
    }

    public static void unbind(Object obj1, Object obj2) {
        checkParameters(obj1, obj2);
        if ((obj1 instanceof List) && (obj2 instanceof ObservableList)) {
            ((ObservableList) obj2).removeListener(new ListContentBinding((List) obj1));
            return;
        }
        if ((obj1 instanceof Set) && (obj2 instanceof ObservableSet)) {
            ((ObservableSet) obj2).removeListener(new SetContentBinding((Set) obj1));
        } else if ((obj1 instanceof Map) && (obj2 instanceof ObservableMap)) {
            ((ObservableMap) obj2).removeListener(new MapContentBinding((Map) obj1));
        }
    }

    private static class ListContentBinding<E> implements ListChangeListener<E>, WeakListener {
        private final WeakReference<List<E>> listRef;

        public ListContentBinding(List<E> list) {
            this.listRef = new WeakReference<>(list);
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener
        public void onChanged(ListChangeListener.Change<? extends E> change) {
            List<E> list = this.listRef.get();
            if (list == null) {
                change.getList().removeListener(this);
                return;
            }
            while (change.next()) {
                if (change.wasPermutated()) {
                    list.subList(change.getFrom(), change.getTo()).clear();
                    list.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                } else {
                    if (change.wasRemoved()) {
                        list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                    }
                    if (change.wasAdded()) {
                        list.addAll(change.getFrom(), change.getAddedSubList());
                    }
                }
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.listRef.get() == null;
        }

        public int hashCode() {
            List<E> list = this.listRef.get();
            if (list == null) {
                return 0;
            }
            return list.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            List<E> list1 = this.listRef.get();
            if (list1 == null || !(obj instanceof ListContentBinding)) {
                return false;
            }
            ListContentBinding<?> other = (ListContentBinding) obj;
            if (list1 == other.listRef.get()) {
                return true;
            }
            return false;
        }
    }

    private static class SetContentBinding<E> implements SetChangeListener<E>, WeakListener {
        private final WeakReference<Set<E>> setRef;

        public SetContentBinding(Set<E> set) {
            this.setRef = new WeakReference<>(set);
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener
        public void onChanged(SetChangeListener.Change<? extends E> change) {
            Set<E> set = this.setRef.get();
            if (set == null) {
                change.getSet().removeListener(this);
            } else if (change.wasRemoved()) {
                set.remove(change.getElementRemoved());
            } else {
                set.add(change.getElementAdded());
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.setRef.get() == null;
        }

        public int hashCode() {
            Set<E> set = this.setRef.get();
            if (set == null) {
                return 0;
            }
            return set.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            Set<E> set1 = this.setRef.get();
            if (set1 == null || !(obj instanceof SetContentBinding)) {
                return false;
            }
            SetContentBinding<?> other = (SetContentBinding) obj;
            if (set1 == other.setRef.get()) {
                return true;
            }
            return false;
        }
    }

    private static class MapContentBinding<K, V> implements MapChangeListener<K, V>, WeakListener {
        private final WeakReference<Map<K, V>> mapRef;

        public MapContentBinding(Map<K, V> map) {
            this.mapRef = new WeakReference<>(map);
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener
        public void onChanged(MapChangeListener.Change<? extends K, ? extends V> change) {
            Map<K, V> map = this.mapRef.get();
            if (map == null) {
                change.getMap().removeListener(this);
                return;
            }
            if (change.wasRemoved()) {
                map.remove(change.getKey());
            }
            if (change.wasAdded()) {
                map.put(change.getKey(), change.getValueAdded());
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.mapRef.get() == null;
        }

        public int hashCode() {
            Map<K, V> map = this.mapRef.get();
            if (map == null) {
                return 0;
            }
            return map.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            Map<K, V> map1 = this.mapRef.get();
            if (map1 == null || !(obj instanceof MapContentBinding)) {
                return false;
            }
            MapContentBinding<?, ?> other = (MapContentBinding) obj;
            if (map1 == other.mapRef.get()) {
                return true;
            }
            return false;
        }
    }
}
