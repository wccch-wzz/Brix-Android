package com.github.junrar.crc;

import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class RarCRC {
    private static final int[] crcTab = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int c = i;
            for (int j = 0; j < 8; j++) {
                if ((c & 1) != 0) {
                    c = (c >>> 1) ^ (-306674912);
                } else {
                    c >>>= 1;
                }
            }
            crcTab[i] = c;
        }
    }

    private RarCRC() {
    }

    public static int checkCrc(int startCrc, byte[] data, int offset, int count) {
        int size = Math.min(data.length - offset, count);
        for (int i = 0; i < size; i++) {
            startCrc = crcTab[(data[offset + i] ^ startCrc) & 255] ^ (startCrc >>> 8);
        }
        return startCrc;
    }

    public static short checkOldCrc(short startCrc, byte[] data, int count) {
        int n = Math.min(data.length, count);
        for (int i = 0; i < n; i++) {
            short startCrc2 = (short) (((short) (((short) (data[i] & UByte.MAX_VALUE)) + startCrc)) & (-1));
            startCrc = (short) (((startCrc2 << 1) | (startCrc2 >>> 15)) & (-1));
        }
        return startCrc;
    }
}
