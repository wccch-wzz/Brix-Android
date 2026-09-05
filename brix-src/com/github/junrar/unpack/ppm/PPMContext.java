package com.github.junrar.unpack.ppm;

import com.github.junrar.io.Raw;
import kotlin.UShort;

/* JADX INFO: loaded from: classes.dex */
public class PPMContext extends Pointer {
    private final FreqData freqData;
    private int numStats;
    private final State oneState;
    private final int[] ps;
    private int suffix;
    private PPMContext tempPPMContext;
    private final State tempState1;
    private final State tempState2;
    private final State tempState3;
    private final State tempState4;
    private final State tempState5;
    private static final int unionSize = Math.max(6, 6);
    public static final int size = (unionSize + 2) + 4;
    public static final int[] ExpEscape = {25, 14, 9, 7, 5, 5, 4, 4, 4, 3, 3, 3, 2, 2, 2, 2};

    public PPMContext(byte[] mem) {
        super(mem);
        this.tempState1 = new State(null);
        this.tempState2 = new State(null);
        this.tempState3 = new State(null);
        this.tempState4 = new State(null);
        this.tempState5 = new State(null);
        this.tempPPMContext = null;
        this.ps = new int[256];
        this.oneState = new State(mem);
        this.freqData = new FreqData(mem);
    }

    public PPMContext init(byte[] mem) {
        this.mem = mem;
        this.pos = 0;
        this.oneState.init(mem);
        this.freqData.init(mem);
        return this;
    }

    public FreqData getFreqData() {
        return this.freqData;
    }

    public void setFreqData(FreqData freqData) {
        this.freqData.setSummFreq(freqData.getSummFreq());
        this.freqData.setStats(freqData.getStats());
    }

    public final int getNumStats() {
        if (this.mem != null) {
            this.numStats = Raw.readShortLittleEndian(this.mem, this.pos) & UShort.MAX_VALUE;
        }
        return this.numStats;
    }

    public final void setNumStats(int numStats) {
        this.numStats = 65535 & numStats;
        if (this.mem != null) {
            Raw.writeShortLittleEndian(this.mem, this.pos, (short) numStats);
        }
    }

    public State getOneState() {
        return this.oneState;
    }

    public void setOneState(StateRef oneState) {
        this.oneState.setValues(oneState);
    }

    public int getSuffix() {
        if (this.mem != null) {
            this.suffix = Raw.readIntLittleEndian(this.mem, this.pos + 8);
        }
        return this.suffix;
    }

    public void setSuffix(PPMContext suffix) {
        setSuffix(suffix.getAddress());
    }

    public void setSuffix(int suffix) {
        this.suffix = suffix;
        if (this.mem != null) {
            Raw.writeIntLittleEndian(this.mem, this.pos + 8, suffix);
        }
    }

    @Override // com.github.junrar.unpack.ppm.Pointer
    public void setAddress(int pos) {
        super.setAddress(pos);
        this.oneState.setAddress(pos + 2);
        this.freqData.setAddress(pos + 2);
    }

    private PPMContext getTempPPMContext(byte[] mem) {
        if (this.tempPPMContext == null) {
            this.tempPPMContext = new PPMContext(null);
        }
        return this.tempPPMContext.init(mem);
    }

    public int createChild(ModelPPM model, State pStats, StateRef firstState) {
        PPMContext pc = getTempPPMContext(model.getSubAlloc().getHeap());
        pc.setAddress(model.getSubAlloc().allocContext());
        if (pc != null) {
            pc.setNumStats(1);
            pc.setOneState(firstState);
            pc.setSuffix(this);
            pStats.setSuccessor(pc);
        }
        return pc.getAddress();
    }

