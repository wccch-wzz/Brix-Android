package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class CommentHeader extends BaseBlock {
    public static final short commentHeaderSize = 6;
    private final short commCRC;
    private byte unpMethod;
    private final short unpSize;
    private byte unpVersion;

    public CommentHeader(BaseBlock bb, byte[] commentHeader) {
        super(bb);
        this.unpSize = Raw.readShortLittleEndian(commentHeader, 0);
        int pos = 0 + 2;
        this.unpVersion = (byte) (this.unpVersion | (commentHeader[pos] & UByte.MAX_VALUE));
        int pos2 = pos + 1;
        this.unpMethod = (byte) (this.unpMethod | (commentHeader[pos2] & UByte.MAX_VALUE));
        this.commCRC = Raw.readShortLittleEndian(commentHeader, pos2 + 1);
    }

    public short getCommCRC() {
        return this.commCRC;
    }

    public byte getUnpMethod() {
        return this.unpMethod;
    }

    public short getUnpSize() {
        return this.unpSize;
    }

    public byte getUnpVersion() {
        return this.unpVersion;
    }
}
