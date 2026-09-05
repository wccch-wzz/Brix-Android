package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;

/* JADX INFO: loaded from: classes.dex */
public class EndArcHeader extends BaseBlock {
    public static final short endArcArchiveDataCrcSize = 4;
    public static final short endArcVolumeNumberSize = 2;
    private int archiveDataCRC;
    private short volumeNumber;

    public EndArcHeader(BaseBlock bb, byte[] endArcHeader) {
        super(bb);
        int pos = 0;
        if (hasArchiveDataCRC()) {
            this.archiveDataCRC = Raw.readIntLittleEndian(endArcHeader, 0);
            pos = 0 + 4;
        }
        if (hasVolumeNumber()) {
            this.volumeNumber = Raw.readShortLittleEndian(endArcHeader, pos);
        }
    }

    public boolean isValid() {
        return getHeadCRC() == 15812 && getHeaderType() == UnrarHeadertype.EndArcHeader && getFlags() == 16384 && getHeaderSize(false) == 7;
    }

    public int getArchiveDataCRC() {
        return this.archiveDataCRC;
    }

    public short getVolumeNumber() {
        return this.volumeNumber;
    }
}
