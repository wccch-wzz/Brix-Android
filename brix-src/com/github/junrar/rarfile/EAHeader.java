package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import kotlin.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class EAHeader extends SubBlockHeader {
    public static final short EAHeaderSize = 10;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) EAHeader.class);
    private final int EACRC;
    private byte method;
    private final int unpSize;
    private byte unpVer;

    public EAHeader(SubBlockHeader sb, byte[] eahead) {
        super(sb);
        this.unpSize = Raw.readIntLittleEndian(eahead, 0);
        int pos = 0 + 4;
        this.unpVer = (byte) (this.unpVer | (eahead[pos] & UByte.MAX_VALUE));
        int pos2 = pos + 1;
        this.method = (byte) (this.method | (eahead[pos2] & UByte.MAX_VALUE));
        this.EACRC = Raw.readIntLittleEndian(eahead, pos2 + 1);
    }

    public int getEACRC() {
        return this.EACRC;
    }

    public byte getMethod() {
        return this.method;
    }

    public int getUnpSize() {
        return this.unpSize;
    }

    public byte getUnpVer() {
        return this.unpVer;
    }

    @Override // com.github.junrar.rarfile.SubBlockHeader, com.github.junrar.rarfile.BlockHeader, com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            logger.info("unpSize: {}", Integer.valueOf(this.unpSize));
            logger.info("unpVersion: {}", Byte.valueOf(this.unpVer));
            logger.info("method: {}", Byte.valueOf(this.method));
            logger.info("EACRC: {}", Integer.valueOf(this.EACRC));
        }
    }
}
