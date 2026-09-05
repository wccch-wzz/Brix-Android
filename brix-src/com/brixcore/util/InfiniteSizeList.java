package com.brixcore.util;

import java.util.ArrayList;
import java.util.Collection;

/* JADX INFO: loaded from: classes11.dex */
public final class InfiniteSizeList<T> extends ArrayList<T> {
    private int actualSize;

    public InfiniteSizeList(int initialCapacity) {
        super(initialCapacity);
        this.actualSize = 0;
    }

    public InfiniteSizeList() {
        this.actualSize = 0;
    }

    public InfiniteSizeList(Collection<? extends T> c) {
        super(c);
        this.actualSize = 0;
        for (int i = super.size() - 1; i >= 0; i--) {
            if (super.get(i) != null) {
                this.actualSize = i + 1;
                return;
            }
        }
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public T get(int i) {
        if (i >= super.size()) {
            return null;
        }
        return (T) super.get(i);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public T set(int i, T t) {
        T t2;
        if (t == null) {
            if (i >= super.size() || (t2 = (T) super.get(i)) == null) {
                return null;
            }
            super.set(i, null);
            if (i == this.actualSize - 1) {
                this.actualSize = 0;
                for (int i2 = i - 1; i2 >= 0; i2--) {
                    if (super.get(i2) != null) {
                        this.actualSize = i2 + 1;
                        break;
                    }
                }
            }
            return t2;
        }
        if (i >= super.size()) {
            allocate0(i);
        }
        T t3 = (T) super.get(i);
        super.set(i, t);
        if (t3 != null) {
            return t3;
        }
        if (i >= this.actualSize) {
            this.actualSize = i + 1;
        }
        return null;
    }

    private void allocate0(int index) {
        addAll(Lang.immutableListOf(new Object[(index + 1) - super.size()]));
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.actualSize;
    }
}
