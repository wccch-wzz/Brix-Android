package com.brixcore.fakefx.beans.value;

/* JADX INFO: loaded from: classes2.dex */
public interface WritableBooleanValue extends WritableValue<Boolean> {
    boolean get();

    void set(boolean z);

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // 
    void setValue(Boolean bool);
}
