package com.github.junrar.unpack.ppm;

/* JADX INFO: loaded from: classes.dex */
public class StateRef {
    private int freq;
    private int successor;
    private int symbol;

    public int getSymbol() {
        return this.symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol & 255;
    }

    public int getFreq() {
        return this.freq;
    }

    public void setFreq(int freq) {
        this.freq = freq & 255;
    }

    public void incFreq(int dFreq) {
        this.freq = (this.freq + dFreq) & 255;
    }

    public void decFreq(int dFreq) {
        this.freq = (this.freq - dFreq) & 255;
    }

    public void setValues(State statePtr) {
        setFreq(statePtr.getFreq());
        setSuccessor(statePtr.getSuccessor());
        setSymbol(statePtr.getSymbol());
    }

    public int getSuccessor() {
        return this.successor;
    }

    public void setSuccessor(PPMContext successor) {
        setSuccessor(successor.getAddress());
    }

    public void setSuccessor(int successor) {
        this.successor = successor;
    }

    public String toString() {
        return "State[\n  symbol=" + getSymbol() + "\n  freq=" + getFreq() + "\n  successor=" + getSuccessor() + "\n]";
    }
}
