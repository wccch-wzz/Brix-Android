package com.github.junrar.unpack.ppm;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes.dex */
public class SubAllocator {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int FIXED_UNIT_SIZE = 12;
    public static final int N1 = 4;
    public static final int N2 = 4;
    public static final int N3 = 4;
    public static final int N4 = 26;
    public static final int N_INDEXES = 38;
    public static final int UNIT_SIZE = Math.max(PPMContext.size, 12);
    private int fakeUnitsStart;
    private int freeListPos;
    private int glueCount;
    private byte[] heap;
    private int heapEnd;
    private int heapStart;
    private int hiUnit;
    private int loUnit;
    private int pText;
    private int subAllocatorSize;
    private int tempMemBlockPos;
    private int unitsStart;
    private final int[] indx2Units = new int[38];
    private final int[] units2Indx = new int[128];
    private final RarNode[] freeList = new RarNode[38];
    private RarNode tempRarNode = null;
    private RarMemBlock tempRarMemBlock1 = null;
    private RarMemBlock tempRarMemBlock2 = null;
    private RarMemBlock tempRarMemBlock3 = null;

    public SubAllocator() {
        clean();
    }

    public void clean() {
        this.subAllocatorSize = 0;
    }

    private void insertNode(int p, int indx) {
        RarNode temp = this.tempRarNode;
        temp.setAddress(p);
        temp.setNext(this.freeList[indx].getNext());
        this.freeList[indx].setNext(temp);
    }

    public void incPText() {
        this.pText++;
    }

    private int removeNode(int indx) {
        int retVal = this.freeList[indx].getNext();
        RarNode temp = this.tempRarNode;
        temp.setAddress(retVal);
        this.freeList[indx].setNext(temp.getNext());
        return retVal;
    }

    private int U2B(int NU) {
        return UNIT_SIZE * NU;
    }

    private int MBPtr(int BasePtr, int Items) {
        return U2B(Items) + BasePtr;
    }

    private void splitBlock(int pv, int oldIndx, int newIndx) {
        int uDiff = this.indx2Units[oldIndx] - this.indx2Units[newIndx];
        int p = U2B(this.indx2Units[newIndx]) + pv;
        int[] iArr = this.indx2Units;
        int i = this.units2Indx[uDiff - 1];
        if (iArr[i] != uDiff) {
            int i2 = i - 1;
            insertNode(p, i2);
            int i3 = this.indx2Units[i2];
            p += U2B(i3);
            uDiff -= i3;
        }
        insertNode(p, this.units2Indx[uDiff - 1]);
    }

    public void stopSubAllocator() {
        if (this.subAllocatorSize != 0) {
            this.subAllocatorSize = 0;
            this.heap = null;
            this.heapStart = 1;
            this.tempRarNode = null;
            this.tempRarMemBlock1 = null;
            this.tempRarMemBlock2 = null;
            this.tempRarMemBlock3 = null;
        }
    }

    public int GetAllocatedMemory() {
        return this.subAllocatorSize;
    }

    public boolean startSubAllocator(int SASize) {
        int t = SASize << 20;
        if (this.subAllocatorSize == t) {
            return true;
        }
        stopSubAllocator();
        int allocSize = ((t / 12) * UNIT_SIZE) + UNIT_SIZE;
        int realAllocSize = allocSize + 1 + 152;
        this.tempMemBlockPos = realAllocSize;
        int realAllocSize2 = realAllocSize + 12;
        this.heap = new byte[realAllocSize2];
        this.heapStart = 1;
        this.heapEnd = (this.heapStart + allocSize) - UNIT_SIZE;
        this.subAllocatorSize = t;
        this.freeListPos = this.heapStart + allocSize;
        if (realAllocSize2 - this.tempMemBlockPos != 12) {
            throw new AssertionError(realAllocSize2 + StringUtils.SPACE + this.tempMemBlockPos + StringUtils.SPACE + 12);
        }
        int i = 0;
        int pos = this.freeListPos;
        while (i < this.freeList.length) {
            this.freeList[i] = new RarNode(this.heap);
            this.freeList[i].setAddress(pos);
            i++;
            pos += 4;
        }
        this.tempRarNode = new RarNode(this.heap);
        this.tempRarMemBlock1 = new RarMemBlock(this.heap);
        this.tempRarMemBlock2 = new RarMemBlock(this.heap);
        this.tempRarMemBlock3 = new RarMemBlock(this.heap);
        return true;
    }

