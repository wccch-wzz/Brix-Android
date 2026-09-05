package com.brixcore.fakefx.collections;

import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public interface SortableList<E> extends List<E> {
    void sort();

    void sort(Comparator<? super E> comparator);
}
