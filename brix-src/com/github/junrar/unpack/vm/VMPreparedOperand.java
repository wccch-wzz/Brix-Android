package com.github.junrar.unpack.vm;

/* JADX INFO: loaded from: classes.dex */
public class VMPreparedOperand {
    private int Base;
    private int Data;
    private VMOpType Type;
    private int offset;

    public int getBase() {
        return this.Base;
    }

    public void setBase(int base) {
        this.Base = base;
    }

    public int getData() {
        return this.Data;
    }

    public void setData(int data) {
        this.Data = data;
    }

    public VMOpType getType() {
        return this.Type;
    }

    public void setType(VMOpType type) {
        this.Type = type;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
