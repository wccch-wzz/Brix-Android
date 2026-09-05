package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.util.Callback;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes3.dex */
public final class ObservableSequentialListWrapper<E> extends ModifiableObservableListBase<E> implements ObservableList<E>, SortableList<E> {
    private final List<E> backingList;
    private final ElementObserver elementObserver;
    private SortHelper helper;

    public ObservableSequentialListWrapper(List<E> list) {
        this.backingList = list;
        this.elementObserver = null;
    }

    public ObservableSequentialListWrapper(List<E> list, Callback<E, Observable[]> extractor) {
        this.backingList = list;
        this.elementObserver = new ElementObserver(extractor, new Callback<E, InvalidationListener>() { // from class: com.brixcore.fakefx.collections.ObservableSequentialListWrapper.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.util.Callback
            public InvalidationListener call(final E e) {
                return new InvalidationListener() { // from class: com.brixcore.fakefx.collections.ObservableSequentialListWrapper.1.1
                    @Override // com.brixcore.fakefx.beans.InvalidationListener
                    public void invalidated(Observable observable) {
                        ObservableSequentialListWrapper.this.beginChange();
                        int i = 0;
                        Iterator<E> it = ObservableSequentialListWrapper.this.backingList.iterator();
                        while (it.hasNext()) {
                            if (it.next() == e) {
                                ObservableSequentialListWrapper.this.nextUpdate(i);
                            }
                            i++;
                        }
                        ObservableSequentialListWrapper.this.endChange();
                    }
                };
            }
        }, this);
        for (E e : this.backingList) {
            this.elementObserver.attachListener(e);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object o) {
        return this.backingList.contains(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean containsAll(Collection<?> c) {
        return this.backingList.containsAll(c);
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object o) {
        return this.backingList.indexOf(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object o) {
        return this.backingList.lastIndexOf(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator(final int index) {
        return new ListIterator<E>() { // from class: com.brixcore.fakefx.collections.ObservableSequentialListWrapper.2
            private final ListIterator<E> backingIt;
            private E lastReturned;

            {
                this.backingIt = ObservableSequentialListWrapper.this.backingList.listIterator(index);
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public boolean hasNext() {
                return this.backingIt.hasNext();
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public E next() {
                E next = this.backingIt.next();
                this.lastReturned = next;
                return next;
            }

            @Override // java.util.ListIterator
            public boolean hasPrevious() {
                return this.backingIt.hasPrevious();
            }

            @Override // java.util.ListIterator
            public E previous() {
                E ePrevious = this.backingIt.previous();
                this.lastReturned = ePrevious;
                return ePrevious;
            }

            @Override // java.util.ListIterator
            public int nextIndex() {
                return this.backingIt.nextIndex();
            }

            @Override // java.util.ListIterator
            public int previousIndex() {
                return this.backingIt.previousIndex();
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public void remove() {
                ObservableSequentialListWrapper.this.beginChange();
                int idx = previousIndex();
                this.backingIt.remove();
                ObservableSequentialListWrapper.this.nextRemove(idx, this.lastReturned);
                ObservableSequentialListWrapper.this.endChange();
            }

            @Override // java.util.ListIterator
            public void set(E e) {
                ObservableSequentialListWrapper.this.beginChange();
                int idx = previousIndex();
                this.backingIt.set(e);
                ObservableSequentialListWrapper.this.nextSet(idx, this.lastReturned);
                ObservableSequentialListWrapper.this.endChange();
            }

            @Override // java.util.ListIterator
            public void add(E e) {
                ObservableSequentialListWrapper.this.beginChange();
                int idx = nextIndex();
                this.backingIt.add(e);
                ObservableSequentialListWrapper.this.nextAdd(idx, idx + 1);
                ObservableSequentialListWrapper.this.endChange();
            }
        };
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractList, java.util.List
    public E get(int index) {
        try {
            return this.backingList.listIterator(index).next();
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        try {
            beginChange();
            boolean modified = false;
            ListIterator<E> e1 = listIterator(index);
            Iterator<? extends E> e2 = c.iterator();
            while (e2.hasNext()) {
                e1.add(e2.next());
                modified = true;
            }
            endChange();
            return modified;
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase, java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.backingList.size();
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase
    protected void doAdd(int index, E element) {
        try {
            this.backingList.listIterator(index).add(element);
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase
    protected E doSet(int index, E element) {
        try {
            ListIterator<E> e = this.backingList.listIterator(index);
            E oldVal = e.next();
            e.set(element);
            return oldVal;
        } catch (NoSuchElementException e2) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    @Override // com.brixcore.fakefx.collections.ModifiableObservableListBase
    protected E doRemove(int index) {
        try {
            ListIterator<E> e = this.backingList.listIterator(index);
            E outCast = e.next();
            e.remove();
            return outCast;
        } catch (NoSuchElementException e2) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
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
