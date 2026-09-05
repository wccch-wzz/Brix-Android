package com.sun.nio.zipfs;

import androidx.core.text.HtmlCompat;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
class ZipUtils {
    private static char EOL = 0;
    private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
    private static final String globMetaChars = "\\*?[{";
    private static final String regexMetaChars = ".^$+{[]|()";

    ZipUtils() {
    }

    public static void writeShort(OutputStream os, int v) throws IOException {
        os.write(v & 255);
        os.write((v >>> 8) & 255);
    }

    public static void writeInt(OutputStream os, long v) throws IOException {
        os.write((int) (v & 255));
        os.write((int) ((v >>> 8) & 255));
        os.write((int) ((v >>> 16) & 255));
        os.write((int) (255 & (v >>> 24)));
    }

    public static void writeLong(OutputStream os, long v) throws IOException {
        os.write((int) (v & 255));
        os.write((int) ((v >>> 8) & 255));
        os.write((int) ((v >>> 16) & 255));
        os.write((int) ((v >>> 24) & 255));
        os.write((int) ((v >>> 32) & 255));
        os.write((int) ((v >>> 40) & 255));
        os.write((int) ((v >>> 48) & 255));
        os.write((int) (255 & (v >>> 56)));
    }

    public static void writeBytes(OutputStream os, byte[] b) throws IOException {
        os.write(b, 0, b.length);
    }

    public static void writeBytes(OutputStream os, byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
    }

    public static byte[] toDirectoryPath(byte[] dir) {
        if (dir.length != 0 && dir[dir.length - 1] != 47) {
            byte[] dir2 = Arrays.copyOf(dir, dir.length + 1);
            dir2[dir2.length - 1] = 47;
            return dir2;
        }
        return dir;
    }

    public static long dosToJavaTime(long dtime) {
        Date d = new Date((int) (((dtime >> 25) & 127) + 80), (int) (((dtime >> 21) & 15) - 1), (int) ((dtime >> 16) & 31), (int) ((dtime >> 11) & 31), (int) ((dtime >> 5) & 63), (int) ((dtime << 1) & 62));
        return d.getTime();
    }

    public static long javaToDosTime(long time) {
        Date d = new Date(time);
        int year = d.getYear() + 1900;
        if (year < 1980) {
            return 2162688L;
        }
        return ((year - 1980) << 25) | ((d.getMonth() + 1) << 21) | (d.getDate() << 16) | (d.getHours() << 11) | (d.getMinutes() << 5) | (d.getSeconds() >> 1);
    }

    public static final long winToJavaTime(long wtime) {
        return TimeUnit.MILLISECONDS.convert((wtime / 10) + WINDOWS_EPOCH_IN_MICROSECONDS, TimeUnit.MICROSECONDS);
    }

    public static final long javaToWinTime(long time) {
        return (TimeUnit.MICROSECONDS.convert(time, TimeUnit.MILLISECONDS) - WINDOWS_EPOCH_IN_MICROSECONDS) * 10;
    }

    public static final long unixToJavaTime(long utime) {
        return TimeUnit.MILLISECONDS.convert(utime, TimeUnit.SECONDS);
    }

