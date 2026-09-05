package com.github.junrar.unpack;

import com.github.junrar.unpack.vm.VMPreparedProgram;

/* JADX INFO: loaded from: classes.dex */
public class UnpackFilter {
    private int BlockLength;
    private int BlockStart;
    private int ExecCount;
    private boolean NextWindow;
    private int ParentFilter;
    private VMPreparedProgram Prg = new VMPreparedProgram();

    public int getBlockLength() {
        return this.BlockLength;
    }

    public void setBlockLength(int blockLength) {
        this.BlockLength = blockLength;
    }

    public int getBlockStart() {
        return this.BlockStart;
    }

    public void setBlockStart(int blockStart) {
        this.BlockStart = blockStart;
    }

    public int getExecCount() {
        return this.ExecCount;
    }

    public void setExecCount(int execCount) {
        this.ExecCount = execCount;
    }

    public boolean isNextWindow() {
        return this.NextWindow;
    }

    public void setNextWindow(boolean nextWindow) {
        this.NextWindow = nextWindow;
    }

    public int getParentFilter() {
        return this.ParentFilter;
    }

    public void setParentFilter(int parentFilter) {
        this.ParentFilter = parentFilter;
    }

    public VMPreparedProgram getPrg() {
        return this.Prg;
    }

    public void setPrg(VMPreparedProgram prg) {
        this.Prg = prg;
    }
}
