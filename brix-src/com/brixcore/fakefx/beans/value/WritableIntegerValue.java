package com.brixcore.fakefx.beans.value;

/* JADX INFO: loaded from: classes2.dex */
public interface WritableIntegerValue extends WritableNumberValue {
    int get();

    void set(int i);

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    void setValue(Number number);
}
