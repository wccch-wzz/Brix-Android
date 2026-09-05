package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class MacInfoHeader extends SubBlockHeader {
    public static final short MacInfoHeaderSize = 8;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) MacInfoHeader.class);
    private int fileCreator;
    private int fileType;

    public MacInfoHeader(SubBlockHeader sb, byte[] macHeader) {
        super(sb);
        this.fileType = Raw.readIntLittleEndian(macHeader, 0);
        int pos = 0 + 4;
        this.fileCreator = Raw.readIntLittleEndian(macHeader, pos);
    }

    public int getFileCreator() {
        return this.fileCreator;
    }

    public void setFileCreator(int fileCreator) {
        this.fileCreator = fileCreator;
    }

    public int getFileType() {
        return this.fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    @Override // com.github.junrar.rarfile.SubBlockHeader, com.github.junrar.rarfile.BlockHeader, com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            logger.info("filetype: {}", Integer.valueOf(this.fileType));
            logger.info("creator: {}", Integer.valueOf(this.fileCreator));
        }
    }
}
