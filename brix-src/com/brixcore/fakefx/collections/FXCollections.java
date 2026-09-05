package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.util.Callback;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class FXCollections {
    private static Random r;
    private static ObservableMap EMPTY_OBSERVABLE_MAP = new EmptyObservableMap();
    private static ObservableList EMPTY_OBSERVABLE_LIST = new EmptyObservableList();
    private static ObservableSet EMPTY_OBSERVABLE_SET = new EmptyObservableSet();

    private FXCollections() {
    }

    public static <E> ObservableList<E> observableList(List<E> list) {
        if (list != null) {
            return list instanceof RandomAccess ? new ObservableListWrapper(list) : new ObservableSequentialListWrapper(list);
        }
        throw new NullPointerException();
    }

    public static <E> ObservableList<E> observableList(List<E> list, Callback<E, Observable[]> extractor) {
        if (list == null || extractor == null) {
            throw new NullPointerException();
        }
        return list instanceof RandomAccess ? new ObservableListWrapper(list, extractor) : new ObservableSequentialListWrapper(list, extractor);
    }

    public static <K, V> ObservableMap<K, V> observableMap(Map<K, V> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        return new ObservableMapWrapper(map);
    }

    public static <E> ObservableSet<E> observableSet(Set<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new ObservableSetWrapper(set);
    }

    public static <E> ObservableSet<E> observableSet(E... elements) {
        if (elements == null) {
            throw new NullPointerException();
        }
        Set<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return new ObservableSetWrapper(set);
    }

    public static <K, V> ObservableMap<K, V> unmodifiableObservableMap(ObservableMap<K, V> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        return new UnmodifiableObservableMap(map);
    }

    public static <K, V> ObservableMap<K, V> checkedObservableMap(ObservableMap<K, V> map, Class<K> keyType, Class<V> valueType) {
        if (map == null || keyType == null || valueType == null) {
            throw new NullPointerException();
        }
        return new CheckedObservableMap(map, keyType, valueType);
    }

    public static <K, V> ObservableMap<K, V> synchronizedObservableMap(ObservableMap<K, V> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        return new SynchronizedObservableMap(map);
    }

    public static <K, V> ObservableMap<K, V> emptyObservableMap() {
        return EMPTY_OBSERVABLE_MAP;
    }

    public static ObservableIntegerArray observableIntegerArray() {
        return new ObservableIntegerArrayImpl();
    }

    public static ObservableIntegerArray observableIntegerArray(int... values) {
        return new ObservableIntegerArrayImpl(values);
    }

    public static ObservableIntegerArray observableIntegerArray(ObservableIntegerArray array) {
        return new ObservableIntegerArrayImpl(array);
    }

    public static ObservableFloatArray observableFloatArray() {
        return new ObservableFloatArrayImpl();
    }

    public static ObservableFloatArray observableFloatArray(float... values) {
        return new ObservableFloatArrayImpl(values);
    }

    public static ObservableFloatArray observableFloatArray(ObservableFloatArray array) {
        return new ObservableFloatArrayImpl(array);
    }

    public static <E> ObservableList<E> observableArrayList() {
        return observableList(new ArrayList());
    }

    public static <E> ObservableList<E> observableArrayList(Callback<E, Observable[]> extractor) {
        return observableList(new ArrayList(), extractor);
    }

    public static <E> ObservableList<E> observableArrayList(E... items) {
        return observableList(new ArrayList(Arrays.asList(items)));
    }

    public static <E> ObservableList<E> observableArrayList(Collection<? extends E> col) {
        return observableList(new ArrayList(col));
    }

    public static <K, V> ObservableMap<K, V> observableHashMap() {
        return observableMap(new HashMap());
    }

    public static <E> ObservableList<E> concat(ObservableList<E>... lists) {
        if (lists.length == 0) {
            return observableArrayList();
        }
        if (lists.length == 1) {
            return observableArrayList(lists[0]);
        }
        ArrayList<E> backingList = new ArrayList<>();
        for (ObservableList<E> s : lists) {
            backingList.addAll(s);
        }
        return observableList(backingList);
    }

    public static <E> ObservableList<E> unmodifiableObservableList(ObservableList<E> list) {
        if (list == null) {
            throw new NullPointerException();
        }
        return new UnmodifiableObservableListImpl(list);
    }

    public static <E> ObservableList<E> checkedObservableList(ObservableList<E> list, Class<E> type) {
        if (list == null) {
            throw new NullPointerException();
        }
        return new CheckedObservableList(list, type);
    }

    public static <E> ObservableList<E> synchronizedObservableList(ObservableList<E> list) {
        if (list == null) {
            throw new NullPointerException();
        }
        return new SynchronizedObservableList(list);
    }

    public static <E> ObservableList<E> emptyObservableList() {
        return EMPTY_OBSERVABLE_LIST;
    }

    public static <E> ObservableList<E> singletonObservableList(E e) {
        return new SingletonObservableList(e);
    }

    public static <E> ObservableSet<E> unmodifiableObservableSet(ObservableSet<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new UnmodifiableObservableSet(set);
    }

    public static <E> ObservableSet<E> checkedObservableSet(ObservableSet<E> set, Class<E> type) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new CheckedObservableSet(set, type);
    }

    public static <E> ObservableSet<E> synchronizedObservableSet(ObservableSet<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new SynchronizedObservableSet(set);
    }

    public static <E> ObservableSet<E> emptyObservableSet() {
        return EMPTY_OBSERVABLE_SET;
    }

    public static <T> void copy(ObservableList<? super T> dest, List<? extends T> src) {
        int srcSize = src.size();
        if (srcSize > dest.size()) {
            throw new IndexOutOfBoundsException("Source does not fit in dest");
        }
        Object[] array = dest.toArray();
        System.arraycopy(src.toArray(), 0, array, 0, srcSize);
        dest.setAll(array);
    }

    public static <T> void fill(ObservableList<? super T> list, T obj) {
        Object[] objArr = new Object[list.size()];
        Arrays.fill(objArr, obj);
        list.setAll(objArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T> boolean replaceAll(ObservableList<T> observableList, T oldVal, T newVal) {
        Object[] array = observableList.toArray();
        boolean modified = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(oldVal)) {
                array[i] = newVal;
                modified = true;
            }
        }
        if (modified) {
            observableList.setAll((T[]) array);
        }
        return modified;
    }

    public static void reverse(ObservableList list) {
        Object[] newContent = list.toArray();
        for (int i = 0; i < newContent.length / 2; i++) {
            Object tmp = newContent[i];
            newContent[i] = newContent[(newContent.length - i) - 1];
            newContent[(newContent.length - i) - 1] = tmp;
        }
        list.setAll(newContent);
    }

    public static void rotate(ObservableList list, int distance) {
        Object[] newContent = list.toArray();
        int size = list.size();
        int distance2 = distance % size;
        if (distance2 < 0) {
            distance2 += size;
        }
        if (distance2 == 0) {
            return;
        }
        int cycleStart = 0;
        int nMoved = 0;
        while (nMoved != size) {
            Object displaced = newContent[cycleStart];
            int i = cycleStart;
            do {
                i += distance2;
                if (i >= size) {
                    i -= size;
                }
                Object tmp = newContent[i];
                newContent[i] = displaced;
                displaced = tmp;
                nMoved++;
            } while (i != cycleStart);
            cycleStart++;
        }
        list.setAll(newContent);
    }

    public static void shuffle(ObservableList<?> list) {
        if (r == null) {
            r = new Random();
        }
        shuffle(list, r);
    }

    public static void shuffle(ObservableList list, Random rnd) {
        Object[] newContent = list.toArray();
        for (int i = list.size(); i > 1; i--) {
            swap(newContent, i - 1, rnd.nextInt(i));
        }
        list.setAll(newContent);
    }

    private static void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static <T extends Comparable<? super T>> void sort(ObservableList<T> list) {
        if (list instanceof SortableList) {
            ((SortableList) list).sort();
            return;
        }
        List<T> newContent = new ArrayList<>(list);
        Collections.sort(newContent);
        list.setAll(newContent);
    }

    public static <T> void sort(ObservableList<T> list, Comparator<? super T> c) {
        if (list instanceof SortableList) {
            ((SortableList) list).sort(c);
            return;
        }
        List<T> newContent = new ArrayList<>(list);
        Collections.sort(newContent, c);
        list.setAll(newContent);
    }

    private static class EmptyObservableList<E> extends AbstractList<E> implements ObservableList<E> {
        private static final ListIterator iterator = new ListIterator() { // from class: com.brixcore.fakefx.collections.FXCollections.EmptyObservableList.1
            @Override // java.util.ListIterator, java.util.Iterator
            public boolean hasNext() {
                return false;
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public Object next() {
                throw new NoSuchElementException();
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.ListIterator
            public boolean hasPrevious() {
                return false;
            }

            @Override // java.util.ListIterator
            public Object previous() {
                throw new NoSuchElementException();
            }

            @Override // java.util.ListIterator
            public int nextIndex() {
                return 0;
            }

            @Override // java.util.ListIterator
            public int previousIndex() {
                return -1;
            }

            @Override // java.util.ListIterator
            public void set(Object e) {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.ListIterator
            public void add(Object e) {
                throw new UnsupportedOperationException();
            }
        };

        @Override // com.brixcore.fakefx.beans.Observable
        public final void addListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public final void removeListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void addListener(ListChangeListener<? super E> o) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void removeListener(ListChangeListener<? super E> o) {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return 0;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object o) {
            return false;
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
        public Iterator<E> iterator() {
            return iterator;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object o) {
            return -1;
        }

        @Override // java.util.AbstractList, java.util.List
        public int lastIndexOf(Object o) {
            return -1;
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<E> listIterator() {
            return iterator;
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<E> listIterator(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException();
            }
            return iterator;
        }

        @Override // java.util.AbstractList, java.util.List
        public List<E> subList(int fromIndex, int toIndex) {
            if (fromIndex != 0 || toIndex != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this;
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean addAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(Collection<? extends E> col) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean removeAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean retainAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void remove(int from, int to) {
            throw new UnsupportedOperationException();
        }
    }

    private static class SingletonObservableList<E> extends AbstractList<E> implements ObservableList<E> {
        private final E element;

        public SingletonObservableList(E element) {
            if (element == null) {
                throw new NullPointerException();
            }
            this.element = element;
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean addAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(Collection<? extends E> col) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean removeAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean retainAll(E... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void remove(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void addListener(ListChangeListener<? super E> o) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void removeListener(ListChangeListener<? super E> o) {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return 1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean isEmpty() {
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object o) {
            return this.element.equals(o);
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.element;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class UnmodifiableObservableListImpl<T> extends ObservableListBase<T> implements ObservableList<T> {
        private final ObservableList<T> backingList;
        private final ListChangeListener<T> listener = new ListChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$UnmodifiableObservableListImpl$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.collections.ListChangeListener
            public final void onChanged(ListChangeListener.Change change) {
                this.f$0.lambda$new$0(change);
            }
        };

        public UnmodifiableObservableListImpl(ObservableList<T> backingList) {
            this.backingList = backingList;
            this.backingList.addListener(new WeakListChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ListChangeListener.Change c) {
            fireChange(new SourceAdapterChange(this, c));
        }

        @Override // java.util.AbstractList, java.util.List
        public T get(int index) {
            return this.backingList.get(index);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.backingList.size();
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean addAll(T... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(T... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(Collection<? extends T> col) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean removeAll(T... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean retainAll(T... elements) {
            throw new UnsupportedOperationException();
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public void remove(int from, int to) {
            throw new UnsupportedOperationException();
        }
    }

    private static class SynchronizedList<T> implements List<T> {
        private final List<T> backingList;
        final Object mutex;

        SynchronizedList(List<T> list, Object mutex) {
            this.backingList = list;
            this.mutex = mutex;
        }

        SynchronizedList(List<T> list) {
            this.backingList = list;
            this.mutex = this;
        }

        @Override // java.util.List, java.util.Collection
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.backingList.size();
            }
            return size;
        }

        @Override // java.util.List, java.util.Collection
        public boolean isEmpty() {
            boolean zIsEmpty;
            synchronized (this.mutex) {
                zIsEmpty = this.backingList.isEmpty();
            }
            return zIsEmpty;
        }

        @Override // java.util.List, java.util.Collection
        public boolean contains(Object o) {
            boolean zContains;
            synchronized (this.mutex) {
                zContains = this.backingList.contains(o);
            }
            return zContains;
        }

        @Override // java.util.List, java.util.Collection, java.lang.Iterable
        public Iterator<T> iterator() {
            return this.backingList.iterator();
        }

        @Override // java.util.List, java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (this.mutex) {
                array = this.backingList.toArray();
            }
            return array;
        }

        @Override // java.util.List, java.util.Collection
        public <X> X[] toArray(X[] xArr) {
            X[] xArr2;
            synchronized (this.mutex) {
                xArr2 = (X[]) this.backingList.toArray(xArr);
            }
            return xArr2;
        }

        @Override // java.util.List, java.util.Collection
        public boolean add(T e) {
            boolean zAdd;
            synchronized (this.mutex) {
                zAdd = this.backingList.add(e);
            }
            return zAdd;
        }

        @Override // java.util.List, java.util.Collection
        public boolean remove(Object o) {
            boolean zRemove;
            synchronized (this.mutex) {
                zRemove = this.backingList.remove(o);
            }
            return zRemove;
        }

        @Override // java.util.List, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            boolean zContainsAll;
            synchronized (this.mutex) {
                zContainsAll = this.backingList.containsAll(c);
            }
            return zContainsAll;
        }

        @Override // java.util.List, java.util.Collection
        public boolean addAll(Collection<? extends T> c) {
            boolean zAddAll;
            synchronized (this.mutex) {
                zAddAll = this.backingList.addAll(c);
            }
            return zAddAll;
        }

        @Override // java.util.List
        public boolean addAll(int index, Collection<? extends T> c) {
            boolean zAddAll;
            synchronized (this.mutex) {
                zAddAll = this.backingList.addAll(index, c);
            }
            return zAddAll;
        }

        @Override // java.util.List, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            boolean zRemoveAll;
            synchronized (this.mutex) {
                zRemoveAll = this.backingList.removeAll(c);
            }
            return zRemoveAll;
        }

        @Override // java.util.List, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            boolean zRetainAll;
            synchronized (this.mutex) {
                zRetainAll = this.backingList.retainAll(c);
            }
            return zRetainAll;
        }

        @Override // java.util.List, java.util.Collection
        public void clear() {
            synchronized (this.mutex) {
                this.backingList.clear();
            }
        }

        @Override // java.util.List
        public T get(int index) {
            T t;
            synchronized (this.mutex) {
                t = this.backingList.get(index);
            }
            return t;
        }

        @Override // java.util.List
        public T set(int index, T element) {
            T t;
            synchronized (this.mutex) {
                t = this.backingList.set(index, element);
            }
            return t;
        }

        @Override // java.util.List
        public void add(int index, T element) {
            synchronized (this.mutex) {
                this.backingList.add(index, element);
            }
        }

        @Override // java.util.List
        public T remove(int index) {
            T tRemove;
            synchronized (this.mutex) {
                tRemove = this.backingList.remove(index);
            }
            return tRemove;
        }

        @Override // java.util.List
        public int indexOf(Object o) {
            int iIndexOf;
            synchronized (this.mutex) {
                iIndexOf = this.backingList.indexOf(o);
            }
            return iIndexOf;
        }

        @Override // java.util.List
        public int lastIndexOf(Object o) {
            int iLastIndexOf;
            synchronized (this.mutex) {
                iLastIndexOf = this.backingList.lastIndexOf(o);
            }
            return iLastIndexOf;
        }

        @Override // java.util.List
        public ListIterator<T> listIterator() {
            return this.backingList.listIterator();
        }

        @Override // java.util.List
        public ListIterator<T> listIterator(int index) {
            ListIterator<T> listIterator;
            synchronized (this.mutex) {
                listIterator = this.backingList.listIterator(index);
            }
            return listIterator;
        }

        @Override // java.util.List
        public List<T> subList(int fromIndex, int toIndex) {
            SynchronizedList synchronizedList;
            synchronized (this.mutex) {
                synchronizedList = new SynchronizedList(this.backingList.subList(fromIndex, toIndex), this.mutex);
            }
            return synchronizedList;
        }

        public String toString() {
            String string;
            synchronized (this.mutex) {
                string = this.backingList.toString();
            }
            return string;
        }

        @Override // java.util.List, java.util.Collection
        public int hashCode() {
            int iHashCode;
            synchronized (this.mutex) {
                iHashCode = this.backingList.hashCode();
            }
            return iHashCode;
        }

        @Override // java.util.List, java.util.Collection
        public boolean equals(Object o) {
            boolean zEquals;
            synchronized (this.mutex) {
                zEquals = this.backingList.equals(o);
            }
            return zEquals;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class SynchronizedObservableList<T> extends SynchronizedList<T> implements ObservableList<T> {
        private final ObservableList<T> backingList;
        private ListListenerHelper helper;
        private final ListChangeListener<T> listener;

        SynchronizedObservableList(ObservableList<T> seq) {
            super(seq);
            this.backingList = seq;
            this.listener = new ListChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$SynchronizedObservableList$$ExternalSyntheticLambda0
                @Override // com.brixcore.fakefx.collections.ListChangeListener
                public final void onChanged(ListChangeListener.Change change) {
                    this.f$0.lambda$new$0(change);
                }
            };
            this.backingList.addListener(new WeakListChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ListChangeListener.Change c) {
            ListListenerHelper.fireValueChangedEvent(this.helper, new SourceAdapterChange(this, c));
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean addAll(T... elements) {
            boolean zAddAll;
            synchronized (this.mutex) {
                zAddAll = this.backingList.addAll(elements);
            }
            return zAddAll;
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(T... elements) {
            boolean all;
            synchronized (this.mutex) {
                all = this.backingList.setAll(elements);
            }
            return all;
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean removeAll(T... elements) {
            boolean zRemoveAll;
            synchronized (this.mutex) {
                zRemoveAll = this.backingList.removeAll(elements);
            }
            return zRemoveAll;
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean retainAll(T... elements) {
            boolean zRetainAll;
            synchronized (this.mutex) {
                zRetainAll = this.backingList.retainAll(elements);
            }
            return zRetainAll;
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void remove(int from, int to) {
            synchronized (this.mutex) {
                this.backingList.remove(from, to);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(Collection<? extends T> col) {
            boolean all;
            synchronized (this.mutex) {
                all = this.backingList.setAll(col);
            }
            return all;
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public final void addListener(InvalidationListener listener) {
            synchronized (this.mutex) {
                this.helper = ListListenerHelper.addListener(this.helper, listener);
            }
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public final void removeListener(InvalidationListener listener) {
            synchronized (this.mutex) {
                this.helper = ListListenerHelper.removeListener(this.helper, listener);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void addListener(ListChangeListener<? super T> listener) {
            synchronized (this.mutex) {
                this.helper = ListListenerHelper.addListener(this.helper, listener);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableList
        public void removeListener(ListChangeListener<? super T> listener) {
            synchronized (this.mutex) {
                this.helper = ListListenerHelper.removeListener(this.helper, listener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CheckedObservableList<T> extends ObservableListBase<T> implements ObservableList<T> {
        private final ObservableList<T> list;
        private final ListChangeListener<T> listener;
        private final Class<T> type;

        CheckedObservableList(ObservableList<T> list, Class<T> type) {
            if (list == null || type == null) {
                throw new NullPointerException();
            }
            this.list = list;
            this.type = type;
            this.listener = new ListChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$CheckedObservableList$$ExternalSyntheticLambda0
                @Override // com.brixcore.fakefx.collections.ListChangeListener
                public final void onChanged(ListChangeListener.Change change) {
                    this.f$0.lambda$new$0(change);
                }
            };
            list.addListener(new WeakListChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ListChangeListener.Change c) {
            fireChange(new SourceAdapterChange(this, c));
        }

        void typeCheck(Object o) {
            if (o != null && !this.type.isInstance(o)) {
                throw new ClassCastException("Attempt to insert " + o.getClass() + " element into collection with element type " + this.type);
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.list.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object o) {
            return this.list.contains(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public Object[] toArray() {
            return this.list.toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public <X> X[] toArray(X[] xArr) {
            return (X[]) this.list.toArray(xArr);
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            return this.list.toString();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean remove(Object o) {
            return this.list.remove(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean containsAll(Collection<?> coll) {
            return this.list.containsAll(coll);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean removeAll(Collection<?> coll) {
            return this.list.removeAll(coll);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean retainAll(Collection<?> coll) {
            return this.list.retainAll(coll);
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean removeAll(T... elements) {
            return this.list.removeAll(elements);
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean retainAll(T... elements) {
            return this.list.retainAll(elements);
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public void remove(int from, int to) {
            this.list.remove(from, to);
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public void clear() {
            this.list.clear();
        }

        @Override // java.util.AbstractList, java.util.Collection, java.util.List
        public boolean equals(Object o) {
            return o == this || this.list.equals(o);
        }

        @Override // java.util.AbstractList, java.util.Collection, java.util.List
        public int hashCode() {
            return this.list.hashCode();
        }

        @Override // java.util.AbstractList, java.util.List
        public T get(int index) {
            return this.list.get(index);
        }

        @Override // java.util.AbstractList, java.util.List
        public T remove(int index) {
            return this.list.remove(index);
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Override // java.util.AbstractList, java.util.List
        public int lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }

        @Override // java.util.AbstractList, java.util.List
        public T set(int index, T element) {
            typeCheck(element);
            return this.list.set(index, element);
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int index, T element) {
            typeCheck(element);
            this.list.add(index, element);
        }

        @Override // java.util.AbstractList, java.util.List
        public boolean addAll(int index, Collection<? extends T> c) {
            try {
                return this.list.addAll(index, Arrays.asList(c.toArray((Object[]) Array.newInstance((Class<?>) this.type, 0))));
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean addAll(Collection<? extends T> coll) {
            try {
                return this.list.addAll(Arrays.asList(coll.toArray((Object[]) Array.newInstance((Class<?>) this.type, 0))));
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<T> listIterator() {
            return listIterator(0);
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<T> listIterator(final int index) {
            return new ListIterator<T>() { // from class: com.brixcore.fakefx.collections.FXCollections.CheckedObservableList.1
                ListIterator<T> i;

                {
                    this.i = (ListIterator<T>) CheckedObservableList.this.list.listIterator(index);
                }

                @Override // java.util.ListIterator, java.util.Iterator
                public boolean hasNext() {
                    return this.i.hasNext();
                }

                @Override // java.util.ListIterator, java.util.Iterator
                public T next() {
                    return this.i.next();
                }

                @Override // java.util.ListIterator
                public boolean hasPrevious() {
                    return this.i.hasPrevious();
                }

                @Override // java.util.ListIterator
                public T previous() {
                    return this.i.previous();
                }

                @Override // java.util.ListIterator
                public int nextIndex() {
                    return this.i.nextIndex();
                }

                @Override // java.util.ListIterator
                public int previousIndex() {
                    return this.i.previousIndex();
                }

                @Override // java.util.ListIterator, java.util.Iterator
                public void remove() {
                    this.i.remove();
                }

                @Override // java.util.ListIterator
                public void set(T e) {
                    CheckedObservableList.this.typeCheck(e);
                    this.i.set(e);
                }

                @Override // java.util.ListIterator
                public void add(T e) {
                    CheckedObservableList.this.typeCheck(e);
                    this.i.add(e);
                }
            };
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
        public Iterator<T> iterator() {
            return new Iterator<T>() { // from class: com.brixcore.fakefx.collections.FXCollections.CheckedObservableList.2
                private final Iterator<T> it;

                {
                    this.it = (Iterator<T>) CheckedObservableList.this.list.iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.it.hasNext();
                }

                @Override // java.util.Iterator
                public T next() {
                    return this.it.next();
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.it.remove();
                }
            };
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean add(T e) {
            typeCheck(e);
            return this.list.add(e);
        }

        @Override // java.util.AbstractList, java.util.List
        public List<T> subList(int fromIndex, int toIndex) {
            return Collections.checkedList(this.list.subList(fromIndex, toIndex), this.type);
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean addAll(T... tArr) {
            try {
                Object[] objArr = (Object[]) Array.newInstance((Class<?>) this.type, tArr.length);
                System.arraycopy(tArr, 0, objArr, 0, tArr.length);
                return this.list.addAll((T[]) objArr);
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(T... tArr) {
            try {
                Object[] objArr = (Object[]) Array.newInstance((Class<?>) this.type, tArr.length);
                System.arraycopy(tArr, 0, objArr, 0, tArr.length);
                return this.list.setAll((T[]) objArr);
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
        public boolean setAll(Collection<? extends T> col) {
            try {
                return this.list.setAll(Arrays.asList(col.toArray((Object[]) Array.newInstance((Class<?>) this.type, 0))));
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        }
    }

    private static class EmptyObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void addListener(SetChangeListener<? super E> listener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void removeListener(SetChangeListener<? super E> listener) {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return 0;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public Object[] toArray() {
            return new Object[0];
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public <X> X[] toArray(X[] a) {
            if (a.length > 0) {
                a[0] = null;
            }
            return a;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<E> iterator() {
            return new Iterator() { // from class: com.brixcore.fakefx.collections.FXCollections.EmptyObservableSet.1
                @Override // java.util.Iterator
                public boolean hasNext() {
                    return false;
                }

                @Override // java.util.Iterator
                public Object next() {
                    throw new NoSuchElementException();
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class UnmodifiableObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
        private final ObservableSet<E> backingSet;
        private SetChangeListener<E> listener = null;
        private SetListenerHelper<E> listenerHelper;

        public UnmodifiableObservableSet(ObservableSet<E> backingSet) {
            this.backingSet = backingSet;
        }

        private void initListener() {
            if (this.listener == null) {
                this.listener = new SetChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$UnmodifiableObservableSet$$ExternalSyntheticLambda0
                    @Override // com.brixcore.fakefx.collections.SetChangeListener
                    public final void onChanged(SetChangeListener.Change change) {
                        this.f$0.lambda$initListener$0(change);
                    }
                };
                this.backingSet.addListener(new WeakSetChangeListener(this.listener));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$initListener$0(SetChangeListener.Change c) {
            callObservers(new SetAdapterChange(this, c));
        }

        private void callObservers(SetChangeListener.Change<? extends E> change) {
            SetListenerHelper.fireValueChangedEvent(this.listenerHelper, change);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<E> iterator() {
            return new Iterator<E>() { // from class: com.brixcore.fakefx.collections.FXCollections.UnmodifiableObservableSet.1
                private final Iterator<? extends E> i;

                {
                    this.i = UnmodifiableObservableSet.this.backingSet.iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.i.hasNext();
                }

                @Override // java.util.Iterator
                public E next() {
                    return this.i.next();
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.backingSet.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return this.backingSet.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            return this.backingSet.contains(o);
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
            initListener();
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void addListener(SetChangeListener<? super E> listener) {
            initListener();
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void removeListener(SetChangeListener<? super E> listener) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    private static class SynchronizedSet<E> implements Set<E> {
        private final Set<E> backingSet;
        final Object mutex;

        SynchronizedSet(Set<E> set, Object mutex) {
            this.backingSet = set;
            this.mutex = mutex;
        }

        SynchronizedSet(Set<E> set) {
            this.backingSet = set;
            this.mutex = this;
        }

        @Override // java.util.Set, java.util.Collection
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.backingSet.size();
            }
            return size;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean isEmpty() {
            boolean zIsEmpty;
            synchronized (this.mutex) {
                zIsEmpty = this.backingSet.isEmpty();
            }
            return zIsEmpty;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean contains(Object o) {
            boolean zContains;
            synchronized (this.mutex) {
                zContains = this.backingSet.contains(o);
            }
            return zContains;
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return this.backingSet.iterator();
        }

        @Override // java.util.Set, java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (this.mutex) {
                array = this.backingSet.toArray();
            }
            return array;
        }

        @Override // java.util.Set, java.util.Collection
        public <X> X[] toArray(X[] xArr) {
            X[] xArr2;
            synchronized (this.mutex) {
                xArr2 = (X[]) this.backingSet.toArray(xArr);
            }
            return xArr2;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean add(E e) {
            boolean zAdd;
            synchronized (this.mutex) {
                zAdd = this.backingSet.add(e);
            }
            return zAdd;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean remove(Object o) {
            boolean zRemove;
            synchronized (this.mutex) {
                zRemove = this.backingSet.remove(o);
            }
            return zRemove;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            boolean zContainsAll;
            synchronized (this.mutex) {
                zContainsAll = this.backingSet.containsAll(c);
            }
            return zContainsAll;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean addAll(Collection<? extends E> c) {
            boolean zAddAll;
            synchronized (this.mutex) {
                zAddAll = this.backingSet.addAll(c);
            }
            return zAddAll;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            boolean zRetainAll;
            synchronized (this.mutex) {
                zRetainAll = this.backingSet.retainAll(c);
            }
            return zRetainAll;
        }

        @Override // java.util.Set, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            boolean zRemoveAll;
            synchronized (this.mutex) {
                zRemoveAll = this.backingSet.removeAll(c);
            }
            return zRemoveAll;
        }

        @Override // java.util.Set, java.util.Collection
        public void clear() {
            synchronized (this.mutex) {
                this.backingSet.clear();
            }
        }

        @Override // java.util.Set, java.util.Collection
        public boolean equals(Object o) {
            boolean zEquals;
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                zEquals = this.backingSet.equals(o);
            }
            return zEquals;
        }

        @Override // java.util.Set, java.util.Collection
        public int hashCode() {
            int iHashCode;
            synchronized (this.mutex) {
                iHashCode = this.backingSet.hashCode();
            }
            return iHashCode;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class SynchronizedObservableSet<E> extends SynchronizedSet<E> implements ObservableSet<E> {
        private final ObservableSet<E> backingSet;
        private final SetChangeListener<E> listener;
        private SetListenerHelper listenerHelper;

        SynchronizedObservableSet(ObservableSet<E> set) {
            super(set);
            this.backingSet = set;
            this.listener = new SetChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$SynchronizedObservableSet$$ExternalSyntheticLambda0
                @Override // com.brixcore.fakefx.collections.SetChangeListener
                public final void onChanged(SetChangeListener.Change change) {
                    this.f$0.lambda$new$0(change);
                }
            };
            this.backingSet.addListener(new WeakSetChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(SetChangeListener.Change c) {
            SetListenerHelper.fireValueChangedEvent(this.listenerHelper, new SetAdapterChange(this, c));
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
            synchronized (this.mutex) {
                this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener);
            }
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
            synchronized (this.mutex) {
                this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void addListener(SetChangeListener<? super E> listener) {
            synchronized (this.mutex) {
                this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void removeListener(SetChangeListener<? super E> listener) {
            synchronized (this.mutex) {
                this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CheckedObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
        private final ObservableSet<E> backingSet;
        private final SetChangeListener<E> listener;
        private SetListenerHelper listenerHelper;
        private final Class<E> type;

        CheckedObservableSet(ObservableSet<E> set, Class<E> type) {
            if (set == null || type == null) {
                throw new NullPointerException();
            }
            this.backingSet = set;
            this.type = type;
            this.listener = new SetChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$CheckedObservableSet$$ExternalSyntheticLambda0
                @Override // com.brixcore.fakefx.collections.SetChangeListener
                public final void onChanged(SetChangeListener.Change change) {
                    this.f$0.lambda$new$0(change);
                }
            };
            this.backingSet.addListener(new WeakSetChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(SetChangeListener.Change c) {
            callObservers(new SetAdapterChange(this, c));
        }

        private void callObservers(SetChangeListener.Change<? extends E> c) {
            SetListenerHelper.fireValueChangedEvent(this.listenerHelper, c);
        }

        void typeCheck(Object o) {
            if (o != null && !this.type.isInstance(o)) {
                throw new ClassCastException("Attempt to insert " + o.getClass() + " element into collection with element type " + this.type);
            }
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void addListener(SetChangeListener<? super E> listener) {
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.collections.ObservableSet
        public void removeListener(SetChangeListener<? super E> listener) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.backingSet.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return this.backingSet.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            return this.backingSet.contains(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public Object[] toArray() {
            return this.backingSet.toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public <T> T[] toArray(T[] tArr) {
            return (T[]) this.backingSet.toArray(tArr);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean add(E e) {
            typeCheck(e);
            return this.backingSet.add(e);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            return this.backingSet.remove(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean containsAll(Collection<?> c) {
            return this.backingSet.containsAll(c);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean addAll(Collection<? extends E> c) {
            try {
                return this.backingSet.addAll(Arrays.asList(c.toArray((Object[]) Array.newInstance((Class<?>) this.type, 0))));
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean retainAll(Collection<?> c) {
            return this.backingSet.retainAll(c);
        }

        @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean removeAll(Collection<?> c) {
            return this.backingSet.removeAll(c);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.backingSet.clear();
        }

        @Override // java.util.AbstractSet, java.util.Collection, java.util.Set
        public boolean equals(Object o) {
            return o == this || this.backingSet.equals(o);
        }

        @Override // java.util.AbstractSet, java.util.Collection, java.util.Set
        public int hashCode() {
            return this.backingSet.hashCode();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<E> iterator() {
            final Iterator<E> it = this.backingSet.iterator();
            return new Iterator<E>() { // from class: com.brixcore.fakefx.collections.FXCollections.CheckedObservableSet.1
                @Override // java.util.Iterator
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override // java.util.Iterator
                public E next() {
                    return (E) it.next();
                }

                @Override // java.util.Iterator
                public void remove() {
                    it.remove();
                }
            };
        }
    }

    private static class EmptyObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void addListener(MapChangeListener<? super K, ? super V> listener) {
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void removeListener(MapChangeListener<? super K, ? super V> listener) {
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int size() {
            return 0;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean isEmpty() {
            return true;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            return false;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsValue(Object value) {
            return false;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V get(Object key) {
            return null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<K> keySet() {
            return FXCollections.emptyObservableSet();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Collection<V> values() {
            return FXCollections.emptyObservableSet();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            return FXCollections.emptyObservableSet();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean equals(Object o) {
            return (o instanceof Map) && ((Map) o).isEmpty();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int hashCode() {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CheckedObservableMap<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
        private final ObservableMap<K, V> backingMap;
        private final Class<K> keyType;
        private MapListenerHelper listenerHelper;
        private final Class<V> valueType;
        private transient Set<Map.Entry<K, V>> entrySet = null;
        private final MapChangeListener<K, V> listener = new MapChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$CheckedObservableMap$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.collections.MapChangeListener
            public final void onChanged(MapChangeListener.Change change) {
                this.f$0.lambda$new$0(change);
            }
        };

        CheckedObservableMap(ObservableMap<K, V> map, Class<K> keyType, Class<V> valueType) {
            this.backingMap = map;
            this.keyType = keyType;
            this.valueType = valueType;
            this.backingMap.addListener(new WeakMapChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(MapChangeListener.Change c) {
            callObservers(new MapAdapterChange(this, c));
        }

        private void callObservers(MapChangeListener.Change<? extends K, ? extends V> c) {
            MapListenerHelper.fireValueChangedEvent(this.listenerHelper, c);
        }

        void typeCheck(Object key, Object value) {
            if (key != null && !this.keyType.isInstance(key)) {
                throw new ClassCastException("Attempt to insert " + key.getClass() + " key into map with key type " + this.keyType);
            }
            if (value != null && !this.valueType.isInstance(value)) {
                throw new ClassCastException("Attempt to insert " + value.getClass() + " value into map with value type " + this.valueType);
            }
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
        public void addListener(MapChangeListener<? super K, ? super V> listener) {
            this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener);
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void removeListener(MapChangeListener<? super K, ? super V> listener) {
            this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener);
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
        public V put(K key, V value) {
            typeCheck(key, value);
            return this.backingMap.put(key, value);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V remove(Object key) {
            return this.backingMap.remove(key);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractMap, java.util.Map
        public void putAll(Map map) {
            Object[] array = map.entrySet().toArray();
            ArrayList<Map.Entry> arrayList = new ArrayList(array.length);
            for (Object obj : array) {
                Map.Entry entry = (Map.Entry) obj;
                Object key = entry.getKey();
                Object value = entry.getValue();
                typeCheck(key, value);
                arrayList.add(new AbstractMap.SimpleImmutableEntry(key, value));
            }
            for (Map.Entry entry2 : arrayList) {
                this.backingMap.put(entry2.getKey(), entry2.getValue());
            }
        }

        @Override // java.util.AbstractMap, java.util.Map
        public void clear() {
            this.backingMap.clear();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<K> keySet() {
            return this.backingMap.keySet();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Collection<V> values() {
            return this.backingMap.values();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set entrySet() {
            if (this.entrySet == null) {
                this.entrySet = new CheckedEntrySet(this.backingMap.entrySet(), this.valueType);
            }
            return this.entrySet;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean equals(Object o) {
            return o == this || this.backingMap.equals(o);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int hashCode() {
            return this.backingMap.hashCode();
        }

        static class CheckedEntrySet<K, V> implements Set<Map.Entry<K, V>> {
            private final Set<Map.Entry<K, V>> s;
            private final Class<V> valueType;

            CheckedEntrySet(Set<Map.Entry<K, V>> s, Class<V> valueType) {
                this.s = s;
                this.valueType = valueType;
            }

            @Override // java.util.Set, java.util.Collection
            public int size() {
                return this.s.size();
            }

            @Override // java.util.Set, java.util.Collection
            public boolean isEmpty() {
                return this.s.isEmpty();
            }

            public String toString() {
                return this.s.toString();
            }

            @Override // java.util.Set, java.util.Collection
            public int hashCode() {
                return this.s.hashCode();
            }

            @Override // java.util.Set, java.util.Collection
            public void clear() {
                this.s.clear();
            }

            @Override // java.util.Set, java.util.Collection
            public boolean add(Map.Entry<K, V> e) {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.Set, java.util.Collection
            public boolean addAll(Collection<? extends Map.Entry<K, V>> coll) {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.Set, java.util.Collection, java.lang.Iterable
            public Iterator<Map.Entry<K, V>> iterator() {
                final Iterator<Map.Entry<K, V>> i = this.s.iterator();
                final Class<V> valueType = this.valueType;
                return new Iterator<Map.Entry<K, V>>() { // from class: com.brixcore.fakefx.collections.FXCollections.CheckedObservableMap.CheckedEntrySet.1
                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        i.remove();
                    }

                    @Override // java.util.Iterator
                    public Map.Entry<K, V> next() {
                        return CheckedEntrySet.checkedEntry((Map.Entry) i.next(), valueType);
                    }
                };
            }

            @Override // java.util.Set, java.util.Collection
            public Object[] toArray() {
                Object[] dest;
                Object[] source = this.s.toArray();
                if (CheckedEntry.class.isInstance(source.getClass().getComponentType())) {
                    dest = source;
                } else {
                    dest = new Object[source.length];
                }
                for (int i = 0; i < source.length; i++) {
                    dest[i] = checkedEntry((Map.Entry) source[i], this.valueType);
                }
                return dest;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Set, java.util.Collection
            public <T> T[] toArray(T[] tArr) {
                T[] tArr2 = (T[]) this.s.toArray(tArr.length == 0 ? tArr : Arrays.copyOf(tArr, 0));
                for (int i = 0; i < tArr2.length; i++) {
                    tArr2[i] = checkedEntry((Map.Entry) tArr2[i], this.valueType);
                }
                if (tArr2.length > tArr.length) {
                    return tArr2;
                }
                System.arraycopy(tArr2, 0, tArr, 0, tArr2.length);
                if (tArr.length > tArr2.length) {
                    tArr[tArr2.length] = null;
                }
                return tArr;
            }

            @Override // java.util.Set, java.util.Collection
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry<?, ?> e = (Map.Entry) o;
                return this.s.contains(e instanceof CheckedEntry ? e : checkedEntry(e, this.valueType));
            }

            @Override // java.util.Set, java.util.Collection
            public boolean containsAll(Collection<?> c) {
                for (Object o : c) {
                    if (!contains(o)) {
                        return false;
                    }
                }
                return true;
            }

            @Override // java.util.Set, java.util.Collection
            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                return this.s.remove(new AbstractMap.SimpleImmutableEntry((Map.Entry) o));
            }

            @Override // java.util.Set, java.util.Collection
            public boolean removeAll(Collection<?> c) {
                return batchRemove(c, false);
            }

            @Override // java.util.Set, java.util.Collection
            public boolean retainAll(Collection<?> c) {
                return batchRemove(c, true);
            }

            private boolean batchRemove(Collection<?> c, boolean complement) {
                boolean modified = false;
                Iterator<Map.Entry<K, V>> it = iterator();
                while (it.hasNext()) {
                    if (c.contains(it.next()) != complement) {
                        it.remove();
                        modified = true;
                    }
                }
                return modified;
            }

            @Override // java.util.Set, java.util.Collection
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof Set)) {
                    return false;
                }
                Set<?> that = (Set) o;
                return that.size() == this.s.size() && containsAll(that);
            }

            static <K, V, T> CheckedEntry<K, V, T> checkedEntry(Map.Entry<K, V> e, Class<T> valueType) {
                return new CheckedEntry<>(e, valueType);
            }

            private static class CheckedEntry<K, V, T> implements Map.Entry<K, V> {
                private final Map.Entry<K, V> e;
                private final Class<T> valueType;

                CheckedEntry(Map.Entry<K, V> e, Class<T> valueType) {
                    this.e = e;
                    this.valueType = valueType;
                }

                @Override // java.util.Map.Entry
                public K getKey() {
                    return this.e.getKey();
                }

                @Override // java.util.Map.Entry
                public V getValue() {
                    return this.e.getValue();
                }

                @Override // java.util.Map.Entry
                public int hashCode() {
                    return this.e.hashCode();
                }

                public String toString() {
                    return this.e.toString();
                }

                @Override // java.util.Map.Entry
                public V setValue(V value) {
                    if (value != null && !this.valueType.isInstance(value)) {
                        throw new ClassCastException(badValueMsg(value));
                    }
                    return this.e.setValue(value);
                }

                private String badValueMsg(Object value) {
                    return "Attempt to insert " + value.getClass() + " value into map with value type " + this.valueType;
                }

                @Override // java.util.Map.Entry
                public boolean equals(Object o) {
                    if (o == this) {
                        return true;
                    }
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    return this.e.equals(new AbstractMap.SimpleImmutableEntry((Map.Entry) o));
                }
            }
        }
    }

    private static class SynchronizedMap<K, V> implements Map<K, V> {
        private final Map<K, V> backingMap;
        private transient Set<K> keySet = null;
        private transient Set<Map.Entry<K, V>> entrySet = null;
        private transient Collection<V> values = null;
        final Object mutex = this;

        SynchronizedMap(Map<K, V> map) {
            this.backingMap = map;
        }

        @Override // java.util.Map
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.backingMap.size();
            }
            return size;
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            boolean zIsEmpty;
            synchronized (this.mutex) {
                zIsEmpty = this.backingMap.isEmpty();
            }
            return zIsEmpty;
        }

        @Override // java.util.Map
        public boolean containsKey(Object key) {
            boolean zContainsKey;
            synchronized (this.mutex) {
                zContainsKey = this.backingMap.containsKey(key);
            }
            return zContainsKey;
        }

        @Override // java.util.Map
        public boolean containsValue(Object value) {
            boolean zContainsValue;
            synchronized (this.mutex) {
                zContainsValue = this.backingMap.containsValue(value);
            }
            return zContainsValue;
        }

        @Override // java.util.Map
        public V get(Object key) {
            V v;
            synchronized (this.mutex) {
                v = this.backingMap.get(key);
            }
            return v;
        }

        @Override // java.util.Map
        public V put(K key, V value) {
            V vPut;
            synchronized (this.mutex) {
                vPut = this.backingMap.put(key, value);
            }
            return vPut;
        }

        @Override // java.util.Map
        public V remove(Object key) {
            V vRemove;
            synchronized (this.mutex) {
                vRemove = this.backingMap.remove(key);
            }
            return vRemove;
        }

        @Override // java.util.Map
        public void putAll(Map<? extends K, ? extends V> m) {
            synchronized (this.mutex) {
                this.backingMap.putAll(m);
            }
        }

        @Override // java.util.Map
        public void clear() {
            synchronized (this.mutex) {
                this.backingMap.clear();
            }
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            Set<K> set;
            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = new SynchronizedSet(this.backingMap.keySet(), this.mutex);
                }
                set = this.keySet;
            }
            return set;
        }

        @Override // java.util.Map
        public Collection<V> values() {
            Collection<V> collection;
            synchronized (this.mutex) {
                if (this.values == null) {
                    this.values = new SynchronizedCollection(this.backingMap.values(), this.mutex);
                }
                collection = this.values;
            }
            return collection;
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            Set<Map.Entry<K, V>> set;
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = new SynchronizedSet(this.backingMap.entrySet(), this.mutex);
                }
                set = this.entrySet;
            }
            return set;
        }

        @Override // java.util.Map
        public boolean equals(Object o) {
            boolean zEquals;
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                zEquals = this.backingMap.equals(o);
            }
            return zEquals;
        }

        @Override // java.util.Map
        public int hashCode() {
            int iHashCode;
            synchronized (this.mutex) {
                iHashCode = this.backingMap.hashCode();
            }
            return iHashCode;
        }
    }

    private static class SynchronizedCollection<E> implements Collection<E> {
        private final Collection<E> backingCollection;
        final Object mutex;

        SynchronizedCollection(Collection<E> c, Object mutex) {
            this.backingCollection = c;
            this.mutex = mutex;
        }

        SynchronizedCollection(Collection<E> c) {
            this(c, new Object());
        }

        @Override // java.util.Collection
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.backingCollection.size();
            }
            return size;
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            boolean zIsEmpty;
            synchronized (this.mutex) {
                zIsEmpty = this.backingCollection.isEmpty();
            }
            return zIsEmpty;
        }

        @Override // java.util.Collection
        public boolean contains(Object o) {
            boolean zContains;
            synchronized (this.mutex) {
                zContains = this.backingCollection.contains(o);
            }
            return zContains;
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return this.backingCollection.iterator();
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (this.mutex) {
                array = this.backingCollection.toArray();
            }
            return array;
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            T[] tArr2;
            synchronized (this.mutex) {
                tArr2 = (T[]) this.backingCollection.toArray(tArr);
            }
            return tArr2;
        }

        @Override // java.util.Collection
        public boolean add(E e) {
            boolean zAdd;
            synchronized (this.mutex) {
                zAdd = this.backingCollection.add(e);
            }
            return zAdd;
        }

        @Override // java.util.Collection
        public boolean remove(Object o) {
            boolean zRemove;
            synchronized (this.mutex) {
                zRemove = this.backingCollection.remove(o);
            }
            return zRemove;
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> c) {
            boolean zContainsAll;
            synchronized (this.mutex) {
                zContainsAll = this.backingCollection.containsAll(c);
            }
            return zContainsAll;
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends E> c) {
            boolean zAddAll;
            synchronized (this.mutex) {
                zAddAll = this.backingCollection.addAll(c);
            }
            return zAddAll;
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> c) {
            boolean zRemoveAll;
            synchronized (this.mutex) {
                zRemoveAll = this.backingCollection.removeAll(c);
            }
            return zRemoveAll;
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> c) {
            boolean zRetainAll;
            synchronized (this.mutex) {
                zRetainAll = this.backingCollection.retainAll(c);
            }
            return zRetainAll;
        }

        @Override // java.util.Collection
        public void clear() {
            synchronized (this.mutex) {
                this.backingCollection.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class SynchronizedObservableMap<K, V> extends SynchronizedMap<K, V> implements ObservableMap<K, V> {
        private final ObservableMap<K, V> backingMap;
        private final MapChangeListener<K, V> listener;
        private MapListenerHelper listenerHelper;

        SynchronizedObservableMap(ObservableMap<K, V> map) {
            super(map);
            this.backingMap = map;
            this.listener = new MapChangeListener() { // from class: com.brixcore.fakefx.collections.FXCollections$SynchronizedObservableMap$$ExternalSyntheticLambda0
                @Override // com.brixcore.fakefx.collections.MapChangeListener
                public final void onChanged(MapChangeListener.Change change) {
                    this.f$0.lambda$new$0(change);
                }
            };
            this.backingMap.addListener(new WeakMapChangeListener(this.listener));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(MapChangeListener.Change c) {
            MapListenerHelper.fireValueChangedEvent(this.listenerHelper, new MapAdapterChange(this, c));
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void addListener(InvalidationListener listener) {
            synchronized (this.mutex) {
                this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener);
            }
        }

        @Override // com.brixcore.fakefx.beans.Observable
        public void removeListener(InvalidationListener listener) {
            synchronized (this.mutex) {
                this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void addListener(MapChangeListener<? super K, ? super V> listener) {
            synchronized (this.mutex) {
                this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener);
            }
        }

        @Override // com.brixcore.fakefx.collections.ObservableMap
        public void removeListener(MapChangeListener<? super K, ? super V> listener) {
            synchronized (this.mutex) {
                this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener);
            }
        }
    }
}