    private void glueFreeBlocks() {
        RarMemBlock s0 = this.tempRarMemBlock1;
        s0.setAddress(this.tempMemBlockPos);
        RarMemBlock p = this.tempRarMemBlock2;
        RarMemBlock p1 = this.tempRarMemBlock3;
        if (this.loUnit != this.hiUnit) {
            this.heap[this.loUnit] = 0;
        }
        s0.setPrev(s0);
        s0.setNext(s0);
        for (int i = 0; i < 38; i++) {
            while (this.freeList[i].getNext() != 0) {
                p.setAddress(removeNode(i));
                p.insertAt(s0);
                p.setStamp(65535);
                p.setNU(this.indx2Units[i]);
            }
        }
        p.setAddress(s0.getNext());
        while (p.getAddress() != s0.getAddress()) {
            p1.setAddress(MBPtr(p.getAddress(), p.getNU()));
            while (p1.getStamp() == 65535 && p.getNU() + p1.getNU() < 65536) {
                p1.remove();
                p.setNU(p.getNU() + p1.getNU());
                p1.setAddress(MBPtr(p.getAddress(), p.getNU()));
            }
            p.setAddress(p.getNext());
        }
        p.setAddress(s0.getNext());
        while (p.getAddress() != s0.getAddress()) {
            p.remove();
            int sz = p.getNU();
            while (sz > 128) {
                insertNode(p.getAddress(), 37);
                sz -= 128;
                p.setAddress(MBPtr(p.getAddress(), 128));
            }
            int[] iArr = this.indx2Units;
            int i2 = this.units2Indx[sz - 1];
            int i3 = i2;
            if (iArr[i2] != sz) {
                i3--;
                int k = sz - this.indx2Units[i3];
                insertNode(MBPtr(p.getAddress(), sz - k), k - 1);
            }
            insertNode(p.getAddress(), i3);
            p.setAddress(s0.getNext());
        }
    }

    private int allocUnitsRare(int indx) {
        if (this.glueCount == 0) {
            this.glueCount = 255;
            glueFreeBlocks();
            if (this.freeList[indx].getNext() != 0) {
                return removeNode(indx);
            }
        }
        int i = indx;
        do {
            i++;
            if (i == 38) {
                this.glueCount--;
                int i2 = U2B(this.indx2Units[indx]);
                int j = this.indx2Units[indx] * 12;
                if (this.fakeUnitsStart - this.pText > j) {
                    this.fakeUnitsStart -= j;
                    this.unitsStart -= i2;
                    return this.unitsStart;
                }
                return 0;
            }
        } while (this.freeList[i].getNext() == 0);
        int retVal = removeNode(i);
        splitBlock(retVal, i, indx);
        return retVal;
    }

    public int allocUnits(int NU) {
        int indx = this.units2Indx[NU - 1];
        if (this.freeList[indx].getNext() != 0) {
            return removeNode(indx);
        }
        int retVal = this.loUnit;
        this.loUnit += U2B(this.indx2Units[indx]);
        if (this.loUnit <= this.hiUnit) {
            return retVal;
        }
        this.loUnit -= U2B(this.indx2Units[indx]);
        return allocUnitsRare(indx);
    }

    public int allocContext() {
        if (this.hiUnit != this.loUnit) {
            int i = this.hiUnit - UNIT_SIZE;
            this.hiUnit = i;
            return i;
        }
        if (this.freeList[0].getNext() != 0) {
            return removeNode(0);
        }
        return allocUnitsRare(0);
    }

