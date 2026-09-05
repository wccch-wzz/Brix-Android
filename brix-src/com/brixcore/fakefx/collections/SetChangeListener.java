package com.brixcore.fakefx.collections;

/* JADX INFO: loaded from: classes3.dex */
@FunctionalInterface
public interface SetChangeListener<E> {
    void onChanged(Change<? extends E> change);

    public static abstract class Change<E> {
        private ObservableSet<E> set;

        public abstract E getElementAdded();

        public abstract E getElementRemoved();

        public abstract boolean wasAdded();

        public abstract boolean wasRemoved();

        public Change(ObservableSet<E> set) {
            this.set = set;
        }

        public ObservableSet<E> getSet() {
            return this.set;
        }
    }
}
