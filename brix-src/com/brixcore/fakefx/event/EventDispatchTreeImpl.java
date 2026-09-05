package com.brixcore.fakefx.event;

/* JADX INFO: loaded from: classes5.dex */
public final class EventDispatchTreeImpl implements EventDispatchTree {
    private static final int CAPACITY_GROWTH_FACTOR = 8;
    private static final int NULL_INDEX = -1;
    private EventDispatcher[] dispatchers;
    private boolean expandTailFirstPath;
    private int[] nextChildren;
    private int[] nextSiblings;
    private int reservedCount;
    private int rootIndex = -1;
    private int tailFirstIndex = -1;
    private int tailLastIndex = -1;

    public void reset() {
        for (int i = 0; i < this.reservedCount; i++) {
            this.dispatchers[i] = null;
        }
        this.reservedCount = 0;
        this.rootIndex = -1;
        this.tailFirstIndex = -1;
        this.tailLastIndex = -1;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchTree
    public EventDispatchTree createTree() {
        return new EventDispatchTreeImpl();
    }

    @Override // com.brixcore.fakefx.event.EventDispatchTree
    public EventDispatchTree mergeTree(EventDispatchTree tree) {
        int srcLevelIndex;
        if (this.tailFirstIndex != -1) {
            if (this.rootIndex != -1) {
                this.expandTailFirstPath = true;
                expandTail(this.rootIndex);
            } else {
                this.rootIndex = this.tailFirstIndex;
            }
            this.tailFirstIndex = -1;
            this.tailLastIndex = -1;
        }
        EventDispatchTreeImpl treeImpl = (EventDispatchTreeImpl) tree;
        if (treeImpl.rootIndex != -1) {
            srcLevelIndex = treeImpl.rootIndex;
        } else {
            srcLevelIndex = treeImpl.tailFirstIndex;
        }
        if (this.rootIndex == -1) {
            this.rootIndex = copyTreeLevel(treeImpl, srcLevelIndex);
        } else {
            mergeTreeLevel(treeImpl, this.rootIndex, srcLevelIndex);
        }
        return this;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchTree, com.brixcore.fakefx.event.EventDispatchChain
    public EventDispatchTree append(EventDispatcher eventDispatcher) {
        ensureCapacity(this.reservedCount + 1);
        this.dispatchers[this.reservedCount] = eventDispatcher;
        this.nextSiblings[this.reservedCount] = -1;
        this.nextChildren[this.reservedCount] = -1;
        if (this.tailFirstIndex == -1) {
            this.tailFirstIndex = this.reservedCount;
        } else {
            this.nextChildren[this.tailLastIndex] = this.reservedCount;
        }
        this.tailLastIndex = this.reservedCount;
        this.reservedCount++;
        return this;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchTree, com.brixcore.fakefx.event.EventDispatchChain
    public EventDispatchTree prepend(EventDispatcher eventDispatcher) {
        ensureCapacity(this.reservedCount + 1);
        this.dispatchers[this.reservedCount] = eventDispatcher;
        this.nextSiblings[this.reservedCount] = -1;
        this.nextChildren[this.reservedCount] = this.rootIndex;
        this.rootIndex = this.reservedCount;
        this.reservedCount++;
        return this;
    }

    @Override // com.brixcore.fakefx.event.EventDispatchChain
    public Event dispatchEvent(Event event) {
        if (this.rootIndex == -1) {
            if (this.tailFirstIndex == -1) {
                return event;
            }
            this.rootIndex = this.tailFirstIndex;
            this.tailFirstIndex = -1;
            this.tailLastIndex = -1;
        }
        int savedReservedCount = this.reservedCount;
        int savedRootIndex = this.rootIndex;
        int savedTailFirstIndex = this.tailFirstIndex;
        int savedTailLastIndex = this.tailLastIndex;
        Event returnEvent = null;
        int index = this.rootIndex;
        do {
            this.rootIndex = this.nextChildren[index];
            Event branchReturnEvent = this.dispatchers[index].dispatchEvent(event, this);
            if (branchReturnEvent != null) {
                returnEvent = returnEvent != null ? event : branchReturnEvent;
            }
            index = this.nextSiblings[index];
        } while (index != -1);
        this.reservedCount = savedReservedCount;
        this.rootIndex = savedRootIndex;
        this.tailFirstIndex = savedTailFirstIndex;
        this.tailLastIndex = savedTailLastIndex;
        return returnEvent;
    }

    public String toString() {
        int levelIndex = this.rootIndex != -1 ? this.rootIndex : this.tailFirstIndex;
        if (levelIndex == -1) {
            return "()";
        }
        StringBuilder sb = new StringBuilder();
        appendTreeLevel(sb, levelIndex);
        return sb.toString();
    }

    private void ensureCapacity(int size) {
        int newCapacity = ((size + 8) - 1) & (-8);
        if (newCapacity == 0) {
            return;
        }
        if (this.dispatchers == null || this.dispatchers.length < newCapacity) {
            EventDispatcher[] newDispatchers = new EventDispatcher[newCapacity];
            int[] newNextChildren = new int[newCapacity];
            int[] newNextSiblings = new int[newCapacity];
            if (this.reservedCount > 0) {
                System.arraycopy(this.dispatchers, 0, newDispatchers, 0, this.reservedCount);
                System.arraycopy(this.nextChildren, 0, newNextChildren, 0, this.reservedCount);
                System.arraycopy(this.nextSiblings, 0, newNextSiblings, 0, this.reservedCount);
            }
            this.dispatchers = newDispatchers;
            this.nextChildren = newNextChildren;
            this.nextSiblings = newNextSiblings;
        }
    }

    private void expandTail(int levelIndex) {
        int index = levelIndex;
        while (index != -1) {
            if (this.nextChildren[index] != -1) {
                expandTail(this.nextChildren[index]);
            } else if (this.expandTailFirstPath) {
                this.nextChildren[index] = this.tailFirstIndex;
                this.expandTailFirstPath = false;
            } else {
                int childLevelIndex = copyTreeLevel(this, this.tailFirstIndex);
                this.nextChildren[index] = childLevelIndex;
            }
            index = this.nextSiblings[index];
        }
    }

    private void mergeTreeLevel(EventDispatchTreeImpl srcTree, int dstLevelIndex, int srcLevelIndex) {
        int srcIndex = srcLevelIndex;
        while (srcIndex != -1) {
            EventDispatcher srcDispatcher = srcTree.dispatchers[srcIndex];
            int dstIndex = dstLevelIndex;
            int lastDstIndex = dstLevelIndex;
            while (dstIndex != -1 && srcDispatcher != this.dispatchers[dstIndex]) {
                lastDstIndex = dstIndex;
                dstIndex = this.nextSiblings[dstIndex];
            }
            if (dstIndex == -1) {
                int siblingIndex = copySubtree(srcTree, srcIndex);
                this.nextSiblings[lastDstIndex] = siblingIndex;
                this.nextSiblings[siblingIndex] = -1;
            } else {
                int nextDstLevelIndex = this.nextChildren[dstIndex];
                int nextSrcLevelIndex = getChildIndex(srcTree, srcIndex);
                if (nextDstLevelIndex != -1) {
                    mergeTreeLevel(srcTree, nextDstLevelIndex, nextSrcLevelIndex);
                } else {
                    this.nextChildren[dstIndex] = copyTreeLevel(srcTree, nextSrcLevelIndex);
                }
            }
            srcIndex = srcTree.nextSiblings[srcIndex];
        }
    }

    private int copyTreeLevel(EventDispatchTreeImpl srcTree, int srcLevelIndex) {
        if (srcLevelIndex == -1) {
            return -1;
        }
        int dstLevelIndex = copySubtree(srcTree, srcLevelIndex);
        int lastDstIndex = dstLevelIndex;
        int srcIndex = srcTree.nextSiblings[srcLevelIndex];
        while (srcIndex != -1) {
            int dstIndex = copySubtree(srcTree, srcIndex);
            this.nextSiblings[lastDstIndex] = dstIndex;
            lastDstIndex = dstIndex;
            srcIndex = srcTree.nextSiblings[srcIndex];
        }
        this.nextSiblings[lastDstIndex] = -1;
        return dstLevelIndex;
    }

    private int copySubtree(EventDispatchTreeImpl srcTree, int srcIndex) {
        ensureCapacity(this.reservedCount + 1);
        int dstIndex = this.reservedCount;
        this.reservedCount = dstIndex + 1;
        int dstChildLevelIndex = copyTreeLevel(srcTree, getChildIndex(srcTree, srcIndex));
        this.dispatchers[dstIndex] = srcTree.dispatchers[srcIndex];
        this.nextChildren[dstIndex] = dstChildLevelIndex;
        return dstIndex;
    }

    private void appendTreeLevel(StringBuilder sb, int levelIndex) {
        sb.append('(');
        appendSubtree(sb, levelIndex);
        int index = this.nextSiblings[levelIndex];
        while (index != -1) {
            sb.append(",");
            appendSubtree(sb, index);
            index = this.nextSiblings[index];
        }
        sb.append(')');
    }

    private void appendSubtree(StringBuilder sb, int index) {
        sb.append(this.dispatchers[index]);
        int childIndex = getChildIndex(this, index);
        if (childIndex != -1) {
            sb.append("->");
            appendTreeLevel(sb, childIndex);
        }
    }

    private static int getChildIndex(EventDispatchTreeImpl tree, int index) {
        int childIndex = tree.nextChildren[index];
        if (childIndex == -1 && index != tree.tailLastIndex) {
            return tree.tailFirstIndex;
        }
        return childIndex;
    }
}
