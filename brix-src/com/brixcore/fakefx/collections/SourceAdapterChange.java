package com.brixcore.fakefx.collections;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class SourceAdapterChange<E> extends ListChangeListener.Change<E> {
    private final ListChangeListener.Change<? extends E> change;
    private int[] perm;

    public SourceAdapterChange(ObservableList<E> list, ListChangeListener.Change<? extends E> change) {
        super(list);
        this.change = change;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public boolean next() {
        this.perm = null;
        return this.change.next();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public void reset() {
        this.change.reset();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getTo() {
        return this.change.getTo();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public List<E> getRemoved() {
        return this.change.getRemoved();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getFrom() {
        return this.change.getFrom();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public boolean wasUpdated() {
        return this.change.wasUpdated();
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    protected int[] getPermutation() {
        if (this.perm == null) {
            if (this.change.wasPermutated()) {
                int from = this.change.getFrom();
                int n = this.change.getTo() - from;
                this.perm = new int[n];
                for (int i = 0; i < n; i++) {
                    this.perm[i] = this.change.getPermutation(from + i);
                }
            } else {
                this.perm = new int[0];
            }
        }
        return this.perm;
    }

    public String toString() {
        return this.change.toString();
    }
}
