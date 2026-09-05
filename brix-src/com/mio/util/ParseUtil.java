package com.mio.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes10.dex */
public class ParseUtil {
    private static final long H_ENCODED = -5188146764422578176L;
    private static final long L_ENCODED = -576319817246572545L;

    public static boolean isValidCharacters(String s) {
        try {
            File file = new File(s);
            String s1 = normalizeString(file.toURI().toASCIIString());
            String s2 = normalizeString(fileToEncodedURL(file).toString());
            return s1.equals(s2);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static String normalizeString(String s) {
        String result = s.toLowerCase(Locale.ROOT);
        if (result.startsWith("file://")) {
            return result.replace("file://", "");
        }
        if (result.startsWith("file:")) {
            return result.replace("file:", "");
        }
        return result;
    }

    public static URL fileToEncodedURL(File file) throws MalformedURLException {
        String path = encodePath(file.getAbsolutePath());
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/") && file.isDirectory()) {
            path = path + "/";
        }
        return new URL("file", "", path);
    }

    public static String encodePath(String path) {
        return encodePath(path, true);
    }

    public static String encodePath(String path, boolean flag) {
        if (flag && File.separatorChar != '/') {
            return encodePath(path, 0, File.separatorChar);
        }
        int index = firstEncodeIndex(path);
        if (index > -1) {
            return encodePath(path, index, IOUtils.DIR_SEPARATOR_UNIX);
        }
        return path;
    }

    private static int firstEncodeIndex(String path) {
        int len = path.length();
        for (int i = 0; i < len; i++) {
            char c = path.charAt(i);
            if ((c < 'a' || c > 'z') && ((c < '&' || c > ':') && ((c < 'A' || c > 'Z') && (c > 127 || match(c, L_ENCODED, H_ENCODED))))) {
                return i;
            }
        }
        return -1;
    }

    private static String encodePath(String path, int index, char sep) {
        char[] pathCC = path.toCharArray();
        char[] retCC = new char[((pathCC.length * 2) + 16) - index];
        if (index > 0) {
            System.arraycopy(pathCC, 0, retCC, 0, index);
        }
        int retLen = index;
        for (int i = index; i < pathCC.length; i++) {
            char c = pathCC[i];
            if (c == sep) {
                retCC[retLen] = IOUtils.DIR_SEPARATOR_UNIX;
                retLen++;
            } else if (c <= 127) {
                if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && ((c < '0' || c > '9') && match(c, L_ENCODED, H_ENCODED)))) {
                    retLen = escape(retCC, c, retLen);
                } else {
                    retCC[retLen] = c;
                    retLen++;
                }
            } else if (c > 2047) {
                retLen = escape(retCC, (char) (((c >> 0) & 63) | 128), escape(retCC, (char) (((c >> 6) & 63) | 128), escape(retCC, (char) (((c >> '\f') & 15) | 224), retLen)));
            } else {
                retLen = escape(retCC, (char) (((c >> 0) & 63) | 128), escape(retCC, (char) (((c >> 6) & 31) | 192), retLen));
            }
            if (retLen + 9 > retCC.length) {
                int newLen = (retCC.length * 2) + 16;
                if (newLen < 0) {
                    newLen = Integer.MAX_VALUE;
                }
                char[] buf = new char[newLen];
                System.arraycopy(retCC, 0, buf, 0, retLen);
                retCC = buf;
            }
        }
        return new String(retCC, 0, retLen);
    }

    private static int escape(char[] cc, char c, int index) {
        int index2 = index + 1;
        cc[index] = '%';
        int index3 = index2 + 1;
        cc[index2] = Character.forDigit((c >> 4) & 15, 16);
        int index4 = index3 + 1;
        cc[index3] = Character.forDigit(c & 15, 16);
        return index4;
    }

    private static boolean match(char c, long lowMask, long highMask) {
        if (c < '@') {
            return ((1 << c) & lowMask) != 0;
        }
        return c < 128 && ((1 << (c + (-64))) & highMask) != 0;
    }
}
