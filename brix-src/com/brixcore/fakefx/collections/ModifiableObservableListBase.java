package com.brixcore.fakefx.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ModifiableObservableListBase<E> extends ObservableListBase<E> {
    protected abstract void doAdd(int i, E e);

    protected abstract E doRemove(int i);

    protected abstract E doSet(int i, E e);

    @Override // java.util.AbstractList, java.util.List
    public abstract E get(int i);

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public abstract int size();

    @Override // com.brixcore.fakefx.collections.ObservableListBase, com.brixcore.fakefx.collections.ObservableList
    public boolean setAll(Collection<? extends E> col) {
        if (isEmpty() && col.isEmpty()) {
            return false;
        }
        beginChange();
        try {
            clear();
            addAll(col);
            return true;
        } finally {
            endChange();
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends E> c) {
        beginChange();
        try {
            boolean res = super.addAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        beginChange();
        try {
            boolean res = super.addAll(index, c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override // java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex) {
        beginChange();
        try {
            super.removeRange(fromIndex, toIndex);
        } finally {
            endChange();
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        beginChange();
        try {
            boolean res = super.removeAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean retainAll(Collection<?> c) {
        beginChange();
        try {
            boolean res = super.retainAll(c);
            return res;
        } finally {
            endChange();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        doAdd(index, element);
        beginChange();
        nextAdd(index, index + 1);
        this.modCount++;
        endChange();
    }

    @Override // java.util.AbstractList, java.util.List
    public E set(int index, E element) {
        E old = doSet(index, element);
        beginChange();
        nextSet(index, old);
        endChange();
        return old;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i != -1) {
            remove(i);
            return true;
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public E remove(int index) {
        E old = doRemove(index);
        beginChange();
        nextRemove(index, old);
        this.modCount++;
        endChange();
        return old;
    }

    @Override // java.util.AbstractList, java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return new SubObservableList(super.subList(fromIndex, toIndex));
    }

    private class SubObservableList implements List<E> {
        private List<E> sublist;

        public SubObservableList(List<E> sublist) {
            this.sublist = sublist;
        }

        @Override // java.util.List, java.util.Collection
        public int size() {
            return this.sublist.size();
        }

        @Override // java.util.List, java.util.Collection
        public boolean isEmpty() {
            return this.sublist.isEmpty();
        }

        @Override // java.util.List, java.util.Collection
        public boolean contains(Object o) {
            return this.sublist.contains(o);
        }

        @Override // java.util.List, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return this.sublist.iterator();
        }

        @Override // java.util.List, java.util.Collection
        public Object[] toArray() {
            return this.sublist.toArray();
        }

        @Override // java.util.List, java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            return (T[]) this.sublist.toArray(tArr);
        }

        @Override // java.util.List, java.util.Collection
        public boolean add(E e) {
            return this.sublist.add(e);
        }

        @Override // java.util.List, java.util.Collection
        public boolean remove(Object o) {
            return this.sublist.remove(o);
        }

        @Override // java.util.List, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return this.sublist.containsAll(c);
        }

        @Override // java.util.List, java.util.Collection
        public boolean addAll(Collection<? extends E> c) {
            ModifiableObservableListBase.this.beginChange();
            try {
                boolean res = this.sublist.addAll(c);
                return res;
            } finally {
                ModifiableObservableListBase.this.endChange();
            }
        }

        @Override // java.util.List
        public boolean addAll(int index, Collection<? extends E> c) {
            ModifiableObservableListBase.this.beginChange();
            try {
                boolean res = this.sublist.addAll(index, c);
                return res;
            } finally {
                ModifiableObservableListBase.this.endChange();
            }
        }

        @Override // java.util.List, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            ModifiableObservableListBase.this.beginChange();
            try {
                boolean res = this.sublist.removeAll(c);
                return res;
            } finally {
                ModifiableObservableListBase.this.endChange();
            }
        }

        @Override // java.util.List, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            ModifiableObservableListBase.this.beginChange();
            try {
                boolean res = this.sublist.retainAll(c);
                return res;
            } finally {
                ModifiableObservableListBase.this.endChange();
            }
        }

        @Override // java.util.List, java.util.Collection
        public void clear() {
            ModifiableObservableListBase.this.beginChange();
            try {
                this.sublist.clear();
            } finally {
                ModifiableObservableListBase.this.endChange();
            }
        }

        @Override // java.util.List
        public E get(int index) {
            return this.sublist.get(index);
        }

        @Override // java.util.List
        public E set(int index, E element) {
            return this.sublist.set(index, element);
        }

        @Override // java.util.List
        public void add(int index, E element) {
            this.sublist.add(index, element);
        }

        @Override // java.util.List
        public E remove(int index) {
            return this.sublist.remove(index);
        }

        @Override // java.util.List
        public int indexOf(Object o) {
            return this.sublist.indexOf(o);
        }

        @Override // java.util.List
        public int lastIndexOf(Object o) {
            return this.sublist.lastIndexOf(o);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator() {
            return this.sublist.listIterator();
        }

        @Override // java.util.List
        public ListIterator<E> listIterator(int index) {
            return this.sublist.listIterator(index);
        }

        @Override // java.util.List
        public List<E> subList(int fromIndex, int toIndex) {
            return new SubObservableList(this.sublist.subList(fromIndex, toIndex));
        }

        @Override // java.util.List, java.util.Collection
        public boolean equals(Object obj) {
            return this.sublist.equals(obj);
        }

        @Override // java.util.List, java.util.Collection
        public int hashCode() {
            return this.sublist.hashCode();
        }

        public String toString() {
            return this.sublist.toString();
        }
    }
}
