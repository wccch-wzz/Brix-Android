package com.github.junrar.unpack;

import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;
import com.github.junrar.exception.RarException;
import com.github.junrar.unpack.decode.Compress;
import com.github.junrar.unpack.vm.BitInput;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.glavo.chardet.prober.HebrewProber;

/* JADX INFO: loaded from: classes.dex */
public abstract class Unpack15 extends BitInput {
    private static final int STARTHF0 = 4;
    private static final int STARTHF1 = 5;
    private static final int STARTHF2 = 5;
    private static final int STARTHF3 = 6;
    private static final int STARTHF4 = 8;
    private static final int STARTL1 = 2;
    private static final int STARTL2 = 3;
    protected int AvrLn1;
    protected int AvrLn2;
    protected int AvrLn3;
    protected int AvrPlc;
    protected int AvrPlcB;
    protected int Buf60;
    protected int FlagBuf;
    protected int FlagsCnt;
    protected int LCount;
    protected int MaxDist3;
    protected int Nhfb;
    protected int Nlzb;
    protected int NumHuf;
    protected int StMode;
    protected long destUnpSize;
    protected int lastDist;
    protected int lastLength;
    protected int oldDistPtr;
    protected int readBorder;
    protected int readTop;
    protected boolean suspended;
    protected boolean unpAllBuf;
    protected ComprDataIO unpIO;
    protected int unpPtr;
    protected boolean unpSomeRead;
    protected byte[] window;
    protected int wrPtr;
    private static final int[] DecL1 = {32768, 40960, CpioConstants.C_ISSOCK, 53248, 57344, 59904, 60928, 61440, 61952, 61952, 65535};
    private static final int[] PosL1 = {0, 0, 0, 2, 3, 5, 7, 11, 16, 20, 24, 32, 32};
    private static final int[] DecL2 = {40960, CpioConstants.C_ISSOCK, 53248, 57344, 59904, 60928, 61440, 61952, 62016, 65535};
    private static final int[] PosL2 = {0, 0, 0, 0, 5, 7, 9, 13, 18, 22, 26, 34, 36};
    private static final int[] DecHf0 = {32768, CpioConstants.C_ISSOCK, 57344, 61952, 61952, 61952, 61952, 61952, 65535};
    private static final int[] PosHf0 = {0, 0, 0, 0, 0, 8, 16, 24, 33, 33, 33, 33, 33};
    private static final int[] DecHf1 = {8192, CpioConstants.C_ISSOCK, 57344, 61440, 61952, 61952, 63456, 65535};
    private static final int[] PosHf1 = {0, 0, 0, 0, 0, 0, 4, 44, 60, 76, 80, 80, 127};
    private static final int[] DecHf2 = {4096, 9216, 32768, CpioConstants.C_ISSOCK, 64000, 65535, 65535, 65535};
    private static final int[] PosHf2 = {0, 0, 0, 0, 0, 0, 2, 7, 53, 117, 233, 0, 0};
    private static final int[] DecHf3 = {2048, 9216, 60928, 65152, 65535, 65535, 65535};
    private static final int[] PosHf3 = {0, 0, 0, 0, 0, 0, 0, 2, 16, 218, 251, 0, 0};
    private static final int[] DecHf4 = {MotionEventCompat.ACTION_POINTER_INDEX_MASK, 65535, 65535, 65535, 65535, 65535};
    private static final int[] PosHf4 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 0, 0, 0};
    static int[] ShortLen1 = {1, 3, 4, 4, 5, 6, 7, 8, 8, 4, 4, 5, 6, 6, 4, 0};
    static int[] ShortXor1 = {0, 160, 208, 224, HebrewProber.NORMAL_NUN, 248, 252, 254, 255, 192, 128, 144, 152, TarConstants.LF_OFFSET, 176};
    static int[] ShortLen2 = {2, 3, 3, 3, 4, 4, 5, 6, 6, 4, 4, 5, 6, 6, 4, 0};
    static int[] ShortXor2 = {0, 64, 96, 160, 208, 224, HebrewProber.NORMAL_NUN, 248, 252, 192, 128, 144, 152, TarConstants.LF_OFFSET, 176};
    protected int[] oldDist = new int[4];
    protected int[] ChSet = new int[256];
    protected int[] ChSetA = new int[256];
    protected int[] ChSetB = new int[256];
    protected int[] ChSetC = new int[256];
    protected int[] Place = new int[256];
    protected int[] PlaceA = new int[256];
    protected int[] PlaceB = new int[256];
    protected int[] PlaceC = new int[256];
    protected int[] NToPl = new int[256];
    protected int[] NToPlB = new int[256];
    protected int[] NToPlC = new int[256];

    protected abstract void unpInitData(boolean z);

    protected void unpack15(boolean solid) throws RarException, IOException {
        if (this.suspended) {
            this.unpPtr = this.wrPtr;
        } else {
            unpInitData(solid);
            oldUnpInitData(solid);
            unpReadBuf();
            if (!solid) {
                initHuff();
                this.unpPtr = 0;
            } else {
                this.unpPtr = this.wrPtr;
            }
            this.destUnpSize--;
        }
        if (this.destUnpSize >= 0) {
            getFlagsBuf();
            this.FlagsCnt = 8;
        }
        while (this.destUnpSize >= 0) {
            this.unpPtr &= Compress.MAXWINMASK;
            if (this.inAddr > this.readTop - 30 && !unpReadBuf()) {
                break;
            }
            if (((this.wrPtr - this.unpPtr) & Compress.MAXWINMASK) < 270 && this.wrPtr != this.unpPtr) {
                oldUnpWriteBuf();
                if (this.suspended) {
                    return;
                }
            }
            if (this.StMode != 0) {
                huffDecode();
            } else {
                int i = this.FlagsCnt - 1;
                this.FlagsCnt = i;
                if (i < 0) {
                    getFlagsBuf();
                    this.FlagsCnt = 7;
                }
                if ((this.FlagBuf & 128) != 0) {
                    this.FlagBuf <<= 1;
                    if (this.Nlzb > this.Nhfb) {
                        longLZ();
                    } else {
                        huffDecode();
                    }
                } else {
                    this.FlagBuf <<= 1;
                    int i2 = this.FlagsCnt - 1;
                    this.FlagsCnt = i2;
                    if (i2 < 0) {
                        getFlagsBuf();
                        this.FlagsCnt = 7;
                    }
                    if ((this.FlagBuf & 128) != 0) {
                        this.FlagBuf <<= 1;
                        if (this.Nlzb > this.Nhfb) {
                            huffDecode();
                        } else {
                            longLZ();
                        }
                    } else {
                        this.FlagBuf <<= 1;
                        shortLZ();
                    }
                }
            }
        }
        oldUnpWriteBuf();
    }

    protected boolean unpReadBuf() throws RarException, IOException {
        int dataSize = this.readTop - this.inAddr;
        if (dataSize < 0) {
            return false;
        }
        if (this.inAddr > 16384) {
            if (dataSize > 0) {
                System.arraycopy(this.inBuf, this.inAddr, this.inBuf, 0, dataSize);
            }
            this.inAddr = 0;
            this.readTop = dataSize;
        } else {
            dataSize = this.readTop;
        }
        int readCode = this.unpIO.unpRead(this.inBuf, dataSize, (32768 - dataSize) & (-16));
        if (readCode > 0) {
            this.readTop += readCode;
        }
        this.readBorder = this.readTop - 30;
        return readCode != -1;
    }

    private int getShortLen1(int pos) {
        return pos == 1 ? this.Buf60 + 3 : ShortLen1[pos];
    }

    private int getShortLen2(int pos) {
        return pos == 3 ? this.Buf60 + 3 : ShortLen2[pos];
    }

    protected void shortLZ() {
        int Length;
        this.NumHuf = 0;
        int BitField = fgetbits();
        if (this.LCount == 2) {
            faddbits(1);
            if (BitField >= 32768) {
                oldCopyString(this.lastDist, this.lastLength);
                return;
            } else {
                BitField <<= 1;
                this.LCount = 0;
            }
        }
        int BitField2 = BitField >>> 8;
        if (this.AvrLn1 < 37) {
            Length = 0;
            while (((ShortXor1[Length] ^ BitField2) & (~(255 >>> getShortLen1(Length)))) != 0) {
                Length++;
            }
            faddbits(getShortLen1(Length));
        } else {
            Length = 0;
            while (((ShortXor2[Length] ^ BitField2) & (~(255 >>> getShortLen2(Length)))) != 0) {
                Length++;
            }
            faddbits(getShortLen2(Length));
        }
        if (Length >= 9) {
            if (Length == 9) {
                this.LCount++;
                oldCopyString(this.lastDist, this.lastLength);
                return;
            }
            if (Length == 14) {
                this.LCount = 0;
                int Length2 = decodeNum(fgetbits(), 3, DecL2, PosL2) + 5;
                int Length3 = fgetbits();
                int Distance = (Length3 >>> 1) | 32768;
                faddbits(15);
                this.lastLength = Length2;
                this.lastDist = Distance;
                oldCopyString(Distance, Length2);
                return;
            }
            this.LCount = 0;
            int SaveLength = Length;
            int Distance2 = this.oldDist[(this.oldDistPtr - (Length - 9)) & 3];
            int Length4 = decodeNum(fgetbits(), 2, DecL1, PosL1) + 2;
            if (Length4 == 257 && SaveLength == 10) {
                this.Buf60 ^= 1;
                return;
            }
            if (Distance2 > 256) {
                Length4++;
            }
            if (Distance2 >= this.MaxDist3) {
                Length4++;
            }
            int[] iArr = this.oldDist;
            int i = this.oldDistPtr;
            this.oldDistPtr = i + 1;
            iArr[i] = Distance2;
            this.oldDistPtr &= 3;
            this.lastLength = Length4;
            this.lastDist = Distance2;
            oldCopyString(Distance2, Length4);
            return;
        }
        this.LCount = 0;
        this.AvrLn1 += Length;
        this.AvrLn1 -= this.AvrLn1 >>> 4;
        int DistancePlace = decodeNum(fgetbits(), 5, DecHf2, PosHf2) & 255;
        int Distance3 = this.ChSetA[DistancePlace];
        int DistancePlace2 = DistancePlace - 1;
        if (DistancePlace2 != -1) {
            int[] iArr2 = this.PlaceA;
            iArr2[Distance3] = iArr2[Distance3] - 1;
            int LastDistance = this.ChSetA[DistancePlace2];
            int[] iArr3 = this.PlaceA;
            iArr3[LastDistance] = iArr3[LastDistance] + 1;
            this.ChSetA[DistancePlace2 + 1] = LastDistance;
            this.ChSetA[DistancePlace2] = Distance3;
        }
        int Length5 = Length + 2;
        int[] iArr4 = this.oldDist;
        int i2 = this.oldDistPtr;
        this.oldDistPtr = i2 + 1;
        int Distance4 = Distance3 + 1;
        iArr4[i2] = Distance4;
        this.oldDistPtr &= 3;
        this.lastLength = Length5;
        this.lastDist = Distance4;
        oldCopyString(Distance4, Length5);
    }

    protected void longLZ() {
        int Length;
        int DistancePlace;
        int Distance;
        int NewDistancePlace;
        this.NumHuf = 0;
        this.Nlzb += 16;
        if (this.Nlzb > 255) {
            this.Nlzb = 144;
            this.Nhfb >>>= 1;
        }
        int OldAvr2 = this.AvrLn2;
        int BitField = fgetbits();
        if (this.AvrLn2 >= 122) {
            Length = decodeNum(BitField, 3, DecL2, PosL2);
        } else if (this.AvrLn2 >= 64) {
            Length = decodeNum(BitField, 2, DecL1, PosL1);
        } else if (BitField < 256) {
            faddbits(16);
            Length = BitField;
        } else {
            Length = 0;
            while (((BitField << Length) & 32768) == 0) {
                Length++;
            }
            faddbits(Length + 1);
        }
        this.AvrLn2 += Length;
        this.AvrLn2 -= this.AvrLn2 >>> 5;
        int BitField2 = fgetbits();
        if (this.AvrPlcB > 10495) {
            DistancePlace = decodeNum(BitField2, 5, DecHf2, PosHf2);
        } else {
            int DistancePlace2 = this.AvrPlcB;
            if (DistancePlace2 > 1791) {
                DistancePlace = decodeNum(BitField2, 5, DecHf1, PosHf1);
            } else {
                DistancePlace = decodeNum(BitField2, 4, DecHf0, PosHf0);
            }
        }
        this.AvrPlcB += DistancePlace;
        this.AvrPlcB -= this.AvrPlcB >>> 8;
        while (true) {
            int Distance2 = this.ChSetB[DistancePlace & 255];
            int[] iArr = this.NToPlB;
            Distance = Distance2 + 1;
            int Distance3 = Distance2 & 255;
            NewDistancePlace = iArr[Distance3];
            iArr[Distance3] = NewDistancePlace + 1;
            if ((Distance & 255) != 0) {
                break;
            } else {
                corrHuff(this.ChSetB, this.NToPlB);
            }
        }
        this.ChSetB[DistancePlace] = this.ChSetB[NewDistancePlace];
        this.ChSetB[NewDistancePlace] = Distance;
        int Distance4 = ((65280 & Distance) | (fgetbits() >>> 8)) >>> 1;
        faddbits(7);
        int OldAvr3 = this.AvrLn3;
        if (Length != 1 && Length != 4) {
            if (Length == 0 && Distance4 <= this.MaxDist3) {
                this.AvrLn3++;
                this.AvrLn3 -= this.AvrLn3 >>> 8;
            } else if (this.AvrLn3 > 0) {
                this.AvrLn3--;
            }
        }
        int Length2 = Length + 3;
        if (Distance4 >= this.MaxDist3) {
            Length2++;
        }
        if (Distance4 <= 256) {
            Length2 += 8;
        }
        if (OldAvr3 > 176 || (this.AvrPlc >= 10752 && OldAvr2 < 64)) {
            this.MaxDist3 = 32512;
        } else {
            this.MaxDist3 = 8193;
        }
        int[] iArr2 = this.oldDist;
        int i = this.oldDistPtr;
        this.oldDistPtr = i + 1;
        iArr2[i] = Distance4;
        this.oldDistPtr &= 3;
        this.lastLength = Length2;
        this.lastDist = Distance4;
        oldCopyString(Distance4, Length2);
    }

    protected void huffDecode() {
        int BytePlace;
        int BitField = fgetbits();
        if (this.AvrPlc > 30207) {
            BytePlace = decodeNum(BitField, 8, DecHf4, PosHf4);
        } else {
            int BytePlace2 = this.AvrPlc;
            if (BytePlace2 > 24063) {
                BytePlace = decodeNum(BitField, 6, DecHf3, PosHf3);
            } else {
                int BytePlace3 = this.AvrPlc;
                if (BytePlace3 > 13823) {
                    BytePlace = decodeNum(BitField, 5, DecHf2, PosHf2);
                } else {
                    int BytePlace4 = this.AvrPlc;
                    if (BytePlace4 > 3583) {
                        BytePlace = decodeNum(BitField, 5, DecHf1, PosHf1);
                    } else {
                        BytePlace = decodeNum(BitField, 4, DecHf0, PosHf0);
                    }
                }
            }
        }
        int BytePlace5 = BytePlace & 255;
        if (this.StMode != 0) {
            if (BytePlace5 == 0 && BitField > 4095) {
                BytePlace5 = 256;
            }
            BytePlace5--;
            if (BytePlace5 == -1) {
                int BitField2 = fgetbits();
                faddbits(1);
                if ((32768 & BitField2) != 0) {
                    this.StMode = 0;
                    this.NumHuf = 0;
                    return;
                }
                int Length = (BitField2 & 16384) == 0 ? 3 : 4;
                faddbits(1);
                int Distance = decodeNum(fgetbits(), 5, DecHf2, PosHf2);
                int Distance2 = (Distance << 5) | (fgetbits() >>> 11);
                faddbits(5);
                oldCopyString(Distance2, Length);
                return;
            }
        } else {
            int Length2 = this.NumHuf;
            this.NumHuf = Length2 + 1;
            if (Length2 >= 16 && this.FlagsCnt == 0) {
                this.StMode = 1;
            }
        }
        this.AvrPlc += BytePlace5;
        this.AvrPlc -= this.AvrPlc >>> 8;
        this.Nhfb += 16;
        if (this.Nhfb > 255) {
            this.Nhfb = 144;
            this.Nlzb >>>= 1;
        }
        byte[] bArr = this.window;
        int i = this.unpPtr;
        this.unpPtr = i + 1;
        bArr[i] = (byte) (this.ChSet[BytePlace5] >>> 8);
        this.destUnpSize--;
        while (true) {
            int CurByte = this.ChSet[BytePlace5];
            int[] iArr = this.NToPl;
            int CurByte2 = CurByte + 1;
            int CurByte3 = CurByte & 255;
            int NewBytePlace = iArr[CurByte3];
            iArr[CurByte3] = NewBytePlace + 1;
            if ((CurByte2 & 255) > 161) {
                corrHuff(this.ChSet, this.NToPl);
            } else {
                this.ChSet[BytePlace5] = this.ChSet[NewBytePlace];
                this.ChSet[NewBytePlace] = CurByte2;
                return;
            }
        }
    }

    protected void getFlagsBuf() {
        int FlagsPlace = decodeNum(fgetbits(), 5, DecHf2, PosHf2);
        while (true) {
            int Flags = this.ChSetC[FlagsPlace];
            this.FlagBuf = Flags >>> 8;
            int[] iArr = this.NToPlC;
            int Flags2 = Flags + 1;
            int Flags3 = Flags & 255;
            int NewFlagsPlace = iArr[Flags3];
            iArr[Flags3] = NewFlagsPlace + 1;
            if ((Flags2 & 255) == 0) {
                corrHuff(this.ChSetC, this.NToPlC);
            } else {
                this.ChSetC[FlagsPlace] = this.ChSetC[NewFlagsPlace];
                this.ChSetC[NewFlagsPlace] = Flags2;
                return;
            }
        }
    }

    protected void oldUnpInitData(boolean Solid) {
        if (!Solid) {
            this.Buf60 = 0;
            this.NumHuf = 0;
            this.AvrLn3 = 0;
            this.AvrLn2 = 0;
            this.AvrLn1 = 0;
            this.AvrPlcB = 0;
            this.AvrPlc = 13568;
            this.MaxDist3 = 8193;
            this.Nlzb = 128;
            this.Nhfb = 128;
        }
        this.FlagsCnt = 0;
        this.FlagBuf = 0;
        this.StMode = 0;
        this.LCount = 0;
        this.readTop = 0;
    }

    protected void initHuff() {
        for (int I = 0; I < 256; I++) {
            int[] iArr = this.Place;
            int[] iArr2 = this.PlaceA;
            this.PlaceB[I] = I;
            iArr2[I] = I;
            iArr[I] = I;
            this.PlaceC[I] = ((~I) + 1) & 255;
            int[] iArr3 = this.ChSet;
            int i = I << 8;
            this.ChSetB[I] = i;
            iArr3[I] = i;
            this.ChSetA[I] = I;
            this.ChSetC[I] = (((~I) + 1) & 255) << 8;
        }
        Arrays.fill(this.NToPl, 0);
        Arrays.fill(this.NToPlB, 0);
        Arrays.fill(this.NToPlC, 0);
        corrHuff(this.ChSetB, this.NToPlB);
    }

    protected void corrHuff(int[] CharSet, int[] NumToPlace) {
        int pos = 0;
        for (int I = 7; I >= 0; I--) {
            int J = 0;
            while (J < 32) {
                CharSet[pos] = (CharSet[pos] & InputDeviceCompat.SOURCE_ANY) | I;
                J++;
                pos++;
            }
        }
        Arrays.fill(NumToPlace, 0);
        for (int I2 = 6; I2 >= 0; I2--) {
            NumToPlace[I2] = (7 - I2) * 32;
        }
    }

    protected void oldCopyString(int distance, int length) {
        this.destUnpSize -= (long) length;
        int destPtr = (this.unpPtr - distance) & Compress.MAXWINMASK;
        if (distance == 1) {
            Arrays.fill(this.window, this.unpPtr, this.unpPtr + length, this.window[destPtr]);
            this.unpPtr += length;
        } else {
            if (destPtr + length <= this.unpPtr) {
                System.arraycopy(this.window, destPtr, this.window, this.unpPtr, length);
                this.unpPtr += length;
                return;
            }
            while (true) {
                int length2 = length - 1;
                if (length != 0) {
                    this.window[this.unpPtr] = this.window[destPtr];
                    this.unpPtr = (this.unpPtr + 1) & Compress.MAXWINMASK;
                    destPtr = (destPtr + 1) & Compress.MAXWINMASK;
                    length = length2;
                } else {
                    return;
                }
            }
        }
    }

    protected int decodeNum(int Num, int StartPos, int[] DecTab, int[] PosTab) {
        int Num2 = Num & 65520;
        int I = 0;
        while (DecTab[I] <= Num2) {
            StartPos++;
            I++;
        }
        faddbits(StartPos);
        return ((Num2 - (I != 0 ? DecTab[I - 1] : 0)) >>> (16 - StartPos)) + PosTab[StartPos];
    }

    protected void oldUnpWriteBuf() throws IOException {
        if (this.unpPtr != this.wrPtr) {
            this.unpSomeRead = true;
        }
        if (this.unpPtr < this.wrPtr) {
            this.unpIO.unpWrite(this.window, this.wrPtr, (-this.wrPtr) & Compress.MAXWINMASK);
            this.unpIO.unpWrite(this.window, 0, this.unpPtr);
            this.unpAllBuf = true;
        } else {
            this.unpIO.unpWrite(this.window, this.wrPtr, this.unpPtr - this.wrPtr);
        }
        this.wrPtr = this.unpPtr;
    }
}
