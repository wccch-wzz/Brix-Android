package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.util.Callback;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/* JADX INFO: loaded from: classes3.dex */
public final class ElementObservableListDecorator<E> extends ObservableListBase<E> implements ObservableList<E> {
    private final ObservableList<E> decoratedList;
    private final ListChangeListener<E> listener;
    private ElementObserver<E> observer;

    public ElementObservableListDecorator(ObservableList<E> observableList, Callback<E, Observable[]> callback) {
        this.observer = new ElementObserver<>(callback, new Callback<E, InvalidationListener>() { // from class: com.brixcore.fakefx.collections.ElementObservableListDecorator.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.brixcore.fakefx.util.Callback
            public InvalidationListener call(final E e) {
                return new InvalidationListener() { // from class: com.brixcore.fakefx.collections.ElementObservableListDecorator.1.1
                    @Override // com.brixcore.fakefx.beans.InvalidationListener
                    public void invalidated(Observable observable) {
                        ElementObservableListDecorator.this.beginChange();
                        int i = 0;
                        if (ElementObservableListDecorator.this.decoratedList instanceof RandomAccess) {
                            int size = ElementObservableListDecorator.this.size();
                            while (i < size) {
                                if (ElementObservableListDecorator.this.get(i) == e) {
                                    ElementObservableListDecorator.this.nextUpdate(i);
                                }
                                i++;
                            }
                        } else {
                            Iterator<E> it = ElementObservableListDecorator.this.iterator();
                            while (it.hasNext()) {
                                if (it.next() == e) {
                                    ElementObservableListDecorator.this.nextUpdate(i);
                                }
                                i++;
                            }
                        }
                        ElementObservableListDecorator.this.endChange();
                    }
                };
            }
        }, this);
        this.decoratedList = observableList;
        int size = this.decoratedList.size();
        for (int i = 0; i < size; i++) {
            this.observer.attachListener(this.decoratedList.get(i));
        }
        this.listener = new ListChangeListener<E>() { // from class: com.brixcore.fakefx.collections.ElementObservableListDecorator.2
            @Override // com.brixcore.fakefx.collections.ListChangeListener
            public void onChanged(ListChangeListener.Change<? extends E> c) {
                while (c.next()) {
                    if (c.wasAdded() || c.wasRemoved()) {
                        int removedSize = c.getRemovedSize();
                        List<? extends E> removed = c.getRemoved();
                        for (int i2 = 0; i2 < removedSize; i2++) {
                            ElementObservableListDecorator.this.observer.detachListener(removed.get(i2));
                        }
                        if (ElementObservableListDecorator.this.decoratedList instanceof RandomAccess) {
                            int to = c.getTo();
                            for (int i3 = c.getFrom(); i3 < to; i3++) {
                                ElementObservableListDecorator.this.observer.attachListener(ElementObservableListDecorator.this.decoratedList.get(i3));
                            }
                        } else {
                            for (E e : c.getAddedSubList()) {
                                ElementObservableListDecorator.this.observer.attachListener(e);
                            }
                        }
                    }
                }
                c.reset();
                ElementObservableListDecorator.this.fireChange(c);
            }
        };
        this.decoratedList.addListener(new WeakListChangeListener(this.listener));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.decoratedList.toArray(tArr);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        return this.decoratedList.toArray();
    }

    @Override // java.util.AbstractList, java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return this.decoratedList.subList(fromIndex, toIndex);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.decoratedList.size();
    }

    @Override // java.util.AbstractList, java.util.List
    public E set(int index, E element) {
        return this.decoratedList.set(index, element);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean retainAll(Collection<?> c) {
        return this.decoratedList.retainAll(c);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        return this.decoratedList.removeAll(c);
    }

    @Override // java.util.AbstractList, java.util.List
    public E remove(int index) {
        return this.decoratedList.remove(index);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object o) {
        return this.decoratedList.remove(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator(int index) {
        return this.decoratedList.listIterator(index);
    }

    @Override // java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator() {
        return this.decoratedList.listIterator();
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object o) {
        return this.decoratedList.lastIndexOf(o);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator<E> iterator() {
        return this.decoratedList.iterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean isEmpty() {
        return this.decoratedList.isEmpty();
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object o) {
        return this.decoratedList.indexOf(o);
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        return this.decoratedList.get(index);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean containsAll(Collection<?> c) {
        return this.decoratedList.containsAll(c);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object o) {
        return this.decoratedList.contains(o);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.decoratedList.clear();
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        return this.decoratedList.addAll(index, c);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends E> c) {
        return this.decoratedList.addAll(c);
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        this.decoratedList.add(index, element);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(E e) {
        return this.decoratedList.add(e);
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public boolean setAll(Collection<? extends E> col) {
        return this.decoratedList.setAll(col);
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public boolean setAll(E... elements) {
        return this.decoratedList.setAll(elements);
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public boolean retainAll(E... elements) {
        return this.decoratedList.retainAll(elements);
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public boolean removeAll(E... elements) {
        return this.decoratedList.removeAll(elements);
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public void remove(int from, int to) {
        this.decoratedList.remove(from, to);
    }

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public boolean addAll(E... elements) {
        return this.decoratedList.addAll(elements);
    }
}
