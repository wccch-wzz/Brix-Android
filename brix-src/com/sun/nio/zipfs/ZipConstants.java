package com.sun.nio.zipfs;

/* JADX INFO: loaded from: classes2.dex */
class ZipConstants {
    static final int CENATT = 36;
    static final int CENATX = 38;
    static final int CENCOM = 32;
    static final int CENCRC = 16;
    static final int CENDSK = 34;
    static final int CENEXT = 30;
    static final int CENFLG = 8;
    static final int CENHDR = 46;
    static final int CENHOW = 10;
    static final int CENLEN = 24;
    static final int CENNAM = 28;
    static final int CENOFF = 42;
    static final int CENSIZ = 20;
    static final int CENTIM = 12;
    static final int CENVEM = 4;
    static final int CENVER = 6;
    static final int ENDCOM = 20;
    static final int ENDHDR = 22;
    static final int ENDOFF = 16;
    static final int ENDSIZ = 12;
    static final int ENDSUB = 8;
    static final int ENDTOT = 10;
    static final long END_MAXLEN = 65557;
    static final int EXTCRC = 4;
    static final int EXTHDR = 16;
    static final int EXTID_EFS = 23;
    static final int EXTID_EXTT = 21589;
    static final int EXTID_NTFS = 10;
    static final int EXTID_UNIX = 13;
    static final int EXTID_ZIP64 = 1;
    static final int EXTLEN = 12;
    static final int EXTSIZ = 8;
    static final int FLAG_DATADESCR = 8;
    static final int FLAG_EFS = 2048;
    static final int FLAG_ENCRYPTED = 1;
    static final int LOCCRC = 14;
    static final int LOCEXT = 28;
    static final int LOCFLG = 6;
    static final int LOCHDR = 30;
    static final int LOCHOW = 8;
    static final int LOCLEN = 22;
    static final int LOCNAM = 26;
    static final int LOCSIZ = 18;
    static final int LOCTIM = 10;
    static final int LOCVER = 4;
    static final int METHOD_AES = 99;
    static final int METHOD_BZIP2 = 12;
    static final int METHOD_DEFLATED = 8;
    static final int METHOD_DEFLATED64 = 9;
    static final int METHOD_LZ77 = 19;
    static final int METHOD_LZMA = 14;
    static final int METHOD_STORED = 0;
    static final int READBLOCKSZ = 128;
    static final int ZIP64_ENDDSK = 20;
    static final int ZIP64_ENDEXT = 56;
    static final int ZIP64_ENDHDR = 56;
    static final int ZIP64_ENDLEN = 4;
    static final int ZIP64_ENDNMD = 16;
    static final int ZIP64_ENDOFF = 48;
    static final long ZIP64_ENDSIG = 101075792;
    static final int ZIP64_ENDSIZ = 40;
    static final int ZIP64_ENDTOD = 24;
    static final int ZIP64_ENDTOT = 32;
    static final int ZIP64_ENDVEM = 12;
    static final int ZIP64_ENDVER = 14;
    static final int ZIP64_EXTCRC = 4;
    static final int ZIP64_EXTHDR = 24;
    static final int ZIP64_EXTID = 1;
    static final int ZIP64_EXTLEN = 16;
    static final int ZIP64_EXTSIZ = 8;
    static final int ZIP64_LOCDSK = 4;
    static final int ZIP64_LOCHDR = 20;
    static final int ZIP64_LOCOFF = 8;
    static final long ZIP64_LOCSIG = 117853008;
    static final int ZIP64_LOCTOT = 16;
    static final long ZIP64_MINVAL = 4294967295L;
    static final int ZIP64_MINVAL32 = 65535;
    static long LOCSIG = 67324752;
    static long EXTSIG = 134695760;
    static long CENSIG = 33639248;
    static long ENDSIG = 101010256;

    ZipConstants() {
    }

    static final int CH(byte[] b, int n) {
        return Byte.toUnsignedInt(b[n]);
    }

    static final int SH(byte[] b, int n) {
        return Byte.toUnsignedInt(b[n]) | (Byte.toUnsignedInt(b[n + 1]) << 8);
    }

    static final long LG(byte[] b, int n) {
        return ((long) (SH(b, n) | (SH(b, n + 2) << 16))) & ZIP64_MINVAL;
    }

    static final long LL(byte[] b, int n) {
        return LG(b, n) | (LG(b, n + 4) << 32);
    }

    static long getSig(byte[] b, int n) {
        return LG(b, n);
    }

