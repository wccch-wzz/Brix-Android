package com.brixcore.fakefx.collections;

/* JADX INFO: loaded from: classes3.dex */
public interface ObservableIntegerArray extends ObservableArray<ObservableIntegerArray> {
    void addAll(ObservableIntegerArray observableIntegerArray);

    void addAll(ObservableIntegerArray observableIntegerArray, int i, int i2);

    void addAll(int... iArr);

    void addAll(int[] iArr, int i, int i2);

    void copyTo(int i, ObservableIntegerArray observableIntegerArray, int i2, int i3);

    void copyTo(int i, int[] iArr, int i2, int i3);

    int get(int i);

    void set(int i, int i2);

    void set(int i, ObservableIntegerArray observableIntegerArray, int i2, int i3);

    void set(int i, int[] iArr, int i2, int i3);

    void setAll(ObservableIntegerArray observableIntegerArray);

    void setAll(ObservableIntegerArray observableIntegerArray, int i, int i2);

    void setAll(int... iArr);

    void setAll(int[] iArr, int i, int i2);

    int[] toArray(int i, int[] iArr, int i2);

    int[] toArray(int[] iArr);
}