    public static final long javaToUnixTime(long time) {
        return TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS);
    }

    private static boolean isRegexMeta(char c) {
        return regexMetaChars.indexOf(c) != -1;
    }

    private static boolean isGlobMeta(char c) {
        return globMetaChars.indexOf(c) != -1;
    }

    private static char next(String glob, int i) {
        if (i < glob.length()) {
            return glob.charAt(i);
        }
        return EOL;
    }

    /* JADX WARN: Code duplicated, block: B:106:0x0119 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:71:0x0112  */
    public static String toRegexPattern(String globPattern) {
        boolean inGroup = false;
        StringBuilder regex = new StringBuilder("^");
        int i = 0;
        while (i < globPattern.length()) {
            int i2 = i + 1;
            char c = globPattern.charAt(i);
            switch (c) {
                case '*':
                    if (next(globPattern, i2) == '*') {
                        regex.append(".*");
                        i = i2 + 1;
                    } else {
                        regex.append("[^/]*");
                    }
                    break;
                case ',':
                    if (inGroup) {
                        regex.append(")|(?:");
                    } else {
                        regex.append(',');
                    }
                    break;
                case '/':
                    regex.append(c);
                    break;
                case HtmlCompat.FROM_HTML_MODE_COMPACT /* 63 */:
                    regex.append("[^/]");
                    break;
                case '[':
                    regex.append("[[^/]&&[");
                    if (next(globPattern, i2) == '^') {
                        regex.append("\\^");
                        i2++;
                    } else {
                        if (next(globPattern, i2) == '!') {
                            regex.append('^');
                            i2++;
                        }
                        if (next(globPattern, i2) == '-') {
                            regex.append('-');
                            i2++;
                        }
                    }
                    boolean hasRangeStart = false;
                    char last = 0;
                    while (i2 < globPattern.length()) {
                        int i3 = i2 + 1;
                        c = globPattern.charAt(i2);
                        if (c == ']') {
                            i2 = i3;
                        } else {
                            if (c == '/') {
                                throw new PatternSyntaxException("Explicit 'name separator' in class", globPattern, i3 - 1);
                            }
                            if (c == '\\' || c == '[' || (c == '&' && next(globPattern, i3) == '&')) {
                                regex.append(IOUtils.DIR_SEPARATOR_WINDOWS);
                            }
                            regex.append(c);
                            if (c == '-') {
                                if (!hasRangeStart) {
                                    throw new PatternSyntaxException("Invalid range", globPattern, i3 - 1);
                                }
                                int i4 = i3 + 1;
                                char next = next(globPattern, i3);
                                c = next;
                                if (next != EOL && c != ']') {
                                    if (c < last) {
                                        throw new PatternSyntaxException("Invalid range", globPattern, i4 - 3);
                                    }
                                    regex.append(c);
                                    hasRangeStart = false;
                                    i2 = i4;
                                } else {
                                    i2 = i4;
                                }
                            } else {
                                hasRangeStart = true;
                                last = c;
                                i2 = i3;
                            }
                        }
                        if (c == ']') {
                            throw new PatternSyntaxException("Missing ']", globPattern, i2 - 1);
                        }
                        regex.append("]]");
                        i = i2;
                        continue;
                    }
                    if (c == ']') {
                        throw new PatternSyntaxException("Missing ']", globPattern, i2 - 1);
                    }
                    regex.append("]]");
                    i = i2;
                    continue;
                    break;
                case '\\':
                    if (i2 == globPattern.length()) {
                        throw new PatternSyntaxException("No character to escape", globPattern, i2 - 1);
                    }
                    int i5 = i2 + 1;
                    char next2 = globPattern.charAt(i2);
                    if (isGlobMeta(next2) || isRegexMeta(next2)) {
                        regex.append(IOUtils.DIR_SEPARATOR_WINDOWS);
                    }
                    regex.append(next2);
                    i = i5;
                    continue;
                    break;
                case '{':
                    if (inGroup) {
                        throw new PatternSyntaxException("Cannot nest groups", globPattern, i2 - 1);
                    }
                    regex.append("(?:(?:");
                    inGroup = true;
                    i = i2;
                    continue;
                    break;
                case '}':
                    if (inGroup) {
                        regex.append("))");
                        inGroup = false;
                        i = i2;
                    } else {
                        regex.append('}');
                    }
                    break;
                default:
                    if (isRegexMeta(c)) {
                        regex.append(IOUtils.DIR_SEPARATOR_WINDOWS);
                    }
                    regex.append(c);
                    break;
            }
            i = i2;
        }
        if (inGroup) {
            throw new PatternSyntaxException("Missing '}", globPattern, i - 1);
        }
        return regex.append('$').toString();
    }
}
