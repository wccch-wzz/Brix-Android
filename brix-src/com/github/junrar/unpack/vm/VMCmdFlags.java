package com.github.junrar.unpack.vm;

import org.lwjgl.system.linux.liburing.LibIOURing;

/* JADX INFO: loaded from: classes.dex */
public class VMCmdFlags {
    public static final byte VMCF_BYTEMODE = 4;
    public static final byte VMCF_JUMP = 8;
    public static final byte VMCF_OP0 = 0;
    public static final byte VMCF_OP1 = 1;
    public static final byte VMCF_OP2 = 2;
    public static final byte VMCF_OPMASK = 3;
    public static final byte VMCF_PROC = 16;
    public static final byte VMCF_USEFLAGS = 32;
    public static final byte VMCF_CHFLAGS = 64;
    public static byte[] VM_CmdFlags = {6, 70, 70, 70, LibIOURing.IORING_OP_FSETXATTR, LibIOURing.IORING_OP_FSETXATTR, 69, 69, 9, 70, 70, 70, 70, LibIOURing.IORING_OP_FSETXATTR, LibIOURing.IORING_OP_FSETXATTR, LibIOURing.IORING_OP_FSETXATTR, LibIOURing.IORING_OP_FSETXATTR, LibIOURing.IORING_OP_FSETXATTR, LibIOURing.IORING_OP_FSETXATTR, 1, 1, 17, 16, 5, 70, 70, 70, 69, 0, 0, 32, VMCF_CHFLAGS, 2, 2, 6, 6, 6, 102, 102, 0};
}