    public void rescale(ModelPPM model) {
        int OldNS = getNumStats();
        int i = getNumStats() - 1;
        State p1 = new State(model.getHeap());
        State p = new State(model.getHeap());
        State temp = new State(model.getHeap());
        p.setAddress(model.getFoundState().getAddress());
        while (p.getAddress() != this.freqData.getStats()) {
            temp.setAddress(p.getAddress() - 6);
            State.ppmdSwap(p, temp);
            p.decAddress();
        }
        temp.setAddress(this.freqData.getStats());
        temp.incFreq(4);
        this.freqData.incSummFreq(4);
        int EscFreq = this.freqData.getSummFreq() - p.getFreq();
        int Adder = model.getOrderFall() != 0 ? 1 : 0;
        p.setFreq((p.getFreq() + Adder) >>> 1);
        this.freqData.setSummFreq(p.getFreq());
        do {
            p.incAddress();
            EscFreq -= p.getFreq();
            p.setFreq((p.getFreq() + Adder) >>> 1);
            this.freqData.incSummFreq(p.getFreq());
            temp.setAddress(p.getAddress() - 6);
            if (p.getFreq() > temp.getFreq()) {
                p1.setAddress(p.getAddress());
                StateRef tmp = new StateRef();
                tmp.setValues(p1);
                State temp2 = new State(model.getHeap());
                State temp3 = new State(model.getHeap());
                do {
                    temp2.setAddress(p1.getAddress() - 6);
                    p1.setValues(temp2);
                    p1.decAddress();
                    temp3.setAddress(p1.getAddress() - 6);
                    if (p1.getAddress() == this.freqData.getStats()) {
                        break;
                    }
                } while (tmp.getFreq() > temp3.getFreq());
                p1.setValues(tmp);
            }
            i--;
        } while (i != 0);
        if (p.getFreq() == 0) {
            do {
                i++;
                p.decAddress();
            } while (p.getFreq() == 0);
            EscFreq += i;
            setNumStats(getNumStats() - i);
            if (getNumStats() == 1) {
                StateRef tmp2 = new StateRef();
                temp.setAddress(this.freqData.getStats());
                tmp2.setValues(temp);
                do {
                    tmp2.decFreq(tmp2.getFreq() >>> 1);
                    EscFreq >>>= 1;
                } while (EscFreq > 1);
                model.getSubAlloc().freeUnits(this.freqData.getStats(), (OldNS + 1) >>> 1);
                this.oneState.setValues(tmp2);
                model.getFoundState().setAddress(this.oneState.getAddress());
                return;
            }
        }
        this.freqData.incSummFreq(EscFreq - (EscFreq >>> 1));
        int n0 = (OldNS + 1) >>> 1;
        int n1 = (getNumStats() + 1) >>> 1;
        if (n0 != n1) {
            this.freqData.setStats(model.getSubAlloc().shrinkUnits(this.freqData.getStats(), n0, n1));
        }
        model.getFoundState().setAddress(this.freqData.getStats());
    }

    private int getArrayIndex(ModelPPM Model, State rs) {
        PPMContext tempSuffix = getTempPPMContext(Model.getSubAlloc().getHeap());
        tempSuffix.setAddress(getSuffix());
        int ret = 0 + Model.getPrevSuccess();
        return ret + Model.getNS2BSIndx()[tempSuffix.getNumStats() - 1] + Model.getHiBitsFlag() + (Model.getHB2Flag()[rs.getSymbol()] * 2) + ((Model.getRunLength() >>> 26) & 32);
    }

    public int getMean(int summ, int shift, int round) {
        return ((1 << (shift - round)) + summ) >>> shift;
    }

    public void decodeBinSymbol(ModelPPM model) {
        State rs = this.tempState1.init(model.getHeap());
        rs.setAddress(this.oneState.getAddress());
        model.setHiBitsFlag(model.getHB2Flag()[model.getFoundState().getSymbol()]);
        int off1 = rs.getFreq() - 1;
        int off2 = getArrayIndex(model, rs);
        int bs = model.getBinSumm()[off1][off2];
        if (model.getCoder().getCurrentShiftCount(14) < bs) {
            model.getFoundState().setAddress(rs.getAddress());
            rs.incFreq(rs.getFreq() < 128 ? 1 : 0);
            model.getCoder().getSubRange().setLowCount(0L);
            model.getCoder().getSubRange().setHighCount(bs);
            model.getBinSumm()[off1][off2] = ((bs + 128) - getMean(bs, 7, 2)) & 65535;
            model.setPrevSuccess(1);
            model.incRunLength(1);
            return;
        }
        model.getCoder().getSubRange().setLowCount(bs);
        int bs2 = (bs - getMean(bs, 7, 2)) & 65535;
        model.getBinSumm()[off1][off2] = bs2;
        model.getCoder().getSubRange().setHighCount(16384L);
        model.setInitEsc(ExpEscape[bs2 >>> 10]);
        model.setNumMasked(1);
        model.getCharMask()[rs.getSymbol()] = model.getEscCount();
        model.setPrevSuccess(0);
        model.getFoundState().setAddress(0);
    }

    public void update1(ModelPPM model, int p) {
        model.getFoundState().setAddress(p);
        model.getFoundState().incFreq(4);
        this.freqData.incSummFreq(4);
        State p0 = this.tempState3.init(model.getHeap());
        State p1 = this.tempState4.init(model.getHeap());
        p0.setAddress(p);
        p1.setAddress(p - 6);
        if (p0.getFreq() > p1.getFreq()) {
            State.ppmdSwap(p0, p1);
            model.getFoundState().setAddress(p1.getAddress());
            if (p1.getFreq() > 124) {
                rescale(model);
            }
        }
    }

