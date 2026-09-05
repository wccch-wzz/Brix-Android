package com.brixcore.fakefx.collections;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes3.dex */
public class SortHelper {
    private static final int INSERTIONSORT_THRESHOLD = 7;
    private int[] permutation;
    private int[] reversePermutation;

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends Comparable<? super T>> int[] sort(List<T> list) {
        try {
            Comparable[] comparableArr = (Comparable[]) list.toArray((Comparable[]) Array.newInstance((Class<?>) Comparable.class, list.size()));
            int[] iArrSort = sort(comparableArr);
            ListIterator<T> listIterator = list.listIterator();
            for (Comparable comparable : comparableArr) {
                listIterator.next();
                listIterator.set(comparable);
            }
            return iArrSort;
        } catch (ArrayStoreException e) {
            throw new ClassCastException();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> int[] sort(List<T> list, Comparator<? super T> c) {
        Object[] a = list.toArray();
        int[] result = sort(a, c);
        ListIterator i = list.listIterator();
        for (Object obj : a) {
            i.next();
            i.set(obj);
        }
        return result;
    }

    public <T extends Comparable<? super T>> int[] sort(T[] a) {
        return sort(a, (Comparator) null);
    }

    public <T> int[] sort(T[] a, Comparator<? super T> c) {
        SortHelper sortHelper;
        Object[] objArr = (Object[]) a.clone();
        int[] result = initPermutation(a.length);
        if (c == null) {
            sortHelper = this;
            sortHelper.mergeSort(objArr, a, 0, a.length, 0);
        } else {
            sortHelper = this;
            sortHelper.mergeSort(objArr, a, 0, a.length, 0, c);
        }
        sortHelper.reversePermutation = null;
        sortHelper.permutation = null;
        return result;
    }

    public <T> int[] sort(T[] a, int fromIndex, int toIndex, Comparator<? super T> c) {
        int fromIndex2;
        int toIndex2;
        SortHelper sortHelper;
        rangeCheck(a.length, fromIndex, toIndex);
        Object[] objArrCopyOfRange = copyOfRange(a, fromIndex, toIndex);
        int[] result = initPermutation(a.length);
        if (c == null) {
            sortHelper = this;
            fromIndex2 = fromIndex;
            toIndex2 = toIndex;
            sortHelper.mergeSort(objArrCopyOfRange, a, fromIndex2, toIndex2, -fromIndex);
        } else {
            fromIndex2 = fromIndex;
            toIndex2 = toIndex;
            sortHelper = this;
            sortHelper.mergeSort(objArrCopyOfRange, a, fromIndex2, toIndex2, -fromIndex2, c);
        }
        sortHelper.reversePermutation = null;
        sortHelper.permutation = null;
        return Arrays.copyOfRange(result, fromIndex2, toIndex2);
    }

    public int[] sort(int[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        int[] aux = copyOfRange(a, fromIndex, toIndex);
        int[] result = initPermutation(a.length);
        mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
        this.reversePermutation = null;
        this.permutation = null;
        return Arrays.copyOfRange(result, fromIndex, toIndex);
    }

    private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLen) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }

    private static int[] copyOfRange(int[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        int[] copy = new int[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    private static <T> T[] copyOfRange(T[] tArr, int i, int i2) {
        return (T[]) copyOfRange(tArr, i, i2, tArr.getClass());
    }

    private static <T, U> T[] copyOfRange(U[] uArr, int i, int i2, Class<? extends T[]> cls) {
        T[] tArr;
        int i3 = i2 - i;
        if (i3 < 0) {
            throw new IllegalArgumentException(i + " > " + i2);
        }
        if (cls == Object[].class) {
            tArr = (T[]) new Object[i3];
        } else {
            tArr = (T[]) ((Object[]) Array.newInstance(cls.getComponentType(), i3));
        }
        System.arraycopy(uArr, i, tArr, 0, Math.min(uArr.length - i, i3));
        return tArr;
    }

    private void mergeSort(int[] src, int[] dest, int low, int high, int off) {
        int length = high - low;
        if (length < 7) {
            for (int i = low; i < high; i++) {
                for (int j = i; j > low && Integer.valueOf(dest[j - 1]).compareTo(Integer.valueOf(dest[j])) > 0; j--) {
                    swap(dest, j, j - 1);
                }
            }
            return;
        }
        int low2 = low + off;
        int high2 = high + off;
        int mid = (low2 + high2) >>> 1;
        mergeSort(dest, src, low2, mid, -off);
        mergeSort(dest, src, mid, high2, -off);
        if (Integer.valueOf(src[mid - 1]).compareTo(Integer.valueOf(src[mid])) <= 0) {
            System.arraycopy(src, low2, dest, low, length);
            return;
        }
        int p = low2;
        int q = mid;
        for (int i2 = low; i2 < high; i2++) {
            if (q >= high2 || (p < mid && Integer.valueOf(src[p]).compareTo(Integer.valueOf(src[q])) <= 0)) {
                dest[i2] = src[p];
                this.permutation[this.reversePermutation[p]] = i2;
                p++;
            } else {
                dest[i2] = src[q];
                this.permutation[this.reversePermutation[q]] = i2;
                q++;
            }
        }
        for (int i3 = low; i3 < high; i3++) {
            this.reversePermutation[this.permutation[i3]] = i3;
        }
    }

    private void mergeSort(Object[] src, Object[] dest, int low, int high, int off) {
        int length = high - low;
        if (length < 7) {
            for (int i = low; i < high; i++) {
                for (int j = i; j > low && ((Comparable) dest[j - 1]).compareTo(dest[j]) > 0; j--) {
                    swap(dest, j, j - 1);
                }
            }
            return;
        }
        int low2 = low + off;
        int high2 = high + off;
        int mid = (low2 + high2) >>> 1;
        mergeSort(dest, src, low2, mid, -off);
        mergeSort(dest, src, mid, high2, -off);
        if (((Comparable) src[mid - 1]).compareTo(src[mid]) <= 0) {
            System.arraycopy(src, low2, dest, low, length);
            return;
        }
        int p = low2;
        int q = mid;
        for (int i2 = low; i2 < high; i2++) {
            if (q >= high2 || (p < mid && ((Comparable) src[p]).compareTo(src[q]) <= 0)) {
                dest[i2] = src[p];
                this.permutation[this.reversePermutation[p]] = i2;
                p++;
            } else {
                dest[i2] = src[q];
                this.permutation[this.reversePermutation[q]] = i2;
                q++;
            }
        }
        for (int i3 = low; i3 < high; i3++) {
            this.reversePermutation[this.permutation[i3]] = i3;
        }
    }

    private void mergeSort(Object[] src, Object[] dest, int low, int high, int off, Comparator c) {
        int length = high - low;
        if (length < 7) {
            for (int i = low; i < high; i++) {
                for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--) {
                    swap(dest, j, j - 1);
                }
            }
            return;
        }
        int low2 = low + off;
        int high2 = high + off;
        int mid = (low2 + high2) >>> 1;
        mergeSort(dest, src, low2, mid, -off, c);
        mergeSort(dest, src, mid, high2, -off, c);
        if (c.compare(src[mid - 1], src[mid]) <= 0) {
            System.arraycopy(src, low2, dest, low, length);
            return;
        }
        int p = low2;
        int q = mid;
        for (int i2 = low; i2 < high; i2++) {
            if (q >= high2 || (p < mid && c.compare(src[p], src[q]) <= 0)) {
                dest[i2] = src[p];
                this.permutation[this.reversePermutation[p]] = i2;
                p++;
            } else {
                dest[i2] = src[q];
                this.permutation[this.reversePermutation[q]] = i2;
                q++;
            }
        }
        for (int i3 = low; i3 < high; i3++) {
            this.reversePermutation[this.permutation[i3]] = i3;
        }
    }

    private void swap(int[] x, int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
        this.permutation[this.reversePermutation[a]] = b;
        this.permutation[this.reversePermutation[b]] = a;
        int tp = this.reversePermutation[a];
        this.reversePermutation[a] = this.reversePermutation[b];
        this.reversePermutation[b] = tp;
    }

    private void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
        this.permutation[this.reversePermutation[a]] = b;
        this.permutation[this.reversePermutation[b]] = a;
        int tp = this.reversePermutation[a];
        this.reversePermutation[a] = this.reversePermutation[b];
        this.reversePermutation[b] = tp;
    }

    private int[] initPermutation(int length) {
        this.permutation = new int[length];
        this.reversePermutation = new int[length];
        for (int i = 0; i < length; i++) {
            int[] iArr = this.permutation;
            this.reversePermutation[i] = i;
            iArr[i] = i;
        }
        return this.permutation;
    }
}
