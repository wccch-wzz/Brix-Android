package com.brixcore.util;

import java.nio.charset.StandardCharsets;
import kotlin.UByte;

/* JADX INFO: loaded from: classes11.dex */
public final class MurmurHash2 {
    private static final int M32 = 1540483477;
    private static final long M64 = -4132994306676758123L;
    private static final int R32 = 24;
    private static final int R64 = 47;

    private MurmurHash2() {
    }

    public static int hash32(byte[] data, int length, int seed) {
        int h = seed ^ length;
        int nblocks = length >> 2;
        for (int i = 0; i < nblocks; i++) {
            int index = i << 2;
            int k = getLittleEndianInt(data, index) * M32;
            h = (h * M32) ^ ((k ^ (k >>> 24)) * M32);
        }
        int i2 = nblocks << 2;
        switch (length - i2) {
            case 3:
                h ^= (data[i2 + 2] & 255) << 16;
            case 2:
                h ^= (data[i2 + 1] & 255) << 8;
            case 1:
                h = (h ^ (data[i2] & 255)) * M32;
                break;
        }
        int h2 = (h ^ (h >>> 13)) * M32;
        return h2 ^ (h2 >>> 15);
    }

    public static int hash32(byte[] data, int length) {
        return hash32(data, length, -1756908916);
    }

    public static int hash32(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        return hash32(bytes, bytes.length);
    }

    public static int hash32(String text, int from, int length) {
        return hash32(text.substring(from, from + length));
    }

    public static long hash64(byte[] data, int length, int seed) {
        long h = (((long) seed) & 4294967295L) ^ (((long) length) * M64);
        int nblocks = length >> 3;
        for (int i = 0; i < nblocks; i++) {
            int index = i << 3;
            long k = getLittleEndianLong(data, index) * M64;
            h = (h ^ ((k ^ (k >>> 47)) * M64)) * M64;
        }
        int i2 = nblocks << 3;
        switch (length - i2) {
            case 7:
                h ^= (((long) data[i2 + 6]) & 255) << 48;
            case 6:
                h ^= (((long) data[i2 + 5]) & 255) << 40;
            case 5:
                h ^= (((long) data[i2 + 4]) & 255) << 32;
            case 4:
                h ^= (((long) data[i2 + 3]) & 255) << 24;
            case 3:
                h ^= (((long) data[i2 + 2]) & 255) << 16;
            case 2:
                h ^= (((long) data[i2 + 1]) & 255) << 8;
            case 1:
                h = (h ^ (((long) data[i2]) & 255)) * M64;
                break;
        }
        long h2 = (h ^ (h >>> 47)) * M64;
        return h2 ^ (h2 >>> 47);
    }

    public static long hash64(byte[] data, int length) {
        return hash64(data, length, -512093083);
    }

    public static long hash64(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        return hash64(bytes, bytes.length);
    }

    public static long hash64(String text, int from, int length) {
        return hash64(text.substring(from, from + length));
    }

    private static int getLittleEndianInt(byte[] data, int index) {
        return (data[index] & UByte.MAX_VALUE) | ((data[index + 1] & UByte.MAX_VALUE) << 8) | ((data[index + 2] & UByte.MAX_VALUE) << 16) | ((data[index + 3] & UByte.MAX_VALUE) << 24);
    }

    private static long getLittleEndianLong(byte[] data, int index) {
        return (((long) data[index]) & 255) | ((((long) data[index + 1]) & 255) << 8) | ((((long) data[index + 2]) & 255) << 16) | ((((long) data[index + 3]) & 255) << 24) | ((((long) data[index + 4]) & 255) << 32) | ((((long) data[index + 5]) & 255) << 40) | ((((long) data[index + 6]) & 255) << 48) | ((255 & ((long) data[index + 7])) << 56);
    }
}
