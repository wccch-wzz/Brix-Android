package com.github.junrar.unpack.vm;

/* JADX INFO: loaded from: classes.dex */
public enum VMOpType {
    VM_OPREG(0),
    VM_OPINT(1),
    VM_OPREGMEM(2),
    VM_OPNONE(3);

    private final int opType;

    VMOpType(int opType) {
        this.opType = opType;
    }

    public int getOpType() {
        return this.opType;
    }

    public boolean equals(int opType) {
        return this.opType == opType;
    }

    public static VMOpType findOpType(int opType) {
        if (VM_OPREG.equals(opType)) {
            return VM_OPREG;
        }
        if (VM_OPINT.equals(opType)) {
            return VM_OPINT;
        }
        if (VM_OPREGMEM.equals(opType)) {
            return VM_OPREGMEM;
        }
        if (VM_OPNONE.equals(opType)) {
            return VM_OPNONE;
        }
        return null;
    }
}
