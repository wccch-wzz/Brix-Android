package com.github.junrar.unpack.vm;

/* JADX INFO: loaded from: classes.dex */
public class BitInput {
    public static final int MAX_SIZE = 32768;
    protected int inAddr;
    protected int inBit;
    protected byte[] inBuf = new byte[32768];

    public void InitBitInput() {
        this.inAddr = 0;
        this.inBit = 0;
    }

    public void addbits(int Bits) {
        int Bits2 = Bits + this.inBit;
        this.inAddr += Bits2 >>> 3;
        this.inBit = Bits2 & 7;
    }

    public int getbits() {
        return (((((this.inBuf[this.inAddr] & 255) << 16) + ((this.inBuf[this.inAddr + 1] & 255) << 8)) + (this.inBuf[this.inAddr + 2] & 255)) >>> (8 - this.inBit)) & 65535;
    }

    public void faddbits(int Bits) {
        addbits(Bits);
    }

    public int fgetbits() {
        return getbits();
    }

    public boolean Overflow(int IncPtr) {
        return this.inAddr + IncPtr >= 32768;
    }

    public byte[] getInBuf() {
        return this.inBuf;
    }
}
