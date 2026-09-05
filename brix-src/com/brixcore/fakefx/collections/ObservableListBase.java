package com.brixcore.fakefx.collections;

import com.brixcore.fakefx.beans.InvalidationListener;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ObservableListBase<E> extends AbstractList<E> implements ObservableList<E> {
    private final ListChangeBuilder<E> changeBuilder = new ListChangeBuilder<>(this);
    private ListListenerHelper<E> listenerHelper;

    protected final void nextUpdate(int pos) {
        this.changeBuilder.nextUpdate(pos);
    }

    protected final void nextSet(int idx, E old) {
        this.changeBuilder.nextSet(idx, old);
    }

    protected final void nextReplace(int from, int to, List<? extends E> removed) {
        this.changeBuilder.nextReplace(from, to, removed);
    }

    protected final void nextRemove(int idx, List<? extends E> removed) {
        this.changeBuilder.nextRemove(idx, (List) removed);
    }

    protected final void nextRemove(int idx, E removed) {
        this.changeBuilder.nextRemove(idx, removed);
    }

    protected final void nextPermutation(int from, int to, int[] perm) {
        this.changeBuilder.nextPermutation(from, to, perm);
    }

    protected final void nextAdd(int from, int to) {
        this.changeBuilder.nextAdd(from, to);
    }

    protected final void beginChange() {
        this.changeBuilder.beginChange();
    }

    protected final void endChange() {
        this.changeBuilder.endChange();
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public final void addListener(InvalidationListener listener) {
        this.listenerHelper = ListListenerHelper.addListener(this.listenerHelper, listener);
    }

    @Override // com.brixcore.fakefx.beans.Observable
    public final void removeListener(InvalidationListener listener) {
        this.listenerHelper = ListListenerHelper.removeListener(this.listenerHelper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public final void addListener(ListChangeListener<? super E> listener) {
        this.listenerHelper = ListListenerHelper.addListener(this.listenerHelper, listener);
    }

    @Override // com.brixcore.fakefx.collections.ObservableList
    public final void removeListener(ListChangeListener<? super E> listener) {
        this.listenerHelper = ListListenerHelper.removeListener(this.listenerHelper, listener);
    }

    protected final void fireChange(ListChangeListener.Change<? extends E> change) {
        ListListenerHelper.fireValueChangedEvent(this.listenerHelper, change);
    }

    protected final boolean hasListeners() {
        return ListListenerHelper.hasListeners(this.listenerHelper);
    }

    public boolean addAll(E... elements) {
        return addAll(Arrays.asList(elements));
    }

    public boolean setAll(E... elements) {
        return setAll(Arrays.asList(elements));
    }

    public boolean setAll(Collection<? extends E> col) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(E... elements) {
        return removeAll(Arrays.asList(elements));
    }

    public boolean retainAll(E... elements) {
        return retainAll(Arrays.asList(elements));
    }

    public void remove(int from, int to) {
        removeRange(from, to);
    }
}
