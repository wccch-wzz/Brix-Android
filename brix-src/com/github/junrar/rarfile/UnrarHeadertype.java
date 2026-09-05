package com.github.junrar.rarfile;

import org.apache.commons.compress.archivers.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
public enum UnrarHeadertype {
    MainHeader((byte) 115),
    MarkHeader((byte) 114),
    FileHeader((byte) 116),
    CommHeader((byte) 117),
    AvHeader((byte) 118),
    SubHeader((byte) 119),
    ProtectHeader(TarConstants.LF_PAX_EXTENDED_HEADER_LC),
    SignHeader((byte) 121),
    NewSubHeader((byte) 122),
    EndArcHeader((byte) 123);

    private final byte headerByte;

    public static UnrarHeadertype findType(byte headerType) {
        if (MarkHeader.equals(headerType)) {
            return MarkHeader;
        }
        if (MainHeader.equals(headerType)) {
            return MainHeader;
        }
        if (FileHeader.equals(headerType)) {
            return FileHeader;
        }
        if (EndArcHeader.equals(headerType)) {
            return EndArcHeader;
        }
        if (NewSubHeader.equals(headerType)) {
            return NewSubHeader;
        }
        if (SubHeader.equals(headerType)) {
            return SubHeader;
        }
        if (SignHeader.equals(headerType)) {
            return SignHeader;
        }
        if (ProtectHeader.equals(headerType)) {
            return ProtectHeader;
        }
        if (CommHeader.equals(headerType)) {
            return CommHeader;
        }
        if (AvHeader.equals(headerType)) {
            return AvHeader;
        }
        return null;
    }

    UnrarHeadertype(byte headerByte) {
        this.headerByte = headerByte;
    }

    public boolean equals(byte header) {
        return this.headerByte == header;
    }

    public byte getHeaderByte() {
        return this.headerByte;
    }
}
