package com.brixcore.fakefx.collections;

import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
@FunctionalInterface
public interface ListChangeListener<E> {
    void onChanged(Change<? extends E> change);

    public static abstract class Change<E> {
        private final ObservableList<E> list;

        public abstract int getFrom();

        protected abstract int[] getPermutation();

        public abstract List<E> getRemoved();

        public abstract int getTo();

        public abstract boolean next();

        public abstract void reset();

        public Change(ObservableList<E> list) {
            this.list = list;
        }

        public ObservableList<E> getList() {
            return this.list;
        }

        public boolean wasPermutated() {
            return getPermutation().length != 0;
        }

        public boolean wasAdded() {
            return (wasPermutated() || wasUpdated() || getFrom() >= getTo()) ? false : true;
        }

        public boolean wasRemoved() {
            return !getRemoved().isEmpty();
        }

        public boolean wasReplaced() {
            return wasAdded() && wasRemoved();
        }

        public boolean wasUpdated() {
            return false;
        }

        public List<E> getAddedSubList() {
            return wasAdded() ? getList().subList(getFrom(), getTo()) : Collections.emptyList();
        }

        public int getRemovedSize() {
            return getRemoved().size();
        }

        public int getAddedSize() {
            if (wasAdded()) {
                return getTo() - getFrom();
            }
            return 0;
        }

        public int getPermutation(int i) {
            if (!wasPermutated()) {
                throw new IllegalStateException("Not a permutation change");
            }
            return getPermutation()[i - getFrom()];
        }
    }
}
