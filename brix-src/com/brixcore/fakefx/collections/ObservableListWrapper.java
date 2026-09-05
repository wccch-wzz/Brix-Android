package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.util.Callback;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

/* JADX INFO: loaded from: classes3.dex */
public class ObservableListWrapper<E> extends ModifiableObservableListBase<E> implements ObservableList<E>, SortableList<E>, RandomAccess {
    private final List<E> backingList;
    private final ElementObserver elementObserver;
    private SortHelper helper;

    public ObservableListWrapper(List<E> list) {
        this.backingList = list;
        this.elementObserver = null;
    }

    public ObservableListWrapper(List<E> list, Callback<E, Observable[]> extractor) {
        this.backingList = list;
        this.elementObserver = new ElementObserver(extractor, new Callback<E, InvalidationListener>() { // from class: com.brixcore.fakefx.collections.ObservableListWrapper.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.util.Callback
            public InvalidationListener call(final E e) {
                return new InvalidationListener() { // from class: com.brixcore.fakefx.collections.ObservableListWrapper.1.1
                    @Override // com.brixcore.fakefx.beans.InvalidationListener
                    public void invalidated(Observable observable) {
                        ObservableListWrapper.this.beginChange();
                        int size = ObservableListWrapper.this.size();
                        for (int i = 0; i < size; i++) {
                            if (ObservableListWrapper.this.get(i) == e) {
                                ObservableListWrapper.this.nextUpdate(i);
                            }
                        }
                        ObservableListWrapper.this.endChange();
                    }
                };
            }
        }, this);
        int sz = this.backingList.size();
        for (int i = 0; i < sz; i++) {
            this.elementObserver.attachListener(this.backingList.get(i));
        }
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractList, java.util.List
    public E get(int index) {
        return this.backingList.get(index);
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.backingList.size();
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase
    protected void doAdd(int index, E element) {
        if (this.elementObserver != null) {
            this.elementObserver.attachListener(element);
        }
        this.backingList.add(index, element);
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase
    protected E doSet(int index, E element) {
        E removed = this.backingList.set(index, element);
        if (this.elementObserver != null) {
            this.elementObserver.detachListener(removed);
            this.elementObserver.attachListener(element);
        }
        return removed;
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase
    protected E doRemove(int index) {
        E removed = this.backingList.remove(index);
        if (this.elementObserver != null) {
            this.elementObserver.detachListener(removed);
        }
        return removed;
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object o) {
        return this.backingList.indexOf(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object o) {
        return this.backingList.lastIndexOf(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object o) {
        return this.backingList.contains(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean containsAll(Collection<?> c) {
        return this.backingList.containsAll(c);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        if (this.elementObserver != null) {
            int sz = size();
            for (int i = 0; i < sz; i++) {
                this.elementObserver.detachListener(get(i));
            }
        }
        if (hasListeners()) {
            beginChange();
            nextRemove(0, (List) this);
        }
        this.backingList.clear();
        this.modCount++;
        if (hasListeners()) {
            endChange();
        }
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public void remove(int fromIndex, int toIndex) {
        beginChange();
        for (int i = fromIndex; i < toIndex; i++) {
            remove(fromIndex);
        }
        endChange();
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        beginChange();
        BitSet bs = new BitSet(c.size());
        for (int i = 0; i < size(); i++) {
            if (c.contains(get(i))) {
                bs.set(i);
            }
        }
        if (!bs.isEmpty()) {
            int cur = size();
            while (true) {
                int iPreviousSetBit = bs.previousSetBit(cur - 1);
                cur = iPreviousSetBit;
                if (iPreviousSetBit < 0) {
                    break;
                }
                remove(cur);
            }
        }
        endChange();
        return !bs.isEmpty();
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean retainAll(Collection<?> c) {
        beginChange();
        BitSet bs = new BitSet(c.size());
        for (int i = 0; i < size(); i++) {
            if (!c.contains(get(i))) {
                bs.set(i);
            }
        }
        if (!bs.isEmpty()) {
            int cur = size();
            while (true) {
                int iPreviousSetBit = bs.previousSetBit(cur - 1);
                cur = iPreviousSetBit;
                if (iPreviousSetBit < 0) {
                    break;
                }
                remove(cur);
            }
        }
        endChange();
        return !bs.isEmpty();
    }

    @Override // com.brixcore.fakefx.collections.SortableList
    public void sort() {
        if (this.backingList.isEmpty()) {
            return;
        }
        int[] perm = getSortHelper().sort(this.backingList);
        fireChange(new NonIterableChange.SimplePermutationChange(0, size(), perm, this));
    }

    @Override // java.util.List, com.brixcore.fakefx.collections.SortableList
    public void sort(Comparator<? super E> comparator) {
        if (this.backingList.isEmpty()) {
            return;
        }
        int[] perm = getSortHelper().sort(this.backingList, comparator);
        fireChange(new NonIterableChange.SimplePermutationChange(0, size(), perm, this));
    }

    private SortHelper getSortHelper() {
        if (this.helper == null) {
            this.helper = new SortHelper();
        }
        return this.helper;
    }
}
