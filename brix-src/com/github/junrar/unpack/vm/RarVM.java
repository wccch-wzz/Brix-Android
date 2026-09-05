package com.github.junrar.unpack.vm;

import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import com.github.junrar.crc.RarCRC;
import com.github.junrar.io.Raw;
import java.util.List;
import java.util.Vector;
import kotlin.UByte;
import org.tomlj.internal.TomlParser;

/* JADX INFO: loaded from: classes.dex */
public class RarVM extends BitInput {
    private static final long UINT_MASK = -1;
    public static final int VM_FIXEDGLOBALSIZE = 64;
    public static final int VM_GLOBALMEMADDR = 245760;
    public static final int VM_GLOBALMEMSIZE = 8192;
    public static final int VM_MEMMASK = 262143;
    public static final int VM_MEMSIZE = 262144;
    private static final int regCount = 8;
    private int IP;
    private int codeSize;
    private int flags;
    private final int[] R = new int[8];
    private int maxOpCount = 25000000;
    private byte[] mem = null;

    public void init() {
        if (this.mem == null) {
            this.mem = new byte[262148];
        }
    }

    private boolean isVMMem(byte[] mem) {
        return this.mem == mem;
    }

    private int getValue(boolean byteMode, byte[] mem, int offset) {
        if (byteMode) {
            if (isVMMem(mem)) {
                return mem[offset];
            }
            return mem[offset] & UByte.MAX_VALUE;
        }
        if (isVMMem(mem)) {
            return Raw.readIntLittleEndian(mem, offset);
        }
        return Raw.readIntBigEndian(mem, offset);
    }

    private void setValue(boolean byteMode, byte[] mem, int offset, int value) {
        if (byteMode) {
            if (isVMMem(mem)) {
                mem[offset] = (byte) value;
                return;
            } else {
                byte b = mem[offset];
                mem[offset] = (byte) (((byte) (value & 255)) | 0);
                return;
            }
        }
        if (isVMMem(mem)) {
            Raw.writeIntLittleEndian(mem, offset, value);
        } else {
            Raw.writeIntBigEndian(mem, offset, value);
        }
    }

    public void setLowEndianValue(byte[] mem, int offset, int value) {
        Raw.writeIntLittleEndian(mem, offset, value);
    }

    public void setLowEndianValue(Vector<Byte> mem, int offset, int value) {
        mem.set(offset + 0, Byte.valueOf((byte) (value & 255)));
        mem.set(offset + 1, Byte.valueOf((byte) ((value >>> 8) & 255)));
        mem.set(offset + 2, Byte.valueOf((byte) ((value >>> 16) & 255)));
        mem.set(offset + 3, Byte.valueOf((byte) ((value >>> 24) & 255)));
    }

    private int getOperand(VMPreparedOperand cmdOp) {
        if (cmdOp.getType() == VMOpType.VM_OPREGMEM) {
            int pos = (cmdOp.getOffset() + cmdOp.getBase()) & VM_MEMMASK;
            int ret = Raw.readIntLittleEndian(this.mem, pos);
            return ret;
        }
        int pos2 = cmdOp.getOffset();
        int ret2 = Raw.readIntLittleEndian(this.mem, pos2);
        return ret2;
    }

    public void execute(VMPreparedProgram prg) {
        List<VMPreparedCommand> preparedCode;
        for (int i = 0; i < prg.getInitR().length; i++) {
            this.R[i] = prg.getInitR()[i];
        }
        long globalSize = Math.min(prg.getGlobalData().size(), 8192) & (-1);
        if (globalSize != 0) {
            for (int i2 = 0; i2 < globalSize; i2++) {
                this.mem[i2 + VM_GLOBALMEMADDR] = prg.getGlobalData().get(i2).byteValue();
            }
        }
        long staticSize = Math.min(prg.getStaticData().size(), 8192 - globalSize) & (-1);
        if (staticSize != 0) {
            for (int i3 = 0; i3 < staticSize; i3++) {
                this.mem[((int) globalSize) + VM_GLOBALMEMADDR + i3] = prg.getStaticData().get(i3).byteValue();
            }
        }
        this.R[7] = 262144;
        this.flags = 0;
        if (prg.getAltCmd().size() != 0) {
            preparedCode = prg.getAltCmd();
        } else {
            preparedCode = prg.getCmd();
        }
        if (!ExecuteCode(preparedCode, prg.getCmdCount())) {
            preparedCode.get(0).setOpCode(VMCommands.VM_RET);
        }
        int newBlockPos = getValue(false, this.mem, 245792) & VM_MEMMASK;
        int newBlockSize = 262143 & getValue(false, this.mem, 245788);
        if (newBlockPos + newBlockSize >= 262144) {
            newBlockPos = 0;
            newBlockSize = 0;
        }
        prg.setFilteredDataOffset(newBlockPos);
        prg.setFilteredDataSize(newBlockSize);
        prg.getGlobalData().clear();
        int dataSize = Math.min(getValue(false, this.mem, 245808), 8128);
        if (dataSize != 0) {
            prg.getGlobalData().setSize(dataSize + 64);
            for (int i4 = 0; i4 < dataSize + 64; i4++) {
                prg.getGlobalData().set(i4, Byte.valueOf(this.mem[i4 + VM_GLOBALMEMADDR]));
            }
        }
    }

    public byte[] getMem() {
        return this.mem;
    }

    private boolean setIP(int ip) {
        if (ip >= this.codeSize) {
            return true;
        }
        int i = this.maxOpCount - 1;
        this.maxOpCount = i;
        if (i <= 0) {
            return false;
        }
        this.IP = ip;
        return true;
    }

