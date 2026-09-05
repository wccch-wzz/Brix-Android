package com.github.junrar.unpack.vm;

/* JADX INFO: loaded from: classes.dex */
public class VMStandardFilterSignature {
    private int CRC;
    private int length;
    private VMStandardFilters type;

    public VMStandardFilterSignature(int length, int crc, VMStandardFilters type) {
        this.length = length;
        this.CRC = crc;
        this.type = type;
    }

    public int getCRC() {
        return this.CRC;
    }

    public void setCRC(int crc) {
        this.CRC = crc;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public VMStandardFilters getType() {
        return this.type;
    }

    public void setType(VMStandardFilters type) {
        this.type = type;
    }
}
