package com.github.junrar.unpack;

import com.github.junrar.exception.RarException;
import com.github.junrar.unpack.decode.Compress;
import com.github.junrar.unpack.ppm.BlockTypes;
import com.github.junrar.unpack.ppm.ModelPPM;
import com.github.junrar.unpack.ppm.SubAllocator;
import com.github.junrar.unpack.vm.BitInput;
import com.github.junrar.unpack.vm.RarVM;
import com.github.junrar.unpack.vm.VMPreparedProgram;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import kotlin.UByte;
import org.apache.commons.compress.compressors.bzip2.BZip2Constants;

/* JADX INFO: loaded from: classes.dex */
public final class Unpack extends Unpack20 {
    public static int[] DBitLengthCounts = {4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 14, 0, 12};
    private boolean fileExtracted;
    private int lastFilter;
    private int lowDistRepCount;
    private boolean ppmError;
    private int ppmEscChar;
    private int prevLowDist;
    private boolean tablesRead;
    private BlockTypes unpBlockType;
    private long writtenFileSize;
    private final ModelPPM ppm = new ModelPPM();
    private final RarVM rarVM = new RarVM();
    private final List<UnpackFilter> filters = new ArrayList();
    private final List<UnpackFilter> prgStack = new ArrayList();
    private final List<Integer> oldFilterLengths = new ArrayList();
    private final byte[] unpOldTable = new byte[Compress.HUFF_TABLE_SIZE];

    public Unpack(ComprDataIO DataIO) {
        this.unpIO = DataIO;
        this.window = null;
        this.suspended = false;
        this.unpAllBuf = false;
        this.unpSomeRead = false;
    }

    public void init(byte[] window) {
        if (window == null) {
            this.window = new byte[4194304];
        } else {
            this.window = window;
        }
        this.inAddr = 0;
        unpInitData(false);
    }

    public void doUnpack(int method, boolean solid) throws RarException, IOException {
        if (this.unpIO.getSubHeader().getUnpMethod() == 48) {
            unstoreFile();
        }
        switch (method) {
            case 15:
                unpack15(solid);
                break;
            case 20:
            case 26:
                unpack20(solid);
                break;
            case 29:
            case 36:
                unpack29(solid);
                break;
        }
    }

    private void unstoreFile() throws RarException, IOException {
        byte[] buffer = new byte[65536];
        while (true) {
            int code = this.unpIO.unpRead(buffer, 0, (int) Math.min(buffer.length, this.destUnpSize));
            if (code != 0 && code != -1) {
                int code2 = ((long) code) < this.destUnpSize ? code : (int) this.destUnpSize;
                this.unpIO.unpWrite(buffer, 0, code2);
                if (this.destUnpSize >= 0) {
                    this.destUnpSize -= (long) code2;
                }
            } else {
                return;
            }
        }
    }