    private boolean ExecuteCode(List<VMPreparedCommand> preparedCode, int cmdCount) {
        int flag;
        int i;
        int flag2;
        int i2;
        int flag3;
        int flag4;
        int i3;
        int flag5;
        int i4;
        int flag6;
        this.maxOpCount = 25000000;
        this.codeSize = cmdCount;
        this.IP = 0;
        while (true) {
            VMPreparedCommand cmd = preparedCode.get(this.IP);
            int op1 = getOperand(cmd.getOp1());
            int op2 = getOperand(cmd.getOp2());
            switch (AnonymousClass1.$SwitchMap$com$github$junrar$unpack$vm$VMCommands[cmd.getOpCode().ordinal()]) {
                case 1:
                    setValue(cmd.isByteMode(), this.mem, op1, getValue(cmd.isByteMode(), this.mem, op2));
                    break;
                case 2:
                    setValue(true, this.mem, op1, getValue(true, this.mem, op2));
                    break;
                case 3:
                    setValue(false, this.mem, op1, getValue(false, this.mem, op2));
                    break;
                case 4:
                    int value1 = getValue(cmd.isByteMode(), this.mem, op1);
                    int result = value1 - getValue(cmd.isByteMode(), this.mem, op2);
                    if (result == 0) {
                        this.flags = VMFlags.VM_FZ.getFlag();
                    } else {
                        this.flags = result > value1 ? 1 : (VMFlags.VM_FS.getFlag() & result) | 0;
                    }
                    break;
                case 5:
                    int value2 = getValue(true, this.mem, op1);
                    int result2 = value2 - getValue(true, this.mem, op2);
                    if (result2 == 0) {
                        this.flags = VMFlags.VM_FZ.getFlag();
                    } else {
                        this.flags = result2 > value2 ? 1 : (VMFlags.VM_FS.getFlag() & result2) | 0;
                    }
                    break;
                case 6:
                    int value3 = getValue(false, this.mem, op1);
                    int result3 = value3 - getValue(false, this.mem, op2);
                    if (result3 == 0) {
                        this.flags = VMFlags.VM_FZ.getFlag();
                    } else {
                        this.flags = result3 > value3 ? 1 : (VMFlags.VM_FS.getFlag() & result3) | 0;
                    }
                    break;
                case 7:
                    int value4 = getValue(cmd.isByteMode(), this.mem, op1);
                    int result4 = (int) ((((long) value4) + ((long) getValue(cmd.isByteMode(), this.mem, op2))) & (-1));
                    if (cmd.isByteMode()) {
                        result4 &= 255;
                        if (result4 < value4) {
                            i2 = 1;
                        } else {
                            if (result4 == 0) {
                                flag2 = VMFlags.VM_FZ.getFlag();
                            } else if ((result4 & 128) == 0) {
                                flag2 = 0;
                            } else {
                                flag2 = VMFlags.VM_FS.getFlag();
                            }
                            i2 = flag2 | 0;
                        }
                        this.flags = i2;
                    } else {
                        if (result4 < value4) {
                            i = 1;
                        } else {
                            if (result4 == 0) {
                                flag = VMFlags.VM_FZ.getFlag();
                            } else {
                                flag = VMFlags.VM_FS.getFlag() & result4;
                            }
                            i = flag | 0;
                        }
                        this.flags = i;
                    }
                    setValue(cmd.isByteMode(), this.mem, op1, result4);
                    break;
                case 8:
                    setValue(true, this.mem, op1, (int) (((long) getValue(true, this.mem, op1)) & (((long) getValue(true, this.mem, op2)) - 1) & (-1)));
                    break;
                case 9:
                    setValue(false, this.mem, op1, (int) (((long) getValue(false, this.mem, op1)) & (((long) getValue(false, this.mem, op2)) - 1) & (-1)));
                    break;
                case 10:
                    int value5 = getValue(cmd.isByteMode(), this.mem, op1);
                    int result5 = (int) (((long) value5) & ((-1) - ((long) getValue(cmd.isByteMode(), this.mem, op2))) & (-1));
                    if (result5 == 0) {
                        flag3 = VMFlags.VM_FZ.getFlag();
                    } else {
                        flag3 = result5 > value5 ? 1 : (VMFlags.VM_FS.getFlag() & result5) | 0;
                    }
                    this.flags = flag3;
                    setValue(cmd.isByteMode(), this.mem, op1, result5);
                    break;
                case 11:
                    setValue(true, this.mem, op1, (int) (((long) getValue(true, this.mem, op1)) & ((-1) - ((long) getValue(true, this.mem, op2))) & (-1)));
                    break;
                case 12:
                    setValue(false, this.mem, op1, (int) (((long) getValue(false, this.mem, op1)) & ((-1) - ((long) getValue(false, this.mem, op2))) & (-1)));
                    break;
                case 13:
                    if ((this.flags & VMFlags.VM_FZ.getFlag()) != 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 14:
                    if ((this.flags & VMFlags.VM_FZ.getFlag()) == 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 15:
                    int result6 = (int) (((long) getValue(cmd.isByteMode(), this.mem, op1)) & 0);
                    if (cmd.isByteMode()) {
                        result6 &= 255;
                    }
                    setValue(cmd.isByteMode(), this.mem, op1, result6);
                    this.flags = result6 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result6;
                    break;
                case 16:
                    setValue(true, this.mem, op1, (int) (((long) getValue(true, this.mem, op1)) & 0));
                    break;
                case 17:
                    setValue(false, this.mem, op1, (int) (((long) getValue(false, this.mem, op1)) & 0));
                    break;
                case 18:
                    int result7 = (int) (((long) getValue(cmd.isByteMode(), this.mem, op1)) & (-2));
                    setValue(cmd.isByteMode(), this.mem, op1, result7);
                    this.flags = result7 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result7;
                    break;
                case 19:
                    setValue(true, this.mem, op1, (int) (((long) getValue(true, this.mem, op1)) & (-2)));
                    break;
                case 20:
                    setValue(false, this.mem, op1, (int) (((long) getValue(false, this.mem, op1)) & (-2)));
                    break;
                case 21:
                    setIP(getValue(false, this.mem, op1));
                    continue;
                case 22:
                    int result8 = getValue(cmd.isByteMode(), this.mem, op1) ^ getValue(cmd.isByteMode(), this.mem, op2);
                    this.flags = result8 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result8;
                    setValue(cmd.isByteMode(), this.mem, op1, result8);
                    break;
                case 23:
                    int result9 = getValue(cmd.isByteMode(), this.mem, op1) & getValue(cmd.isByteMode(), this.mem, op2);
                    this.flags = result9 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result9;
                    setValue(cmd.isByteMode(), this.mem, op1, result9);
                    break;
                case 24:
                    int result10 = getValue(cmd.isByteMode(), this.mem, op1) | getValue(cmd.isByteMode(), this.mem, op2);
                    this.flags = result10 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result10;
                    setValue(cmd.isByteMode(), this.mem, op1, result10);
                    break;
                case 25:
                    int result11 = getValue(cmd.isByteMode(), this.mem, op1) & getValue(cmd.isByteMode(), this.mem, op2);
                    this.flags = result11 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result11;
                    break;
                case 26:
                    if ((this.flags & VMFlags.VM_FS.getFlag()) != 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 27:
                    if ((this.flags & VMFlags.VM_FS.getFlag()) == 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 28:
                    if ((this.flags & VMFlags.VM_FC.getFlag()) != 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 29:
                    if ((this.flags & (VMFlags.VM_FC.getFlag() | VMFlags.VM_FZ.getFlag())) != 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 30:
                    if ((this.flags & (VMFlags.VM_FC.getFlag() | VMFlags.VM_FZ.getFlag())) == 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 31:
                    if ((this.flags & VMFlags.VM_FC.getFlag()) == 0) {
                        setIP(getValue(false, this.mem, op1));
                    }
                    break;
                case 32:
                    int[] iArr = this.R;
                    iArr[7] = iArr[7] - 4;
                    setValue(false, this.mem, this.R[7] & VM_MEMMASK, getValue(false, this.mem, op1));
                    break;
                case 33:
                    setValue(false, this.mem, op1, getValue(false, this.mem, this.R[7] & VM_MEMMASK));
                    int[] iArr2 = this.R;
                    iArr2[7] = iArr2[7] + 4;
                    break;
                case 34:
                    int[] iArr3 = this.R;
                    iArr3[7] = iArr3[7] - 4;
                    setValue(false, this.mem, this.R[7] & VM_MEMMASK, this.IP + 1);
                    setIP(getValue(false, this.mem, op1));
                    continue;
                case 35:
                    setValue(cmd.isByteMode(), this.mem, op1, ~getValue(cmd.isByteMode(), this.mem, op1));
                    break;
                case 36:
                    int value6 = getValue(cmd.isByteMode(), this.mem, op1);
                    int value7 = getValue(cmd.isByteMode(), this.mem, op2);
                    int result12 = value6 << value7;
                    int flag7 = result12 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result12;
                    if (((value6 << (value7 - 1)) & Integer.MIN_VALUE) == 0) {
                        flag4 = 0;
                    } else {
                        flag4 = VMFlags.VM_FC.getFlag();
                    }
                    this.flags = flag7 | flag4;
                    setValue(cmd.isByteMode(), this.mem, op1, result12);
                    break;
                case 37:
                    int value8 = getValue(cmd.isByteMode(), this.mem, op1);
                    int value9 = getValue(cmd.isByteMode(), this.mem, op2);
                    int result13 = value8 >>> value9;
                    this.flags = (result13 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result13) | ((value8 >>> (value9 - 1)) & VMFlags.VM_FC.getFlag());
                    setValue(cmd.isByteMode(), this.mem, op1, result13);
                    break;
                case 38:
                    int value10 = getValue(cmd.isByteMode(), this.mem, op1);
                    int value11 = getValue(cmd.isByteMode(), this.mem, op2);
                    int result14 = value10 >>> value11;
                    this.flags = (result14 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FS.getFlag() & result14) | ((value10 >>> (value11 - 1)) & VMFlags.VM_FC.getFlag());
                    setValue(cmd.isByteMode(), this.mem, op1, result14);
                    break;
                case 39:
                    int result15 = -getValue(cmd.isByteMode(), this.mem, op1);
                    this.flags = result15 == 0 ? VMFlags.VM_FZ.getFlag() : VMFlags.VM_FC.getFlag() | (VMFlags.VM_FS.getFlag() & result15);
                    setValue(cmd.isByteMode(), this.mem, op1, result15);
                    break;
                case 40:
                    setValue(true, this.mem, op1, -getValue(true, this.mem, op1));
                    break;
                case 41:
                    setValue(false, this.mem, op1, -getValue(false, this.mem, op1));
                    break;
                case 42:
                    int i5 = 0;
                    int SP = this.R[7] - 4;
                    while (i5 < 8) {
                        setValue(false, this.mem, SP & VM_MEMMASK, this.R[i5]);
                        i5++;
                        SP -= 4;
                    }
                    int[] iArr4 = this.R;
                    iArr4[7] = iArr4[7] - 32;
                    break;
                case 43:
                    int i6 = 0;
                    int SP2 = this.R[7];
                    while (i6 < 8) {
                        this.R[7 - i6] = getValue(false, this.mem, SP2 & VM_MEMMASK);
                        i6++;
                        SP2 += 4;
                    }
                    break;
                case 44:
                    int[] iArr5 = this.R;
                    iArr5[7] = iArr5[7] - 4;
                    setValue(false, this.mem, this.R[7] & VM_MEMMASK, this.flags);
                    break;
                case 45:
                    this.flags = getValue(false, this.mem, this.R[7] & VM_MEMMASK);
                    int[] iArr6 = this.R;
                    iArr6[7] = iArr6[7] + 4;
                    break;
                case 46:
                    setValue(false, this.mem, op1, getValue(true, this.mem, op2));
                    break;
                case 47:
                    setValue(false, this.mem, op1, (byte) getValue(true, this.mem, op2));
                    break;
                case 48:
                    int value12 = getValue(cmd.isByteMode(), this.mem, op1);
                    setValue(cmd.isByteMode(), this.mem, op1, getValue(cmd.isByteMode(), this.mem, op2));
                    setValue(cmd.isByteMode(), this.mem, op2, value12);
                    break;
                case 49:
                    setValue(cmd.isByteMode(), this.mem, op1, (int) (((long) getValue(cmd.isByteMode(), this.mem, op1)) & (((long) getValue(cmd.isByteMode(), this.mem, op2)) * (-1)) & (-1) & (-1)));
                    break;
                case 50:
                    int divider = getValue(cmd.isByteMode(), this.mem, op2);
                    if (divider != 0) {
                        setValue(cmd.isByteMode(), this.mem, op1, getValue(cmd.isByteMode(), this.mem, op1) / divider);
                    }
                    break;
                case 51:
                    int value13 = getValue(cmd.isByteMode(), this.mem, op1);
                    int FC = this.flags & VMFlags.VM_FC.getFlag();
                    int result16 = (int) (((long) value13) & (((long) getValue(cmd.isByteMode(), this.mem, op2)) - 1) & (((long) FC) - 1) & (-1));
                    if (cmd.isByteMode()) {
                        result16 &= 255;
                    }
                    if (result16 >= value13 && (result16 != value13 || FC == 0)) {
                        if (result16 == 0) {
                            flag5 = VMFlags.VM_FZ.getFlag();
                        } else {
                            flag5 = VMFlags.VM_FS.getFlag() & result16;
                        }
                        i3 = flag5 | 0;
                    } else {
                        i3 = 1;
                    }
                    this.flags = i3;
                    setValue(cmd.isByteMode(), this.mem, op1, result16);
                    break;
                case TomlParser.RULE_arrayValue /* 52 */:
                    int value14 = getValue(cmd.isByteMode(), this.mem, op1);
                    int FC2 = this.flags & VMFlags.VM_FC.getFlag();
                    int result17 = (int) (((long) value14) & ((-1) - ((long) getValue(cmd.isByteMode(), this.mem, op2))) & ((-1) - ((long) FC2)) & (-1));
                    if (cmd.isByteMode()) {
                        result17 &= 255;
                    }
                    if (result17 <= value14 && (result17 != value14 || FC2 == 0)) {
                        if (result17 == 0) {
                            flag6 = VMFlags.VM_FZ.getFlag();
                        } else {
                            flag6 = VMFlags.VM_FS.getFlag() & result17;
                        }
                        i4 = flag6 | 0;
                    } else {
                        i4 = 1;
                    }
                    this.flags = i4;
                    setValue(cmd.isByteMode(), this.mem, op1, result17);
                    break;
                case TomlParser.RULE_table /* 53 */:
                    if (this.R[7] >= 262144) {
                        return true;
                    }
                    setIP(getValue(false, this.mem, this.R[7] & VM_MEMMASK));
                    int[] iArr7 = this.R;
                    iArr7[7] = iArr7[7] + 4;
                    continue;
                    break;
                case TomlParser.RULE_standardTable /* 54 */:
                    ExecuteStandardFilter(VMStandardFilters.findFilter(cmd.getOp1().getData()));
                    break;
            }
            this.IP++;
            this.maxOpCount--;
        }
    }

    public void prepare(byte[] code, int codeSize, VMPreparedProgram prg) {
        int distance;
        int codeSize2 = codeSize;
        InitBitInput();
        int cpLength = Math.min(32768, codeSize2);
        for (int i = 0; i < cpLength; i++) {
            byte[] bArr = this.inBuf;
            bArr[i] = (byte) (bArr[i] | code[i]);
        }
        byte xorSum = 0;
        for (int i2 = 1; i2 < codeSize2; i2++) {
            xorSum = (byte) (code[i2] ^ xorSum);
        }
        faddbits(8);
        prg.setCmdCount(0);
        if (xorSum == code[0]) {
            VMStandardFilters filterType = IsStandardFilter(code, codeSize);
            if (filterType != VMStandardFilters.VMSF_NONE) {
                VMPreparedCommand curCmd = new VMPreparedCommand();
                curCmd.setOpCode(VMCommands.VM_STANDARD);
                curCmd.getOp1().setData(filterType.getFilter());
                curCmd.getOp1().setType(VMOpType.VM_OPNONE);
                curCmd.getOp2().setType(VMOpType.VM_OPNONE);
                codeSize2 = 0;
                prg.getCmd().add(curCmd);
                prg.setCmdCount(prg.getCmdCount() + 1);
            }
            int dataFlag = fgetbits();
            faddbits(1);
            if ((dataFlag & 32768) != 0) {
                long dataSize = ((long) ReadData(this)) & 0;
                for (int i3 = 0; this.inAddr < codeSize2 && i3 < dataSize; i3++) {
                    prg.getStaticData().add(Byte.valueOf((byte) (fgetbits() >>> 8)));
                    faddbits(8);
                }
            }
            while (this.inAddr < codeSize2) {
                VMPreparedCommand curCmd2 = new VMPreparedCommand();
                int data = fgetbits();
                if ((data & 32768) == 0) {
                    curCmd2.setOpCode(VMCommands.findVMCommand(data >>> 12));
                    faddbits(4);
                } else {
                    curCmd2.setOpCode(VMCommands.findVMCommand((data >>> 10) - 24));
                    faddbits(6);
                }
                if ((VMCmdFlags.VM_CmdFlags[curCmd2.getOpCode().getVMCommand()] & 4) != 0) {
                    curCmd2.setByteMode((fgetbits() >>> 15) == 1);
                    faddbits(1);
                } else {
                    curCmd2.setByteMode(false);
                }
                curCmd2.getOp1().setType(VMOpType.VM_OPNONE);
                curCmd2.getOp2().setType(VMOpType.VM_OPNONE);
                int opNum = VMCmdFlags.VM_CmdFlags[curCmd2.getOpCode().getVMCommand()] & 3;
                if (opNum > 0) {
                    decodeArg(curCmd2.getOp1(), curCmd2.isByteMode());
                    if (opNum == 2) {
                        decodeArg(curCmd2.getOp2(), curCmd2.isByteMode());
                    } else if (curCmd2.getOp1().getType() == VMOpType.VM_OPINT && (VMCmdFlags.VM_CmdFlags[curCmd2.getOpCode().getVMCommand()] & 24) != 0) {
                        int distance2 = curCmd2.getOp1().getData();
                        if (distance2 >= 256) {
                            distance = distance2 + InputDeviceCompat.SOURCE_ANY;
                        } else {
                            if (distance2 >= 136) {
                                distance2 -= 264;
                            } else if (distance2 >= 16) {
                                distance2 -= 8;
                            } else if (distance2 >= 8) {
                                distance2 -= 16;
                            }
                            distance = distance2 + prg.getCmdCount();
                        }
                        curCmd2.getOp1().setData(distance);
                    }
                }
                prg.setCmdCount(prg.getCmdCount() + 1);
                prg.getCmd().add(curCmd2);
            }
        }
        VMPreparedCommand curCmd3 = new VMPreparedCommand();
        curCmd3.setOpCode(VMCommands.VM_RET);
        curCmd3.getOp1().setType(VMOpType.VM_OPNONE);
        curCmd3.getOp2().setType(VMOpType.VM_OPNONE);
        prg.getCmd().add(curCmd3);
        prg.setCmdCount(prg.getCmdCount() + 1);
        if (codeSize2 != 0) {
            optimize(prg);
        }
    }

    private void decodeArg(VMPreparedOperand op, boolean byteMode) {
        int data = fgetbits();
        if ((32768 & data) != 0) {
            op.setType(VMOpType.VM_OPREG);
            op.setData((data >>> 12) & 7);
            op.setOffset(op.getData());
            faddbits(4);
            return;
        }
        if ((49152 & data) == 0) {
            op.setType(VMOpType.VM_OPINT);
            if (byteMode) {
                op.setData((data >>> 6) & 255);
                faddbits(10);
                return;
            } else {
                faddbits(2);
                op.setData(ReadData(this));
                return;
            }
        }
        op.setType(VMOpType.VM_OPREGMEM);
        if ((data & 8192) == 0) {
            op.setData((data >>> 10) & 7);
            op.setOffset(op.getData());
            op.setBase(0);
            faddbits(6);
            return;
        }
        if ((data & 4096) == 0) {
            op.setData((data >>> 9) & 7);
            op.setOffset(op.getData());
            faddbits(7);
        } else {
            op.setData(0);
            faddbits(4);
        }
        op.setBase(ReadData(this));
    }

    /* JADX WARN: Code duplicated, block: B:37:0x0099  */
    /* JADX WARN: Code duplicated, block: B:38:0x009c  */
    /* JADX WARN: Code duplicated, block: B:42:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:43:0x00ac  */
    /* JADX WARN: Code duplicated, block: B:47:0x00b9  */
    /* JADX WARN: Code duplicated, block: B:48:0x00bc  */
    /* JADX WARN: Code duplicated, block: B:52:0x00c9  */
    /* JADX WARN: Code duplicated, block: B:53:0x00cc  */
    /* JADX WARN: Code duplicated, block: B:57:0x00d9  */
    /* JADX WARN: Code duplicated, block: B:58:0x00dc  */
    /* JADX WARN: Code duplicated, block: B:65:0x0082 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:66:0x0093 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x00a3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:68:0x00b3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:69:0x00c3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:70:0x00d3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:76:0x0008 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:77:0x0008 A[DONT_GENERATE, SYNTHETIC] */
    private void optimize(VMPreparedProgram prg) {
        VMCommands vMCommands;
        VMCommands vMCommands2;
        VMCommands vMCommands3;
        VMCommands vMCommands4;
        VMCommands vMCommands5;
        int flags;
        List<VMPreparedCommand> commands = prg.getCmd();
        for (VMPreparedCommand cmd : commands) {
            switch (cmd.getOpCode()) {
                case VM_MOV:
                    cmd.setOpCode(cmd.isByteMode() ? VMCommands.VM_MOVB : VMCommands.VM_MOVD);
                    break;
                case VM_CMP:
                    cmd.setOpCode(cmd.isByteMode() ? VMCommands.VM_CMPB : VMCommands.VM_CMPD);
                    break;
                default:
                    if ((VMCmdFlags.VM_CmdFlags[cmd.getOpCode().getVMCommand()] & VMCmdFlags.VMCF_CHFLAGS) == 0) {
                        break;
                    } else {
                        boolean flagsRequired = false;
                        int i = commands.indexOf(cmd);
                        do {
                            i++;
                            if (i < commands.size()) {
                                flags = VMCmdFlags.VM_CmdFlags[commands.get(i).getOpCode().getVMCommand()];
                                if ((flags & 56) != 0) {
                                    flagsRequired = true;
                                }
                            }
                            if (flagsRequired) {
                                break;
                            } else {
                                switch (cmd.getOpCode()) {
                                    case 7:
                                        if (cmd.isByteMode()) {
                                            vMCommands = VMCommands.VM_ADDB;
                                        } else {
                                            vMCommands = VMCommands.VM_ADDD;
                                        }
                                        cmd.setOpCode(vMCommands);
                                        break;
                                    case 10:
                                        if (cmd.isByteMode()) {
                                            vMCommands2 = VMCommands.VM_SUBB;
                                        } else {
                                            vMCommands2 = VMCommands.VM_SUBD;
                                        }
                                        cmd.setOpCode(vMCommands2);
                                        break;
                                    case 15:
                                        if (cmd.isByteMode()) {
                                            vMCommands3 = VMCommands.VM_INCB;
                                        } else {
                                            vMCommands3 = VMCommands.VM_INCD;
                                        }
                                        cmd.setOpCode(vMCommands3);
                                        break;
                                    case 18:
                                        if (cmd.isByteMode()) {
                                            vMCommands4 = VMCommands.VM_DECB;
                                        } else {
                                            vMCommands4 = VMCommands.VM_DECD;
                                        }
                                        cmd.setOpCode(vMCommands4);
                                        break;
                                    case 39:
                                        if (cmd.isByteMode()) {
                                            vMCommands5 = VMCommands.VM_NEGB;
                                        } else {
                                            vMCommands5 = VMCommands.VM_NEGD;
                                        }
                                        cmd.setOpCode(vMCommands5);
                                        break;
                                }
                            }
                        } while ((flags & 64) == 0);
                        if (flagsRequired) {
                            break;
                        } else {
                            switch (cmd.getOpCode()) {
                                case VM_ADD:
                                    if (cmd.isByteMode()) {
                                        vMCommands = VMCommands.VM_ADDB;
                                    } else {
                                        vMCommands = VMCommands.VM_ADDD;
                                    }
                                    cmd.setOpCode(vMCommands);
                                    break;
                                case VM_SUB:
                                    if (cmd.isByteMode()) {
                                        vMCommands2 = VMCommands.VM_SUBB;
                                    } else {
                                        vMCommands2 = VMCommands.VM_SUBD;
                                    }
                                    cmd.setOpCode(vMCommands2);
                                    break;
                                case VM_INC:
                                    if (cmd.isByteMode()) {
                                        vMCommands3 = VMCommands.VM_INCB;
                                    } else {
                                        vMCommands3 = VMCommands.VM_INCD;
                                    }
                                    cmd.setOpCode(vMCommands3);
                                    break;
                                case VM_DEC:
                                    if (cmd.isByteMode()) {
                                        vMCommands4 = VMCommands.VM_DECB;
                                    } else {
                                        vMCommands4 = VMCommands.VM_DECD;
                                    }
                                    cmd.setOpCode(vMCommands4);
                                    break;
                                case VM_NEG:
                                    if (cmd.isByteMode()) {
                                        vMCommands5 = VMCommands.VM_NEGB;
                                    } else {
                                        vMCommands5 = VMCommands.VM_NEGD;
                                    }
                                    cmd.setOpCode(vMCommands5);
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    public static int ReadData(BitInput rarVM) {
        int data = rarVM.fgetbits();
        switch (49152 & data) {
            case 0:
                rarVM.faddbits(6);
                return (data >>> 10) & 15;
            case 16384:
                if ((data & 15360) == 0) {
                    int data2 = ((data >>> 2) & 255) | InputDeviceCompat.SOURCE_ANY;
                    rarVM.faddbits(14);
                    return data2;
                }
                int data3 = (data >>> 6) & 255;
                rarVM.faddbits(10);
                return data3;
            case 32768:
                rarVM.faddbits(2);
                int data4 = rarVM.fgetbits();
                rarVM.faddbits(16);
                return data4;
            default:
                rarVM.faddbits(2);
                int data5 = rarVM.fgetbits() << 16;
                rarVM.faddbits(16);
                int data6 = data5 | rarVM.fgetbits();
                rarVM.faddbits(16);
                return data6;
        }
    }

    private VMStandardFilters IsStandardFilter(byte[] code, int codeSize) {
        VMStandardFilterSignature[] stdList = {new VMStandardFilterSignature(53, -1386780537, VMStandardFilters.VMSF_E8), new VMStandardFilterSignature(57, 1020781950, VMStandardFilters.VMSF_E8E9), new VMStandardFilterSignature(120, 929663295, VMStandardFilters.VMSF_ITANIUM), new VMStandardFilterSignature(29, 235276157, VMStandardFilters.VMSF_DELTA), new VMStandardFilterSignature(149, 472669640, VMStandardFilters.VMSF_RGB), new VMStandardFilterSignature(216, -1132075263, VMStandardFilters.VMSF_AUDIO), new VMStandardFilterSignature(40, 1186579808, VMStandardFilters.VMSF_UPCASE)};
        int CodeCRC = ~RarCRC.checkCrc(-1, code, 0, code.length);
        for (int i = 0; i < stdList.length; i++) {
            if (stdList[i].getCRC() == CodeCRC && stdList[i].getLength() == code.length) {
                return stdList[i].getType();
            }
        }
        return VMStandardFilters.VMSF_NONE;
    }

    private void ExecuteStandardFilter(VMStandardFilters filterType) {
        byte cmdMask;
        int i;
        int dataSize;
        int width;
        int posR;
        int channels;
        long predicted;
        int srcPos;
        int i2 = 2;
        char c = 4;
        char c2 = 0;
        switch (filterType) {
            case VMSF_E8:
            case VMSF_E8E9:
                int dataSize2 = this.R[4];
                long fileOffset = this.R[6] & (-1);
                if (dataSize2 < 245760) {
                    byte cmpByte2 = (byte) (filterType == VMStandardFilters.VMSF_E8E9 ? 233 : 232);
                    int curPos = 0;
                    while (curPos < dataSize2 - 4) {
                        int curPos2 = curPos + 1;
                        byte curByte = this.mem[curPos];
                        if (curByte == -24 || curByte == cmpByte2) {
                            long offset = ((long) curPos2) + fileOffset;
                            long Addr = getValue(false, this.mem, curPos2);
                            if ((Addr & (-2147483648L)) != 0) {
                                if (((Addr + offset) & (-2147483648L)) == 0) {
                                    setValue(false, this.mem, curPos2, ((int) Addr) + 16777216);
                                }
                            } else if (((Addr - ((long) 16777216)) & (-2147483648L)) != 0) {
                                setValue(false, this.mem, curPos2, (int) (Addr - offset));
                            }
                            curPos = curPos2 + 4;
                        } else {
                            curPos = curPos2;
                        }
                    }
                }
                break;
            case VMSF_ITANIUM:
                int i3 = 2;
                int i4 = 4;
                int i5 = 5;
                int dataSize3 = this.R[4];
                long fileOffset2 = this.R[6] & (-1);
                if (dataSize3 < 245760) {
                    int curPos3 = 0;
                    byte[] Masks = {4, 4, 6, 6, 0, 0, 7, 7, 4, 4, 0, 0, 4, 4, 0, 0};
                    long fileOffset3 = fileOffset2 >>> 4;
                    while (curPos3 < dataSize3 - 21) {
                        int Byte = (this.mem[curPos3] & 31) - 16;
                        if (Byte >= 0 && (cmdMask = Masks[Byte]) != 0) {
                            int i6 = 0;
                            while (i6 <= i3) {
                                if (((1 << i6) & cmdMask) == 0) {
                                    i = i5;
                                } else {
                                    int startPos = (i6 * 41) + 5;
                                    int opType = filterItanium_GetBits(curPos3, startPos + 37, i4);
                                    i = i5;
                                    if (opType == i) {
                                        filterItanium_SetBits(curPos3, ((int) (((long) filterItanium_GetBits(curPos3, startPos + 13, 20)) - fileOffset3)) & 1048575, startPos + 13, 20);
                                    }
                                }
                                i6++;
                                i5 = i;
                                i4 = 4;
                                i3 = 2;
                            }
                        }
                        curPos3 += 16;
                        fileOffset3++;
                        i5 = i5;
                        i4 = 4;
                        i3 = 2;
                    }
                }
                break;
            case VMSF_DELTA:
                int dataSize4 = this.R[4] & (-1);
                int channels2 = this.R[0] & (-1);
                int srcPos2 = 0;
                int border = (dataSize4 * 2) & (-1);
                setValue(false, this.mem, 245792, dataSize4);
                if (dataSize4 < 122880) {
                    for (int curChannel = 0; curChannel < channels2; curChannel++) {
                        byte PrevByte = 0;
                        int destPos = dataSize4 + curChannel;
                        while (destPos < border) {
                            byte[] bArr = this.mem;
                            int srcPos3 = srcPos2 + 1;
                            byte b = (byte) (PrevByte - this.mem[srcPos2]);
                            PrevByte = b;
                            bArr[destPos] = b;
                            destPos += channels2;
                            srcPos2 = srcPos3;
                        }
                    }
                }
                break;
            case VMSF_RGB:
                int i7 = 3;
                int dataSize5 = this.R[4];
                int srcPos4 = this.R[0] - 3;
                int posR2 = this.R[1];
                int channels3 = 3;
                int srcPos5 = 0;
                setValue(false, this.mem, 245792, dataSize5);
                if (dataSize5 < 122880 && posR2 >= 0) {
                    int curChannel2 = 0;
                    while (curChannel2 < channels3) {
                        long prevByte = 0;
                        int i8 = curChannel2;
                        while (i8 < dataSize5) {
                            int upperPos = i8 - srcPos4;
                            if (upperPos >= i7) {
                                int upperDataPos = dataSize5 + upperPos;
                                int upperByte = this.mem[upperDataPos] & UByte.MAX_VALUE;
                                int upperLeftByte = this.mem[upperDataPos - 3] & UByte.MAX_VALUE;
                                dataSize = dataSize5;
                                width = srcPos4;
                                long predicted2 = (((long) upperByte) + prevByte) - ((long) upperLeftByte);
                                int pa = Math.abs((int) (predicted2 - prevByte));
                                posR = posR2;
                                int pb = Math.abs((int) (predicted2 - ((long) upperByte)));
                                channels = channels3;
                                int pc = Math.abs((int) (predicted2 - ((long) upperLeftByte)));
                                if (pa <= pb && pa <= pc) {
                                    predicted = prevByte;
                                } else if (pb <= pc) {
                                    predicted = upperByte;
                                } else {
                                    predicted = upperLeftByte;
                                }
                            } else {
                                dataSize = dataSize5;
                                width = srcPos4;
                                posR = posR2;
                                channels = channels3;
                                predicted = prevByte;
                            }
                            prevByte = (predicted - ((long) this.mem[srcPos5])) & 255 & 255;
                            this.mem[dataSize5 + i8] = (byte) (prevByte & 255);
                            i8 += channels;
                            srcPos5++;
                            dataSize5 = dataSize;
                            srcPos4 = width;
                            posR2 = posR;
                            channels3 = channels;
                            i7 = 3;
                        }
                        curChannel2++;
                        i7 = 3;
                    }
                    int dataSize6 = dataSize5;
                    int border2 = dataSize6 - 2;
                    for (int i9 = posR2; i9 < border2; i9 += 3) {
                        byte G = this.mem[dataSize5 + i9 + 1];
                        byte[] bArr2 = this.mem;
                        int i10 = dataSize5 + i9;
                        bArr2[i10] = (byte) (bArr2[i10] + G);
                        byte[] bArr3 = this.mem;
                        int i11 = dataSize5 + i9 + 2;
                        bArr3[i11] = (byte) (bArr3[i11] + G);
                    }
                }
                break;
            case VMSF_AUDIO:
                int dataSize7 = this.R[4];
                int channels4 = this.R[0];
                int srcPos6 = 0;
                setValue(false, this.mem, 245792, dataSize7);
                if (dataSize7 < 122880) {
                    int curChannel3 = 0;
                    while (curChannel3 < channels4) {
                        long prevByte2 = 0;
                        long[] Dif = new long[7];
                        int srcPos7 = 0;
                        int D2 = 0;
                        int i12 = curChannel3;
                        int byteCount = 0;
                        int K3 = i2;
                        int K4 = 0;
                        char c3 = c;
                        long prevDelta = 0;
                        int K1 = 0;
                        int K2 = 0;
                        int i13 = i12;
                        while (i13 < dataSize7) {
                            int D3 = D2;
                            char c4 = c2;
                            D2 = ((int) prevDelta) - srcPos7;
                            int D1 = (int) prevDelta;
                            int dataSize8 = dataSize7;
                            int channels5 = channels4;
                            long predicted3 = (8 * prevByte2) + ((long) (K1 * D1)) + ((long) (K2 * D2)) + ((long) (K4 * D3));
                            int srcPos8 = srcPos6 + 1;
                            long predicted4 = this.mem[srcPos6] & 255;
                            long predicted5 = (((predicted3 >>> 3) & 255) - predicted4) & (-1);
                            int curChannel4 = curChannel3;
                            this.mem[dataSize7 + i13] = (byte) predicted5;
                            prevDelta = (byte) (predicted5 - prevByte2);
                            prevByte2 = predicted5;
                            int D = ((byte) predicted4) << 3;
                            Dif[c4] = Dif[c4] + ((long) Math.abs(D));
                            Dif[1] = Dif[1] + ((long) Math.abs(D - D1));
                            Dif[K3] = Dif[K3] + ((long) Math.abs(D + D1));
                            Dif[3] = Dif[3] + ((long) Math.abs(D - D2));
                            Dif[c3] = Dif[c3] + ((long) Math.abs(D + D2));
                            Dif[5] = Dif[5] + ((long) Math.abs(D - D3));
                            Dif[6] = Dif[6] + ((long) Math.abs(D + D3));
                            if ((byteCount & 31) == 0) {
                                long minDif = Dif[c4];
                                long numMinDif = 0;
                                Dif[c4] = 0;
                                long minDif2 = minDif;
                                for (int j = 1; j < Dif.length; j++) {
                                    if (Dif[j] < minDif2) {
                                        minDif2 = Dif[j];
                                        numMinDif = j;
                                    }
                                    Dif[j] = 0;
                                }
                                int j2 = (int) numMinDif;
                                switch (j2) {
                                    case 1:
                                        if (K1 >= -16) {
                                            K1--;
                                        }
                                        break;
                                    case 2:
                                        if (K1 < 16) {
                                            K1++;
                                        }
                                        break;
                                    case 3:
                                        if (K2 >= -16) {
                                            K2--;
                                        }
                                        break;
                                    case 4:
                                        if (K2 < 16) {
                                            K2++;
                                        }
                                        break;
                                    case 5:
                                        if (K4 >= -16) {
                                            K4--;
                                        }
                                        break;
                                    case 6:
                                        if (K4 < 16) {
                                            K4++;
                                        }
                                        break;
                                }
                            }
                            i13 += channels5;
                            byteCount++;
                            srcPos6 = srcPos8;
                            c2 = c4;
                            curChannel3 = curChannel4;
                            dataSize7 = dataSize8;
                            channels4 = channels5;
                            srcPos7 = D1;
                        }
                        curChannel3++;
                        c = c3;
                        i2 = K3;
                    }
                }
                break;
            case VMSF_UPCASE:
                int dataSize9 = this.R[4];
                int srcPos9 = 0;
                int destPos2 = dataSize9;
                if (dataSize9 < 122880) {
                    while (srcPos9 < dataSize9) {
                        int srcPos10 = srcPos9 + 1;
                        byte curByte2 = this.mem[srcPos9];
                        if (curByte2 == 2) {
                            srcPos = srcPos10 + 1;
                            byte b2 = this.mem[srcPos10];
                            curByte2 = b2;
                            if (b2 != 2) {
                                curByte2 = (byte) (curByte2 - 32);
                            }
                        } else {
                            srcPos = srcPos10;
                        }
                        this.mem[destPos2] = curByte2;
                        destPos2++;
                        srcPos9 = srcPos;
                    }
                    setValue(false, this.mem, 245788, destPos2 - dataSize9);
                    setValue(false, this.mem, 245792, dataSize9);
                }
                break;
        }
    }

    private void filterItanium_SetBits(int curPos, int bitField, int bitPos, int bitCount) {
        int inAddr = bitPos / 8;
        int inBit = bitPos & 7;
        int andMask = (-1) >>> (32 - bitCount);
        int andMask2 = ~(andMask << inBit);
        int bitField2 = bitField << inBit;
        for (int i = 0; i < 4; i++) {
            byte[] bArr = this.mem;
            int i2 = curPos + inAddr + i;
            bArr[i2] = (byte) (bArr[i2] & andMask2);
            byte[] bArr2 = this.mem;
            int i3 = curPos + inAddr + i;
            bArr2[i3] = (byte) (bArr2[i3] | bitField2);
            andMask2 = (andMask2 >>> 8) | ViewCompat.MEASURED_STATE_MASK;
            bitField2 >>>= 8;
        }
    }

    private int filterItanium_GetBits(int curPos, int bitPos, int bitCount) {
        int inAddr = bitPos / 8;
        int inBit = bitPos & 7;
        int inAddr2 = inAddr + 1;
        int bitField = this.mem[inAddr + curPos] & UByte.MAX_VALUE;
        int inAddr3 = inAddr2 + 1;
        return ((-1) >>> (32 - bitCount)) & ((((bitField | ((this.mem[inAddr2 + curPos] & UByte.MAX_VALUE) << 8)) | ((this.mem[inAddr3 + curPos] & UByte.MAX_VALUE) << 16)) | ((this.mem[curPos + (inAddr3 + 1)] & UByte.MAX_VALUE) << 24)) >>> inBit);
    }

    public void setMemory(int pos, byte[] data, int offset, int dataSize) {
        if (pos < 262144) {
            for (int i = 0; i < Math.min(data.length - offset, dataSize) && 262144 - pos >= i; i++) {
                this.mem[pos + i] = data[offset + i];
            }
        }
    }
}
