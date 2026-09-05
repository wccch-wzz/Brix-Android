package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import kotlin.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class BaseBlock {
    public static final short BaseBlockSize = 7;
    public static final short EARC_DATACRC = 2;
    public static final short EARC_NEXT_VOLUME = 1;
    public static final short EARC_REVSPACE = 4;
    public static final short EARC_VOLNUMBER = 8;
    public static final short LHD_COMMENT = 8;
    public static final short LHD_DIRECTORY = 224;
    public static final short LHD_EXTFLAGS = 8192;
    public static final short LHD_EXTTIME = 4096;
    public static final short LHD_LARGE = 256;
    public static final short LHD_PASSWORD = 4;
    public static final short LHD_SALT = 1024;
    public static final short LHD_SOLID = 16;
    public static final short LHD_SPLIT_AFTER = 2;
    public static final short LHD_SPLIT_BEFORE = 1;
    public static final short LHD_UNICODE = 512;
    public static final short LHD_VERSION = 2048;
    public static final short LHD_WINDOW1024 = 128;
    public static final short LHD_WINDOW128 = 32;
    public static final short LHD_WINDOW2048 = 160;
    public static final short LHD_WINDOW256 = 64;
    public static final short LHD_WINDOW4096 = 192;
    public static final short LHD_WINDOW512 = 96;
    public static final short LHD_WINDOW64 = 0;
    public static final short LHD_WINDOWMASK = 224;
    public static final short LONG_BLOCK = Short.MIN_VALUE;
    public static final short MHD_AV = 32;
    public static final short MHD_COMMENT = 2;
    public static final short MHD_ENCRYPTVER = 512;
    public static final short MHD_FIRSTVOLUME = 256;
    public static final short MHD_LOCK = 4;
    public static final short MHD_NEWNUMBERING = 16;
    public static final short MHD_PACK_COMMENT = 16;
    public static final short MHD_PASSWORD = 128;
    public static final short MHD_PROTECT = 64;
    public static final short MHD_SOLID = 8;
    public static final short MHD_VOLUME = 1;
    public static final short SKIP_IF_UNKNOWN = 16384;
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) BaseBlock.class);
    protected short flags;
    protected short headCRC;
    protected short headerSize;
    protected byte headerType;
    protected long positionInFile;

    public BaseBlock() {
        this.headCRC = (short) 0;
        this.headerType = (byte) 0;
        this.flags = (short) 0;
        this.headerSize = (short) 0;
    }

    public BaseBlock(BaseBlock bb) {
        this.headCRC = (short) 0;
        this.headerType = (byte) 0;
        this.flags = (short) 0;
        this.headerSize = (short) 0;
        this.flags = bb.getFlags();
        this.headCRC = bb.getHeadCRC();
        this.headerType = bb.getHeaderType().getHeaderByte();
        this.headerSize = bb.getHeaderSize(false);
        this.positionInFile = bb.getPositionInFile();
    }

    public BaseBlock(byte[] baseBlockHeader) {
        this.headCRC = (short) 0;
        this.headerType = (byte) 0;
        this.flags = (short) 0;
        this.headerSize = (short) 0;
        this.headCRC = Raw.readShortLittleEndian(baseBlockHeader, 0);
        int pos = 0 + 2;
        this.headerType = (byte) (this.headerType | (baseBlockHeader[pos] & UByte.MAX_VALUE));
        int pos2 = pos + 1;
        this.flags = Raw.readShortLittleEndian(baseBlockHeader, pos2);
        this.headerSize = Raw.readShortLittleEndian(baseBlockHeader, pos2 + 2);
    }

    public boolean hasArchiveDataCRC() {
        return (this.flags & 2) != 0;
    }

    public boolean hasVolumeNumber() {
        return (this.flags & 8) != 0;
    }

    public boolean hasEncryptVersion() {
        return (this.flags & 512) != 0;
    }

    public boolean isSubBlock() {
        if (UnrarHeadertype.SubHeader.equals(this.headerType)) {
            return true;
        }
        return UnrarHeadertype.NewSubHeader.equals(this.headerType) && (this.flags & 16) != 0;
    }

    public long getPositionInFile() {
        return this.positionInFile;
    }

    public short getFlags() {
        return this.flags;
    }

    public short getHeadCRC() {
        return this.headCRC;
    }

    @Deprecated
    public short getHeaderSize() {
        return this.headerSize;
    }

    public short getHeaderSize(boolean encrypted) {
        if (encrypted) {
            return (short) (this.headerSize + getHeaderPaddingSize());
        }
        return this.headerSize;
    }

    private short getHeaderPaddingSize() {
        return (short) (((~this.headerSize) + 1) & 15);
    }

    public UnrarHeadertype getHeaderType() {
        return UnrarHeadertype.findType(this.headerType);
    }

    public void setPositionInFile(long positionInFile) {
        this.positionInFile = positionInFile;
    }

    public void print() {
        if (logger.isInfoEnabled()) {
            StringBuilder str = new StringBuilder();
            str.append("HeaderType: ").append(getHeaderType());
            str.append("\nHeadCRC: ").append(Integer.toHexString(getHeadCRC()));
            str.append("\nFlags: ").append(Integer.toHexString(getFlags()));
            str.append("\nHeaderSize: ").append((int) getHeaderSize(false));
            str.append("\nPosition in file: ").append(getPositionInFile());
            logger.info(str.toString());
        }
    }
}
