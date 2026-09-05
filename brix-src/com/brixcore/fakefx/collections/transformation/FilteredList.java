package com.brixcore.fakefx.collections.transformation;

import com.brixcore.fakefx.beans.NamedArg;
import com.brixcore.fakefx.beans.property.ObjectProperty;
import com.brixcore.fakefx.beans.property.ObjectPropertyBase;
import com.brixcore.fakefx.collections.ListChangeListener;
import com.brixcore.fakefx.collections.NonIterableChange;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.fakefx.collections.SortHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes2.dex */
public final class FilteredList<E> extends TransformationList<E, E> {
    private static final Predicate ALWAYS_TRUE = new Predicate() { // from class: com.brixcore.fakefx.collections.transformation.FilteredList$$ExternalSyntheticLambda0
        @Override // java.util.function.Predicate
        public final boolean test(Object obj) {
            return FilteredList.lambda$static$0(obj);
        }
    };
    private int[] filtered;
    private SortHelper helper;
    private ObjectProperty<Predicate<? super E>> predicate;
    private int size;

    static /* synthetic */ boolean lambda$static$0(Object t) {
        return true;
    }

    public FilteredList(@NamedArg("source") ObservableList<E> source, @NamedArg("predicate") Predicate<? super E> predicate) {
        super(source);
        this.filtered = new int[((source.size() * 3) / 2) + 1];
        if (predicate != null) {
            setPredicate(predicate);
            return;
        }
        int i = 0;
        while (true) {
            this.size = i;
            if (this.size < source.size()) {
                this.filtered[this.size] = this.size;
                i = this.size + 1;
            } else {
                return;
            }
        }
    }

    public FilteredList(@NamedArg("source") ObservableList<E> source) {
        this(source, null);
    }

    public final ObjectProperty<Predicate<? super E>> predicateProperty() {
        if (this.predicate == null) {
            this.predicate = new ObjectPropertyBase<Predicate<? super E>>() { // from class: com.brixcore.fakefx.collections.transformation.FilteredList.1
                @Override // com.brixcore.fakefx.beans.property.ObjectPropertyBase
                protected void invalidated() {
                    FilteredList.this.refilter();
                }

                @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
                public Object getBean() {
                    return FilteredList.this;
                }

                @Override // com.brixcore.fakefx.beans.property.ReadOnlyProperty
                public String getName() {
                    return "predicate";
                }
            };
        }
        return this.predicate;
    }

    public final Predicate<? super E> getPredicate() {
        if (this.predicate == null) {
            return null;
        }
        return this.predicate.get();
    }

    public final void setPredicate(Predicate<? super E> predicate) {
        predicateProperty().set(predicate);
    }

    private Predicate<? super E> getPredicateImpl() {
        if (getPredicate() != null) {
            return getPredicate();
        }
        return ALWAYS_TRUE;
    }

    @Override // com.brixcore.fakefx.collections.transformation.TransformationList
    /* JADX INFO: renamed from: sourceChanged */
    protected void lambda$getListener$0(ListChangeListener.Change<? extends E> c) {
        beginChange();
        while (c.next()) {
            if (c.wasPermutated()) {
                permutate(c);
            } else if (c.wasUpdated()) {
                update(c);
            } else {
                addRemove(c);
            }
        }
        endChange();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        return getSource().get(this.filtered[index]);
    }

    @Override // com.brixcore.fakefx.collections.transformation.TransformationList
    public int getSourceIndex(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        return this.filtered[index];
    }

    @Override // com.brixcore.fakefx.collections.transformation.TransformationList
    public int getViewIndex(int index) {
        return Arrays.binarySearch(this.filtered, 0, this.size, index);
    }

    private SortHelper getSortHelper() {
        if (this.helper == null) {
            this.helper = new SortHelper();
        }
        return this.helper;
    }

    private int findPosition(int p) {
        if (this.filtered.length == 0 || p == 0) {
            return 0;
        }
        int pos = Arrays.binarySearch(this.filtered, 0, this.size, p);
        if (pos < 0) {
            return ~pos;
        }
        return pos;
    }

    private void ensureSize(int size) {
        if (this.filtered.length < size) {
            int[] replacement = new int[((size * 3) / 2) + 1];
            System.arraycopy(this.filtered, 0, replacement, 0, this.size);
            this.filtered = replacement;
        }
    }

