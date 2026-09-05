package com.github.junrar.unpack;

import androidx.core.view.InputDeviceCompat;
import androidx.profileinstaller.ProfileVerifier;
import com.github.junrar.exception.RarException;
import com.github.junrar.unpack.decode.AudioVariables;
import com.github.junrar.unpack.decode.BitDecode;
import com.github.junrar.unpack.decode.Compress;
import com.github.junrar.unpack.decode.Decode;
import com.github.junrar.unpack.decode.DistDecode;
import com.github.junrar.unpack.decode.LitDecode;
import com.github.junrar.unpack.decode.LowDistDecode;
import com.github.junrar.unpack.decode.MultDecode;
import com.github.junrar.unpack.decode.RepDecode;
import com.google.android.material.internal.ViewUtils;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.compress.archivers.cpio.CpioConstants;

/* JADX INFO: loaded from: classes.dex */
public abstract class Unpack20 extends Unpack15 {
    protected int UnpAudioBlock;
    protected int UnpChannelDelta;
    protected int UnpChannels;
    protected int UnpCurChannel;
    public static final int[] LDecode = {0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224};
    public static final byte[] LBits = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5};
    public static final int[] DDecode = {0, 1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512, ViewUtils.EDGE_TO_EDGE_FLAGS, 1024, 1536, 2048, 3072, 4096, 6144, 8192, 12288, 16384, CpioConstants.C_ISBLK, 32768, CpioConstants.C_ISSOCK, 65536, 98304, 131072, ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE, 262144, 327680, 393216, 458752, 524288, 589824, 655360, 720896, 786432, 851968, 917504, 983040};
    public static final int[] DBits = {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16};
    public static final int[] SDDecode = {0, 4, 8, 16, 32, 64, 128, 192};
    public static final int[] SDBits = {2, 2, 3, 4, 5, 6, 6, 6};
    protected MultDecode[] MD = new MultDecode[4];
    protected byte[] UnpOldTable20 = new byte[1028];
    protected AudioVariables[] AudV = new AudioVariables[4];
    protected LitDecode LD = new LitDecode();
    protected DistDecode DD = new DistDecode();
    protected LowDistDecode LDD = new LowDistDecode();
    protected RepDecode RD = new RepDecode();
    protected BitDecode BD = new BitDecode();

    protected void unpack20(boolean solid) throws RarException, IOException {
        if (this.suspended) {
            this.unpPtr = this.wrPtr;
        } else {
            unpInitData(solid);
            if (!unpReadBuf()) {
                return;
            }
            if (!solid && !ReadTables20()) {
                return;
            } else {
                this.destUnpSize--;
            }
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
            if (this.UnpAudioBlock != 0) {
                int AudioNumber = decodeNumber(this.MD[this.UnpCurChannel]);
                if (AudioNumber == 256) {
                    if (!ReadTables20()) {
                        break;
                    }
                } else {
                    byte[] bArr = this.window;
                    int i = this.unpPtr;
                    this.unpPtr = i + 1;
                    bArr[i] = DecodeAudio(AudioNumber);
                    int i2 = this.UnpCurChannel + 1;
                    this.UnpCurChannel = i2;
                    if (i2 == this.UnpChannels) {
                        this.UnpCurChannel = 0;
                    }
                    this.destUnpSize--;
                }
            } else {
                int Number = decodeNumber(this.LD);
                if (Number < 256) {
                    byte[] bArr2 = this.window;
                    int i3 = this.unpPtr;
                    this.unpPtr = i3 + 1;
                    bArr2[i3] = (byte) Number;
                    this.destUnpSize--;
                } else if (Number > 269) {
                    int Number2 = Number - 270;
                    int Length = LDecode[Number2] + 3;
                    int Bits = LBits[Number2];
                    if (Bits > 0) {
                        Length += getbits() >>> (16 - Bits);
                        addbits(Bits);
                    }
                    int DistNumber = decodeNumber(this.DD);
                    int Distance = DDecode[DistNumber] + 1;
                    int Bits2 = DBits[DistNumber];
                    if (Bits2 > 0) {
                        Distance += getbits() >>> (16 - Bits2);
                        addbits(Bits2);
                    }
                    if (Distance >= 8192) {
                        Length++;
                        if (Distance >= 262144) {
                            Length++;
                        }
                    }
                    CopyString20(Length, Distance);
                } else if (Number == 269) {
                    if (!ReadTables20()) {
                        break;
                    }
                } else if (Number == 256) {
                    CopyString20(this.lastLength, this.lastDist);
                } else if (Number < 261) {
                    int Distance2 = this.oldDist[(this.oldDistPtr - (Number + InputDeviceCompat.SOURCE_ANY)) & 3];
                    int LengthNumber = decodeNumber(this.RD);
                    int Length2 = LDecode[LengthNumber] + 2;
                    int Bits3 = LBits[LengthNumber];
                    if (Bits3 > 0) {
                        Length2 += getbits() >>> (16 - Bits3);
                        addbits(Bits3);
                    }
                    if (Distance2 >= 257) {
                        Length2++;
                        if (Distance2 >= 8192) {
                            Length2++;
                            if (Distance2 >= 262144) {
                                Length2++;
                            }
                        }
                    }
                    CopyString20(Length2, Distance2);
                } else if (Number < 270) {
                    int Number3 = Number - 261;
                    int Distance3 = SDDecode[Number3] + 1;
                    int Bits4 = SDBits[Number3];
                    if (Bits4 > 0) {
                        Distance3 += getbits() >>> (16 - Bits4);
                        addbits(Bits4);
                    }
                    CopyString20(2, Distance3);
                }
            }
        }
        ReadLastTables();
        oldUnpWriteBuf();
    }

    protected void CopyString20(int length, int distance) {
        int[] iArr = this.oldDist;
        int i = this.oldDistPtr;
        this.oldDistPtr = i + 1;
        iArr[i & 3] = distance;
        this.lastDist = distance;
        this.lastLength = length;
        this.destUnpSize -= (long) length;
        int destPtr = this.unpPtr - distance;
        if (destPtr < 4194004 && this.unpPtr < 4194004) {
            if (destPtr + length <= this.unpPtr) {
                System.arraycopy(this.window, destPtr, this.window, this.unpPtr, length);
                this.unpPtr += length;
                return;
            }
            byte[] bArr = this.window;
            int i2 = this.unpPtr;
            this.unpPtr = i2 + 1;
            int destPtr2 = destPtr + 1;
            bArr[i2] = this.window[destPtr];
            byte[] bArr2 = this.window;
            int i3 = this.unpPtr;
            this.unpPtr = i3 + 1;
            bArr2[i3] = this.window[destPtr2];
            int destPtr3 = destPtr2 + 1;
            while (length > 2) {
                length--;
                byte[] bArr3 = this.window;
                int i4 = this.unpPtr;
                this.unpPtr = i4 + 1;
                bArr3[i4] = this.window[destPtr3];
                destPtr3++;
            }
            return;
        }
        while (true) {
            int length2 = length - 1;
            if (length != 0) {
                this.window[this.unpPtr] = this.window[destPtr & Compress.MAXWINMASK];
                this.unpPtr = (this.unpPtr + 1) & Compress.MAXWINMASK;
                length = length2;
                destPtr++;
            } else {
                return;
            }
        }
    }

    protected void makeDecodeTables(byte[] lenTab, int offset, Decode dec, int size) {
        int[] lenCount = new int[16];
        int[] tmpPos = new int[16];
        Arrays.fill(lenCount, 0);
        Arrays.fill(dec.getDecodeNum(), 0);
        for (int i = 0; i < size; i++) {
            int i2 = lenTab[offset + i] & 15;
            lenCount[i2] = lenCount[i2] + 1;
        }
        lenCount[0] = 0;
        tmpPos[0] = 0;
        dec.getDecodePos()[0] = 0;
        dec.getDecodeLen()[0] = 0;
        long N = 0;
        for (int i3 = 1; i3 < 16; i3++) {
            N = (((long) lenCount[i3]) + N) * 2;
            long M = N << (15 - i3);
            if (M > 65535) {
                M = 65535;
            }
            dec.getDecodeLen()[i3] = (int) M;
            int[] decodePos = dec.getDecodePos();
            int i4 = dec.getDecodePos()[i3 - 1] + lenCount[i3 - 1];
            decodePos[i3] = i4;
            tmpPos[i3] = i4;
        }
        for (int i5 = 0; i5 < size; i5++) {
            if (lenTab[offset + i5] != 0) {
                int[] decodeNum = dec.getDecodeNum();
                int i6 = lenTab[offset + i5] & 15;
                int i7 = tmpPos[i6];
                tmpPos[i6] = i7 + 1;
                decodeNum[i7] = i5;
            }
        }
        dec.setMaxNum(size);
    }

    protected int decodeNumber(Decode dec) {
        int bits;
        long bitField = getbits() & 65534;
        int[] decodeLen = dec.getDecodeLen();
        if (bitField < decodeLen[8]) {
            if (bitField < decodeLen[4]) {
                if (bitField < decodeLen[2]) {
                    if (bitField < decodeLen[1]) {
                        bits = 1;
                    } else {
                        bits = 2;
                    }
                } else if (bitField < decodeLen[3]) {
                    bits = 3;
                } else {
                    bits = 4;
                }
            } else if (bitField < decodeLen[6]) {
                if (bitField < decodeLen[5]) {
                    bits = 5;
                } else {
                    bits = 6;
                }
            } else if (bitField < decodeLen[7]) {
                bits = 7;
            } else {
                bits = 8;
            }
        } else if (bitField < decodeLen[12]) {
            if (bitField < decodeLen[10]) {
                if (bitField < decodeLen[9]) {
                    bits = 9;
                } else {
                    bits = 10;
                }
            } else if (bitField < decodeLen[11]) {
                bits = 11;
            } else {
                bits = 12;
            }
        } else if (bitField >= decodeLen[14]) {
            bits = 15;
        } else if (bitField < decodeLen[13]) {
            bits = 13;
        } else {
            bits = 14;
        }
        addbits(bits);
        int N = dec.getDecodePos()[bits] + ((((int) bitField) - decodeLen[bits - 1]) >>> (16 - bits));
        if (N >= dec.getMaxNum()) {
            N = 0;
        }
        return dec.getDecodeNum()[N];
    }

    protected boolean ReadTables20() throws RarException, IOException {
        int TableSize;
        int I;
        byte[] BitLength = new byte[19];
        byte[] Table = new byte[1028];
        if (this.inAddr > this.readTop - 25 && !unpReadBuf()) {
            return false;
        }
        int BitField = getbits();
        this.UnpAudioBlock = 32768 & BitField;
        if ((BitField & 16384) == 0) {
            Arrays.fill(this.UnpOldTable20, (byte) 0);
        }
        addbits(2);
        if (this.UnpAudioBlock != 0) {
            this.UnpChannels = ((BitField >>> 12) & 3) + 1;
            if (this.UnpCurChannel >= this.UnpChannels) {
                this.UnpCurChannel = 0;
            }
            addbits(2);
            TableSize = this.UnpChannels * 257;
        } else {
            TableSize = 374;
        }
        for (int I2 = 0; I2 < 19; I2++) {
            BitLength[I2] = (byte) (getbits() >>> 12);
            addbits(4);
        }
        makeDecodeTables(BitLength, 0, this.BD, 19);
        int I3 = 0;
        while (I3 < TableSize) {
            if (this.inAddr > this.readTop - 5 && !unpReadBuf()) {
                return false;
            }
            int Number = decodeNumber(this.BD);
            if (Number < 16) {
                Table[I3] = (byte) ((this.UnpOldTable20[I3] + Number) & 15);
                I3++;
            } else if (Number == 16) {
                int N = (getbits() >>> 14) + 3;
                addbits(2);
                while (true) {
                    int N2 = N - 1;
                    if (N <= 0 || I3 >= TableSize) {
                        break;
                    }
                    Table[I3] = Table[I3 - 1];
                    I3++;
                    N = N2;
                }
            } else {
                if (Number == 17) {
                    I = (getbits() >>> 13) + 3;
                    addbits(3);
                } else {
                    int N3 = getbits();
                    I = (N3 >>> 9) + 11;
                    addbits(7);
                }
                while (true) {
                    int N4 = I - 1;
                    if (I <= 0 || I3 >= TableSize) {
                        break;
                    }
                    Table[I3] = 0;
                    I3++;
                    I = N4;
                }
            }
        }
        if (this.inAddr > this.readTop) {
            return true;
        }
        if (this.UnpAudioBlock != 0) {
            for (int I4 = 0; I4 < this.UnpChannels; I4++) {
                makeDecodeTables(Table, I4 * 257, this.MD[I4], 257);
            }
        } else {
            makeDecodeTables(Table, 0, this.LD, Compress.NC20);
            makeDecodeTables(Table, Compress.NC20, this.DD, 48);
            makeDecodeTables(Table, 346, this.RD, 28);
        }
        System.arraycopy(Table, 0, this.UnpOldTable20, 0, this.UnpOldTable20.length);
        return true;
    }

    protected void unpInitData20(boolean Solid) {
        if (!Solid) {
            this.UnpCurChannel = 0;
            this.UnpChannelDelta = 0;
            this.UnpChannels = 1;
            for (int i = 0; i < this.AudV.length; i++) {
                this.AudV[i] = new AudioVariables();
            }
            Arrays.fill(this.UnpOldTable20, (byte) 0);
            for (int i2 = 0; i2 < this.MD.length; i2++) {
                this.MD[i2] = new MultDecode();
            }
        }
    }

    protected void ReadLastTables() throws RarException, IOException {
        if (this.readTop >= this.inAddr + 5) {
            if (this.UnpAudioBlock != 0) {
                if (decodeNumber(this.MD[this.UnpCurChannel]) == 256) {
                    ReadTables20();
                }
            } else if (decodeNumber(this.LD) == 269) {
                ReadTables20();
            }
        }
    }

    protected byte DecodeAudio(int Delta) {
        AudioVariables v = this.AudV[this.UnpCurChannel];
        v.setByteCount(v.getByteCount() + 1);
        v.setD4(v.getD3());
        v.setD3(v.getD2());
        v.setD2(v.getLastDelta() - v.getD1());
        v.setD1(v.getLastDelta());
        int PCh = (v.getLastChar() * 8) + (v.getK1() * v.getD1());
        int Ch = ((((PCh + ((v.getK2() * v.getD2()) + (v.getK3() * v.getD3()))) + ((v.getK4() * v.getD4()) + (v.getK5() * this.UnpChannelDelta))) >>> 3) & 255) - Delta;
        int D = ((byte) Delta) << 3;
        int[] dif = v.getDif();
        dif[0] = dif[0] + Math.abs(D);
        int[] dif2 = v.getDif();
        dif2[1] = dif2[1] + Math.abs(D - v.getD1());
        int[] dif3 = v.getDif();
        dif3[2] = dif3[2] + Math.abs(v.getD1() + D);
        int[] dif4 = v.getDif();
        dif4[3] = dif4[3] + Math.abs(D - v.getD2());
        int[] dif5 = v.getDif();
        dif5[4] = dif5[4] + Math.abs(v.getD2() + D);
        int[] dif6 = v.getDif();
        dif6[5] = dif6[5] + Math.abs(D - v.getD3());
        int[] dif7 = v.getDif();
        dif7[6] = dif7[6] + Math.abs(v.getD3() + D);
        int[] dif8 = v.getDif();
        dif8[7] = dif8[7] + Math.abs(D - v.getD4());
        int[] dif9 = v.getDif();
        dif9[8] = dif9[8] + Math.abs(v.getD4() + D);
        int[] dif10 = v.getDif();
        dif10[9] = dif10[9] + Math.abs(D - this.UnpChannelDelta);
        int[] dif11 = v.getDif();
        dif11[10] = dif11[10] + Math.abs(this.UnpChannelDelta + D);
        v.setLastDelta((byte) (Ch - v.getLastChar()));
        this.UnpChannelDelta = v.getLastDelta();
        v.setLastChar(Ch);
        if ((v.getByteCount() & 31) == 0) {
            int MinDif = v.getDif()[0];
            int NumMinDif = 0;
            v.getDif()[0] = 0;
            for (int I = 1; I < v.getDif().length; I++) {
                if (v.getDif()[I] < MinDif) {
                    MinDif = v.getDif()[I];
                    NumMinDif = I;
                }
                v.getDif()[I] = 0;
            }
            switch (NumMinDif) {
                case 1:
                    if (v.getK1() >= -16) {
                        v.setK1(v.getK1() - 1);
                    }
                    break;
                case 2:
                    if (v.getK1() < 16) {
                        v.setK1(v.getK1() + 1);
                    }
                    break;
                case 3:
                    if (v.getK2() >= -16) {
                        v.setK2(v.getK2() - 1);
                    }
                    break;
                case 4:
                    if (v.getK2() < 16) {
                        v.setK2(v.getK2() + 1);
                    }
                    break;
                case 5:
                    if (v.getK3() >= -16) {
                        v.setK3(v.getK3() - 1);
                    }
                    break;
                case 6:
                    if (v.getK3() < 16) {
                        v.setK3(v.getK3() + 1);
                    }
                    break;
                case 7:
                    if (v.getK4() >= -16) {
                        v.setK4(v.getK4() - 1);
                    }
                    break;
                case 8:
                    if (v.getK4() < 16) {
                        v.setK4(v.getK4() + 1);
                    }
                    break;
                case 9:
                    if (v.getK5() >= -16) {
                        v.setK5(v.getK5() - 1);
                    }
                    break;
                case 10:
                    if (v.getK5() < 16) {
                        v.setK5(v.getK5() + 1);
                    }
                    break;
            }
        }
        return (byte) Ch;
    }
}
