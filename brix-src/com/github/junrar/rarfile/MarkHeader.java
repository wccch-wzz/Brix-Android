package com.github.junrar.rarfile;

import com.github.junrar.io.Raw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: classes.dex */
public class MarkHeader extends BaseBlock {
    private final Logger logger;
    private RARVersion version;

    public MarkHeader(BaseBlock bb) {
        super(bb);
        this.logger = LoggerFactory.getLogger(MarkHeader.class.getName());
    }

    public boolean isValid() {
        return getHeadCRC() == 24914 && getHeaderType() == UnrarHeadertype.MarkHeader && getFlags() == 6689 && getHeaderSize(false) == 7;
    }

    public boolean isSignature() {
        byte[] d = new byte[7];
        Raw.writeShortLittleEndian(d, 0, this.headCRC);
        d[2] = this.headerType;
        Raw.writeShortLittleEndian(d, 3, this.flags);
        Raw.writeShortLittleEndian(d, 5, this.headerSize);
        if (d[0] == 82) {
            if (d[1] == 69 && d[2] == 126 && d[3] == 94) {
                this.version = RARVersion.OLD;
            } else if (d[1] == 97 && d[2] == 114 && d[3] == 33 && d[4] == 26 && d[5] == 7) {
                if (d[6] == 0) {
                    this.version = RARVersion.V4;
                } else if (d[6] == 1) {
                    this.version = RARVersion.V5;
                }
            }
        }
        return this.version == RARVersion.OLD || this.version == RARVersion.V4;
    }

    public boolean isOldFormat() {
        return RARVersion.isOldFormat(this.version);
    }

    public RARVersion getVersion() {
        return this.version;
    }

    @Override // com.github.junrar.rarfile.BaseBlock
    public void print() {
        super.print();
        if (this.logger.isInfoEnabled()) {
            this.logger.info("valid: {}", Boolean.valueOf(isValid()));
        }
    }
}
