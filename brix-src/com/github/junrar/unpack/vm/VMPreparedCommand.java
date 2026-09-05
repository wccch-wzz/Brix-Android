package com.github.junrar.unpack.vm;

/* JADX INFO: loaded from: classes.dex */
public class VMPreparedCommand {
    private boolean ByteMode;
    private VMPreparedOperand Op1 = new VMPreparedOperand();
    private VMPreparedOperand Op2 = new VMPreparedOperand();
    private VMCommands OpCode;

    public boolean isByteMode() {
        return this.ByteMode;
    }

    public void setByteMode(boolean byteMode) {
        this.ByteMode = byteMode;
    }

    public VMPreparedOperand getOp1() {
        return this.Op1;
    }

    public void setOp1(VMPreparedOperand op1) {
        this.Op1 = op1;
    }

    public VMPreparedOperand getOp2() {
        return this.Op2;
    }

    public void setOp2(VMPreparedOperand op2) {
        this.Op2 = op2;
    }

    public VMCommands getOpCode() {
        return this.OpCode;
    }

    public void setOpCode(VMCommands opCode) {
        this.OpCode = opCode;
    }
}
