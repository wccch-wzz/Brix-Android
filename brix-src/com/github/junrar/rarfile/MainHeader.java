package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import kotlin.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class MainHeader extends BaseBlock {
    private static final Logger logger = LoggerFactory.getLogger((Class<?>) MainHeader.class);
    public static final short mainHeaderSize = 6;
    public static final short mainHeaderSizeWithEnc = 7;
    private byte encryptVersion;
    private final short highPosAv;
    private final int posAv;

    public MainHeader(BaseBlock bb, byte[] mainHeader) {
        super(bb);
        this.highPosAv = Raw.readShortLittleEndian(mainHeader, 0);
        int pos = 0 + 2;
        this.posAv = Raw.readIntLittleEndian(mainHeader, pos);
        int pos2 = pos + 4;
        if (hasEncryptVersion()) {
            this.encryptVersion = (byte) (this.encryptVersion | (mainHeader[pos2] & UByte.MAX_VALUE));
        }
    }

    public boolean hasArchCmt() {
        return (this.flags & 2) != 0;
    }

    public byte getEncryptVersion() {
        return this.encryptVersion;
    }

    public short getHighPosAv() {
        return this.highPosAv;
    }

    public int getPosAv() {
        return this.posAv;
    }

    public boolean isEncrypted() {
        return (this.flags & 128) != 0;
    }

    public boolean isMultiVolume() {
        return (this.flags & 1) != 0;
    }

    public boolean isFirstVolume() {
        return (this.flags & 256) != 0;
    }

    @Override // com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (logger.isInfoEnabled()) {
            StringBuilder str = new StringBuilder();
            str.append("posav: ").append(getPosAv());
            str.append("\nhighposav: ").append((int) getHighPosAv());
            str.append("\nhasencversion: ").append(hasEncryptVersion()).append(hasEncryptVersion() ? Byte.valueOf(getEncryptVersion()) : "");
            str.append("\nhasarchcmt: ").append(hasArchCmt());
            str.append("\nisEncrypted: ").append(isEncrypted());
            str.append("\nisMultivolume: ").append(isMultiVolume());
            str.append("\nisFirstvolume: ").append(isFirstVolume());
            str.append("\nisSolid: ").append(isSolid());
            str.append("\nisLocked: ").append(isLocked());
            str.append("\nisProtected: ").append(isProtected());
            str.append("\nisAV: ").append(isAV());
            logger.info(str.toString());
        }
    }

    public boolean isSolid() {
        return (this.flags & 8) != 0;
    }

    public boolean isLocked() {
        return (this.flags & 4) != 0;
    }

    public boolean isProtected() {
        return (this.flags & 64) != 0;
    }

    public boolean isAV() {
        return (this.flags & 32) != 0;
    }

    public boolean isNewNumbering() {
        return (this.flags & 16) != 0;
    }
}
