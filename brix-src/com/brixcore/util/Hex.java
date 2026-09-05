package com.brixcore.util;

import java.io.IOException;

/* JADX INFO: loaded from: classes11.dex */
public final class Hex {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] decodeHex(String str) throws IOException {
        char[] data = str.toCharArray();
        int len = data.length;
        if ((len & 1) != 0) {
            throw new IOException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        int i = 0;
        int j = 0;
        while (j < len) {
            int f = toDigit(data[j], j) << 4;
            int j2 = j + 1;
            int f2 = f | toDigit(data[j2], j2);
            j = j2 + 1;
            out[i] = (byte) (f2 & 255);
            i++;
        }
        return out;
    }

    public static String encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int j2 = j + 1;
            out[j] = DIGITS_LOWER[(data[i] & 240) >>> 4];
            j = j2 + 1;
            out[j2] = DIGITS_LOWER[data[i] & 15];
        }
        return new String(out);
    }

    private static int toDigit(char ch, int index) throws IOException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new IOException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    private Hex() {
    }
}
