package com.brixcore.fakefx.collections;

import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class NonIterableChange<E> extends ListChangeListener.Change<E> {
    private static final int[] EMPTY_PERM = new int[0];
    private final int from;
    private boolean invalid;
    private final int to;

    protected NonIterableChange(int from, int to, ObservableList<E> list) {
        super(list);
        this.invalid = true;
        this.from = from;
        this.to = to;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getFrom() {
        checkState();
        return this.from;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public int getTo() {
        checkState();
        return this.to;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    protected int[] getPermutation() {
        checkState();
        return EMPTY_PERM;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public boolean next() {
        if (!this.invalid) {
            return false;
        }
        this.invalid = false;
        return true;
    }

    @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
    public void reset() {
        this.invalid = true;
    }

    public void checkState() {
        if (this.invalid) {
            throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
        }
    }

    public String toString() {
        String ret;
        boolean oldInvalid = this.invalid;
        this.invalid = false;
        if (wasPermutated()) {
            ret = ChangeHelper.permChangeToString(getPermutation());
        } else if (wasUpdated()) {
            ret = ChangeHelper.updateChangeToString(this.from, this.to);
        } else {
            ret = ChangeHelper.addRemoveChangeToString(this.from, this.to, getList(), getRemoved());
        }
        this.invalid = oldInvalid;
        return "{ " + ret + " }";
    }

    public static class GenericAddRemoveChange<E> extends NonIterableChange<E> {
        private final List<E> removed;

        public GenericAddRemoveChange(int from, int to, List<E> removed, ObservableList<E> list) {
            super(from, to, list);
            this.removed = removed;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            checkState();
            return this.removed;
        }
    }

    public static class SimpleRemovedChange<E> extends NonIterableChange<E> {
        private final List<E> removed;

        public SimpleRemovedChange(int from, int to, E removed, ObservableList<E> list) {
            super(from, to, list);
            this.removed = Collections.singletonList(removed);
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean wasRemoved() {
            checkState();
            return true;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            checkState();
            return this.removed;
        }
    }

    public static class SimpleAddChange<E> extends NonIterableChange<E> {
        public SimpleAddChange(int from, int to, ObservableList<E> list) {
            super(from, to, list);
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean wasRemoved() {
            checkState();
            return false;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            checkState();
            return Collections.emptyList();
        }
    }

    public static class SimplePermutationChange<E> extends NonIterableChange<E> {
        private final int[] permutation;

        public SimplePermutationChange(int from, int to, int[] permutation, ObservableList<E> list) {
            super(from, to, list);
            this.permutation = permutation;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            checkState();
            return Collections.emptyList();
        }

        @Override // com.brixcore.fakefx.collections.NonIterableChange, com.brixcore.fakefx.collections.ListChangeListener.Change
        protected int[] getPermutation() {
            checkState();
            return this.permutation;
        }
    }

    public static class SimpleUpdateChange<E> extends NonIterableChange<E> {
        public SimpleUpdateChange(int position, ObservableList<E> list) {
            this(position, position + 1, list);
        }

        public SimpleUpdateChange(int from, int to, ObservableList<E> list) {
            super(from, to, list);
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            return Collections.emptyList();
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean wasUpdated() {
            return true;
        }
    }
}
