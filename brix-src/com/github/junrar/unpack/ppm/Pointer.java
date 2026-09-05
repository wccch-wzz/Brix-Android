package com.github.junrar.unpack.ppm;

/* JADX INFO: loaded from: classes.dex */
public abstract class Pointer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    protected byte[] mem;
    protected int pos;

    public Pointer(byte[] mem) {
        this.mem = mem;
    }

    public int getAddress() {
        if (this.mem == null) {
            throw new AssertionError();
        }
        return this.pos;
    }

    public void setAddress(int pos) {
        if (this.mem == null) {
            throw new AssertionError();
        }
        if (pos < 0 || pos >= this.mem.length) {
            throw new AssertionError(pos);
        }
        this.pos = pos;
    }
}
