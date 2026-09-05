package com.github.junrar.rarfile;

/* JADX INFO: loaded from: classes.dex */
public enum SubBlockHeaderType {
    EA_HEAD(256),
    UO_HEAD(257),
    MAC_HEAD(258),
    BEEA_HEAD(259),
    NTACL_HEAD(260),
    STREAM_HEAD(261);

    private final short subblocktype;

    SubBlockHeaderType(short subblocktype) {
        this.subblocktype = subblocktype;
    }

    public boolean equals(short subblocktype) {
        return this.subblocktype == subblocktype;
    }

    public static SubBlockHeaderType findSubblockHeaderType(short subType) {
        if (EA_HEAD.equals(subType)) {
            return EA_HEAD;
        }
        if (UO_HEAD.equals(subType)) {
            return UO_HEAD;
        }
        if (MAC_HEAD.equals(subType)) {
            return MAC_HEAD;
        }
        if (BEEA_HEAD.equals(subType)) {
            return BEEA_HEAD;
        }
        if (NTACL_HEAD.equals(subType)) {
            return NTACL_HEAD;
        }
        if (STREAM_HEAD.equals(subType)) {
            return STREAM_HEAD;
        }
        return null;
    }

    public short getSubblocktype() {
        return this.subblocktype;
    }
}
