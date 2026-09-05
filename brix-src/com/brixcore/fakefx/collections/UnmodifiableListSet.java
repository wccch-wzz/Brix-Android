package com.brixcore.fakefx.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class UnmodifiableListSet<E> extends AbstractSet<E> {
    private List<E> backingList;

    public UnmodifiableListSet(List<E> backingList) {
        if (backingList == null) {
            throw new NullPointerException();
        }
        this.backingList = backingList;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<E> iterator() {
        final Iterator<E> itr = this.backingList.iterator();
        return new Iterator<E>() { // from class: com.brixcore.fakefx.collections.UnmodifiableListSet.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override // java.util.Iterator
            public E next() {
                return (E) itr.next();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.backingList.size();
    }
}