    public int expandUnits(int oldPtr, int OldNU) {
        int i0 = this.units2Indx[OldNU - 1];
        int i1 = this.units2Indx[(OldNU - 1) + 1];
        if (i0 == i1) {
            return oldPtr;
        }
        int ptr = allocUnits(OldNU + 1);
        if (ptr != 0) {
            System.arraycopy(this.heap, oldPtr, this.heap, ptr, U2B(OldNU));
            insertNode(oldPtr, i0);
        }
        return ptr;
    }

    public int shrinkUnits(int oldPtr, int oldNU, int newNU) {
        int i0 = this.units2Indx[oldNU - 1];
        int i1 = this.units2Indx[newNU - 1];
        if (i0 == i1) {
            return oldPtr;
        }
        if (this.freeList[i1].getNext() != 0) {
            int ptr = removeNode(i1);
            System.arraycopy(this.heap, oldPtr, this.heap, ptr, U2B(newNU));
            insertNode(oldPtr, i0);
            return ptr;
        }
        splitBlock(oldPtr, i0, i1);
        return oldPtr;
    }

    public void freeUnits(int ptr, int OldNU) {
        insertNode(ptr, this.units2Indx[OldNU - 1]);
    }

    public int getFakeUnitsStart() {
        return this.fakeUnitsStart;
    }

    public void setFakeUnitsStart(int fakeUnitsStart) {
        this.fakeUnitsStart = fakeUnitsStart;
    }

    public int getHeapEnd() {
        return this.heapEnd;
    }

    public int getPText() {
        return this.pText;
    }

    public void setPText(int text) {
        this.pText = text;
    }

    public void decPText(int dPText) {
        setPText(getPText() - dPText);
    }

    public int getUnitsStart() {
        return this.unitsStart;
    }

    public void setUnitsStart(int unitsStart) {
        this.unitsStart = unitsStart;
    }

    public void initSubAllocator() {
        Arrays.fill(this.heap, this.freeListPos, this.freeListPos + sizeOfFreeList(), (byte) 0);
        this.pText = this.heapStart;
        int size2 = ((this.subAllocatorSize / 8) / 12) * 7 * 12;
        int realSize2 = (size2 / 12) * UNIT_SIZE;
        int size1 = this.subAllocatorSize - size2;
        int realSize1 = ((size1 / 12) * UNIT_SIZE) + (size1 % 12);
        this.hiUnit = this.heapStart + this.subAllocatorSize;
        int i = this.heapStart + realSize1;
        this.unitsStart = i;
        this.loUnit = i;
        this.fakeUnitsStart = this.heapStart + size1;
        this.hiUnit = this.loUnit + realSize2;
        int i2 = 0;
        int k = 1;
        while (i2 < 4) {
            this.indx2Units[i2] = k & 255;
            i2++;
            k++;
        }
        int k2 = k + 1;
        while (i2 < 8) {
            this.indx2Units[i2] = k2 & 255;
            i2++;
            k2 += 2;
        }
        int k3 = k2 + 1;
        while (i2 < 12) {
            this.indx2Units[i2] = k3 & 255;
            i2++;
            k3 += 3;
        }
        int k4 = k3 + 1;
        while (i2 < 38) {
            this.indx2Units[i2] = k4 & 255;
            i2++;
            k4 += 4;
        }
        this.glueCount = 0;
        int i3 = 0;
        for (int k5 = 0; k5 < 128; k5++) {
            i3 += this.indx2Units[i3] < k5 + 1 ? 1 : 0;
            this.units2Indx[k5] = i3 & 255;
        }
    }

    private int sizeOfFreeList() {
        return this.freeList.length * 4;
    }

    public byte[] getHeap() {
        return this.heap;
    }

    public String toString() {
        return "SubAllocator[\n  subAllocatorSize=" + this.subAllocatorSize + "\n  glueCount=" + this.glueCount + "\n  heapStart=" + this.heapStart + "\n  loUnit=" + this.loUnit + "\n  hiUnit=" + this.hiUnit + "\n  pText=" + this.pText + "\n  unitsStart=" + this.unitsStart + "\n]";
    }
}
