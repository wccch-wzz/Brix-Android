package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.collections.transformation.FilteredList;
import com.brixcore.fakefx.collections.transformation.SortedList;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes3.dex */
public interface ObservableList<E> extends List<E>, Observable {
    boolean addAll(E... eArr);

    void addListener(ListChangeListener<? super E> listChangeListener);

    void remove(int i, int i2);

    boolean removeAll(E... eArr);

    void removeListener(ListChangeListener<? super E> listChangeListener);

    boolean retainAll(E... eArr);

    boolean setAll(Collection<? extends E> collection);

    boolean setAll(E... eArr);

    default FilteredList<E> filtered(Predicate<E> predicate) {
        return new FilteredList<>(this, predicate);
    }

    default SortedList<E> sorted(Comparator<E> comparator) {
        return new SortedList<>(this, comparator);
    }

    default SortedList<E> sorted() {
        return sorted(new Comparator<E>() { // from class: com.brixcore.fakefx.collections.ObservableList.1
            @Override // java.util.Comparator
            public int compare(E o1, E o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                if (o1 instanceof Comparable) {
                    return ((Comparable) o1).compareTo(o2);
                }
                return Collator.getInstance().compare(o1.toString(), o2.toString());
            }
        });
    }
}
