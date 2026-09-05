package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import kotlin.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class SubBlockHeader extends BlockHeader {
    public static final short SubBlockHeaderSize = 3;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) SubBlockHeader.class);
    private byte level;
    private final short subType;

    public SubBlockHeader(SubBlockHeader sb) {
        super(sb);
        this.subType = sb.getSubType().getSubblocktype();
        this.level = sb.getLevel();
    }

    public SubBlockHeader(BlockHeader bh, byte[] subblock) {
        super(bh);
        this.subType = Raw.readShortLittleEndian(subblock, 0);
        int position = 0 + 2;
        this.level = (byte) (this.level | (subblock[position] & UByte.MAX_VALUE));
    }

    public byte getLevel() {
        return this.level;
    }

    public SubBlockHeaderType getSubType() {
        return SubBlockHeaderType.findSubblockHeaderType(this.subType);
    }

    @Override // com.github.junrar.rarfile.BlockHeader, com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            logger.info("subtype: {}", getSubType());
            logger.info("level: {}", Byte.valueOf(this.level));
        }
    }
}
