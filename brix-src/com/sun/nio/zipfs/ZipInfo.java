package com.sun.nio.zipfs;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class ZipInfo {
    public static void main(String[] args) throws Throwable {
        if (args.length < 1) {
            print("Usage: java ZipInfo zfname", new Object[0]);
            return;
        }
        Map<String, ?> env = Collections.emptyMap();
        ZipFileSystem zfs = (ZipFileSystem) new ZipFileSystemProvider().newFileSystem(Paths.get(args[0], new String[0]), env);
        byte[] cen = zfs.cen;
        if (cen == null) {
            print("zip file is empty%n", new Object[0]);
            return;
        }
        int pos = 0;
        byte[] buf = new byte[1024];
        int no = 1;
        while (pos + 46 < cen.length) {
            int no2 = no + 1;
            print("----------------#%d--------------------%n", Integer.valueOf(no));
            printCEN(cen, pos);
            long len = ZipConstants.CENNAM(cen, pos) + 30 + ZipConstants.CENEXT(cen, pos) + 46;
            if (zfs.readFullyAt(buf, 0, len, locoff(cen, pos)) != len) {
                ZipFileSystem.zerror("read loc header failed");
            }
            if (ZipConstants.LOCEXT(buf) > ZipConstants.CENEXT(cen, pos) + 46) {
                long len2 = ZipConstants.LOCNAM(buf) + 30 + ZipConstants.LOCEXT(buf);
                if (zfs.readFullyAt(buf, 0, len2, locoff(cen, pos)) != len2) {
                    ZipFileSystem.zerror("read loc header failed");
                }
            }
            printLOC(buf);
            pos += ZipConstants.CENNAM(cen, pos) + 46 + ZipConstants.CENEXT(cen, pos) + ZipConstants.CENCOM(cen, pos);
            no = no2;
        }
        zfs.close();
    }

    static void print(String fmt, Object... objs) {
        System.out.printf(fmt, objs);
    }

    static void printLOC(byte[] loc) {
        print("%n", new Object[0]);
        print("[Local File Header]%n", new Object[0]);
        print("    Signature   :   %#010x%n", Long.valueOf(ZipConstants.LOCSIG(loc)));
        if (ZipConstants.LOCSIG(loc) != ZipConstants.LOCSIG) {
            print("    Wrong signature!", new Object[0]);
            return;
        }
        print("    Version     :       %#6x    [%d.%d]%n", Integer.valueOf(ZipConstants.LOCVER(loc)), Integer.valueOf(ZipConstants.LOCVER(loc) / 10), Integer.valueOf(ZipConstants.LOCVER(loc) % 10));
        print("    Flag        :       %#6x%n", Integer.valueOf(ZipConstants.LOCFLG(loc)));
        print("    Method      :       %#6x%n", Integer.valueOf(ZipConstants.LOCHOW(loc)));
        print("    LastMTime   :   %#10x    [%tc]%n", Long.valueOf(ZipConstants.LOCTIM(loc)), Long.valueOf(ZipUtils.dosToJavaTime(ZipConstants.LOCTIM(loc))));
        print("    CRC         :   %#10x%n", Long.valueOf(ZipConstants.LOCCRC(loc)));
        print("    CSize       :   %#10x%n", Long.valueOf(ZipConstants.LOCSIZ(loc)));
        print("    Size        :   %#10x%n", Long.valueOf(ZipConstants.LOCLEN(loc)));
        print("    NameLength  :       %#6x    [%s]%n", Integer.valueOf(ZipConstants.LOCNAM(loc)), new String(loc, 30, ZipConstants.LOCNAM(loc)));
        print("    ExtraLength :       %#6x%n", Integer.valueOf(ZipConstants.LOCEXT(loc)));
        if (ZipConstants.LOCEXT(loc) != 0) {
            printExtra(loc, ZipConstants.LOCNAM(loc) + 30, ZipConstants.LOCEXT(loc));
        }
    }

    static void printCEN(byte[] cen, int off) {
        print("[Central Directory Header]%n", new Object[0]);
        print("    Signature   :   %#010x%n", Long.valueOf(ZipConstants.CENSIG(cen, off)));
        if (ZipConstants.CENSIG(cen, off) != ZipConstants.CENSIG) {
            print("    Wrong signature!", new Object[0]);
            return;
        }
        print("    VerMadeby   :       %#6x    [%d, %d.%d]%n", Integer.valueOf(ZipConstants.CENVEM(cen, off)), Integer.valueOf(ZipConstants.CENVEM(cen, off) >> 8), Integer.valueOf((ZipConstants.CENVEM(cen, off) & 255) / 10), Integer.valueOf((ZipConstants.CENVEM(cen, off) & 255) % 10));
        print("    VerExtract  :       %#6x    [%d.%d]%n", Integer.valueOf(ZipConstants.CENVER(cen, off)), Integer.valueOf(ZipConstants.CENVER(cen, off) / 10), Integer.valueOf(ZipConstants.CENVER(cen, off) % 10));
        print("    Flag        :       %#6x%n", Integer.valueOf(ZipConstants.CENFLG(cen, off)));
        print("    Method      :       %#6x%n", Integer.valueOf(ZipConstants.CENHOW(cen, off)));
        print("    LastMTime   :   %#10x    [%tc]%n", Long.valueOf(ZipConstants.CENTIM(cen, off)), Long.valueOf(ZipUtils.dosToJavaTime(ZipConstants.CENTIM(cen, off))));
        print("    CRC         :   %#10x%n", Long.valueOf(ZipConstants.CENCRC(cen, off)));
        print("    CSize       :   %#10x%n", Long.valueOf(ZipConstants.CENSIZ(cen, off)));
        print("    Size        :   %#10x%n", Long.valueOf(ZipConstants.CENLEN(cen, off)));
        print("    NameLen     :       %#6x    [%s]%n", Integer.valueOf(ZipConstants.CENNAM(cen, off)), new String(cen, off + 46, ZipConstants.CENNAM(cen, off)));
        print("    ExtraLen    :       %#6x%n", Integer.valueOf(ZipConstants.CENEXT(cen, off)));
        if (ZipConstants.CENEXT(cen, off) != 0) {
            printExtra(cen, off + 46 + ZipConstants.CENNAM(cen, off), ZipConstants.CENEXT(cen, off));
        }
        print("    CommentLen  :       %#6x%n", Integer.valueOf(ZipConstants.CENCOM(cen, off)));
        print("    DiskStart   :       %#6x%n", Integer.valueOf(ZipConstants.CENDSK(cen, off)));
        print("    Attrs       :       %#6x%n", Integer.valueOf(ZipConstants.CENATT(cen, off)));
        print("    AttrsEx     :   %#10x%n", Long.valueOf(ZipConstants.CENATX(cen, off)));
        print("    LocOff      :   %#10x%n", Long.valueOf(ZipConstants.CENOFF(cen, off)));
    }

    static long locoff(byte[] cen, int pos) {
        long locoff = ZipConstants.CENOFF(cen, pos);
        if (locoff == 4294967295L) {
            int off = pos + 46 + ZipConstants.CENNAM(cen, pos);
            int end = ZipConstants.CENEXT(cen, pos) + off;
            while (off + 4 < end) {
                int tag = ZipConstants.SH(cen, off);
                int sz = ZipConstants.SH(cen, off + 2);
                if (tag != 1) {
                    off += sz + 4;
                } else {
                    int off2 = off + 4;
                    if (ZipConstants.CENLEN(cen, pos) == 4294967295L) {
                        off2 += 8;
                    }
                    if (ZipConstants.CENSIZ(cen, pos) == 4294967295L) {
                        off2 += 8;
                    }
                    return ZipConstants.LL(cen, off2);
                }
            }
        }
        return locoff;
    }

    static void printExtra(byte[] extra, int off, int len) {
        int end = off + len;
        while (off + 4 <= end) {
            int tag = ZipConstants.SH(extra, off);
            int sz = ZipConstants.SH(extra, off + 2);
            print("        [tag=0x%04x, sz=%d, data= ", Integer.valueOf(tag), Integer.valueOf(sz));
            if (off + sz > end) {
                print("    Error: Invalid extra data, beyond extra length", new Object[0]);
                return;
            }
            int off2 = off + 4;
            for (int i = 0; i < sz; i++) {
                print("%02x ", Byte.valueOf(extra[off2 + i]));
            }
            print("]%n", new Object[0]);
            switch (tag) {
                case 1:
                    print("         ->ZIP64: ", new Object[0]);
                    for (int pos = off2; pos + 8 <= off2 + sz; pos += 8) {
                        print(" *0x%x ", Long.valueOf(ZipConstants.LL(extra, pos)));
                    }
                    print("%n", new Object[0]);
                    break;
                case 10:
                    print("         ->PKWare NTFS%n", new Object[0]);
                    if (ZipConstants.SH(extra, off2 + 4) != 1 || ZipConstants.SH(extra, off2 + 6) != 24) {
                        print("    Error: Invalid NTFS sub-tag or subsz", new Object[0]);
                    }
                    print("            mtime:%tc%n", Long.valueOf(ZipUtils.winToJavaTime(ZipConstants.LL(extra, off2 + 8))));
                    print("            atime:%tc%n", Long.valueOf(ZipUtils.winToJavaTime(ZipConstants.LL(extra, off2 + 16))));
                    print("            ctime:%tc%n", Long.valueOf(ZipUtils.winToJavaTime(ZipConstants.LL(extra, off2 + 24))));
                    break;
                case 21589:
                    print("         ->Info-ZIP Extended Timestamp: flag=%x%n", Byte.valueOf(extra[off2]));
                    for (int pos2 = off2 + 1; pos2 + 4 <= off2 + sz; pos2 += 4) {
                        print("            *%tc%n", Long.valueOf(ZipUtils.unixToJavaTime(ZipConstants.LG(extra, pos2))));
                    }
                    break;
                default:
                    print("         ->[tag=%x, size=%d]%n", Integer.valueOf(tag), Integer.valueOf(sz));
                    break;
            }
            off = off2 + sz;
        }
    }
}
