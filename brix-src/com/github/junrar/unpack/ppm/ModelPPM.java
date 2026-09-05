package com.github.junrar.unpack.ppm;

import com.github.junrar.exception.RarException;
import com.github.junrar.unpack.Unpack;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ModelPPM {
    public static final int BIN_SCALE = 16384;
    public static final int INTERVAL = 128;
    public static final int INT_BITS = 7;
    private static final int[] InitBinEsc = {15581, 7999, 22975, 18675, 25761, 23228, 26162, 24657};
    public static final int MAX_FREQ = 124;
    public static final int MAX_O = 64;
    public static final int PERIOD_BITS = 7;
    public static final int TOT_BITS = 14;
    private SEE2Context dummySEE2Cont;
    private int escCount;
    private State foundState;
    private int hiBitsFlag;
    private int initEsc;
    private int initRL;
    private int maxOrder;
    private int numMasked;
    private int orderFall;
    private int prevSuccess;
    private int runLength;
    private final SEE2Context[][] SEE2Cont = (SEE2Context[][]) Array.newInstance((Class<?>) SEE2Context.class, 25, 16);
    private final int[] charMask = new int[256];
    private final int[] NS2Indx = new int[256];
    private final int[] NS2BSIndx = new int[256];
    private final int[] HB2Flag = new int[256];
    private final int[][] binSumm = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, 128, 64);
    private final RangeCoder coder = new RangeCoder();
    private final SubAllocator subAlloc = new SubAllocator();
    private final State tempState1 = new State(null);
    private final State tempState2 = new State(null);
    private final State tempState3 = new State(null);
    private final State tempState4 = new State(null);
    private final StateRef tempStateRef1 = new StateRef();
    private final StateRef tempStateRef2 = new StateRef();
    private final PPMContext tempPPMContext1 = new PPMContext(null);
    private final PPMContext tempPPMContext2 = new PPMContext(null);
    private final PPMContext tempPPMContext3 = new PPMContext(null);
    private final PPMContext tempPPMContext4 = new PPMContext(null);
    private final int[] ps = new int[64];
    private PPMContext minContext = null;
    private PPMContext maxContext = null;

    public SubAllocator getSubAlloc() {
        return this.subAlloc;
    }

    private void restartModelRare() {
        Arrays.fill(this.charMask, 0);
        this.subAlloc.initSubAllocator();
        this.initRL = (-Math.min(this.maxOrder, 12)) - 1;
        int addr = this.subAlloc.allocContext();
        this.minContext.setAddress(addr);
        this.maxContext.setAddress(addr);
        this.minContext.setSuffix(0);
        this.orderFall = this.maxOrder;
        this.minContext.setNumStats(256);
        this.minContext.getFreqData().setSummFreq(this.minContext.getNumStats() + 1);
        int addr2 = this.subAlloc.allocUnits(128);
        this.foundState.setAddress(addr2);
        this.minContext.getFreqData().setStats(addr2);
        State state = new State(this.subAlloc.getHeap());
        int addr3 = this.minContext.getFreqData().getStats();
        this.runLength = this.initRL;
        this.prevSuccess = 0;
        for (int i = 0; i < 256; i++) {
            state.setAddress((i * 6) + addr3);
            state.setSymbol(i);
            state.setFreq(1);
            state.setSuccessor(0);
        }
        for (int i2 = 0; i2 < 128; i2++) {
            for (int k = 0; k < 8; k++) {
                for (int m = 0; m < 64; m += 8) {
                    this.binSumm[i2][k + m] = 16384 - (InitBinEsc[k] / (i2 + 2));
                }
            }
        }
        for (int i3 = 0; i3 < 25; i3++) {
            for (int k2 = 0; k2 < 16; k2++) {
                this.SEE2Cont[i3][k2].init((i3 * 5) + 10);
            }
        }
    }

    private void startModelRare(int MaxOrder) {
        this.escCount = 1;
        this.maxOrder = MaxOrder;
        restartModelRare();
        this.NS2BSIndx[0] = 0;
        this.NS2BSIndx[1] = 2;
        for (int j = 0; j < 9; j++) {
            this.NS2BSIndx[j + 2] = 4;
        }
        for (int j2 = 0; j2 < 245; j2++) {
            this.NS2BSIndx[j2 + 11] = 6;
        }
        int i = 0;
        while (i < 3) {
            this.NS2Indx[i] = i;
            i++;
        }
        int m = i;
        int k = 1;
        int Step = 1;
        while (i < 256) {
            this.NS2Indx[i] = m;
            k--;
            if (k == 0) {
                Step++;
                k = Step;
                m++;
            }
            i++;
        }
        for (int j3 = 0; j3 < 64; j3++) {
            this.HB2Flag[j3] = 0;
        }
        for (int j4 = 0; j4 < 192; j4++) {
            this.HB2Flag[j4 + 64] = 8;
        }
        this.dummySEE2Cont.setShift(7);
    }

    private void clearMask() {
        this.escCount = 1;
        Arrays.fill(this.charMask, 0);
    }

    public boolean decodeInit(Unpack unpackRead, int escChar) throws RarException, IOException {
        int MaxOrder = unpackRead.getChar() & 255;
        boolean reset = (MaxOrder & 32) != 0;
        int MaxMB = 0;
        if (reset) {
            MaxMB = unpackRead.getChar();
            if (MaxMB > 1) {
                MaxMB = 1;
            }
        } else if (this.subAlloc.GetAllocatedMemory() == 0) {
            return false;
        }
        if ((MaxOrder & 64) != 0) {
            int escChar2 = unpackRead.getChar();
            unpackRead.setPpmEscChar(escChar2);
        }
        this.coder.initDecoder(unpackRead);
        if (reset) {
            int MaxOrder2 = (MaxOrder & 31) + 1;
            if (MaxOrder2 > 16) {
                MaxOrder2 = ((MaxOrder2 - 16) * 3) + 16;
            }
            if (MaxOrder2 == 1) {
                this.subAlloc.stopSubAllocator();
                return false;
            }
            this.subAlloc.startSubAllocator(MaxMB + 1);
            this.minContext = new PPMContext(getHeap());
            this.maxContext = new PPMContext(getHeap());
            this.foundState = new State(getHeap());
            this.dummySEE2Cont = new SEE2Context();
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 16; j++) {
                    this.SEE2Cont[i][j] = new SEE2Context();
                }
            }
            startModelRare(MaxOrder2);
        }
        return this.minContext.getAddress() != 0;
    }

    public int decodeChar() throws RarException, IOException {
        if (this.minContext.getAddress() <= this.subAlloc.getPText() || this.minContext.getAddress() > this.subAlloc.getHeapEnd()) {
            return -1;
        }
        if (this.minContext.getNumStats() != 1) {
            if (this.minContext.getFreqData().getStats() <= this.subAlloc.getPText() || this.minContext.getFreqData().getStats() > this.subAlloc.getHeapEnd() || !this.minContext.decodeSymbol1(this)) {
                return -1;
            }
        } else {
            this.minContext.decodeBinSymbol(this);
        }
        this.coder.decode();
        while (this.foundState.getAddress() == 0) {
            this.coder.ariDecNormalize();
            do {
                this.orderFall++;
                this.minContext.setAddress(this.minContext.getSuffix());
                if (this.minContext.getAddress() <= this.subAlloc.getPText() || this.minContext.getAddress() > this.subAlloc.getHeapEnd()) {
                    return -1;
                }
            } while (this.minContext.getNumStats() == this.numMasked);
            if (!this.minContext.decodeSymbol2(this)) {
                return -1;
            }
            this.coder.decode();
        }
        int Symbol = this.foundState.getSymbol();
        if (this.orderFall == 0 && this.foundState.getSuccessor() > this.subAlloc.getPText()) {
            int addr = this.foundState.getSuccessor();
            this.minContext.setAddress(addr);
            this.maxContext.setAddress(addr);
        } else {
            updateModel();
            if (this.escCount == 0) {
                clearMask();
            }
        }
        this.coder.ariDecNormalize();
        return Symbol;
    }

    public SEE2Context[][] getSEE2Cont() {
        return this.SEE2Cont;
    }

    public SEE2Context getDummySEE2Cont() {
        return this.dummySEE2Cont;
    }

    public int getInitRL() {
        return this.initRL;
    }

    public void setEscCount(int escCount) {
        this.escCount = escCount & 255;
    }

    public int getEscCount() {
        return this.escCount;
    }

    public void incEscCount(int dEscCount) {
        setEscCount(getEscCount() + dEscCount);
    }

    public int[] getCharMask() {
        return this.charMask;
    }

    public int getNumMasked() {
        return this.numMasked;
    }

    public void setNumMasked(int numMasked) {
        this.numMasked = numMasked;
    }

    public void setPrevSuccess(int prevSuccess) {
        this.prevSuccess = prevSuccess & 255;
    }

    public int getInitEsc() {
        return this.initEsc;
    }

    public void setInitEsc(int initEsc) {
        this.initEsc = initEsc;
    }

    public void setRunLength(int runLength) {
        this.runLength = runLength;
    }

    public int getRunLength() {
        return this.runLength;
    }

    public void incRunLength(int dRunLength) {
        setRunLength(getRunLength() + dRunLength);
    }

    public int getPrevSuccess() {
        return this.prevSuccess;
    }

    public int getHiBitsFlag() {
        return this.hiBitsFlag;
    }

    public void setHiBitsFlag(int hiBitsFlag) {
        this.hiBitsFlag = hiBitsFlag & 255;
    }

    public int[][] getBinSumm() {
        return this.binSumm;
    }

    public RangeCoder getCoder() {
        return this.coder;
    }

    public int[] getHB2Flag() {
        return this.HB2Flag;
    }

    public int[] getNS2BSIndx() {
        return this.NS2BSIndx;
    }

    public int[] getNS2Indx() {
        return this.NS2Indx;
    }

    public State getFoundState() {
        return this.foundState;
    }

    public byte[] getHeap() {
        return this.subAlloc.getHeap();
    }

    public int getOrderFall() {
        return this.orderFall;
    }

    private int createSuccessors(boolean Skip, State p1) {
        int i;
        StateRef upState = this.tempStateRef2;
        State tempState = this.tempState1.init(getHeap());
        PPMContext pc = this.tempPPMContext1.init(getHeap());
        pc.setAddress(this.minContext.getAddress());
        PPMContext upBranch = this.tempPPMContext2.init(getHeap());
        upBranch.setAddress(this.foundState.getSuccessor());
        State p = this.tempState2.init(getHeap());
        int pps = 0;
        boolean noLoop = false;
        if (!Skip) {
            int pps2 = 0 + 1;
            this.ps[0] = this.foundState.getAddress();
            if (pc.getSuffix() != 0) {
                pps = pps2;
            } else {
                noLoop = true;
                pps = pps2;
            }
        }
        if (!noLoop) {
            boolean loopEntry = false;
            if (p1.getAddress() != 0) {
                p.setAddress(p1.getAddress());
                pc.setAddress(pc.getSuffix());
                loopEntry = true;
            }
            while (true) {
                if (!loopEntry) {
                    pc.setAddress(pc.getSuffix());
                    if (pc.getNumStats() != 1) {
                        p.setAddress(pc.getFreqData().getStats());
                        if (p.getSymbol() != this.foundState.getSymbol()) {
                            do {
                                p.incAddress();
                            } while (p.getSymbol() != this.foundState.getSymbol());
                        }
                    } else {
                        p.setAddress(pc.getOneState().getAddress());
                    }
                }
                loopEntry = false;
                if (p.getSuccessor() != upBranch.getAddress()) {
                    pc.setAddress(p.getSuccessor());
                    break;
                }
                int pps3 = pps + 1;
                this.ps[pps] = p.getAddress();
                if (pc.getSuffix() == 0) {
                    pps = pps3;
                    break;
                }
                pps = pps3;
            }
        }
        if (pps == 0) {
            return pc.getAddress();
        }
        upState.setSymbol(getHeap()[upBranch.getAddress()]);
        upState.setSuccessor(upBranch.getAddress() + 1);
        if (pc.getNumStats() != 1) {
            if (pc.getAddress() <= this.subAlloc.getPText()) {
                return 0;
            }
            p.setAddress(pc.getFreqData().getStats());
            if (p.getSymbol() != upState.getSymbol()) {
                do {
                    p.incAddress();
                } while (p.getSymbol() != upState.getSymbol());
            }
            int cf = p.getFreq() - 1;
            int s0 = (pc.getFreqData().getSummFreq() - pc.getNumStats()) - cf;
            if (cf * 2 <= s0) {
                i = cf * 5 > s0 ? 1 : 0;
            } else {
                i = (((cf * 2) + (s0 * 3)) - 1) / (s0 * 2);
            }
            upState.setFreq(1 + i);
        } else {
            upState.setFreq(pc.getOneState().getFreq());
        }
        do {
            pps--;
            tempState.setAddress(this.ps[pps]);
            pc.setAddress(pc.createChild(this, tempState, upState));
            if (pc.getAddress() == 0) {
                return 0;
            }
        } while (pps != 0);
        return pc.getAddress();
    }

    private void updateModelRestart() {
        restartModelRare();
        this.escCount = 0;
    }

    private void updateModel() {
        int cf;
        StateRef fs = this.tempStateRef1;
        fs.setValues(this.foundState);
        State p = this.tempState3.init(getHeap());
        State tempState = this.tempState4.init(getHeap());
        PPMContext pc = this.tempPPMContext3.init(getHeap());
        PPMContext successor = this.tempPPMContext4.init(getHeap());
        pc.setAddress(this.minContext.getSuffix());
        if (fs.getFreq() < 31 && pc.getAddress() != 0) {
            if (pc.getNumStats() != 1) {
                p.setAddress(pc.getFreqData().getStats());
                if (p.getSymbol() != fs.getSymbol()) {
                    do {
                        p.incAddress();
                    } while (p.getSymbol() != fs.getSymbol());
                    tempState.setAddress(p.getAddress() - 6);
                    if (p.getFreq() >= tempState.getFreq()) {
                        State.ppmdSwap(p, tempState);
                        p.decAddress();
                    }
                }
                if (p.getFreq() < 115) {
                    p.incFreq(2);
                    pc.getFreqData().incSummFreq(2);
                }
            } else {
                p.setAddress(pc.getOneState().getAddress());
                if (p.getFreq() < 32) {
                    p.incFreq(1);
                }
            }
        }
        if (this.orderFall == 0) {
            this.foundState.setSuccessor(createSuccessors(true, p));
            this.minContext.setAddress(this.foundState.getSuccessor());
            this.maxContext.setAddress(this.foundState.getSuccessor());
            if (this.minContext.getAddress() == 0) {
                updateModelRestart();
                return;
            }
            return;
        }
        this.subAlloc.getHeap()[this.subAlloc.getPText()] = (byte) fs.getSymbol();
        this.subAlloc.incPText();
        successor.setAddress(this.subAlloc.getPText());
        if (this.subAlloc.getPText() >= this.subAlloc.getFakeUnitsStart()) {
            updateModelRestart();
            return;
        }
        if (fs.getSuccessor() != 0) {
            if (fs.getSuccessor() <= this.subAlloc.getPText()) {
                fs.setSuccessor(createSuccessors(false, p));
                if (fs.getSuccessor() == 0) {
                    updateModelRestart();
                    return;
                }
            }
            int i = this.orderFall - 1;
            this.orderFall = i;
            if (i == 0) {
                successor.setAddress(fs.getSuccessor());
                if (this.maxContext.getAddress() != this.minContext.getAddress()) {
                    this.subAlloc.decPText(1);
                }
            }
        } else {
            this.foundState.setSuccessor(successor.getAddress());
            fs.setSuccessor(this.minContext);
        }
        int ns = this.minContext.getNumStats();
        int s0 = (this.minContext.getFreqData().getSummFreq() - ns) - (fs.getFreq() - 1);
        pc.setAddress(this.maxContext.getAddress());
        while (pc.getAddress() != this.minContext.getAddress()) {
            int ns1 = pc.getNumStats();
            if (ns1 != 1) {
                if ((ns1 & 1) == 0) {
                    pc.getFreqData().setStats(this.subAlloc.expandUnits(pc.getFreqData().getStats(), ns1 >>> 1));
                    if (pc.getFreqData().getStats() == 0) {
                        updateModelRestart();
                        return;
                    }
                }
                int sum = (ns1 * 2 < ns ? 1 : 0) + (((ns1 * 4 <= ns ? 1 : 0) & (pc.getFreqData().getSummFreq() <= ns1 * 8 ? 1 : 0)) * 2);
                pc.getFreqData().incSummFreq(sum);
            } else {
                p.setAddress(this.subAlloc.allocUnits(1));
                if (p.getAddress() == 0) {
                    updateModelRestart();
                    return;
                }
                p.setValues(pc.getOneState());
                pc.getFreqData().setStats(p);
                if (p.getFreq() < 30) {
                    p.incFreq(p.getFreq());
                } else {
                    p.setFreq(120);
                }
                pc.getFreqData().setSummFreq(p.getFreq() + this.initEsc + (ns > 3 ? 1 : 0));
            }
            int cf2 = fs.getFreq() * 2 * (pc.getFreqData().getSummFreq() + 6);
            int sf = pc.getFreqData().getSummFreq() + s0;
            if (cf2 < sf * 6) {
                cf = (cf2 > sf ? 1 : 0) + 1 + (cf2 >= sf * 4 ? 1 : 0);
                pc.getFreqData().incSummFreq(3);
            } else {
                cf = (cf2 >= sf * 15 ? 1 : 0) + (cf2 >= sf * 9 ? 1 : 0) + 4 + (cf2 >= sf * 12 ? 1 : 0);
                pc.getFreqData().incSummFreq(cf);
            }
            p.setAddress(pc.getFreqData().getStats() + (ns1 * 6));
            p.setSuccessor(successor);
            p.setSymbol(fs.getSymbol());
            p.setFreq(cf);
            pc.setNumStats(ns1 + 1);
            pc.setAddress(pc.getSuffix());
        }
        int address = fs.getSuccessor();
        this.maxContext.setAddress(address);
        this.minContext.setAddress(address);
    }

    public String toString() {
        return "ModelPPM[\n  numMasked=" + this.numMasked + "\n  initEsc=" + this.initEsc + "\n  orderFall=" + this.orderFall + "\n  maxOrder=" + this.maxOrder + "\n  runLength=" + this.runLength + "\n  initRL=" + this.initRL + "\n  escCount=" + this.escCount + "\n  prevSuccess=" + this.prevSuccess + "\n  foundState=" + this.foundState + "\n  coder=" + this.coder + "\n  subAlloc=" + this.subAlloc + "\n]";
    }
}
