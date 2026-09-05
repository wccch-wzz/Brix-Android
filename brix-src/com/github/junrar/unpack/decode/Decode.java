package com.github.junrar.unpack.decode;

/* JADX INFO: loaded from: classes.dex */
public class Decode {
    private int maxNum;
    private final int[] decodeLen = new int[16];
    private final int[] decodePos = new int[16];
    protected int[] decodeNum = new int[2];

    public int[] getDecodeLen() {
        return this.decodeLen;
    }

    public int[] getDecodeNum() {
        return this.decodeNum;
    }

    public int[] getDecodePos() {
        return this.decodePos;
    }

    public int getMaxNum() {
        return this.maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }
}
