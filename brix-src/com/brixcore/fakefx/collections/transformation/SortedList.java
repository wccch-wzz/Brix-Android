package com.brixcore.fakefx.collections.transformation;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.property.ObjectProperty;
import com.brixcore.fakefx.beans.property.ObjectPropertyBase;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.NonIterableChange;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.SortHelper;
import com.brixcore.fakefx.collections.SourceAdapterChange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class SortedList<E> extends TransformationList<E, E> {
    private ObjectProperty<Comparator<? super E>> comparator;
    private Comparator<Element<E>> elementComparator;
    private final SortHelper helper;
    private int[] perm;
    private int size;
    private Element<E>[] sorted;
    private final Element<E> tempElement;

    public SortedList(@NamedArg("source") ObservableList<? extends E> source, @NamedArg("comparator") Comparator<? super E> comparator) {
        super(source);
        this.helper = new SortHelper();
        this.tempElement = new Element<>(null, -1);
        this.sorted = new Element[((source.size() * 3) / 2) + 1];
        this.perm = new int[this.sorted.length];
        this.size = source.size();
        for (int i = 0; i < this.size; i++) {
            this.sorted[i] = new Element<>(source.get(i), i);
            this.perm[i] = i;
        }
        if (comparator != null) {
            setComparator(comparator);
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public SortedList(@NamedArg("source") ObservableList<? extends E> source) {
        this(source, null);
    }

    @Override // com.brixcore.fakefx.collections.transformation.TransformationList
    /* JADX INFO: renamed from: sourceChanged */
    protected void lambda$getListener$0(ListChangeListener.Change<? extends E> c) {
        if (this.elementComparator != null) {
            beginChange();
            while (c.next()) {
                if (c.wasPermutated()) {
                    updatePermutationIndexes(c);
                } else if (c.wasUpdated()) {
                    update(c);
                } else {
                    addRemove(c);
                }
            }
            endChange();
            return;
        }
        updateUnsorted(c);
        fireChange(new SourceAdapterChange(this, c));
    }

    public final ObjectProperty<Comparator<? super E>> comparatorProperty() {
        if (this.comparator == null) {
            this.comparator = new ObjectPropertyBase<Comparator<? super E>>() { // from class: com.brixcore.fakefx.collections.transformation.SortedList.1
                @Override // com.brixcore.fakefx.beans.property.ObjectPropertyBase
                protected void invalidated() {
                    Comparator<? super E> current = get();
                    SortedList.this.elementComparator = current != null ? new ElementComparator(current) : null;
                    SortedList.this.doSortWithPermutationChange();
                }

                @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
                public Object getBean() {
                    return SortedList.this;
                }

                @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
                public String getName() {
                    return "comparator";
                }
            };
        }
        return this.comparator;
    }

    public final Comparator<? super E> getComparator() {
        if (this.comparator == null) {
            return null;
        }
        return this.comparator.get();
    }

    public final void setComparator(Comparator<? super E> comparator) {
        comparatorProperty().set(comparator);
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int i) {
        if (i < this.size) {
            return (E) ((Element) this.sorted[i]).e;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSortWithPermutationChange() {
        if (this.elementComparator != null) {
            int[] perm = this.helper.sort(this.sorted, 0, this.size, this.elementComparator);
            for (int i = 0; i < this.size; i++) {
                this.perm[((Element) this.sorted[i]).index] = i;
            }
            fireChange(new NonIterableChange.SimplePermutationChange(0, this.size, perm, this));
            return;
        }
        int[] perm2 = new int[this.size];
        int[] rperm = new int[this.size];
        for (int i2 = 0; i2 < this.size; i2++) {
            rperm[i2] = i2;
            perm2[i2] = i2;
        }
        int i3 = 0;
        int idx = 0;
        while (idx < this.size) {
            int otherIdx = ((Element) this.sorted[idx]).index;
            if (otherIdx == idx) {
                idx++;
            } else {
                Element<E> other = this.sorted[otherIdx];
                this.sorted[otherIdx] = this.sorted[idx];
                this.sorted[idx] = other;
                this.perm[idx] = idx;
                this.perm[otherIdx] = otherIdx;
                perm2[rperm[idx]] = otherIdx;
                perm2[rperm[otherIdx]] = idx;
                int tp = rperm[idx];
                rperm[idx] = rperm[otherIdx];
                rperm[otherIdx] = tp;
                i3 = 1;
            }
        }
        if (i3 != 0) {
            fireChange(new NonIterableChange.SimplePermutationChange(0, this.size, perm2, this));
        }
    }

    @Override // com.brixcore.fakefx.collections.transformation.TransformationList
    public int getSourceIndex(int index) {
        return ((Element) this.sorted[index]).index;
    }

    @Override // com.brixcore.fakefx.collections.transformation.TransformationList
    public int getViewIndex(int index) {
        return this.perm[index];
    }

    private void updatePermutationIndexes(ListChangeListener.Change<? extends E> change) {
        for (int i = 0; i < this.size; i++) {
            int p = change.getPermutation(((Element) this.sorted[i]).index);
            ((Element) this.sorted[i]).index = p;
            this.perm[p] = i;
        }
    }

    private void updateUnsorted(ListChangeListener.Change<? extends E> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                Element<E>[] elementArr = new Element[this.sorted.length];
                for (int i = 0; i < this.size; i++) {
                    if (i >= c.getFrom() && i < c.getTo()) {
                        int p = c.getPermutation(i);
                        elementArr[p] = this.sorted[i];
                        ((Element) elementArr[p]).index = p;
                        this.perm[i] = i;
                    } else {
                        elementArr[i] = this.sorted[i];
                    }
                }
                this.sorted = elementArr;
            }
            if (c.wasRemoved()) {
                int removedTo = c.getFrom() + c.getRemovedSize();
                System.arraycopy(this.sorted, removedTo, this.sorted, c.getFrom(), this.size - removedTo);
                System.arraycopy(this.perm, removedTo, this.perm, c.getFrom(), this.size - removedTo);
                this.size -= c.getRemovedSize();
                updateIndices(removedTo, removedTo, -c.getRemovedSize());
            }
            if (c.wasAdded()) {
                ensureSize(this.size + c.getAddedSize());
                updateIndices(c.getFrom(), c.getFrom(), c.getAddedSize());
                System.arraycopy(this.sorted, c.getFrom(), this.sorted, c.getTo(), this.size - c.getFrom());
                System.arraycopy(this.perm, c.getFrom(), this.perm, c.getTo(), this.size - c.getFrom());
                this.size += c.getAddedSize();
                for (int i2 = c.getFrom(); i2 < c.getTo(); i2++) {
                    this.sorted[i2] = new Element<>(c.getList().get(i2), i2);
                    this.perm[i2] = i2;
                }
            }
        }
    }

    private static class Element<E> {
        private E e;
        private int index;

        public Element(E e, int index) {
            this.e = e;
            this.index = index;
        }
    }

    private static class ElementComparator<E> implements Comparator<Element<E>> {
        private final Comparator<? super E> comparator;

        public ElementComparator(Comparator<? super E> comparator) {
            this.comparator = comparator;
        }

        @Override // java.util.Comparator
        public int compare(Element<E> element, Element<E> element2) {
            return this.comparator.compare((Object) ((Element) element).e, (Object) ((Element) element2).e);
        }
    }

    private void ensureSize(int size) {
        if (this.sorted.length < size) {
            Element<E>[] replacement = new Element[((size * 3) / 2) + 1];
            System.arraycopy(this.sorted, 0, replacement, 0, this.size);
            this.sorted = replacement;
            int[] replacementPerm = new int[((size * 3) / 2) + 1];
            System.arraycopy(this.perm, 0, replacementPerm, 0, this.size);
            this.perm = replacementPerm;
        }
    }

    private void updateIndices(int from, int viewFrom, int difference) {
        for (int i = 0; i < this.size; i++) {
            if (((Element) this.sorted[i]).index >= from) {
                ((Element) this.sorted[i]).index += difference;
            }
            if (this.perm[i] >= viewFrom) {
                int[] iArr = this.perm;
                iArr[i] = iArr[i] + difference;
            }
        }
    }

    private int findPosition(E e) {
        if (this.sorted.length == 0) {
            return 0;
        }
        ((Element) this.tempElement).e = e;
        int pos = Arrays.binarySearch(this.sorted, 0, this.size, this.tempElement, this.elementComparator);
        return pos;
    }

    private void insertToMapping(E e, int idx) {
        int pos = findPosition(e);
        if (pos < 0) {
            pos = ~pos;
        }
        ensureSize(this.size + 1);
        updateIndices(idx, pos, 1);
        System.arraycopy(this.sorted, pos, this.sorted, pos + 1, this.size - pos);
        this.sorted[pos] = new Element<>(e, idx);
        System.arraycopy(this.perm, idx, this.perm, idx + 1, this.size - idx);
        this.perm[idx] = pos;
        this.size++;
        nextAdd(pos, pos + 1);
    }

    private void setAllToMapping(List<? extends E> list, int to) {
        ensureSize(to);
        this.size = to;
        for (int i = 0; i < to; i++) {
            this.sorted[i] = new Element<>(list.get(i), i);
        }
        int[] perm = this.helper.sort(this.sorted, 0, this.size, this.elementComparator);
        System.arraycopy(perm, 0, this.perm, 0, this.size);
        nextAdd(0, this.size);
    }

    private void removeFromMapping(int idx, E e) {
        int pos = this.perm[idx];
        System.arraycopy(this.sorted, pos + 1, this.sorted, pos, (this.size - pos) - 1);
        System.arraycopy(this.perm, idx + 1, this.perm, idx, (this.size - idx) - 1);
        this.size--;
        this.sorted[this.size] = null;
        updateIndices(idx + 1, pos, -1);
        nextRemove(pos, e);
    }

    private void removeAllFromMapping() {
        ArrayList arrayList = new ArrayList(this);
        for (int i = 0; i < this.size; i++) {
            this.sorted[i] = null;
        }
        this.size = 0;
        nextRemove(0, (List) arrayList);
    }

    private void update(ListChangeListener.Change<? extends E> c) {
        int[] perm = this.helper.sort(this.sorted, 0, this.size, this.elementComparator);
        for (int i = 0; i < this.size; i++) {
            this.perm[((Element) this.sorted[i]).index] = i;
        }
        int i2 = this.size;
        nextPermutation(0, i2, perm);
        int to = c.getTo();
        for (int i3 = c.getFrom(); i3 < to; i3++) {
            nextUpdate(this.perm[i3]);
        }
    }

    private void addRemove(ListChangeListener.Change<? extends E> c) {
        if (c.getFrom() == 0 && c.getRemovedSize() == this.size) {
            removeAllFromMapping();
        } else {
            int sz = c.getRemovedSize();
            for (int i = 0; i < sz; i++) {
                removeFromMapping(c.getFrom(), c.getRemoved().get(i));
            }
        }
        int i2 = this.size;
        if (i2 == 0) {
            setAllToMapping(c.getList(), c.getTo());
            return;
        }
        int to = c.getTo();
        for (int i3 = c.getFrom(); i3 < to; i3++) {
            insertToMapping(c.getList().get(i3), i3);
        }
    }
}
