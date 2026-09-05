package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes3.dex */
public abstract class VetoableListDecorator<E> implements ObservableList<E> {
    private ListListenerHelper<E> helper;
    private final ObservableList<E> list;
    private int modCount;

    private interface ModCountAccessor {
        int decrementAndGet();

        int get();

        int incrementAndGet();
    }

    protected abstract void onProposedChange(List<E> list, int... iArr);

    public VetoableListDecorator(ObservableList<E> decorated) {
        this.list = decorated;
        this.list.addListener(new ListChangeListener() { // from class: com.brixcore.fakefx.collections.VetoableListDecorator$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.collections.ListChangeListener
            public final void onChanged(ListChangeListener.Change change) {
                this.f$0.lambda$new$0(change);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ListChangeListener.Change c) {
        ListListenerHelper.fireValueChangedEvent(this.helper, new SourceAdapterChange(this, c));
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public void addListener(ListChangeListener<? super E> listener) {
        this.helper = ListListenerHelper.addListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public void removeListener(ListChangeListener<? super E> listener) {
        this.helper = ListListenerHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void addListener(InvalidationListener listener) {
        this.helper = ListListenerHelper.addListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public void removeListener(InvalidationListener listener) {
        this.helper = ListListenerHelper.removeListener(this.helper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public boolean addAll(E... elements) {
        return addAll(Arrays.asList(elements));
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public boolean setAll(E... elements) {
        return setAll(Arrays.asList(elements));
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public boolean setAll(Collection<? extends E> col) throws Exception {
        onProposedChange(Collections.unmodifiableList(new ArrayList(col)), 0, size());
        try {
            this.modCount++;
            return this.list.setAll(col);
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFromList(List<E> backingList, int offset, Collection<?> col, boolean complement) {
        int[] toBeRemoved = new int[2];
        int pointer = -1;
        for (int i = 0; i < backingList.size(); i++) {
            E el = backingList.get(i);
            if (col.contains(el) ^ complement) {
                if (pointer == -1) {
                    toBeRemoved[pointer + 1] = offset + i;
                    toBeRemoved[pointer + 2] = offset + i + 1;
                    pointer += 2;
                } else if (toBeRemoved[pointer - 1] != offset + i) {
                    int[] tmp = new int[toBeRemoved.length + 2];
                    System.arraycopy(toBeRemoved, 0, tmp, 0, toBeRemoved.length);
                    toBeRemoved = tmp;
                    toBeRemoved[pointer + 1] = offset + i;
                    toBeRemoved[pointer + 2] = offset + i + 1;
                    pointer += 2;
                } else {
                    toBeRemoved[pointer - 1] = offset + i + 1;
                }
            }
        }
        if (pointer != -1) {
            onProposedChange(Collections.emptyList(), toBeRemoved);
        }
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public boolean removeAll(E... elements) {
        return removeAll(Arrays.asList(elements));
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public boolean retainAll(E... elements) {
        return retainAll(Arrays.asList(elements));
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public void remove(int from, int to) {
        onProposedChange(Collections.emptyList(), from, to);
        try {
            this.modCount++;
            this.list.remove(from, to);
        } catch (Exception e) {
            this.modCount--;
        }
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.list.size();
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new VetoableIteratorDecorator(new ModCountAccessorImpl(), this.list.iterator(), 0);
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.list.toArray(tArr);
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(E e) throws Exception {
        onProposedChange(Collections.singletonList(e), size(), size());
        try {
            this.modCount++;
            this.list.add(e);
            return true;
        } catch (Exception ex) {
            this.modCount--;
            throw ex;
        }
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) throws Exception {
        int i = this.list.indexOf(o);
        if (i != -1) {
            remove(i);
            return true;
        }
        return false;
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends E> c) throws Exception {
        onProposedChange(Collections.unmodifiableList(new ArrayList(c)), size(), size());
        try {
            this.modCount++;
            boolean ret = this.list.addAll(c);
            if (!ret) {
                this.modCount--;
            }
            return ret;
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> c) throws Exception {
        onProposedChange(Collections.unmodifiableList(new ArrayList(c)), index, index);
        try {
            this.modCount++;
            boolean ret = this.list.addAll(index, c);
            if (!ret) {
                this.modCount--;
            }
            return ret;
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> c) throws Exception {
        removeFromList(this, 0, c, false);
        try {
            this.modCount++;
            boolean ret = this.list.removeAll(c);
            if (!ret) {
                this.modCount--;
            }
            return ret;
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> c) throws Exception {
        removeFromList(this, 0, c, true);
        try {
            this.modCount++;
            boolean ret = this.list.retainAll(c);
            if (!ret) {
                this.modCount--;
            }
            return ret;
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List, java.util.Collection
    public void clear() throws Exception {
        onProposedChange(Collections.emptyList(), 0, size());
        try {
            this.modCount++;
            this.list.clear();
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List
    public E get(int index) {
        return this.list.get(index);
    }

    @Override // java.util.List
    public E set(int index, E element) {
        onProposedChange(Collections.singletonList(element), index, index + 1);
        return this.list.set(index, element);
    }

    @Override // java.util.List
    public void add(int index, E element) throws Exception {
        onProposedChange(Collections.singletonList(element), index, index);
        try {
            this.modCount++;
            this.list.add(index, element);
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List
    public E remove(int index) throws Exception {
        onProposedChange(Collections.emptyList(), index, index + 1);
        try {
            this.modCount++;
            E ret = this.list.remove(index);
            return ret;
        } catch (Exception e) {
            this.modCount--;
            throw e;
        }
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return new VetoableListIteratorDecorator(new ModCountAccessorImpl(), this.list.listIterator(), 0);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return new VetoableListIteratorDecorator(new ModCountAccessorImpl(), this.list.listIterator(index), index);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return new VetoableSubListDecorator(new ModCountAccessorImpl(), this.list.subList(fromIndex, toIndex), fromIndex);
    }

    public String toString() {
        return this.list.toString();
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object obj) {
        return this.list.equals(obj);
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        return this.list.hashCode();
    }

    private class VetoableSubListDecorator implements List<E> {
        private int modCount;
        private final ModCountAccessor modCountAccessor;
        private final int offset;
        private final List<E> subList;

        public VetoableSubListDecorator(ModCountAccessor modCountAccessor, List<E> subList, int offset) {
            this.modCountAccessor = modCountAccessor;
            this.modCount = modCountAccessor.get();
            this.subList = subList;
            this.offset = offset;
        }

        @Override // java.util.List, java.util.Collection
        public int size() {
            checkForComodification();
            return this.subList.size();
        }

        @Override // java.util.List, java.util.Collection
        public boolean isEmpty() {
            checkForComodification();
            return this.subList.isEmpty();
        }

        @Override // java.util.List, java.util.Collection
        public boolean contains(Object o) {
            checkForComodification();
            return this.subList.contains(o);
        }

        @Override // java.util.List, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            checkForComodification();
            return new VetoableIteratorDecorator(new ModCountAccessorImplSub(), this.subList.iterator(), this.offset);
        }

        @Override // java.util.List, java.util.Collection
        public Object[] toArray() {
            checkForComodification();
            return this.subList.toArray();
        }

        @Override // java.util.List, java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            checkForComodification();
            return (T[]) this.subList.toArray(tArr);
        }

        @Override // java.util.List, java.util.Collection
        public boolean add(E e) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.singletonList(e), this.offset + size(), this.offset + size());
            try {
                incrementModCount();
                this.subList.add(e);
                return true;
            } catch (Exception ex) {
                decrementModCount();
                throw ex;
            }
        }

        @Override // java.util.List, java.util.Collection
        public boolean remove(Object o) throws Exception {
            checkForComodification();
            int i = indexOf(o);
            if (i != -1) {
                remove(i);
                return true;
            }
            return false;
        }

        @Override // java.util.List, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            checkForComodification();
            return this.subList.containsAll(c);
        }

        @Override // java.util.List, java.util.Collection
        public boolean addAll(Collection<? extends E> c) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.unmodifiableList(new ArrayList(c)), this.offset + size(), this.offset + size());
            try {
                incrementModCount();
                boolean res = this.subList.addAll(c);
                if (!res) {
                    decrementModCount();
                }
                return res;
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List
        public boolean addAll(int index, Collection<? extends E> c) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.unmodifiableList(new ArrayList(c)), this.offset + index, this.offset + index);
            try {
                incrementModCount();
                boolean res = this.subList.addAll(index, c);
                if (!res) {
                    decrementModCount();
                }
                return res;
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List, java.util.Collection
        public boolean removeAll(Collection<?> c) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.removeFromList(this, this.offset, c, false);
            try {
                incrementModCount();
                boolean res = this.subList.removeAll(c);
                if (!res) {
                    decrementModCount();
                }
                return res;
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List, java.util.Collection
        public boolean retainAll(Collection<?> c) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.removeFromList(this, this.offset, c, true);
            try {
                incrementModCount();
                boolean res = this.subList.retainAll(c);
                if (!res) {
                    decrementModCount();
                }
                return res;
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List, java.util.Collection
        public void clear() throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.emptyList(), this.offset, this.offset + size());
            try {
                incrementModCount();
                this.subList.clear();
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List
        public E get(int index) {
            checkForComodification();
            return this.subList.get(index);
        }

        @Override // java.util.List
        public E set(int index, E element) {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.singletonList(element), this.offset + index, this.offset + index + 1);
            return this.subList.set(index, element);
        }

        @Override // java.util.List
        public void add(int index, E element) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.singletonList(element), this.offset + index, this.offset + index);
            try {
                incrementModCount();
                this.subList.add(index, element);
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List
        public E remove(int index) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.emptyList(), this.offset + index, this.offset + index + 1);
            try {
                incrementModCount();
                E res = this.subList.remove(index);
                return res;
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        @Override // java.util.List
        public int indexOf(Object o) {
            checkForComodification();
            return this.subList.indexOf(o);
        }

        @Override // java.util.List
        public int lastIndexOf(Object o) {
            checkForComodification();
            return this.subList.lastIndexOf(o);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator() {
            checkForComodification();
            return new VetoableListIteratorDecorator(new ModCountAccessorImplSub(), this.subList.listIterator(), this.offset);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator(int index) {
            checkForComodification();
            return new VetoableListIteratorDecorator(new ModCountAccessorImplSub(), this.subList.listIterator(index), this.offset + index);
        }

        @Override // java.util.List
        public List<E> subList(int fromIndex, int toIndex) {
            checkForComodification();
            return new VetoableSubListDecorator(new ModCountAccessorImplSub(), this.subList.subList(fromIndex, toIndex), this.offset + fromIndex);
        }

        public String toString() {
            checkForComodification();
            return this.subList.toString();
        }

        @Override // java.util.List, java.util.Collection
        public boolean equals(Object obj) {
            checkForComodification();
            return this.subList.equals(obj);
        }

        @Override // java.util.List, java.util.Collection
        public int hashCode() {
            checkForComodification();
            return this.subList.hashCode();
        }

        private void checkForComodification() {
            if (this.modCount != this.modCountAccessor.get()) {
                throw new ConcurrentModificationException();
            }
        }

        private void incrementModCount() {
            this.modCount = this.modCountAccessor.incrementAndGet();
        }

        private void decrementModCount() {
            this.modCount = this.modCountAccessor.decrementAndGet();
        }

        private class ModCountAccessorImplSub implements ModCountAccessor {
            private ModCountAccessorImplSub() {
            }

            @Override // com.brixcore.fakefx.collections.VetoableListDecorator.ModCountAccessor
            public int get() {
                return VetoableSubListDecorator.this.modCount;
            }

            @Override // com.brixcore.fakefx.collections.VetoableListDecorator.ModCountAccessor
            public int incrementAndGet() {
                VetoableSubListDecorator vetoableSubListDecorator = VetoableSubListDecorator.this;
                int iIncrementAndGet = VetoableSubListDecorator.this.modCountAccessor.incrementAndGet();
                vetoableSubListDecorator.modCount = iIncrementAndGet;
                return iIncrementAndGet;
            }

            @Override // com.brixcore.fakefx.collections.VetoableListDecorator.ModCountAccessor
            public int decrementAndGet() {
                VetoableSubListDecorator vetoableSubListDecorator = VetoableSubListDecorator.this;
                int iDecrementAndGet = VetoableSubListDecorator.this.modCountAccessor.decrementAndGet();
                vetoableSubListDecorator.modCount = iDecrementAndGet;
                return iDecrementAndGet;
            }
        }
    }

    private class VetoableIteratorDecorator implements Iterator<E> {
        protected int cursor;
        private final Iterator<E> it;
        protected int lastReturned;
        private int modCount;
        private final ModCountAccessor modCountAccessor;
        protected final int offset;

        public VetoableIteratorDecorator(ModCountAccessor modCountAccessor, Iterator<E> it, int offset) {
            this.modCountAccessor = modCountAccessor;
            this.modCount = modCountAccessor.get();
            this.it = it;
            this.offset = offset;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            checkForComodification();
            return this.it.hasNext();
        }

        @Override // java.util.Iterator
        public E next() {
            checkForComodification();
            E e = this.it.next();
            int i = this.cursor;
            this.cursor = i + 1;
            this.lastReturned = i;
            return e;
        }

        @Override // java.util.Iterator
        public void remove() throws Exception {
            checkForComodification();
            if (this.lastReturned == -1) {
                throw new IllegalStateException();
            }
            VetoableListDecorator.this.onProposedChange(Collections.emptyList(), this.offset + this.lastReturned, this.offset + this.lastReturned + 1);
            try {
                incrementModCount();
                this.it.remove();
                this.lastReturned = -1;
                this.cursor--;
            } catch (Exception e) {
                decrementModCount();
                throw e;
            }
        }

        protected void checkForComodification() {
            if (this.modCount != this.modCountAccessor.get()) {
                throw new ConcurrentModificationException();
            }
        }

        protected void incrementModCount() {
            this.modCount = this.modCountAccessor.incrementAndGet();
        }

        protected void decrementModCount() {
            this.modCount = this.modCountAccessor.decrementAndGet();
        }
    }

    private class VetoableListIteratorDecorator extends VetoableListDecorator<E>.VetoableIteratorDecorator implements ListIterator<E> {
        private final ListIterator<E> lit;

        public VetoableListIteratorDecorator(ModCountAccessor modCountAccessor, ListIterator<E> it, int offset) {
            super(modCountAccessor, it, offset);
            this.lit = it;
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            checkForComodification();
            return this.lit.hasPrevious();
        }

        @Override // java.util.ListIterator
        public E previous() {
            checkForComodification();
            E e = this.lit.previous();
            int i = this.cursor - 1;
            this.cursor = i;
            this.lastReturned = i;
            return e;
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            checkForComodification();
            return this.lit.nextIndex();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            checkForComodification();
            return this.lit.previousIndex();
        }

        @Override // java.util.ListIterator
        public void set(E e) {
            checkForComodification();
            if (this.lastReturned == -1) {
                throw new IllegalStateException();
            }
            VetoableListDecorator.this.onProposedChange(Collections.singletonList(e), this.offset + this.lastReturned, this.offset + this.lastReturned + 1);
            this.lit.set(e);
        }

        @Override // java.util.ListIterator
        public void add(E e) throws Exception {
            checkForComodification();
            VetoableListDecorator.this.onProposedChange(Collections.singletonList(e), this.offset + this.cursor, this.offset + this.cursor);
            try {
                incrementModCount();
                this.lit.add(e);
                this.cursor++;
            } catch (Exception ex) {
                decrementModCount();
                throw ex;
            }
        }
    }

    private class ModCountAccessorImpl implements ModCountAccessor {
        public ModCountAccessorImpl() {
        }

        @Override // com.brixcore.fakefx.collections.VetoableListDecorator.ModCountAccessor
        public int get() {
            return VetoableListDecorator.this.modCount;
        }

        @Override // com.brixcore.fakefx.collections.VetoableListDecorator.ModCountAccessor
        public int incrementAndGet() {
            VetoableListDecorator vetoableListDecorator = VetoableListDecorator.this;
            int i = vetoableListDecorator.modCount + 1;
            vetoableListDecorator.modCount = i;
            return i;
        }

        @Override // com.brixcore.fakefx.collections.VetoableListDecorator.ModCountAccessor
        public int decrementAndGet() {
            VetoableListDecorator vetoableListDecorator = VetoableListDecorator.this;
            int i = vetoableListDecorator.modCount - 1;
            vetoableListDecorator.modCount = i;
            return i;
        }
    }
}
