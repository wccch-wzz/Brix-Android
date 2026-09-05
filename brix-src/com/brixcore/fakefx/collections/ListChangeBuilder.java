package com.brixcore.fakefx.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes3.dex */
final class ListChangeBuilder<E> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int[] EMPTY_PERM = new int[0];
    private List<SubChange<E>> addRemoveChanges;
    private int changeLock;
    private final ObservableListBase<E> list;
    private SubChange<E> permutationChange;
    private List<SubChange<E>> updateChanges;

    private void checkAddRemoveList() {
        if (this.addRemoveChanges == null) {
            this.addRemoveChanges = new ArrayList();
        }
    }

    private void checkState() {
        if (this.changeLock == 0) {
            throw new IllegalStateException("beginChange was not called on this builder");
        }
    }

    private int findSubChange(int idx, List<SubChange<E>> list) {
        int from = 0;
        int to = list.size() - 1;
        while (from <= to) {
            int changeIdx = (from + to) / 2;
            SubChange<E> change = list.get(changeIdx);
            if (idx >= change.to) {
                from = changeIdx + 1;
            } else if (idx < change.from) {
                to = changeIdx - 1;
            } else {
                return changeIdx;
            }
        }
        return ~from;
    }

    private void insertUpdate(int pos) {
        int idx = findSubChange(pos, this.updateChanges);
        if (idx < 0) {
            int idx2 = ~idx;
            if (idx2 > 0) {
                SubChange<E> change = this.updateChanges.get(idx2 - 1);
                if (change.to == pos) {
                    change.to = pos + 1;
                    return;
                }
            }
            if (idx2 < this.updateChanges.size()) {
                SubChange<E> change2 = this.updateChanges.get(idx2);
                if (change2.from == pos + 1) {
                    change2.from = pos;
                    return;
                }
            }
            this.updateChanges.add(idx2, new SubChange<>(pos, pos + 1, null, EMPTY_PERM, true));
        }
    }

    /* JADX WARN: Code duplicated, block: B:11:0x002c  */
    /* JADX WARN: Code duplicated, block: B:13:0x003b  */
    /* JADX WARN: Code duplicated, block: B:14:0x004f  */
    /* JADX WARN: Code duplicated, block: B:9:0x0024  */
    private void insertRemoved(int pos, E removed) {
        SubChange<E> change;
        int idx = findSubChange(pos, this.addRemoveChanges);
        if (idx < 0) {
            idx = ~idx;
            if (idx > 0) {
                SubChange<E> change2 = this.addRemoveChanges.get(idx - 1);
                if (change2.to == pos) {
                    change2.removed.add(removed);
                    idx--;
                } else if (idx < this.addRemoveChanges.size()) {
                    change = this.addRemoveChanges.get(idx);
                    if (change.from == pos + 1) {
                        change.from--;
                        change.to--;
                        change.removed.add(0, removed);
                    } else {
                        ArrayList<E> removedList = new ArrayList<>();
                        removedList.add(removed);
                        this.addRemoveChanges.add(idx, new SubChange<>(pos, pos, removedList, EMPTY_PERM, false));
                    }
                } else {
                    ArrayList<E> removedList2 = new ArrayList<>();
                    removedList2.add(removed);
                    this.addRemoveChanges.add(idx, new SubChange<>(pos, pos, removedList2, EMPTY_PERM, false));
                }
            } else if (idx < this.addRemoveChanges.size()) {
                change = this.addRemoveChanges.get(idx);
                if (change.from == pos + 1) {
                    change.from--;
                    change.to--;
                    change.removed.add(0, removed);
                } else {
                    ArrayList<E> removedList3 = new ArrayList<>();
                    removedList3.add(removed);
                    this.addRemoveChanges.add(idx, new SubChange<>(pos, pos, removedList3, EMPTY_PERM, false));
                }
            } else {
                ArrayList<E> removedList4 = new ArrayList<>();
                removedList4.add(removed);
                this.addRemoveChanges.add(idx, new SubChange<>(pos, pos, removedList4, EMPTY_PERM, false));
            }
        } else {
            SubChange<E> change3 = this.addRemoveChanges.get(idx);
            change3.to--;
            if (change3.from == change3.to && (change3.removed == null || change3.removed.isEmpty())) {
                this.addRemoveChanges.remove(idx);
            }
        }
        for (int i = idx + 1; i < this.addRemoveChanges.size(); i++) {
            SubChange<E> change4 = this.addRemoveChanges.get(i);
            change4.from--;
            change4.to--;
        }
    }

    /* JADX WARN: Code duplicated, block: B:9:0x0023  */
    private void insertAdd(int from, int to) {
        int idx = findSubChange(from, this.addRemoveChanges);
        int numberOfAdded = to - from;
        if (idx < 0) {
            idx = ~idx;
            if (idx > 0) {
                SubChange<E> change = this.addRemoveChanges.get(idx - 1);
                if (change.to == from) {
                    change.to = to;
                    idx--;
                } else {
                    this.addRemoveChanges.add(idx, new SubChange<>(from, to, new ArrayList(), EMPTY_PERM, false));
                }
            } else {
                this.addRemoveChanges.add(idx, new SubChange<>(from, to, new ArrayList(), EMPTY_PERM, false));
            }
        } else {
            this.addRemoveChanges.get(idx).to += numberOfAdded;
        }
        for (int i = idx + 1; i < this.addRemoveChanges.size(); i++) {
            SubChange<E> change2 = this.addRemoveChanges.get(i);
            change2.from += numberOfAdded;
            change2.to += numberOfAdded;
        }
    }

    private int compress(List<SubChange<E>> list) {
        int removed = 0;
        SubChange<E> prev = list.get(0);
        int sz = list.size();
        for (int i = 1; i < sz; i++) {
            SubChange<E> cur = list.get(i);
            if (prev.to == cur.from) {
                prev.to = cur.to;
                if (prev.removed != null || cur.removed != null) {
                    if (prev.removed == null) {
                        prev.removed = new ArrayList();
                    }
                    prev.removed.addAll(cur.removed);
                }
                list.set(i, null);
                removed++;
            } else {
                prev = cur;
            }
        }
        return removed;
    }

    private static class SubChange<E> {
        int from;
        int[] perm;
        List<E> removed;
        int to;
        boolean updated;

        public SubChange(int from, int to, List<E> removed, int[] perm, boolean updated) {
            this.from = from;
            this.to = to;
            this.removed = removed;
            this.perm = perm;
            this.updated = updated;
        }
    }

    ListChangeBuilder(ObservableListBase<E> list) {
        this.list = list;
    }

    public void nextRemove(int idx, E removed) {
        checkState();
        checkAddRemoveList();
        SubChange<E> last = this.addRemoveChanges.isEmpty() ? null : this.addRemoveChanges.get(this.addRemoveChanges.size() - 1);
        if (last != null && last.to == idx) {
            last.removed.add(removed);
        } else if (last != null && last.from == idx + 1) {
            last.from--;
            last.to--;
            last.removed.add(0, removed);
        } else {
            insertRemoved(idx, removed);
        }
        if (this.updateChanges != null && !this.updateChanges.isEmpty()) {
            int uPos = findSubChange(idx, this.updateChanges);
            if (uPos < 0) {
                uPos = ~uPos;
            } else {
                SubChange<E> change = this.updateChanges.get(uPos);
                if (change.from == change.to - 1) {
                    this.updateChanges.remove(uPos);
                } else {
                    change.to--;
                    uPos++;
                }
            }
            for (int i = uPos; i < this.updateChanges.size(); i++) {
                this.updateChanges.get(i).from--;
                this.updateChanges.get(i).to--;
            }
        }
    }

    public void nextRemove(int idx, List<? extends E> removed) {
        checkState();
        for (int i = 0; i < removed.size(); i++) {
            nextRemove(idx, removed.get(i));
        }
    }

    public void nextAdd(int from, int to) {
        int uPos;
        checkState();
        checkAddRemoveList();
        SubChange<E> last = this.addRemoveChanges.isEmpty() ? null : this.addRemoveChanges.get(this.addRemoveChanges.size() - 1);
        int numberOfAdded = to - from;
        if (last != null && last.to == from) {
            last.to = to;
        } else if (last != null && from >= last.from && from < last.to) {
            last.to += numberOfAdded;
        } else {
            insertAdd(from, to);
        }
        if (this.updateChanges != null && !this.updateChanges.isEmpty()) {
            int uPos2 = findSubChange(from, this.updateChanges);
            if (uPos2 < 0) {
                uPos = ~uPos2;
            } else {
                SubChange<E> change = this.updateChanges.get(uPos2);
                this.updateChanges.add(uPos2 + 1, new SubChange<>(to, (change.to + to) - from, null, EMPTY_PERM, true));
                change.to = from;
                uPos = uPos2 + 2;
            }
            for (int i = uPos; i < this.updateChanges.size(); i++) {
                this.updateChanges.get(i).from += numberOfAdded;
                this.updateChanges.get(i).to += numberOfAdded;
            }
        }
    }

    public void nextPermutation(int from, int to, int[] perm) {
        int prePermFrom;
        checkState();
        int prePermFrom2 = from;
        int prePermTo = to;
        int[] prePerm = perm;
        if (this.addRemoveChanges != null && !this.addRemoveChanges.isEmpty()) {
            int[] mapToOriginal = new int[this.list.size()];
            Set<Integer> removed = new TreeSet<>();
            int last = 0;
            int offset = 0;
            int i = 0;
            int sz = this.addRemoveChanges.size();
            while (i < sz) {
                SubChange<E> change = this.addRemoveChanges.get(i);
                int j = last;
                while (j < change.from) {
                    mapToOriginal[(j < from || j >= to) ? j : perm[j - from]] = j + offset;
                    j++;
                }
                int j2 = change.from;
                while (j2 < change.to) {
                    mapToOriginal[(j2 < from || j2 >= to) ? j2 : perm[j2 - from]] = -1;
                    j2++;
                }
                last = change.to;
                int removedSize = change.removed != null ? change.removed.size() : 0;
                int j3 = change.from + offset;
                int upTo = change.from + offset + removedSize;
                while (j3 < upTo) {
                    removed.add(Integer.valueOf(j3));
                    j3++;
                    prePermFrom2 = prePermFrom2;
                }
                offset += removedSize - (change.to - change.from);
                i++;
                prePermFrom2 = prePermFrom2;
            }
            int i2 = last;
            while (i2 < mapToOriginal.length) {
                mapToOriginal[(i2 < from || i2 >= to) ? i2 : perm[i2 - from]] = i2 + offset;
                i2++;
            }
            int[] newPerm = new int[this.list.size() + offset];
            int mapPtr = 0;
            for (int i3 = 0; i3 < newPerm.length; i3++) {
                if (removed.contains(Integer.valueOf(i3))) {
                    newPerm[i3] = i3;
                } else {
                    while (mapToOriginal[mapPtr] == -1) {
                        mapPtr++;
                    }
                    newPerm[mapToOriginal[mapPtr]] = i3;
                    mapPtr++;
                }
            }
            prePermTo = newPerm.length;
            prePerm = newPerm;
            prePermFrom = 0;
        } else {
            prePermFrom = prePermFrom2;
        }
        if (this.permutationChange == null) {
            this.permutationChange = new SubChange<>(prePermFrom, prePermTo, null, prePerm, false);
        } else if (prePermFrom == this.permutationChange.from && prePermTo == this.permutationChange.to) {
            for (int i4 = 0; i4 < prePerm.length; i4++) {
                this.permutationChange.perm[i4] = prePerm[this.permutationChange.perm[i4] - prePermFrom];
            }
        } else {
            int newTo = Math.max(this.permutationChange.to, prePermTo);
            int newFrom = Math.min(this.permutationChange.from, prePermFrom);
            int[] newPerm2 = new int[newTo - newFrom];
            for (int i5 = newFrom; i5 < newTo; i5++) {
                if (i5 >= this.permutationChange.from && i5 < this.permutationChange.to) {
                    int p = this.permutationChange.perm[i5 - this.permutationChange.from];
                    if (p < prePermFrom || p >= prePermTo) {
                        newPerm2[i5 - newFrom] = p;
                    } else {
                        newPerm2[i5 - newFrom] = prePerm[p - prePermFrom];
                    }
                } else {
                    newPerm2[i5 - newFrom] = prePerm[i5 - prePermFrom];
                }
            }
            this.permutationChange.from = newFrom;
            this.permutationChange.to = newTo;
            this.permutationChange.perm = newPerm2;
        }
        if (this.addRemoveChanges != null && !this.addRemoveChanges.isEmpty()) {
            Set<Integer> newAdded = new TreeSet<>();
            Map<Integer, List<E>> newRemoved = new HashMap<>();
            int sz2 = this.addRemoveChanges.size();
            for (int i6 = 0; i6 < sz2; i6++) {
                SubChange<E> change2 = this.addRemoveChanges.get(i6);
                for (int cIndex = change2.from; cIndex < change2.to; cIndex++) {
                    if (cIndex < from || cIndex >= to) {
                        newAdded.add(Integer.valueOf(cIndex));
                    } else {
                        newAdded.add(Integer.valueOf(perm[cIndex - from]));
                    }
                }
                if (change2.removed != null) {
                    if (change2.from < from || change2.from >= to) {
                        newRemoved.put(Integer.valueOf(change2.from), change2.removed);
                    } else {
                        newRemoved.put(Integer.valueOf(perm[change2.from - from]), change2.removed);
                    }
                }
            }
            this.addRemoveChanges.clear();
            SubChange<E> lastChange = null;
            for (Integer i7 : newAdded) {
                if (lastChange == null || lastChange.to != i7.intValue()) {
                    SubChange<E> lastChange2 = new SubChange<>(i7.intValue(), i7.intValue() + 1, null, EMPTY_PERM, false);
                    this.addRemoveChanges.add(lastChange2);
                    lastChange = lastChange2;
                } else {
                    lastChange.to = i7.intValue() + 1;
                }
                List<E> removed2 = newRemoved.remove(i7);
                if (removed2 != null) {
                    if (lastChange.removed != null) {
                        lastChange.removed.addAll(removed2);
                    } else {
                        lastChange.removed = removed2;
                    }
                }
            }
            for (Map.Entry<Integer, List<E>> e : newRemoved.entrySet()) {
                Integer at = e.getKey();
                int idx = findSubChange(at.intValue(), this.addRemoveChanges);
                if (idx >= 0) {
                    throw new AssertionError();
                }
                this.addRemoveChanges.add(~idx, new SubChange<>(at.intValue(), at.intValue(), e.getValue(), new int[0], false));
            }
        }
        if (this.updateChanges != null && !this.updateChanges.isEmpty()) {
            Set<Integer> newUpdated = new TreeSet<>();
            int sz3 = this.updateChanges.size();
            for (int i8 = 0; i8 < sz3; i8++) {
                SubChange<E> change3 = this.updateChanges.get(i8);
                for (int cIndex2 = change3.from; cIndex2 < change3.to; cIndex2++) {
                    if (cIndex2 < from || cIndex2 >= to) {
                        newUpdated.add(Integer.valueOf(cIndex2));
                    } else {
                        newUpdated.add(Integer.valueOf(perm[cIndex2 - from]));
                    }
                }
            }
            this.updateChanges.clear();
            SubChange<E> lastUpdateChange = null;
            for (Integer i9 : newUpdated) {
                if (lastUpdateChange == null || lastUpdateChange.to != i9.intValue()) {
                    SubChange<E> lastUpdateChange2 = new SubChange<>(i9.intValue(), i9.intValue() + 1, null, EMPTY_PERM, true);
                    this.updateChanges.add(lastUpdateChange2);
                    lastUpdateChange = lastUpdateChange2;
                } else {
                    lastUpdateChange.to = i9.intValue() + 1;
                }
            }
        }
    }

    public void nextReplace(int from, int to, List<? extends E> removed) {
        nextRemove(from, (List) removed);
        nextAdd(from, to);
    }

    public void nextSet(int idx, E old) {
        nextRemove(idx, old);
        nextAdd(idx, idx + 1);
    }

    public void nextUpdate(int idx) {
        checkState();
        if (this.updateChanges == null) {
            this.updateChanges = new ArrayList();
        }
        SubChange<E> last = this.updateChanges.isEmpty() ? null : this.updateChanges.get(this.updateChanges.size() - 1);
        if (last != null && last.to == idx) {
            last.to = idx + 1;
        } else {
            insertUpdate(idx);
        }
    }

    private void commit() {
        boolean addRemoveNotEmpty = (this.addRemoveChanges == null || this.addRemoveChanges.isEmpty()) ? false : true;
        boolean updateNotEmpty = (this.updateChanges == null || this.updateChanges.isEmpty()) ? false : true;
        if (this.changeLock == 0) {
            if (addRemoveNotEmpty || updateNotEmpty || this.permutationChange != null) {
                int totalSize = (this.updateChanges != null ? this.updateChanges.size() : 0) + (this.addRemoveChanges != null ? this.addRemoveChanges.size() : 0) + (this.permutationChange != null ? 1 : 0);
                if (totalSize == 1) {
                    if (addRemoveNotEmpty) {
                        this.list.fireChange(new SingleChange(finalizeSubChange(this.addRemoveChanges.get(0)), this.list));
                        this.addRemoveChanges.clear();
                        return;
                    } else if (updateNotEmpty) {
                        this.list.fireChange(new SingleChange(finalizeSubChange(this.updateChanges.get(0)), this.list));
                        this.updateChanges.clear();
                        return;
                    } else {
                        this.list.fireChange(new SingleChange(finalizeSubChange(this.permutationChange), this.list));
                        this.permutationChange = null;
                        return;
                    }
                }
                if (updateNotEmpty) {
                    int removed = compress(this.updateChanges);
                    totalSize -= removed;
                }
                if (addRemoveNotEmpty) {
                    int removed2 = compress(this.addRemoveChanges);
                    totalSize -= removed2;
                }
                SubChange<E>[] array = new SubChange[totalSize];
                int ptr = 0;
                if (this.permutationChange != null) {
                    int ptr2 = 0 + 1;
                    array[0] = this.permutationChange;
                    ptr = ptr2;
                }
                if (addRemoveNotEmpty) {
                    int sz = this.addRemoveChanges.size();
                    for (int i = 0; i < sz; i++) {
                        SubChange<E> change = this.addRemoveChanges.get(i);
                        if (change != null) {
                            array[ptr] = change;
                            ptr++;
                        }
                    }
                }
                if (updateNotEmpty) {
                    int sz2 = this.updateChanges.size();
                    for (int i2 = 0; i2 < sz2; i2++) {
                        SubChange<E> change2 = this.updateChanges.get(i2);
                        if (change2 != null) {
                            array[ptr] = change2;
                            ptr++;
                        }
                    }
                }
                this.list.fireChange(new IterableChange(finalizeSubChangeArray(array), this.list));
                if (this.addRemoveChanges != null) {
                    this.addRemoveChanges.clear();
                }
                if (this.updateChanges != null) {
                    this.updateChanges.clear();
                }
                this.permutationChange = null;
            }
        }
    }

    public void beginChange() {
        this.changeLock++;
    }

    public void endChange() {
        if (this.changeLock <= 0) {
            throw new IllegalStateException("Called endChange before beginChange");
        }
        this.changeLock--;
        commit();
    }

    private static <E> SubChange<E>[] finalizeSubChangeArray(SubChange<E>[] changes) {
        for (SubChange<E> c : changes) {
            finalizeSubChange(c);
        }
        return changes;
    }

    private static <E> SubChange<E> finalizeSubChange(SubChange<E> c) {
        if (c.perm == null) {
            c.perm = EMPTY_PERM;
        }
        if (c.removed == null) {
            c.removed = Collections.emptyList();
        } else {
            c.removed = Collections.unmodifiableList(c.removed);
        }
        return c;
    }

    private static class SingleChange<E> extends ListChangeListener.Change<E> {
        private final SubChange<E> change;
        private boolean onChange;

        public SingleChange(SubChange<E> change, ObservableListBase<E> list) {
            super(list);
            this.change = change;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean next() {
            if (this.onChange) {
                return false;
            }
            this.onChange = true;
            return true;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public void reset() {
            this.onChange = false;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public int getFrom() {
            checkState();
            return this.change.from;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public int getTo() {
            checkState();
            return this.change.to;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            checkState();
            return this.change.removed;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        protected int[] getPermutation() {
            checkState();
            return this.change.perm;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean wasUpdated() {
            checkState();
            return this.change.updated;
        }

        private void checkState() {
            if (!this.onChange) {
                throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
            }
        }

        public String toString() {
            String ret;
            if (this.change.perm.length != 0) {
                ret = ChangeHelper.permChangeToString(this.change.perm);
            } else if (this.change.updated) {
                ret = ChangeHelper.updateChangeToString(this.change.from, this.change.to);
            } else {
                ret = ChangeHelper.addRemoveChangeToString(this.change.from, this.change.to, getList(), this.change.removed);
            }
            return "{ " + ret + " }";
        }
    }

    private static class IterableChange<E> extends ListChangeListener.Change<E> {
        private SubChange[] changes;
        private int cursor;

        private IterableChange(SubChange[] changes, ObservableList<E> list) {
            super(list);
            this.cursor = -1;
            this.changes = changes;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean next() {
            if (this.cursor + 1 < this.changes.length) {
                this.cursor++;
                return true;
            }
            return false;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public void reset() {
            this.cursor = -1;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public int getFrom() {
            checkState();
            return this.changes[this.cursor].from;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public int getTo() {
            checkState();
            return this.changes[this.cursor].to;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public List<E> getRemoved() {
            checkState();
            return this.changes[this.cursor].removed;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        protected int[] getPermutation() {
            checkState();
            return this.changes[this.cursor].perm;
        }

        @Override // com.brixcore.fakefx.collections.ListChangeListener.Change
        public boolean wasUpdated() {
            checkState();
            return this.changes[this.cursor].updated;
        }

        private void checkState() {
            if (this.cursor == -1) {
                throw new IllegalStateException("Invalid Change state: next() must be called before inspecting the Change.");
            }
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("{ ");
            for (int c = 0; c < this.changes.length; c++) {
                if (this.changes[c].perm.length != 0) {
                    b.append(ChangeHelper.permChangeToString(this.changes[c].perm));
                } else if (this.changes[c].updated) {
                    b.append(ChangeHelper.updateChangeToString(this.changes[c].from, this.changes[c].to));
                } else {
                    b.append(ChangeHelper.addRemoveChangeToString(this.changes[c].from, this.changes[c].to, getList(), this.changes[c].removed));
                }
                if (c != this.changes.length - 1) {
                    b.append(", ");
                }
            }
            b.append(" }");
            return b.toString();
        }
    }
}
