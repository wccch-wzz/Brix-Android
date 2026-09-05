package com.github.junrar.rarfile;

import java.util.Arrays;
import org.apache.commons.compress.archivers.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
public class NewSubHeaderType {
    private final byte[] headerTypes;
    public static final NewSubHeaderType SUBHEAD_TYPE_CMT = new NewSubHeaderType(new byte[]{67, TarConstants.LF_MULTIVOLUME, 84});
    public static final NewSubHeaderType SUBHEAD_TYPE_ACL = new NewSubHeaderType(new byte[]{65, 67, TarConstants.LF_GNUTYPE_LONGNAME});
    public static final NewSubHeaderType SUBHEAD_TYPE_STREAM = new NewSubHeaderType(new byte[]{TarConstants.LF_GNUTYPE_SPARSE, 84, TarConstants.LF_MULTIVOLUME});
    public static final NewSubHeaderType SUBHEAD_TYPE_UOWNER = new NewSubHeaderType(new byte[]{85, 79, 87});
    public static final NewSubHeaderType SUBHEAD_TYPE_AV = new NewSubHeaderType(new byte[]{65, 86});
    public static final NewSubHeaderType SUBHEAD_TYPE_RR = new NewSubHeaderType(new byte[]{82, 82});
    public static final NewSubHeaderType SUBHEAD_TYPE_OS2EA = new NewSubHeaderType(new byte[]{69, 65, TarConstants.LF_SYMLINK});
    public static final NewSubHeaderType SUBHEAD_TYPE_BEOSEA = new NewSubHeaderType(new byte[]{69, 65, 66, 69});

    private NewSubHeaderType(byte[] headerTypes) {
        this.headerTypes = headerTypes;
    }

    public boolean byteEquals(byte[] toCompare) {
        return Arrays.equals(this.headerTypes, toCompare);
    }

    public String toString() {
        return new String(this.headerTypes);
    }
}
