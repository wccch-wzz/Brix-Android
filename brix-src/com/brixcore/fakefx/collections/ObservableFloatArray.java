package com.brixcore.fakefx.collections;

/* JADX INFO: loaded from: classes3.dex */
public interface ObservableFloatArray extends ObservableArray<ObservableFloatArray> {
    void addAll(ObservableFloatArray observableFloatArray);

    void addAll(ObservableFloatArray observableFloatArray, int i, int i2);

    void addAll(float... fArr);

    void addAll(float[] fArr, int i, int i2);

    void copyTo(int i, ObservableFloatArray observableFloatArray, int i2, int i3);

    void copyTo(int i, float[] fArr, int i2, int i3);

    float get(int i);

    void set(int i, float f);

    void set(int i, ObservableFloatArray observableFloatArray, int i2, int i3);

    void set(int i, float[] fArr, int i2, int i3);

    void setAll(ObservableFloatArray observableFloatArray);

    void setAll(ObservableFloatArray observableFloatArray, int i, int i2);

    void setAll(float... fArr);

    void setAll(float[] fArr, int i, int i2);

    float[] toArray(int i, float[] fArr, int i2);

    float[] toArray(float[] fArr);
}
