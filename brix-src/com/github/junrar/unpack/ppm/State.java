package com.github.junrar.unpack.ppm;

import com.github.junrar.io.Raw;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class State extends Pointer {
    public static final int size = 6;

    public State(byte[] mem) {
        super(mem);
    }

    public State init(byte[] mem) {
        this.mem = mem;
        this.pos = 0;
        return this;
    }

    public int getSymbol() {
        return this.mem[this.pos] & UByte.MAX_VALUE;
    }

    public void setSymbol(int symbol) {
        this.mem[this.pos] = (byte) symbol;
    }

    public int getFreq() {
        return this.mem[this.pos + 1] & UByte.MAX_VALUE;
    }

    public void setFreq(int freq) {
        this.mem[this.pos + 1] = (byte) freq;
    }

    public void incFreq(int dFreq) {
        byte[] bArr = this.mem;
        int i = this.pos + 1;
        bArr[i] = (byte) (bArr[i] + dFreq);
    }

    public int getSuccessor() {
        return Raw.readIntLittleEndian(this.mem, this.pos + 2);
    }

    public void setSuccessor(PPMContext successor) {
        setSuccessor(successor.getAddress());
    }

    public void setSuccessor(int successor) {
        Raw.writeIntLittleEndian(this.mem, this.pos + 2, successor);
    }

    public void setValues(StateRef state) {
        setSymbol(state.getSymbol());
        setFreq(state.getFreq());
        setSuccessor(state.getSuccessor());
    }

    public void setValues(State ptr) {
        System.arraycopy(ptr.mem, ptr.pos, this.mem, this.pos, 6);
    }

    public State decAddress() {
        setAddress(this.pos - 6);
        return this;
    }

    public State incAddress() {
        setAddress(this.pos + 6);
        return this;
    }

    public static void ppmdSwap(State ptr1, State ptr2) {
        byte[] mem1 = ptr1.mem;
        byte[] mem2 = ptr2.mem;
        int i = 0;
        int pos1 = ptr1.pos;
        int pos2 = ptr2.pos;
        while (i < 6) {
            byte temp = mem1[pos1];
            mem1[pos1] = mem2[pos2];
            mem2[pos2] = temp;
            i++;
            pos1++;
            pos2++;
        }
    }

    public String toString() {
        return "State[\n  pos=" + this.pos + "\n  size=6\n  symbol=" + getSymbol() + "\n  freq=" + getFreq() + "\n  successor=" + getSuccessor() + "\n]";
    }
}
