package com.github.junrar.unpack.ppm;

import com.github.junrar.exception.RarException;
import com.github.junrar.unpack.Unpack;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class RangeCoder {
    public static final int BOT = 32768;
    public static final int TOP = 16777216;
    private static final long uintMask = 4294967295L;
    private long code;
    private long low;
    private long range;
    private final SubRange subRange = new SubRange();
    private Unpack unpackRead;

    public SubRange getSubRange() {
        return this.subRange;
    }

    public void initDecoder(Unpack unpackRead) throws RarException, IOException {
        this.unpackRead = unpackRead;
        this.code = 0L;
        this.low = 0L;
        this.range = uintMask;
        for (int i = 0; i < 4; i++) {
            this.code = ((this.code << 8) | ((long) getChar())) & uintMask;
        }
    }

    public int getCurrentCount() {
        this.range = (this.range / this.subRange.getScale()) & uintMask;
        return (int) ((this.code - this.low) / this.range);
    }

    public long getCurrentShiftCount(int SHIFT) {
        this.range >>>= SHIFT;
        return ((this.code - this.low) / this.range) & uintMask;
    }

    public void decode() {
        this.low = (this.low + (this.range * this.subRange.getLowCount())) & uintMask;
        this.range = (this.range * (this.subRange.getHighCount() - this.subRange.getLowCount())) & uintMask;
    }

    private int getChar() throws RarException, IOException {
        return this.unpackRead.getChar();
    }

    public void ariDecNormalize() throws RarException, IOException {
        boolean c2 = false;
        while (true) {
            if ((this.low ^ (this.low + this.range)) >= 16777216) {
                boolean z = this.range < 32768;
                c2 = z;
                if (!z) {
                    return;
                }
            }
            if (c2) {
                this.range = (-this.low) & 32767 & uintMask;
                c2 = false;
            }
            this.code = ((this.code << 8) | ((long) getChar())) & uintMask;
            this.range = (this.range << 8) & uintMask;
            this.low = uintMask & (this.low << 8);
        }
    }

    public String toString() {
        return "RangeCoder[\n  low=" + this.low + "\n  code=" + this.code + "\n  range=" + this.range + "\n  subrange=" + this.subRange + "]";
    }

    public static class SubRange {
        private long highCount;
        private long lowCount;
        private long scale;

        public long getHighCount() {
            return this.highCount;
        }

        public void setHighCount(long highCount) {
            this.highCount = RangeCoder.uintMask & highCount;
        }

        public long getLowCount() {
            return this.lowCount & RangeCoder.uintMask;
        }

        public void setLowCount(long lowCount) {
            this.lowCount = RangeCoder.uintMask & lowCount;
        }

        public long getScale() {
            return this.scale;
        }

        public void setScale(long scale) {
            this.scale = RangeCoder.uintMask & scale;
        }

        public void incScale(int dScale) {
            setScale(getScale() + ((long) dScale));
        }

        public String toString() {
            return "SubRange[\n  lowCount=" + this.lowCount + "\n  highCount=" + this.highCount + "\n  scale=" + this.scale + "]";
        }
    }
}
