package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.WeakListener;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.MapChangeListener;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.ObservableMap;
import com.brixcore.fakefx.collections.ObservableSet;
import com.brixcore.fakefx.collections.SetChangeListener;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes6.dex */
public class BidirectionalContentBinding {
    private static void checkParameters(Object property1, Object property2) {
        if (property1 == null || property2 == null) {
            throw new NullPointerException("Both parameters must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind object to itself");
        }
    }

    public static <E> Object bind(ObservableList<E> list1, ObservableList<E> list2) {
        checkParameters(list1, list2);
        ListContentBinding<E> binding = new ListContentBinding<>(list1, list2);
        list1.setAll(list2);
        list1.addListener(binding);
        list2.addListener(binding);
        return binding;
    }

    public static <E> Object bind(ObservableSet<E> set1, ObservableSet<E> set2) {
        checkParameters(set1, set2);
        SetContentBinding<E> binding = new SetContentBinding<>(set1, set2);
        set1.clear();
        set1.addAll(set2);
        set1.addListener(binding);
        set2.addListener(binding);
        return binding;
    }

    public static <K, V> Object bind(ObservableMap<K, V> map1, ObservableMap<K, V> map2) {
        checkParameters(map1, map2);
        MapContentBinding<K, V> binding = new MapContentBinding<>(map1, map2);
        map1.clear();
        map1.putAll(map2);
        map1.addListener(binding);
        map2.addListener(binding);
        return binding;
    }

    public static void unbind(Object obj1, Object obj2) {
        checkParameters(obj1, obj2);
        if ((obj1 instanceof ObservableList) && (obj2 instanceof ObservableList)) {
            ObservableList list1 = (ObservableList) obj1;
            ObservableList list2 = (ObservableList) obj2;
            ListContentBinding binding = new ListContentBinding(list1, list2);
            list1.removeListener(binding);
            list2.removeListener(binding);
            return;
        }
        if ((obj1 instanceof ObservableSet) && (obj2 instanceof ObservableSet)) {
            ObservableSet set1 = (ObservableSet) obj1;
            ObservableSet set2 = (ObservableSet) obj2;
            SetContentBinding binding2 = new SetContentBinding(set1, set2);
            set1.removeListener(binding2);
            set2.removeListener(binding2);
            return;
        }
        if ((obj1 instanceof ObservableMap) && (obj2 instanceof ObservableMap)) {
            ObservableMap map1 = (ObservableMap) obj1;
            ObservableMap map2 = (ObservableMap) obj2;
            MapContentBinding binding3 = new MapContentBinding(map1, map2);
            map1.removeListener(binding3);
            map2.removeListener(binding3);
        }
    }

    private static class ListContentBinding<E> implements ListChangeListener<E>, WeakListener {
        private final WeakReference<ObservableList<E>> propertyRef1;
        private final WeakReference<ObservableList<E>> propertyRef2;
        private boolean updating = false;