    private static boolean pkSigAt(byte[] b, int n, int b1, int b2) {
        return (b[n] == 80) & (b[n + 1] == 75) & (b[n + 2] == b1) & (b[n + 3] == b2);
    }

    static boolean cenSigAt(byte[] b, int n) {
        return pkSigAt(b, n, 1, 2);
    }

    static boolean locSigAt(byte[] b, int n) {
        return pkSigAt(b, n, 3, 4);
    }

    static boolean endSigAt(byte[] b, int n) {
        return pkSigAt(b, n, 5, 6);
    }

    static boolean extSigAt(byte[] b, int n) {
        return pkSigAt(b, n, 7, 8);
    }

    static boolean end64SigAt(byte[] b, int n) {
        return pkSigAt(b, n, 6, 6);
    }

    static boolean locator64SigAt(byte[] b, int n) {
        return pkSigAt(b, n, 6, 7);
    }

    static final long LOCSIG(byte[] b) {
        return LG(b, 0);
    }

    static final int LOCVER(byte[] b) {
        return SH(b, 4);
    }

    static final int LOCFLG(byte[] b) {
        return SH(b, 6);
    }

    static final int LOCHOW(byte[] b) {
        return SH(b, 8);
    }

    static final long LOCTIM(byte[] b) {
        return LG(b, 10);
    }

    static final long LOCCRC(byte[] b) {
        return LG(b, 14);
    }

    static final long LOCSIZ(byte[] b) {
        return LG(b, 18);
    }

    static final long LOCLEN(byte[] b) {
        return LG(b, 22);
    }

    static final int LOCNAM(byte[] b) {
        return SH(b, 26);
    }

    static final int LOCEXT(byte[] b) {
        return SH(b, 28);
    }

    static final long EXTCRC(byte[] b) {
        return LG(b, 4);
    }

    static final long EXTSIZ(byte[] b) {
        return LG(b, 8);
    }

    static final long EXTLEN(byte[] b) {
        return LG(b, 12);
    }

    static final int ENDSUB(byte[] b) {
        return SH(b, 8);
    }

    static final int ENDTOT(byte[] b) {
        return SH(b, 10);
    }

    static final long ENDSIZ(byte[] b) {
        return LG(b, 12);
    }

    static final long ENDOFF(byte[] b) {
        return LG(b, 16);
    }

    static final int ENDCOM(byte[] b) {
        return SH(b, 20);
    }

    static final int ENDCOM(byte[] b, int off) {
        return SH(b, off + 20);
    }

    static final long ZIP64_ENDTOD(byte[] b) {
        return LL(b, 24);
    }

    static final long ZIP64_ENDTOT(byte[] b) {
        return LL(b, 32);
    }

    static final long ZIP64_ENDSIZ(byte[] b) {
        return LL(b, 40);
    }

    static final long ZIP64_ENDOFF(byte[] b) {
        return LL(b, 48);
    }

    static final long ZIP64_LOCOFF(byte[] b) {
        return LL(b, 8);
    }

    static final long CENSIG(byte[] b, int pos) {
        return LG(b, pos + 0);
    }

    static final int CENVEM(byte[] b, int pos) {
        return SH(b, pos + 4);
    }

    static final int CENVER(byte[] b, int pos) {
        return SH(b, pos + 6);
    }

    static final int CENFLG(byte[] b, int pos) {
        return SH(b, pos + 8);
    }

    static final int CENHOW(byte[] b, int pos) {
        return SH(b, pos + 10);
    }

    static final long CENTIM(byte[] b, int pos) {
        return LG(b, pos + 12);
    }

    static final long CENCRC(byte[] b, int pos) {
        return LG(b, pos + 16);
    }

    static final long CENSIZ(byte[] b, int pos) {
        return LG(b, pos + 20);
    }

    static final long CENLEN(byte[] b, int pos) {
        return LG(b, pos + 24);
    }

    static final int CENNAM(byte[] b, int pos) {
        return SH(b, pos + 28);
    }

    static final int CENEXT(byte[] b, int pos) {
        return SH(b, pos + 30);
    }

    static final int CENCOM(byte[] b, int pos) {
        return SH(b, pos + 32);
    }

    static final int CENDSK(byte[] b, int pos) {
        return SH(b, pos + 34);
    }

    static final int CENATT(byte[] b, int pos) {
        return SH(b, pos + 36);
    }

    static final long CENATX(byte[] b, int pos) {
        return LG(b, pos + 38);
    }

    static final long CENOFF(byte[] b, int pos) {
        return LG(b, pos + 42);
    }
}
