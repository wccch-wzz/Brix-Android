package com.brixcore.fakefx.collections;

import java.util.AbstractList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class MappingChange<E, F> extends ListChangeListener.Change<F> {
    public static final Map NOOP_MAP = new Map() { // from class: com.brixcore.fakefx.collections.MappingChange.1
        @Override // com.brixcore.fakefx.collections.MappingChange.Map
        public Object map(Object original) {
            return original;
        }
    };
    private final Map<E, F> map;
    private final ListChangeListener.Change<? extends E> original;
    private List<F> removed;

    public interface Map<E, F> {
        F map(E e);
    }

    public MappingChange(ListChangeListener.Change<? extends E> original, Map<E, F> map, ObservableList<F> list) {
        super(list);
        this.original = original;
        this.map = map;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public boolean next() {
        return this.original.next();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public void reset() {
        this.original.reset();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getFrom() {
        return this.original.getFrom();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getTo() {
        return this.original.getTo();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public List<F> getRemoved() {
        if (this.removed == null) {
            this.removed = new AbstractList<F>() { // from class: com.brixcore.fakefx.collections.MappingChange.2
                @Override // java.util.AbstractList, java.util.List
                public F get(int i) {
                    return (F) MappingChange.this.map.map(MappingChange.this.original.getRemoved().get(i));
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
                public int size() {
                    return MappingChange.this.original.getRemovedSize();
                }
            };
        }
        return this.removed;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    protected int[] getPermutation() {
        return new int[0];
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public boolean wasPermutated() {
        return this.original.wasPermutated();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public boolean wasUpdated() {
        return this.original.wasUpdated();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getPermutation(int i) {
        return this.original.getPermutation(i);
    }

    public String toString() {
        int posToEnd = 0;
        while (next()) {
            posToEnd++;
        }
        int size = 0;
        reset();
        while (next()) {
            size++;
        }
        reset();
        StringBuilder b = new StringBuilder();
        b.append("{ ");
        while (next()) {
            if (wasPermutated()) {
                b.append(ChangeHelper.permChangeToString(getPermutation()));
            } else if (wasUpdated()) {
                b.append(ChangeHelper.updateChangeToString(getFrom(), getTo()));
            } else {
                b.append(ChangeHelper.addRemoveChangeToString(getFrom(), getTo(), getList(), getRemoved()));
            }
            if (0 != size) {
                b.append(", ");
            }
        }
        b.append(" }");
        reset();
        int pos = size - posToEnd;
        while (true) {
            int pos2 = pos - 1;
            if (pos > 0) {
                next();
                pos = pos2;
            } else {
                return b.toString();
            }
        }
    }
}
