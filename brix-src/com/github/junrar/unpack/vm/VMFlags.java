package com.github.junrar.unpack.vm;

/* JADX INFO: loaded from: classes.dex */
public enum VMFlags {
    VM_FC(1),
    VM_FZ(2),
    VM_FS(Integer.MIN_VALUE);

    private final int flag;

    VMFlags(int flag) {
        this.flag = flag;
    }

    public static VMFlags findFlag(int flag) {
        if (VM_FC.equals(flag)) {
            return VM_FC;
        }
        if (VM_FS.equals(flag)) {
            return VM_FS;
        }
        if (VM_FZ.equals(flag)) {
            return VM_FZ;
        }
        return null;
    }

    public boolean equals(int flag) {
        return this.flag == flag;
    }

    public int getFlag() {
        return this.flag;
    }
}
