package com.github.junrar.volume;

/* JADX INFO: loaded from: classes.dex */
public class VolumeHelper {
    private VolumeHelper() {
    }

    public static String nextVolumeName(String arcName, boolean oldNumbering) {
        if (!oldNumbering) {
            int len = arcName.length();
            int indexR = len - 1;
            while (indexR >= 0 && !isDigit(arcName.charAt(indexR))) {
                indexR--;
            }
            int index = indexR + 1;
            int indexL = indexR - 1;
            while (indexL >= 0 && isDigit(arcName.charAt(indexL))) {
                indexL--;
            }
            if (indexL < 0) {
                return null;
            }
            int indexL2 = indexL + 1;
            StringBuilder buffer = new StringBuilder(len);
            buffer.append((CharSequence) arcName, 0, indexL2);
            char[] digits = new char[(indexR - indexL2) + 1];
            arcName.getChars(indexL2, indexR + 1, digits, 0);
            int indexR2 = digits.length - 1;
            while (indexR2 >= 0) {
                char c = (char) (digits[indexR2] + 1);
                digits[indexR2] = c;
                if (c != ':') {
                    break;
                }
                digits[indexR2] = '0';
                indexR2--;
            }
            if (indexR2 < 0) {
                buffer.append('1');
            }
            buffer.append(digits);
            buffer.append((CharSequence) arcName, index, len);
            return buffer.toString();
        }
        int len2 = arcName.length();
        if (len2 <= 4 || arcName.charAt(len2 - 4) != '.') {
            return null;
        }
        StringBuilder buffer2 = new StringBuilder();
        int off = len2 - 3;
        buffer2.append((CharSequence) arcName, 0, off);
        if (!isDigit(arcName.charAt(off + 1)) || !isDigit(arcName.charAt(off + 2))) {
            buffer2.append("r00");
        } else {
            char[] ext = new char[3];
            arcName.getChars(off, len2, ext, 0);
            int i = ext.length;
            while (true) {
                i--;
                char c2 = (char) (ext[i] + 1);
                ext[i] = c2;
                if (c2 != ':') {
                    break;
                }
                ext[i] = '0';
            }
            buffer2.append(ext);
        }
        return buffer2.toString();
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
