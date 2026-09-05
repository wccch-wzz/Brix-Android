package com.brixcore.fakefx.beans.value;

/* JADX INFO: loaded from: classes2.dex */
public interface WritableLongValue extends WritableNumberValue {
    long get();

    void set(long j);

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.brixcore.fakefx.beans.value.WritableValue, com.brixcore.fakefx.beans.value.WritableBooleanValue
    void setValue(Number number);
}
