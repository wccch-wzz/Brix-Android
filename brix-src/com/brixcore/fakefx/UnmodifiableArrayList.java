package com.brixcore.fakefx;

import java.util.AbstractList;
import java.util.RandomAccess;

/* JADX INFO: loaded from: classes9.dex */
public class UnmodifiableArrayList<T> extends AbstractList<T> implements RandomAccess {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private T[] elements;
    private final int size;

    public UnmodifiableArrayList(T[] elements, int size) {
        if (elements != null ? size > elements.length : size != 0) {
            throw new AssertionError();
        }
        this.size = size;
        this.elements = elements;
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int index) {
        return this.elements[index];
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }
}
