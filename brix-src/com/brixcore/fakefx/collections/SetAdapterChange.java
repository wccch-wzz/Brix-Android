package com.brixcore.fakefx.collections;

/* JADX INFO: loaded from: classes3.dex */
public class SetAdapterChange<E> extends SetChangeListener.Change<E> {
    private final SetChangeListener.Change<? extends E> change;

    public SetAdapterChange(ObservableSet<E> set, SetChangeListener.Change<? extends E> change) {
        super(set);
        this.change = change;
    }

    public String toString() {
        return this.change.toString();
    }

    @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
    public boolean wasAdded() {
        return this.change.wasAdded();
    }

    @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
    public boolean wasRemoved() {
        return this.change.wasRemoved();
    }

    @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
    public E getElementAdded() {
        return this.change.getElementAdded();
    }

    @Override // com.brixcore.fakefx.collections.SetChangeListener.Change
    public E getElementRemoved() {
        return this.change.getElementRemoved();
    }
}
