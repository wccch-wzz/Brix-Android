package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;

/* JADX INFO: loaded from: classes.dex */
public class AVHeader extends BaseBlock {
    public static final int avHeaderSize = 7;
    private final int avInfoCRC;
    private byte avVersion;
    private byte method;
    private byte unpackVersion;

    public AVHeader(BaseBlock bb, byte[] avHeader) {
        super(bb);
        this.unpackVersion = (byte) (this.unpackVersion | (avHeader[0] & 255));
        int pos = 0 + 1;
        this.method = (byte) (this.method | (avHeader[pos] & 255));
        int pos2 = pos + 1;
        this.avVersion = (byte) (this.avVersion | (avHeader[pos2] & 255));
        this.avInfoCRC = Raw.readIntLittleEndian(avHeader, pos2 + 1);
    }

    public int getAvInfoCRC() {
        return this.avInfoCRC;
    }

    public byte getAvVersion() {
        return this.avVersion;
    }

    public byte getMethod() {
        return this.method;
    }

    public byte getUnpackVersion() {
        return this.unpackVersion;
    }
}