        public ListContentBinding(ObservableList<E> list1, ObservableList<E> list2) {
            this.propertyRef1 = new WeakReference<>(list1);
            this.propertyRef2 = new WeakReference<>(list2);
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener
        public void onChanged(ListChangeListener.Change<? extends E> change) {
            if (!this.updating) {
                ObservableList<E> observableList = this.propertyRef1.get();
                ObservableList<E> observableList2 = this.propertyRef2.get();
                if (observableList == null || observableList2 == null) {
                    if (observableList != null) {
                        observableList.removeListener(this);
                    }
                    if (observableList2 != null) {
                        observableList2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    this.updating = true;
                    ObservableList<? extends E> observableList3 = observableList == change.getList() ? observableList2 : observableList;
                    while (change.next()) {
                        if (change.wasPermutated()) {
                            observableList3.remove(change.getFrom(), change.getTo());
                            observableList3.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                        } else {
                            if (change.wasRemoved()) {
                                observableList3.remove(change.getFrom(), change.getFrom() + change.getRemovedSize());
                            }
                            if (change.wasAdded()) {
                                observableList3.addAll(change.getFrom(), change.getAddedSubList());
                            }
                        }
                    }
                } finally {
                    this.updating = false;
                }
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
        }

        public int hashCode() {
            ObservableList<E> list1 = this.propertyRef1.get();
            ObservableList<E> list2 = this.propertyRef2.get();
            int hc1 = list1 == null ? 0 : list1.hashCode();
            int hc2 = list2 != null ? list2.hashCode() : 0;
            return hc1 * hc2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            Object propertyA1 = this.propertyRef1.get();
            Object propertyA2 = this.propertyRef2.get();
            if (propertyA1 != null && propertyA2 != null && (obj instanceof ListContentBinding)) {
                ListContentBinding otherBinding = (ListContentBinding) obj;
                Object propertyB1 = otherBinding.propertyRef1.get();
                Object propertyB2 = otherBinding.propertyRef2.get();
                if (propertyB1 == null || propertyB2 == null) {
                    return false;
                }
                if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                    return true;
                }
                if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class SetContentBinding<E> implements SetChangeListener<E>, WeakListener {
        private final WeakReference<ObservableSet<E>> propertyRef1;
        private final WeakReference<ObservableSet<E>> propertyRef2;
        private boolean updating = false;

        public SetContentBinding(ObservableSet<E> list1, ObservableSet<E> list2) {
            this.propertyRef1 = new WeakReference<>(list1);
            this.propertyRef2 = new WeakReference<>(list2);
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener
        public void onChanged(SetChangeListener.Change<? extends E> change) {
            if (!this.updating) {
                ObservableSet<E> observableSet = this.propertyRef1.get();
                ObservableSet<E> observableSet2 = this.propertyRef2.get();
                if (observableSet == null || observableSet2 == null) {
                    if (observableSet != null) {
                        observableSet.removeListener(this);
                    }
                    if (observableSet2 != null) {
                        observableSet2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    this.updating = true;
                    ObservableSet<? extends E> observableSet3 = observableSet == change.getSet() ? observableSet2 : observableSet;
                    if (change.wasRemoved()) {
                        observableSet3.remove(change.getElementRemoved());
                    } else {
                        observableSet3.add(change.getElementAdded());
                    }
                } finally {
                    this.updating = false;
                }
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
        }

        public int hashCode() {
            ObservableSet<E> set1 = this.propertyRef1.get();
            ObservableSet<E> set2 = this.propertyRef2.get();
            int hc1 = set1 == null ? 0 : set1.hashCode();
            int hc2 = set2 != null ? set2.hashCode() : 0;
            return hc1 * hc2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            Object propertyA1 = this.propertyRef1.get();
            Object propertyA2 = this.propertyRef2.get();
            if (propertyA1 != null && propertyA2 != null && (obj instanceof SetContentBinding)) {
                SetContentBinding otherBinding = (SetContentBinding) obj;
                Object propertyB1 = otherBinding.propertyRef1.get();
                Object propertyB2 = otherBinding.propertyRef2.get();
                if (propertyB1 == null || propertyB2 == null) {
                    return false;
                }
                if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                    return true;
                }
                if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class MapContentBinding<K, V> implements MapChangeListener<K, V>, WeakListener {
        private final WeakReference<ObservableMap<K, V>> propertyRef1;
        private final WeakReference<ObservableMap<K, V>> propertyRef2;
        private boolean updating = false;

        public MapContentBinding(ObservableMap<K, V> list1, ObservableMap<K, V> list2) {
            this.propertyRef1 = new WeakReference<>(list1);
            this.propertyRef2 = new WeakReference<>(list2);
        }

        @Override // com.brixcore.fakefx.collections.MapChangeListener
        public void onChanged(MapChangeListener.Change<? extends K, ? extends V> change) {
            if (!this.updating) {
                ObservableMap<K, V> observableMap = this.propertyRef1.get();
                ObservableMap<K, V> observableMap2 = this.propertyRef2.get();
                if (observableMap == null || observableMap2 == null) {
                    if (observableMap != null) {
                        observableMap.removeListener(this);
                    }
                    if (observableMap2 != null) {
                        observableMap2.removeListener(this);
                        return;
                    }
                    return;
                }
                try {
                    this.updating = true;
                    ObservableMap<? extends K, ? extends V> observableMap3 = observableMap == change.getMap() ? observableMap2 : observableMap;
                    if (change.wasRemoved()) {
                        observableMap3.remove(change.getKey());
                    }
                    if (change.wasAdded()) {
                        observableMap3.put(change.getKey(), change.getValueAdded());
                    }
                } finally {
                    this.updating = false;
                }
            }
        }

        @Override // com.brixcore.fakefx.beans.WeakListener
        public boolean wasGarbageCollected() {
            return this.propertyRef1.get() == null || this.propertyRef2.get() == null;
        }

        public int hashCode() {
            ObservableMap<K, V> map1 = this.propertyRef1.get();
            ObservableMap<K, V> map2 = this.propertyRef2.get();
            int hc1 = map1 == null ? 0 : map1.hashCode();
            int hc2 = map2 != null ? map2.hashCode() : 0;
            return hc1 * hc2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            Object propertyA1 = this.propertyRef1.get();
            Object propertyA2 = this.propertyRef2.get();
            if (propertyA1 != null && propertyA2 != null && (obj instanceof MapContentBinding)) {
                MapContentBinding otherBinding = (MapContentBinding) obj;
                Object propertyB1 = otherBinding.propertyRef1.get();
                Object propertyB2 = otherBinding.propertyRef2.get();
                if (propertyB1 == null || propertyB2 == null) {
                    return false;
                }
                if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                    return true;
                }
                if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                    return true;
                }
            }
            return false;
        }
    }
}
