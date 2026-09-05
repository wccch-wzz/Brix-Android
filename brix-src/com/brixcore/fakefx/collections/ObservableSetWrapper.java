package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class ObservableSetWrapper<E> implements ObservableSet<E> {
    private final Set<E> backingSet;
    private SetListenerHelper<E> listenerHelper;

    public ObservableSetWrapper(Set<E> set) {
        this.backingSet = set;
    }

    private class SimpleAddChange extends SetChangeListener.Change<E> {
        private final E added;

        public SimpleAddChange(E added) {
            super(ObservableSetWrapper.this);
            this.added = added;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public boolean wasAdded() {
            return true;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public boolean wasRemoved() {
            return false;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public E getElementAdded() {
            return this.added;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public E getElementRemoved() {
            return null;
        }

        public String toString() {
            return "added " + this.added;
        }
    }

    private class SimpleRemoveChange extends SetChangeListener.Change<E> {
        private final E removed;

        public SimpleRemoveChange(E removed) {
            super(ObservableSetWrapper.this);
            this.removed = removed;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public boolean wasAdded() {
            return false;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public boolean wasRemoved() {
            return true;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public E getElementAdded() {
            return null;
        }

        @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
        public E getElementRemoved() {
            return this.removed;
        }

        public String toString() {
            return "removed " + this.removed;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callObservers(SetChangeListener.Change<E> change) {
        SetListenerHelper.fireValueChangedEvent(this.listenerHelper, change);
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
    public void addListener(SetChangeListener<? super E> observer) {
        this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, observer);
    }

    @Override // com.brixcore.fakefx.collections.ObservableSet
    public void removeListener(SetChangeListener<? super E> observer) {
        this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, observer);
    }

    @Override // java.util.Set, java.util.Collection
    public int size() {
        return this.backingSet.size();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean isEmpty() {
        return this.backingSet.isEmpty();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean contains(Object o) {
        return this.backingSet.contains(o);
    }

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    public Iterator iterator() {
        return new Iterator<E>() { // from class: com.brixcore.fakefx.collections.ObservableSetWrapper.1
            private final Iterator<E> backingIt;
            private E lastElement;

            {
                this.backingIt = ObservableSetWrapper.this.backingSet.iterator();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.backingIt.hasNext();
            }

            @Override // java.util.Iterator
            public E next() {
                this.lastElement = this.backingIt.next();
                return this.lastElement;
            }

            @Override // java.util.Iterator
            public void remove() {
                this.backingIt.remove();
                ObservableSetWrapper.this.callObservers(new SimpleRemoveChange(this.lastElement));
            }
        };
    }

    @Override // java.util.Set, java.util.Collection
    public Object[] toArray() {
        return this.backingSet.toArray();
    }

    @Override // java.util.Set, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.backingSet.toArray(tArr);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean add(E o) {
        boolean ret = this.backingSet.add(o);
        if (ret) {
            callObservers(new SimpleAddChange(o));
        }
        return ret;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean remove(Object o) {
        boolean ret = this.backingSet.remove(o);
        if (ret) {
            callObservers(new SimpleRemoveChange(o));
        }
        return ret;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.backingSet.containsAll(c);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        boolean ret = false;
        for (E element : c) {
            ret |= add(element);
        }
        return ret;
    }

    @Override // java.util.Set, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        return removeRetain(c, false);
    }

    @Override // java.util.Set, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        return removeRetain(c, true);
    }

    private boolean removeRetain(Collection<?> c, boolean remove) {
        boolean removed = false;
        Iterator<E> i = this.backingSet.iterator();
        while (i.hasNext()) {
            E element = i.next();
            if (remove == c.contains(element)) {
                removed = true;
                i.remove();
                callObservers(new SimpleRemoveChange(element));
            }
        }
        return removed;
    }

    @Override // java.util.Set, java.util.Collection
    public void clear() {
        Iterator<E> i = this.backingSet.iterator();
        while (i.hasNext()) {
            E element = i.next();
            i.remove();
            callObservers(new SimpleRemoveChange(element));
        }
    }

    public String toString() {
        return this.backingSet.toString();
    }

    @Override // java.util.Set, java.util.Collection
    public boolean equals(Object obj) {
        return this.backingSet.equals(obj);
    }

    @Override // java.util.Set, java.util.Collection
    public int hashCode() {
        return this.backingSet.hashCode();
    }
}
