package com.github.junrar.unpack.ppm;

import com.github.junrar.io.Raw;
import kotlin.UShort;

/* JADX INFO: loaded from: classes.dex */
public class FreqData extends Pointer {
    public static final int size = 6;

    public FreqData(byte[] mem) {
        super(mem);
    }

    public FreqData init(byte[] mem) {
        this.mem = mem;
        this.pos = 0;
        return this;
    }

    public int getSummFreq() {
        return Raw.readShortLittleEndian(this.mem, this.pos) & UShort.MAX_VALUE;
    }

    public void setSummFreq(int summFreq) {
        Raw.writeShortLittleEndian(this.mem, this.pos, (short) summFreq);
    }

    public void incSummFreq(int dSummFreq) {
        Raw.incShortLittleEndian(this.mem, this.pos, dSummFreq);
    }

    public int getStats() {
        return Raw.readIntLittleEndian(this.mem, this.pos + 2);
    }

    public void setStats(State state) {
        setStats(state.getAddress());
    }

    public void setStats(int state) {
        Raw.writeIntLittleEndian(this.mem, this.pos + 2, state);
    }

    public String toString() {
        return "FreqData[\n  pos=" + this.pos + "\n  size=6\n  summFreq=" + getSummFreq() + "\n  stats=" + getStats() + "\n]";
    }
}
