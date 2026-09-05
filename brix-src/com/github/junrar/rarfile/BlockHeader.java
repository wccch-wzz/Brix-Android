package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class BlockHeader extends BaseBlock {
    public static final short blockHeaderSize = 4;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) BlockHeader.class);
    private long dataSize;
    private long packSize;

    public BlockHeader() {
    }

    public BlockHeader(BlockHeader bh) {
        super(bh);
        this.packSize = bh.getDataSize();
        this.dataSize = this.packSize;
        this.positionInFile = bh.getPositionInFile();
    }

    public BlockHeader(BaseBlock bb, byte[] blockHeader) {
        super(bb);
        this.packSize = Raw.readIntLittleEndianAsLong(blockHeader, 0);
        this.dataSize = this.packSize;
    }

    public long getDataSize() {
        return this.dataSize;
    }

    public long getPackSize() {
        return this.packSize;
    }

    @Override // com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            logger.info("DataSize: {} packSize: {}", Long.valueOf(getDataSize()), Long.valueOf(getPackSize()));
        }
    }
}