    private void unpack29(boolean solid) throws RarException, IOException {
        int[] DDecode = new int[60];
        byte[] DBits = new byte[60];
        if (DDecode[1] == 0) {
            int Dist = 0;
            int BitLength = 0;
            int Slot = 0;
            int I = 0;
            while (I < DBitLengthCounts.length) {
                int count = DBitLengthCounts[I];
                int J = 0;
                while (J < count) {
                    DDecode[Slot] = Dist;
                    DBits[Slot] = (byte) BitLength;
                    J++;
                    Slot++;
                    Dist += 1 << BitLength;
                }
                I++;
                BitLength++;
            }
        }
        this.fileExtracted = true;
        if (!this.suspended) {
            unpInitData(solid);
            if (!unpReadBuf()) {
                return;
            }
            if ((!solid || !this.tablesRead) && !readTables()) {
                return;
            }
        }
        if (this.ppmError) {
            return;
        }
        while (true) {
            this.unpPtr &= Compress.MAXWINMASK;
            if (this.inAddr > this.readBorder && !unpReadBuf()) {
                break;
            }
            if (((this.wrPtr - this.unpPtr) & Compress.MAXWINMASK) < 260 && this.wrPtr != this.unpPtr) {
                UnpWriteBuf();
                if (this.writtenFileSize > this.destUnpSize) {
                    return;
                }
                if (this.suspended) {
                    this.fileExtracted = false;
                    return;
                }
            }
            if (this.unpBlockType != BlockTypes.BLOCK_PPM) {
                int Number = decodeNumber(this.LD);
                if (Number < 256) {
                    byte[] bArr = this.window;
                    int i = this.unpPtr;
                    this.unpPtr = i + 1;
                    bArr[i] = (byte) Number;
                } else if (Number >= 271) {
                    int Number2 = Number - 271;
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
                        if (DistNumber > 9) {
                            if (Bits2 > 4) {
                                Distance += (getbits() >>> (20 - Bits2)) << 4;
                                addbits(Bits2 - 4);
                            }
                            if (this.lowDistRepCount > 0) {
                                this.lowDistRepCount--;
                                Distance += this.prevLowDist;
                            } else {
                                int LowDist = decodeNumber(this.LDD);
                                if (LowDist == 16) {
                                    this.lowDistRepCount = 15;
                                    Distance += this.prevLowDist;
                                } else {
                                    Distance += LowDist;
                                    this.prevLowDist = LowDist;
                                }
                            }
                        } else {
                            Distance += getbits() >>> (16 - Bits2);
                            addbits(Bits2);
                        }
                    }
                    if (Distance >= 8192) {
                        Length++;
                        if (Distance >= 262144) {
                            Length++;
                        }
                    }
                    insertOldDist(Distance);
                    insertLastMatch(Length, Distance);
                    copyString(Length, Distance);
                } else if (Number == 256) {
                    if (!readEndOfBlock()) {
                        break;
                    }
                } else if (Number == 257) {
                    if (!readVMCode()) {
                        break;
                    }
                } else if (Number == 258) {
                    if (this.lastLength != 0) {
                        copyString(this.lastLength, this.lastDist);
                    }
                } else if (Number < 263) {
                    int DistNum = Number - 259;
                    int Distance2 = this.oldDist[DistNum];
                    System.arraycopy(this.oldDist, 0, this.oldDist, 1, DistNum);
                    this.oldDist[0] = Distance2;
                    int LengthNumber = decodeNumber(this.RD);
                    int Length2 = LDecode[LengthNumber] + 2;
                    int Bits3 = LBits[LengthNumber];
                    if (Bits3 > 0) {
                        Length2 += getbits() >>> (16 - Bits3);
                        addbits(Bits3);
                    }
                    insertLastMatch(Length2, Distance2);
                    copyString(Length2, Distance2);
                } else if (Number < 272) {
                    int Number3 = Number - 263;
                    int Distance3 = SDDecode[Number3] + 1;
                    int Bits4 = SDBits[Number3];
                    if (Bits4 > 0) {
                        Distance3 += getbits() >>> (16 - Bits4);
                        addbits(Bits4);
                    }
                    insertOldDist(Distance3);
                    insertLastMatch(2, Distance3);
                    copyString(2, Distance3);
                }
            } else {
                int Ch = this.ppm.decodeChar();
                if (Ch == -1) {
                    this.ppmError = true;
                    break;
                }
                if (Ch == this.ppmEscChar) {
                    int NextCh = this.ppm.decodeChar();
                    if (NextCh == 0) {
                        if (!readTables()) {
                            break;
                        }
                    } else {
                        if (NextCh == 2 || NextCh == -1) {
                            break;
                        }
                        if (NextCh == 3) {
                            if (!readVMCodePPM()) {
                                break;
                            }
                        } else if (NextCh == 4) {
                            int Distance4 = 0;
                            int Length3 = 0;
                            boolean failed = false;
                            for (int I2 = 0; I2 < 4 && !failed; I2++) {
                                int ch = this.ppm.decodeChar();
                                if (ch == -1) {
                                    failed = true;
                                } else if (I2 == 3) {
                                    Length3 = ch & 255;
                                } else {
                                    Distance4 = (Distance4 << 8) + (ch & 255);
                                }
                            }
                            if (failed) {
                                break;
                            } else {
                                copyString(Length3 + 32, Distance4 + 2);
                            }
                        } else if (NextCh == 5) {
                            int Length4 = this.ppm.decodeChar();
                            if (Length4 == -1) {
                                break;
                            } else {
                                copyString(Length4 + 4, 1);
                            }
                        }
                    }
                }
                byte[] bArr2 = this.window;
                int i2 = this.unpPtr;
                this.unpPtr = i2 + 1;
                bArr2[i2] = (byte) Ch;
            }
        }
        UnpWriteBuf();
    }

    private void UnpWriteBuf() throws IOException {
        int i;
        UnpackFilter NextFilter;
        int I;
        UnpackFilter flt;
        char c;
        int WrittenBorder = this.wrPtr;
        int i2 = this.unpPtr - WrittenBorder;
        int i3 = Compress.MAXWINMASK;
        int WriteSize = i2 & Compress.MAXWINMASK;
        int I2 = 0;
        while (I2 < this.prgStack.size()) {
            UnpackFilter flt2 = this.prgStack.get(I2);
            if (flt2 == null) {
                i = i3;
            } else if (flt2.isNextWindow()) {
                flt2.setNextWindow(false);
                i = i3;
            } else {
                int BlockStart = flt2.getBlockStart();
                int BlockLength = flt2.getBlockLength();
                if (((BlockStart - WrittenBorder) & i3) >= WriteSize) {
                    i = i3;
                } else {
                    if (WrittenBorder != BlockStart) {
                        UnpWriteArea(WrittenBorder, BlockStart);
                        WrittenBorder = BlockStart;
                        WriteSize = (this.unpPtr - WrittenBorder) & i3;
                    }
                    if (BlockLength > WriteSize) {
                        for (int J = I2; J < this.prgStack.size(); J++) {
                            UnpackFilter filt = this.prgStack.get(J);
                            if (filt != null && filt.isNextWindow()) {
                                filt.setNextWindow(false);
                            }
                        }
                        this.wrPtr = WrittenBorder;
                        return;
                    }
                    int BlockEnd = (BlockStart + BlockLength) & i3;
                    if (BlockStart < BlockEnd || BlockEnd == 0) {
                        this.rarVM.setMemory(0, this.window, BlockStart, BlockLength);
                    } else {
                        int FirstPartLength = 4194304 - BlockStart;
                        this.rarVM.setMemory(0, this.window, BlockStart, FirstPartLength);
                        this.rarVM.setMemory(FirstPartLength, this.window, 0, BlockEnd);
                    }
                    VMPreparedProgram ParentPrg = this.filters.get(flt2.getParentFilter()).getPrg();
                    VMPreparedProgram Prg = flt2.getPrg();
                    if (ParentPrg.getGlobalData().size() <= 64) {
                        i = i3;
                    } else {
                        Prg.getGlobalData().setSize(ParentPrg.getGlobalData().size());
                        int i4 = 0;
                        while (i4 < ParentPrg.getGlobalData().size() - 64) {
                            Prg.getGlobalData().set(i4 + 64, ParentPrg.getGlobalData().get(i4 + 64));
                            i4++;
                            i3 = i3;
                        }
                        i = i3;
                    }
                    ExecuteCode(Prg);
                    if (Prg.getGlobalData().size() > 64) {
                        if (ParentPrg.getGlobalData().size() < Prg.getGlobalData().size()) {
                            ParentPrg.getGlobalData().setSize(Prg.getGlobalData().size());
                        }
                        for (int i5 = 0; i5 < Prg.getGlobalData().size() - 64; i5++) {
                            ParentPrg.getGlobalData().set(i5 + 64, Prg.getGlobalData().get(i5 + 64));
                        }
                    } else {
                        ParentPrg.getGlobalData().clear();
                    }
                    int FilteredDataOffset = Prg.getFilteredDataOffset();
                    int FilteredDataSize = Prg.getFilteredDataSize();
                    byte[] FilteredData = new byte[FilteredDataSize];
                    for (int i6 = 0; i6 < FilteredDataSize; i6++) {
                        FilteredData[i6] = this.rarVM.getMem()[FilteredDataOffset + i6];
                    }
                    this.prgStack.set(I2, null);
                    while (I2 + 1 < this.prgStack.size() && (NextFilter = this.prgStack.get(I2 + 1)) != null && NextFilter.getBlockStart() == BlockStart && NextFilter.getBlockLength() == FilteredDataSize && !NextFilter.isNextWindow()) {
                        this.rarVM.setMemory(0, FilteredData, 0, FilteredDataSize);
                        VMPreparedProgram pPrg = this.filters.get(NextFilter.getParentFilter()).getPrg();
                        VMPreparedProgram NextPrg = NextFilter.getPrg();
                        int WriteSize2 = WriteSize;
                        int WriteSize3 = pPrg.getGlobalData().size();
                        if (WriteSize3 <= 64) {
                            I = I2;
                            flt = flt2;
                        } else {
                            NextPrg.getGlobalData().setSize(pPrg.getGlobalData().size());
                            int i7 = 0;
                            while (i7 < pPrg.getGlobalData().size() - 64) {
                                int i8 = i7;
                                NextPrg.getGlobalData().set(i8 + 64, pPrg.getGlobalData().get(i8 + 64));
                                i7 = i8 + 1;
                                I2 = I2;
                                flt2 = flt2;
                            }
                            I = I2;
                            flt = flt2;
                        }
                        ExecuteCode(NextPrg);
                        if (NextPrg.getGlobalData().size() > 64) {
                            if (pPrg.getGlobalData().size() < NextPrg.getGlobalData().size()) {
                                pPrg.getGlobalData().setSize(NextPrg.getGlobalData().size());
                            }
                            int i9 = 0;
                            while (true) {
                                c = '@';
                                if (i9 >= NextPrg.getGlobalData().size() - 64) {
                                    break;
                                }
                                int i10 = i9;
                                pPrg.getGlobalData().set(i9 + 64, NextPrg.getGlobalData().get(i10 + 64));
                                i9 = i10 + 1;
                            }
                        } else {
                            c = '@';
                            pPrg.getGlobalData().clear();
                        }
                        FilteredDataOffset = NextPrg.getFilteredDataOffset();
                        FilteredDataSize = NextPrg.getFilteredDataSize();
                        FilteredData = new byte[FilteredDataSize];
                        for (int i11 = 0; i11 < FilteredDataSize; i11++) {
                            FilteredData[i11] = NextPrg.getGlobalData().get(FilteredDataOffset + i11).byteValue();
                        }
                        I2 = I + 1;
                        this.prgStack.set(I2, null);
                        WriteSize = WriteSize2;
                        flt2 = flt;
                    }
                    this.unpIO.unpWrite(FilteredData, 0, FilteredDataSize);
                    this.unpSomeRead = true;
                    this.writtenFileSize += (long) FilteredDataSize;
                    WrittenBorder = BlockEnd;
                    WriteSize = (this.unpPtr - WrittenBorder) & i;
                    I2 = I2;
                }
            }
            I2++;
            i3 = i;
        }
        UnpWriteArea(WrittenBorder, this.unpPtr);
        this.wrPtr = this.unpPtr;
    }

    private void UnpWriteArea(int startPtr, int endPtr) throws IOException {
        if (endPtr != startPtr) {
            this.unpSomeRead = true;
        }
        if (endPtr < startPtr) {
            UnpWriteData(this.window, startPtr, (-startPtr) & Compress.MAXWINMASK);
            UnpWriteData(this.window, 0, endPtr);
            this.unpAllBuf = true;
            return;
        }
        UnpWriteData(this.window, startPtr, endPtr - startPtr);
    }

    private void UnpWriteData(byte[] data, int offset, int size) throws IOException {
        if (this.writtenFileSize >= this.destUnpSize) {
            return;
        }
        int writeSize = size;
        long leftToWrite = this.destUnpSize - this.writtenFileSize;
        if (writeSize > leftToWrite) {
            writeSize = (int) leftToWrite;
        }
        this.unpIO.unpWrite(data, offset, writeSize);
        this.writtenFileSize += (long) size;
    }

    private void insertOldDist(int distance) {
        this.oldDist[3] = this.oldDist[2];
        this.oldDist[2] = this.oldDist[1];
        this.oldDist[1] = this.oldDist[0];
        this.oldDist[0] = distance;
    }

    private void insertLastMatch(int length, int distance) {
        this.lastDist = distance;
        this.lastLength = length;
    }

    private void copyString(int length, int distance) {
        int destPtr = this.unpPtr - distance;
        if (destPtr >= 0 && destPtr < 4194044 && this.unpPtr < 4194044) {
            if (distance == 1) {
                Arrays.fill(this.window, this.unpPtr, this.unpPtr + length, this.window[destPtr]);
                this.unpPtr += length;
                int i = destPtr + length;
                return;
            } else {
                if (destPtr + length <= this.unpPtr) {
                    System.arraycopy(this.window, destPtr, this.window, this.unpPtr, length);
                    this.unpPtr += length;
                    int i2 = destPtr + length;
                    return;
                }
                do {
                    byte[] bArr = this.window;
                    int i3 = this.unpPtr;
                    this.unpPtr = i3 + 1;
                    bArr[i3] = this.window[destPtr];
                    length--;
                    destPtr++;
                } while (length > 0);
                return;
            }
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

    @Override // com.github.junrar.unpack.Unpack15
    protected void unpInitData(boolean solid) {
        if (!solid) {
            this.tablesRead = false;
            Arrays.fill(this.oldDist, 0);
            this.oldDistPtr = 0;
            this.lastDist = 0;
            this.lastLength = 0;
            Arrays.fill(this.unpOldTable, (byte) 0);
            this.unpPtr = 0;
            this.wrPtr = 0;
            this.ppmEscChar = 2;
            initFilters();
        }
        InitBitInput();
        this.ppmError = false;
        this.writtenFileSize = 0L;
        this.readTop = 0;
        this.readBorder = 0;
        unpInitData20(solid);
    }

    private void initFilters() {
        this.oldFilterLengths.clear();
        this.lastFilter = 0;
        this.filters.clear();
        this.prgStack.clear();
    }

    private boolean readEndOfBlock() throws RarException, IOException {
        boolean NewTable;
        int BitField = getbits();
        boolean NewFile = false;
        if ((32768 & BitField) != 0) {
            NewTable = true;
            addbits(1);
        } else {
            NewFile = true;
            NewTable = (BitField & 16384) != 0;
            addbits(2);
        }
        this.tablesRead = !NewTable;
        if (NewFile) {
            return false;
        }
        return !NewTable || readTables();
    }

    private boolean readTables() throws RarException, IOException {
        int N;
        int i;
        byte[] bitLength = new byte[20];
        byte[] table = new byte[Compress.HUFF_TABLE_SIZE];
        if (this.inAddr > this.readTop - 25 && !unpReadBuf()) {
            return false;
        }
        faddbits((8 - this.inBit) & 7);
        long bitField = fgetbits() & (-1);
        if ((32768 & bitField) != 0) {
            this.unpBlockType = BlockTypes.BLOCK_PPM;
            return this.ppm.decodeInit(this, this.ppmEscChar);
        }
        this.unpBlockType = BlockTypes.BLOCK_LZ;
        this.prevLowDist = 0;
        this.lowDistRepCount = 0;
        if ((16384 & bitField) == 0) {
            Arrays.fill(this.unpOldTable, (byte) 0);
        }
        faddbits(2);
        int i2 = 0;
        while (i2 < 20) {
            int length = (fgetbits() >>> 12) & 255;
            faddbits(4);
            if (length == 15) {
                int zeroCount = (fgetbits() >>> 12) & 255;
                faddbits(4);
                if (zeroCount == 0) {
                    bitLength[i2] = 15;
                } else {
                    int zeroCount2 = zeroCount + 2;
                    while (true) {
                        int zeroCount3 = zeroCount2 - 1;
                        if (zeroCount2 <= 0 || i2 >= bitLength.length) {
                            break;
                        }
                        bitLength[i2] = 0;
                        zeroCount2 = zeroCount3;
                        i2++;
                    }
                    i2--;
                }
            } else {
                bitLength[i2] = (byte) length;
            }
            i2++;
        }
        makeDecodeTables(bitLength, 0, this.BD, 20);
        int i3 = 0;
        while (i3 < 404) {
            if (this.inAddr > this.readTop - 5 && !unpReadBuf()) {
                return false;
            }
            int Number = decodeNumber(this.BD);
            if (Number < 16) {
                table[i3] = (byte) ((this.unpOldTable[i3] + Number) & 15);
                i3++;
            } else if (Number < 18) {
                if (Number == 16) {
                    N = (fgetbits() >>> 13) + 3;
                    faddbits(3);
                } else {
                    int N2 = fgetbits();
                    N = (N2 >>> 9) + 11;
                    faddbits(7);
                }
                while (true) {
                    int N3 = N - 1;
                    if (N <= 0 || i3 >= 404) {
                        break;
                    }
                    table[i3] = table[i3 - 1];
                    i3++;
                    N = N3;
                }
            } else {
                if (Number == 18) {
                    i = (fgetbits() >>> 13) + 3;
                    faddbits(3);
                } else {
                    int N4 = fgetbits();
                    i = (N4 >>> 9) + 11;
                    faddbits(7);
                }
                while (true) {
                    int N5 = i - 1;
                    if (i <= 0 || i3 >= 404) {
                        break;
                    }
                    table[i3] = 0;
                    i3++;
                    i = N5;
                }
            }
        }
        this.tablesRead = true;
        if (this.inAddr > this.readTop) {
            return false;
        }
        makeDecodeTables(table, 0, this.LD, Compress.NC);
        makeDecodeTables(table, Compress.NC, this.DD, 60);
        makeDecodeTables(table, 359, this.LDD, 17);
        makeDecodeTables(table, 376, this.RD, 28);
        System.arraycopy(table, 0, this.unpOldTable, 0, this.unpOldTable.length);
        return true;
    }

    private boolean readVMCode() throws RarException, IOException {
        int FirstByte = getbits() >>> 8;
        addbits(8);
        int Length = (FirstByte & 7) + 1;
        if (Length == 7) {
            Length = (getbits() >>> 8) + 7;
            addbits(8);
        } else if (Length == 8) {
            Length = getbits();
            addbits(16);
        }
        List<Byte> vmCode = new ArrayList<>();
        for (int I = 0; I < Length; I++) {
            if (this.inAddr >= this.readTop - 1 && !unpReadBuf() && I < Length - 1) {
                return false;
            }
            vmCode.add(Byte.valueOf((byte) (getbits() >>> 8)));
            addbits(8);
        }
        return addVMCode(FirstByte, vmCode, Length);
    }

    private boolean readVMCodePPM() throws RarException, IOException {
        int B2;
        int FirstByte = this.ppm.decodeChar();
        if (FirstByte == -1) {
            return false;
        }
        int Length = (FirstByte & 7) + 1;
        if (Length == 7) {
            int B1 = this.ppm.decodeChar();
            if (B1 == -1) {
                return false;
            }
            Length = B1 + 7;
        } else if (Length == 8) {
            int B3 = this.ppm.decodeChar();
            if (B3 == -1 || (B2 = this.ppm.decodeChar()) == -1) {
                return false;
            }
            Length = (B3 * 256) + B2;
        }
        List<Byte> vmCode = new ArrayList<>();
        for (int I = 0; I < Length; I++) {
            int Ch = this.ppm.decodeChar();
            if (Ch == -1) {
                return false;
            }
            vmCode.add(Byte.valueOf((byte) Ch));
        }
        return addVMCode(FirstByte, vmCode, Length);
    }

    private boolean addVMCode(int firstByte, List<Byte> vmCode, int length) {
        int FiltPos;
        UnpackFilter Filter;
        int DataSize;
        BitInput Inp = new BitInput();
        Inp.InitBitInput();
        for (int i = 0; i < Math.min(32768, vmCode.size()); i++) {
            Inp.getInBuf()[i] = vmCode.get(i).byteValue();
        }
        this.rarVM.init();
        if ((firstByte & 128) != 0) {
            FiltPos = RarVM.ReadData(Inp);
            if (FiltPos == 0) {
                initFilters();
            } else {
                FiltPos--;
            }
        } else {
            FiltPos = this.lastFilter;
        }
        boolean z = false;
        if (FiltPos <= this.filters.size() && FiltPos <= this.oldFilterLengths.size()) {
            this.lastFilter = FiltPos;
            boolean NewFilter = FiltPos == this.filters.size();
            UnpackFilter StackFilter = new UnpackFilter();
            if (NewFilter) {
                if (FiltPos > 1024) {
                    return false;
                }
                Filter = new UnpackFilter();
                this.filters.add(Filter);
                StackFilter.setParentFilter(this.filters.size() - 1);
                this.oldFilterLengths.add(0);
                Filter.setExecCount(0);
            } else {
                Filter = this.filters.get(FiltPos);
                StackFilter.setParentFilter(FiltPos);
                Filter.setExecCount(Filter.getExecCount() + 1);
            }
            this.prgStack.add(StackFilter);
            StackFilter.setExecCount(Filter.getExecCount());
            int BlockStart = RarVM.ReadData(Inp);
            if ((firstByte & 64) != 0) {
                BlockStart += BZip2Constants.MAX_ALPHA_SIZE;
            }
            StackFilter.setBlockStart((this.unpPtr + BlockStart) & Compress.MAXWINMASK);
            if ((firstByte & 32) != 0) {
                StackFilter.setBlockLength(RarVM.ReadData(Inp));
            } else {
                StackFilter.setBlockLength(FiltPos < this.oldFilterLengths.size() ? this.oldFilterLengths.get(FiltPos).intValue() : 0);
            }
            StackFilter.setNextWindow(this.wrPtr != this.unpPtr && ((this.wrPtr - this.unpPtr) & Compress.MAXWINMASK) <= BlockStart);
            this.oldFilterLengths.set(FiltPos, Integer.valueOf(StackFilter.getBlockLength()));
            Arrays.fill(StackFilter.getPrg().getInitR(), 0);
            int i2 = 3;
            StackFilter.getPrg().getInitR()[3] = 245760;
            StackFilter.getPrg().getInitR()[4] = StackFilter.getBlockLength();
            StackFilter.getPrg().getInitR()[5] = StackFilter.getExecCount();
            if ((firstByte & 16) != 0) {
                int InitMask = Inp.fgetbits() >>> 9;
                Inp.faddbits(7);
                for (int I = 0; I < 7; I++) {
                    if (((1 << I) & InitMask) != 0) {
                        StackFilter.getPrg().getInitR()[I] = RarVM.ReadData(Inp);
                    }
                }
            }
            int InitMask2 = 8;
            if (NewFilter) {
                int VMCodeSize = RarVM.ReadData(Inp);
                if (VMCodeSize >= 65536 || VMCodeSize == 0) {
                    return false;
                }
                byte[] VMCode = new byte[VMCodeSize];
                int I2 = 0;
                while (I2 < VMCodeSize) {
                    if (Inp.Overflow(i2)) {
                        return false;
                    }
                    VMCode[I2] = (byte) (Inp.fgetbits() >>> 8);
                    Inp.faddbits(8);
                    I2++;
                    i2 = 3;
                }
                this.rarVM.prepare(VMCode, VMCodeSize, Filter.getPrg());
            }
            StackFilter.getPrg().setAltCmd(Filter.getPrg().getCmd());
            StackFilter.getPrg().setCmdCount(Filter.getPrg().getCmdCount());
            int StaticDataSize = Filter.getPrg().getStaticData().size();
            if (StaticDataSize > 0 && StaticDataSize < 8192) {
                StackFilter.getPrg().setStaticData(Filter.getPrg().getStaticData());
            }
            if (StackFilter.getPrg().getGlobalData().size() < 64) {
                StackFilter.getPrg().getGlobalData().clear();
                StackFilter.getPrg().getGlobalData().setSize(64);
            }
            Vector<Byte> globalData = StackFilter.getPrg().getGlobalData();
            int I3 = 0;
            for (int i3 = 7; I3 < i3; i3 = 7) {
                this.rarVM.setLowEndianValue(globalData, I3 * 4, StackFilter.getPrg().getInitR()[I3]);
                I3++;
                InitMask2 = InitMask2;
            }
            int i4 = InitMask2;
            this.rarVM.setLowEndianValue(globalData, 28, StackFilter.getBlockLength());
            this.rarVM.setLowEndianValue(globalData, 32, 0);
            this.rarVM.setLowEndianValue(globalData, 36, 0);
            this.rarVM.setLowEndianValue(globalData, 40, 0);
            this.rarVM.setLowEndianValue(globalData, 44, StackFilter.getExecCount());
            for (int i5 = 0; i5 < 16; i5++) {
                globalData.set(i5 + 48, (byte) 0);
            }
            int i6 = firstByte & 8;
            if (i6 != 0) {
                if (Inp.Overflow(3) || (DataSize = RarVM.ReadData(Inp)) > 8128) {
                    return false;
                }
                int CurSize = StackFilter.getPrg().getGlobalData().size();
                if (CurSize < DataSize + 64) {
                    StackFilter.getPrg().getGlobalData().setSize((DataSize + 64) - CurSize);
                }
                Vector<Byte> globalData2 = StackFilter.getPrg().getGlobalData();
                int I4 = 0;
                while (I4 < DataSize) {
                    boolean z2 = z;
                    if (Inp.Overflow(3)) {
                        return z2;
                    }
                    globalData2.set(64 + I4, Byte.valueOf((byte) (Inp.fgetbits() >>> 8)));
                    Inp.faddbits(i4);
                    I4++;
                    z = z2;
                }
            }
            return 1;
        }
        return false;
    }

    private void ExecuteCode(VMPreparedProgram Prg) {
        if (Prg.getGlobalData().size() > 0) {
            Prg.getInitR()[6] = (int) this.writtenFileSize;
            this.rarVM.setLowEndianValue(Prg.getGlobalData(), 36, (int) this.writtenFileSize);
            this.rarVM.setLowEndianValue(Prg.getGlobalData(), 40, (int) (this.writtenFileSize >>> 32));
            this.rarVM.execute(Prg);
        }
    }

    public boolean isFileExtracted() {
        return this.fileExtracted;
    }

    public void setDestSize(long destSize) {
        this.destUnpSize = destSize;
        this.fileExtracted = false;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public int getChar() throws RarException, IOException {
        if (this.inAddr > 32738) {
            unpReadBuf();
        }
        byte[] bArr = this.inBuf;
        int i = this.inAddr;
        this.inAddr = i + 1;
        return bArr[i] & UByte.MAX_VALUE;
    }

    public int getPpmEscChar() {
        return this.ppmEscChar;
    }

    public void setPpmEscChar(int ppmEscChar) {
        this.ppmEscChar = ppmEscChar;
    }

    public void cleanUp() {
        SubAllocator allocator;
        if (this.ppm != null && (allocator = this.ppm.getSubAlloc()) != null) {
            allocator.stopSubAllocator();
        }
    }
}
