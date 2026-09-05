package com.github.junrar.unpack.ppm;

/* JADX INFO: loaded from: classes.dex */
public class SEE2Context {
    public static final int size = 4;
    private int count;
    private int shift;
    private int summ;

    public void init(int initVal) {
        this.shift = 3;
        this.summ = (initVal << this.shift) & 65535;
        this.count = 4;
    }

    public int getMean() {
        int retVal = this.summ >>> this.shift;
        this.summ -= retVal;
        return (retVal == 0 ? 1 : 0) + retVal;
    }

    public void update() {
        if (this.shift < 7) {
            int i = this.count - 1;
            this.count = i;
            if (i == 0) {
                this.summ += this.summ;
                int i2 = this.shift;
                this.shift = i2 + 1;
                this.count = 3 << i2;
            }
        }
        this.summ &= 65535;
        this.count &= 255;
        this.shift &= 255;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count & 255;
    }

    public int getShift() {
        return this.shift;
    }

    public void setShift(int shift) {
        this.shift = shift & 255;
    }

    public int getSumm() {
        return this.summ;
    }

    public void setSumm(int summ) {
        this.summ = 65535 & summ;
    }

    public void incSumm(int dSumm) {
        setSumm(getSumm() + dSumm);
    }

    public String toString() {
        return "SEE2Context[\n  size=4\n  summ=" + this.summ + "\n  shift=" + this.shift + "\n  count=" + this.count + "\n]";
    }
}
