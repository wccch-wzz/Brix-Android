package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;

/* JADX INFO: loaded from: classes.dex */
public class SignHeader extends BaseBlock {
    public static final short signHeaderSize = 8;
    private short arcNameSize;
    private int creationTime;
    private short userNameSize;

    public SignHeader(BaseBlock bb, byte[] signHeader) {
        super(bb);
        this.creationTime = 0;
        this.arcNameSize = (short) 0;
        this.userNameSize = (short) 0;
        this.creationTime = Raw.readIntLittleEndian(signHeader, 0);
        int pos = 0 + 4;
        this.arcNameSize = Raw.readShortLittleEndian(signHeader, pos);
        this.userNameSize = Raw.readShortLittleEndian(signHeader, pos + 2);
    }

    public short getArcNameSize() {
        return this.arcNameSize;
    }

    public int getCreationTime() {
        return this.creationTime;
    }

    public short getUserNameSize() {
        return this.userNameSize;
    }
}