    public boolean decodeSymbol2(ModelPPM model) {
        int i = getNumStats() - model.getNumMasked();
        SEE2Context psee2c = makeEscFreq2(model, i);
        RangeCoder coder = model.getCoder();
        State p = this.tempState1.init(model.getHeap());
        State temp = this.tempState2.init(model.getHeap());
        p.setAddress(this.freqData.getStats() - 6);
        int pps = 0;
        int hiCnt = 0;
        while (true) {
            p.incAddress();
            if (model.getCharMask()[p.getSymbol()] != model.getEscCount()) {
                hiCnt += p.getFreq();
                int pps2 = pps + 1;
                this.ps[pps] = p.getAddress();
                i--;
                if (i == 0) {
                    break;
                }
                pps = pps2;
            }
        }
        coder.getSubRange().incScale(hiCnt);
        long count = coder.getCurrentCount();
        if (count >= coder.getSubRange().getScale()) {
            return false;
        }
        int pps3 = 0;
        p.setAddress(this.ps[0]);
        if (count < hiCnt) {
            int hiCnt2 = 0;
            while (true) {
                int freq = p.getFreq() + hiCnt2;
                hiCnt2 = freq;
                if (freq > count) {
                    break;
                }
                pps3++;
                p.setAddress(this.ps[pps3]);
            }
            coder.getSubRange().setHighCount(hiCnt2);
            coder.getSubRange().setLowCount(hiCnt2 - p.getFreq());
            psee2c.update();
            update2(model, p.getAddress());
        } else {
            coder.getSubRange().setLowCount(hiCnt);
            coder.getSubRange().setHighCount(coder.getSubRange().getScale());
            int i2 = getNumStats() - model.getNumMasked();
            int pps4 = 0 - 1;
            do {
                pps4++;
                temp.setAddress(this.ps[pps4]);
                model.getCharMask()[temp.getSymbol()] = model.getEscCount();
                i2--;
            } while (i2 != 0);
            psee2c.incSumm((int) coder.getSubRange().getScale());
            model.setNumMasked(getNumStats());
        }
        return true;
    }

    public void update2(ModelPPM model, int p) {
        State temp = this.tempState5.init(model.getHeap());
        temp.setAddress(p);
        model.getFoundState().setAddress(p);
        model.getFoundState().incFreq(4);
        this.freqData.incSummFreq(4);
        if (temp.getFreq() > 124) {
            rescale(model);
        }
        model.incEscCount(1);
        model.setRunLength(model.getInitRL());
    }

    private SEE2Context makeEscFreq2(ModelPPM model, int Diff) {
        int numStats = getNumStats();
        if (numStats != 256) {
            PPMContext suff = getTempPPMContext(model.getHeap());
            suff.setAddress(getSuffix());
            int idx1 = model.getNS2Indx()[Diff - 1];
            int idx2 = 0 + (Diff < suff.getNumStats() - numStats ? 1 : 0);
            SEE2Context psee2c = model.getSEE2Cont()[idx1][idx2 + ((this.freqData.getSummFreq() < numStats * 11 ? 1 : 0) * 2) + ((model.getNumMasked() > Diff ? 1 : 0) * 4) + model.getHiBitsFlag()];
            model.getCoder().getSubRange().setScale(psee2c.getMean());
            return psee2c;
        }
        SEE2Context psee2c2 = model.getDummySEE2Cont();
        model.getCoder().getSubRange().setScale(1L);
        return psee2c2;
    }

    public boolean decodeSymbol1(ModelPPM model) {
        RangeCoder coder = model.getCoder();
        coder.getSubRange().setScale(this.freqData.getSummFreq());
        State p = new State(model.getHeap());
        p.setAddress(this.freqData.getStats());
        long count = coder.getCurrentCount();
        if (count >= coder.getSubRange().getScale()) {
            return false;
        }
        int freq = p.getFreq();
        int HiCnt = freq;
        if (count < freq) {
            coder.getSubRange().setHighCount(HiCnt);
            model.setPrevSuccess(((long) (HiCnt * 2)) > coder.getSubRange().getScale() ? 1 : 0);
            model.incRunLength(model.getPrevSuccess());
            int HiCnt2 = HiCnt + 4;
            model.getFoundState().setAddress(p.getAddress());
            model.getFoundState().setFreq(HiCnt2);
            this.freqData.incSummFreq(4);
            if (HiCnt2 > 124) {
                rescale(model);
            }
            coder.getSubRange().setLowCount(0L);
            return true;
        }
        if (model.getFoundState().getAddress() == 0) {
            return false;
        }
        model.setPrevSuccess(0);
        int numStats = getNumStats();
        int i = numStats - 1;
        do {
            int freq2 = p.incAddress().getFreq() + HiCnt;
            HiCnt = freq2;
            if (freq2 <= count) {
                i--;
            } else {
                coder.getSubRange().setLowCount(HiCnt - p.getFreq());
                coder.getSubRange().setHighCount(HiCnt);
                update1(model, p.getAddress());
                return true;
            }
        } while (i != 0);
        model.setHiBitsFlag(model.getHB2Flag()[model.getFoundState().getSymbol()]);
        coder.getSubRange().setLowCount(HiCnt);
        model.getCharMask()[p.getSymbol()] = model.getEscCount();
        model.setNumMasked(numStats);
        int i2 = numStats - 1;
        model.getFoundState().setAddress(0);
        do {
            model.getCharMask()[p.decAddress().getSymbol()] = model.getEscCount();
            i2--;
        } while (i2 != 0);
        coder.getSubRange().setHighCount(coder.getSubRange().getScale());
        return true;
    }

    public String toString() {
        return "PPMContext[\n  pos=" + this.pos + "\n  size=" + size + "\n  numStats=" + getNumStats() + "\n  Suffix=" + getSuffix() + "\n  freqData=" + this.freqData + "\n  oneState=" + this.oneState + "\n]";
    }
}
