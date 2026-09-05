package com.github.junrar.io;

import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class Raw {
    public static short readShortBigEndian(byte[] array, int pos) {
        short temp = (short) ((array[pos] & UByte.MAX_VALUE) | 0);
        return (short) ((array[pos + 1] & UByte.MAX_VALUE) | ((short) (temp << 8)));
    }

    public static int readIntBigEndian(byte[] array, int pos) {
        int temp = 0 | (array[pos] & UByte.MAX_VALUE);
        return (((((temp << 8) | (array[pos + 1] & UByte.MAX_VALUE)) << 8) | (array[pos + 2] & UByte.MAX_VALUE)) << 8) | (array[pos + 3] & UByte.MAX_VALUE);
    }

    public static long readLongBigEndian(byte[] array, int pos) {
        int temp = 0 | (array[pos] & UByte.MAX_VALUE);
        return (((((((((((((temp << 8) | (array[pos + 1] & UByte.MAX_VALUE)) << 8) | (array[pos + 2] & UByte.MAX_VALUE)) << 8) | (array[pos + 3] & UByte.MAX_VALUE)) << 8) | (array[pos + 4] & UByte.MAX_VALUE)) << 8) | (array[pos + 5] & UByte.MAX_VALUE)) << 8) | (array[pos + 6] & UByte.MAX_VALUE)) << 8) | (array[pos + 7] & UByte.MAX_VALUE);
    }

    public static short readShortLittleEndian(byte[] array, int pos) {
        short result = (short) ((array[pos + 1] & UByte.MAX_VALUE) + 0);
        return (short) ((array[pos] & UByte.MAX_VALUE) + ((short) (result << 8)));
    }

    public static int readIntLittleEndian(byte[] array, int pos) {
        return ((array[pos + 3] & UByte.MAX_VALUE) << 24) | ((array[pos + 2] & UByte.MAX_VALUE) << 16) | ((array[pos + 1] & UByte.MAX_VALUE) << 8) | (array[pos] & UByte.MAX_VALUE);
    }

    public static long readIntLittleEndianAsLong(byte[] array, int pos) {
        return ((((long) array[pos + 3]) & 255) << 24) | ((((long) array[pos + 2]) & 255) << 16) | ((((long) array[pos + 1]) & 255) << 8) | (255 & ((long) array[pos]));
    }

    public static long readLongLittleEndian(byte[] array, int pos) {
        long temp = 0 | ((long) (array[pos + 7] & UByte.MAX_VALUE));
        return (((((((((((((temp << 8) | ((long) (array[pos + 6] & UByte.MAX_VALUE))) << 8) | ((long) (array[pos + 5] & UByte.MAX_VALUE))) << 8) | ((long) (array[pos + 4] & UByte.MAX_VALUE))) << 8) | ((long) (array[pos + 3] & UByte.MAX_VALUE))) << 8) | ((long) (array[pos + 2] & UByte.MAX_VALUE))) << 8) | ((long) (array[pos + 1] & UByte.MAX_VALUE))) << 8) | ((long) (array[pos] & UByte.MAX_VALUE));
    }

    public static void writeShortBigEndian(byte[] array, int pos, short value) {
        array[pos] = (byte) (value >>> 8);
        array[pos + 1] = (byte) (value & 255);
    }

    public static void writeIntBigEndian(byte[] array, int pos, int value) {
        array[pos] = (byte) ((value >>> 24) & 255);
        array[pos + 1] = (byte) ((value >>> 16) & 255);
        array[pos + 2] = (byte) ((value >>> 8) & 255);
        array[pos + 3] = (byte) (value & 255);
    }

    public static void writeLongBigEndian(byte[] array, int pos, long value) {
        array[pos] = (byte) (value >>> 56);
        array[pos + 1] = (byte) (value >>> 48);
        array[pos + 2] = (byte) (value >>> 40);
        array[pos + 3] = (byte) (value >>> 32);
        array[pos + 4] = (byte) (value >>> 24);
        array[pos + 5] = (byte) (value >>> 16);
        array[pos + 6] = (byte) (value >>> 8);
        array[pos + 7] = (byte) (255 & value);
    }

    public static void writeShortLittleEndian(byte[] array, int pos, short value) {
        array[pos + 1] = (byte) (value >>> 8);
        array[pos] = (byte) (value & 255);
    }

    public static void incShortLittleEndian(byte[] array, int pos, int dv) {
        int c = ((array[pos] & 255) + (dv & 255)) >>> 8;
        array[pos] = (byte) (array[pos] + (dv & 255));
        if (c > 0 || (65280 & dv) != 0) {
            int i = pos + 1;
            array[i] = (byte) (array[i] + ((dv >>> 8) & 255) + c);
        }
    }

    public static void writeIntLittleEndian(byte[] array, int pos, int value) {
        array[pos + 3] = (byte) (value >>> 24);
        array[pos + 2] = (byte) (value >>> 16);
        array[pos + 1] = (byte) (value >>> 8);
        array[pos] = (byte) (value & 255);
    }

    public static void writeLongLittleEndian(byte[] array, int pos, long value) {
        array[pos + 7] = (byte) (value >>> 56);
        array[pos + 6] = (byte) (value >>> 48);
        array[pos + 5] = (byte) (value >>> 40);
        array[pos + 4] = (byte) (value >>> 32);
        array[pos + 3] = (byte) (value >>> 24);
        array[pos + 2] = (byte) (value >>> 16);
        array[pos + 1] = (byte) (value >>> 8);
        array[pos] = (byte) (255 & value);
    }
}