    private void updateIndexes(int from, int delta) {
        for (int i = from; i < this.size; i++) {
            int[] iArr = this.filtered;
            iArr[i] = iArr[i] + delta;
        }
    }

    private void permutate(ListChangeListener.Change<? extends E> c) {
        int from = findPosition(c.getFrom());
        int to = findPosition(c.getTo());
        if (to > from) {
            for (int i = from; i < to; i++) {
                this.filtered[i] = c.getPermutation(this.filtered[i]);
            }
            int[] perm = getSortHelper().sort(this.filtered, from, to);
            nextPermutation(from, to, perm);
        }
    }

    private void addRemove(ListChangeListener.Change<? extends E> change) {
        Predicate<? super E> predicateImpl = getPredicateImpl();
        ensureSize(getSource().size());
        int iFindPosition = findPosition(change.getFrom());
        int iFindPosition2 = findPosition(change.getFrom() + change.getRemovedSize());
        for (int i = iFindPosition; i < iFindPosition2; i++) {
            nextRemove(iFindPosition, change.getRemoved().get(this.filtered[i] - change.getFrom()));
        }
        updateIndexes(iFindPosition2, change.getAddedSize() - change.getRemovedSize());
        int i2 = iFindPosition;
        int from = change.getFrom();
        ListIterator<? extends E> listIterator = getSource().listIterator(from);
        while (i2 < iFindPosition2 && listIterator.nextIndex() < change.getTo()) {
            if (predicateImpl.test(listIterator.next())) {
                this.filtered[i2] = listIterator.previousIndex();
                nextAdd(i2, i2 + 1);
                i2++;
            }
        }
        if (i2 < iFindPosition2) {
            System.arraycopy(this.filtered, iFindPosition2, this.filtered, i2, this.size - iFindPosition2);
            this.size -= iFindPosition2 - i2;
            return;
        }
        while (listIterator.nextIndex() < change.getTo()) {
            if (predicateImpl.test(listIterator.next())) {
                System.arraycopy(this.filtered, i2, this.filtered, i2 + 1, this.size - i2);
                this.filtered[i2] = listIterator.previousIndex();
                nextAdd(i2, i2 + 1);
                i2++;
                this.size++;
            }
            from++;
        }
    }

    private void update(ListChangeListener.Change<? extends E> c) {
        Predicate<? super E> pred = getPredicateImpl();
        ensureSize(getSource().size());
        int sourceFrom = c.getFrom();
        int sourceTo = c.getTo();
        int filterFrom = findPosition(sourceFrom);
        int filterTo = findPosition(sourceTo);
        ListIterator<E> listIterator = getSource().listIterator(sourceFrom);
        int pos = filterFrom;
        while (true) {
            if (pos < filterTo || sourceFrom < sourceTo) {
                E el = listIterator.next();
                if (pos < this.size && this.filtered[pos] == sourceFrom) {
                    if (!pred.test(el)) {
                        nextRemove(pos, el);
                        System.arraycopy(this.filtered, pos + 1, this.filtered, pos, (this.size - pos) - 1);
                        this.size--;
                        filterTo--;
                    } else {
                        nextUpdate(pos);
                        pos++;
                    }
                } else if (pred.test(el)) {
                    nextAdd(pos, pos + 1);
                    System.arraycopy(this.filtered, pos, this.filtered, pos + 1, this.size - pos);
                    this.filtered[pos] = sourceFrom;
                    this.size++;
                    pos++;
                    filterTo++;
                }
                sourceFrom++;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refilter() {
        ensureSize(getSource().size());
        List<E> arrayList = null;
        if (hasListeners()) {
            arrayList = new ArrayList(this);
        }
        this.size = 0;
        int i = 0;
        Predicate<? super E> predicateImpl = getPredicateImpl();
        Iterator<? extends E> it = getSource().iterator();
        while (it.hasNext()) {
            if (predicateImpl.test(it.next())) {
                int[] iArr = this.filtered;
                int i2 = this.size;
                this.size = i2 + 1;
                iArr[i2] = i;
            }
            i++;
        }
        if (hasListeners()) {
            fireChange(new NonIterableChange.GenericAddRemoveChange(0, this.size, arrayList, this));
        }
    }
}
