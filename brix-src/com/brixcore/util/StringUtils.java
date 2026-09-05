package com.brixcore.util;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.location.LocationRequestCompat;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.CharUtils;
import org.glavo.chardet.prober.CharsetProber;
import org.tomlj.internal.TomlParser;

/* JADX INFO: loaded from: classes11.dex */
public final class StringUtils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§([0-9a-fk-or])");
    private static final String FORMAT_CODE = "format_code";

    private StringUtils() {
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter(512);
        PrintWriter printWriter = new PrintWriter(stringWriter);
        try {
            throwable.printStackTrace(printWriter);
            printWriter.close();
            return stringWriter.toString();
        } catch (Throwable th) {
            try {
                printWriter.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static String getStackTrace(StackTraceElement[] elements) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : elements) {
            builder.append("\tat ").append(element).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public static boolean isBlank(String str) {
        return str == null || StringUtils$$ExternalSyntheticBackport0.m(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String normalizeWhitespaces(String str) {
        if (str == null) {
            return "";
        }
        int start = 0;
        int end = str.length();
        while (start < str.length() && Character.isWhitespace(str.charAt(start))) {
            start++;
        }
        while (end > start && Character.isWhitespace(str.charAt(end - 1))) {
            end--;
        }
        if (end == start) {
            return "";
        }
        StringBuilder builder = null;
        int i = start;
        while (i < end) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                int whitespaceEnd = i + 1;
                while (whitespaceEnd < end && Character.isWhitespace(str.charAt(whitespaceEnd))) {
                    whitespaceEnd++;
                }
                if (whitespaceEnd - i > 1 || ch != ' ') {
                    if (builder == null) {
                        StringBuilder builder2 = new StringBuilder(end - start);
                        builder2.append((CharSequence) str, start, i);
                        builder = builder2;
                    }
                    builder.append(' ');
                    i = whitespaceEnd;
                }
            }
            if (builder != null) {
                builder.append(ch);
            }
            i++;
        }
        return builder != null ? builder.toString() : str.substring(start, end);
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String substringBeforeLast(String str, char delimiter) {
        return substringBeforeLast(str, delimiter, str);
    }

    public static String substringBeforeLast(String str, char delimiter, String missingDelimiterValue) {
        int index = str.lastIndexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(0, index);
    }

    public static String substringBeforeLast(String str, String delimiter) {
        return substringBeforeLast(str, delimiter, str);
    }

    public static String substringBeforeLast(String str, String delimiter, String missingDelimiterValue) {
        int index = str.lastIndexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(0, index);
    }

    public static String substringBefore(String str, char delimiter) {
        return substringBefore(str, delimiter, str);
    }

    public static String substringBefore(String str, char delimiter, String missingDelimiterValue) {
        int index = str.indexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(0, index);
    }

    public static String substringBefore(String str, String delimiter) {
        return substringBefore(str, delimiter, str);
    }

    public static String substringBefore(String str, String delimiter, String missingDelimiterValue) {
        int index = str.indexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(0, index);
    }

    public static String substringAfterLast(String str, char delimiter) {
        return substringAfterLast(str, delimiter, "");
    }

    public static String substringAfterLast(String str, char delimiter, String missingDelimiterValue) {
        int index = str.lastIndexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(index + 1);
    }

    public static String substringAfterLast(String str, String delimiter) {
        return substringAfterLast(str, delimiter, "");
    }

    public static String substringAfterLast(String str, String delimiter, String missingDelimiterValue) {
        int index = str.lastIndexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(delimiter.length() + index);
    }

    public static String substringAfter(String str, char delimiter) {
        return substringAfter(str, delimiter, "");
    }

    public static String substringAfter(String str, char delimiter, String missingDelimiterValue) {
        int index = str.indexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(index + 1);
    }

    public static String substringAfter(String str, String delimiter) {
        return substringAfter(str, delimiter, "");
    }

    public static String substringAfter(String str, String delimiter, String missingDelimiterValue) {
        int index = str.indexOf(delimiter);
        return index == -1 ? missingDelimiterValue : str.substring(delimiter.length() + index);
    }

    public static boolean isSurrounded(String str, String prefix, String suffix) {
        return str.startsWith(prefix) && str.endsWith(suffix);
    }

    public static String removeSurrounding(String str, String delimiter) {
        return removeSurrounding(str, delimiter, delimiter);
    }

    public static String removeSurrounding(String str, String prefix, String suffix) {
        if (str.length() >= prefix.length() + suffix.length() && str.startsWith(prefix) && str.endsWith(suffix)) {
            return str.substring(prefix.length(), str.length() - suffix.length());
        }
        return str;
    }

    public static String addPrefix(String str, String prefix) {
        if (str.startsWith(prefix)) {
            return str;
        }
        return prefix + str;
    }

    public static String addSuffix(String str, String suffix) {
        if (str.endsWith(suffix)) {
            return str;
        }
        return str + suffix;
    }

    public static String removePrefix(String str, String prefix) {
        return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
    }

    public static String removePrefix(String str, String... prefixes) {
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return str.substring(prefix.length());
            }
        }
        return str;
    }

    public static String removeSuffix(String str, String suffix) {
        return str.endsWith(suffix) ? str.substring(0, str.length() - suffix.length()) : str;
    }

    public static String removeSuffix(String str, String... suffixes) {
        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return str.substring(0, str.length() - suffix.length());
            }
        }
        return str;
    }

    public static boolean containsOne(Collection<String> patterns, String... targets) {
        Iterator<String> it = patterns.iterator();
        while (true) {
            if (!it.hasNext()) {
                return false;
            }
            String pattern = it.next();
            String lowerPattern = pattern.toLowerCase(Locale.ROOT);
            for (String target : targets) {
                if (lowerPattern.contains(target.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
        }
    }

    public static boolean containsOne(String pattern, String... targets) {
        String lowerPattern = pattern.toLowerCase(Locale.ROOT);
        for (String target : targets) {
            if (lowerPattern.contains(target.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsChinese(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= 19968 && ch <= 40869) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsEmoji(String str) {
        int i = 0;
        while (i < str.length()) {
            int ch = str.codePointAt(i);
            if (ch >= 127744 && ch <= 129791) {
                return true;
            }
            i += Character.charCount(ch);
        }
        return false;
    }

    private static boolean isVarNameStart(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }

    private static boolean isVarNamePart(char ch) {
        return isVarNameStart(ch) || (ch >= '0' && ch <= '9');
    }

    private static int findVarEnd(String str, int offset) {
        if (offset < str.length() - 1 && isVarNameStart(str.charAt(offset))) {
            int end = offset + 1;
            while (end < str.length() && isVarNamePart(str.charAt(end))) {
                end++;
            }
            return end;
        }
        return -1;
    }

    public static List<String> tokenize(String str) {
        return tokenize(str, null);
    }

    public static List<String> tokenize(String str, Map<String, String> vars) {
        int varEnd;
        int varEnd2;
        if (isBlank(str)) {
            return new ArrayList();
        }
        if (vars == null) {
            vars = Collections.emptyMap();
        }
        ArrayList<String> parts = new ArrayList<>();
        boolean hasValue = false;
        StringBuilder current = new StringBuilder(str.length());
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt(i);
            if (c == '\'') {
                hasValue = true;
                int end = str.indexOf(c, i + 1);
                if (end < 0) {
                    end = str.length();
                }
                current.append((CharSequence) str, i + 1, end);
                int i2 = end + 1;
                i = i2;
            } else if (c == '\"') {
                hasValue = true;
                i++;
                while (i < str.length()) {
                    int i3 = i + 1;
                    char c2 = str.charAt(i);
                    if (c2 == '\"') {
                        i = i3;
                        break;
                    }
                    if (c2 == '`' && i3 < str.length()) {
                        i = i3 + 1;
                        char c3 = str.charAt(i3);
                        switch (c3) {
                            case CharsetProber.ASCII_A /* 97 */:
                                c3 = 7;
                                break;
                            case 'b':
                                c3 = '\b';
                                break;
                            case LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY /* 102 */:
                                c3 = '\f';
                                break;
                            case 'n':
                                c3 = '\n';
                                break;
                            case 'r':
                                c3 = CharUtils.CR;
                                break;
                            case 't':
                                c3 = '\t';
                                break;
                            case 'v':
                                c3 = 11;
                                break;
                        }
                        current.append(c3);
                    } else if (c2 == '$' && (varEnd = findVarEnd(str, i3)) >= 0) {
                        String key = str.substring(i3, varEnd);
                        String value = vars.get(key);
                        if (value != null) {
                            current.append(value);
                        } else {
                            current.append('$').append(key);
                        }
                        i = varEnd;
                    } else {
                        current.append(c2);
                        i = i3;
                    }
                }
            } else if (c == ' ') {
                if (hasValue) {
                    parts.add(current.toString());
                    current.setLength(0);
                    hasValue = false;
                }
                i++;
            } else if (c == '$' && (varEnd2 = findVarEnd(str, i + 1)) >= 0) {
                hasValue = true;
                String key2 = str.substring(i + 1, varEnd2);
                String value2 = vars.get(key2);
                if (value2 != null) {
                    current.append(value2);
                } else {
                    current.append('$').append(key2);
                }
                i = varEnd2;
            } else {
                hasValue = true;
                current.append(c);
                i++;
            }
        }
        if (hasValue) {
            parts.add(current.toString());
        }
        return parts;
    }

    public static String parseColorEscapes(String original) {
        if (original.indexOf(167) < 0) {
            return original;
        }
        return original.replaceAll("§[0-9a-fk-or]", "");
    }

    public static List<Pair<String, String>> parseMinecraftColorCodes(String original) {
        String newColor;
        List<Pair<String, String>> pairs = new ArrayList<>();
        if (isBlank(original)) {
            return pairs;
        }
        Matcher matcher = COLOR_CODE_PATTERN.matcher(original);
        String currentColor = "";
        int lastIndex = 0;
        while (matcher.find()) {
            String text = original.substring(lastIndex, matcher.start());
            if (!text.isEmpty()) {
                pairs.add(new Pair<>(text, currentColor));
            }
            char code = matcher.group(1).charAt(0);
            switch (code) {
                case '0':
                    newColor = "black";
                    break;
                case '1':
                    newColor = "dark_blue";
                    break;
                case '2':
                    newColor = "dark_green";
                    break;
                case '3':
                    newColor = "dark_aqua";
                    break;
                case TomlParser.RULE_arrayValue /* 52 */:
                    newColor = "dark_red";
                    break;
                case TomlParser.RULE_table /* 53 */:
                    newColor = "dark_purple";
                    break;
                case TomlParser.RULE_standardTable /* 54 */:
                    newColor = "gold";
                    break;
                case TomlParser.RULE_inlineTable /* 55 */:
                    newColor = "gray";
                    break;
                case TomlParser.RULE_inlineTableValues /* 56 */:
                    newColor = "dark_gray";
                    break;
                case TomlParser.RULE_arrayTable /* 57 */:
                    newColor = "blue";
                    break;
                case CharsetProber.ASCII_A /* 97 */:
                    newColor = "green";
                    break;
                case 'b':
                    newColor = "aqua";
                    break;
                case 'c':
                    newColor = "red";
                    break;
                case 'd':
                    newColor = "light_purple";
                    break;
                case 'e':
                    newColor = "yellow";
                    break;
                case LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY /* 102 */:
                    newColor = "white";
                    break;
                case 'k':
                case AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR /* 108 */:
                case AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY /* 109 */:
                case 'n':
                case 'o':
                    newColor = FORMAT_CODE;
                    break;
                case 'r':
                    newColor = "";
                    break;
                default:
                    newColor = null;
                    break;
            }
            if (newColor != null && !newColor.equals(FORMAT_CODE)) {
                currentColor = newColor;
            }
            lastIndex = matcher.end();
        }
        if (lastIndex < original.length()) {
            String remainingText = original.substring(lastIndex);
            pairs.add(new Pair<>(remainingText, currentColor));
        }
        return pairs;
    }

    public static String parseEscapeSequence(String str) {
        int idx = str.indexOf(27);
        if (idx < 0) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str.length());
        boolean inEscape = false;
        builder.append((CharSequence) str, 0, idx);
        for (int i = idx; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 27) {
                inEscape = true;
            }
            if (!inEscape) {
                builder.append(ch);
            }
            if (inEscape && ch == 'm') {
                inEscape = false;
            }
        }
        return builder.toString();
    }

    public static String repeats(char ch, int repeat) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            result.append(ch);
        }
        return result.toString();
    }

    public static String truncate(String str, int limit) {
        if (limit <= 5) {
            throw new AssertionError();
        }
        if (str.length() <= limit) {
            return str;
        }
        int halfLength = (limit - 5) / 2;
        return str.substring(0, halfLength) + " ... " + str.substring(str.length() - halfLength);
    }

    public static boolean isASCII(String cs) {
        for (int i = 0; i < cs.length(); i++) {
            if (cs.charAt(i) >= 128) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphabeticOrNumber(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z'))) {
                return false;
            }
        }
        return true;
    }

    public static class LevCalculator {
        private int[][] lev;

        public LevCalculator() {
        }

        public LevCalculator(int length1, int length2) {
            allocate(length1, length2);
        }

        private void allocate(int length1, int length2) {
            int length3 = length1 + 1;
            int length4 = length2 + 1;
            this.lev = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, length3, length4);
            for (int i = 1; i < length3; i++) {
                this.lev[i][0] = i;
            }
            int[] cache = this.lev[0];
            for (int i2 = 0; i2 < length4; i2++) {
                cache[i2] = i2;
            }
        }

        public int getLength1() {
            return this.lev.length;
        }

        public int getLength2() {
            return this.lev[0].length;
        }

        private int min(int a, int b, int c) {
            return Math.min(a, Math.min(b, c));
        }

        public int calc(CharSequence a, CharSequence b) {
            if (this.lev == null || a.length() >= this.lev.length || b.length() >= this.lev[0].length) {
                allocate(a.length(), b.length());
            }
            int lengthA = a.length() + 1;
            int lengthB = b.length() + 1;
            for (int i = 1; i < lengthA; i++) {
                for (int j = 1; j < lengthB; j++) {
                    this.lev[i][j] = min(this.lev[i][j - 1] + 1, this.lev[i - 1][j] + 1, a.charAt(i + (-1)) == b.charAt(j + (-1)) ? this.lev[i - 1][j - 1] : this.lev[i - 1][j - 1] + 1);
                }
            }
            return this.lev[a.length()][b.length()];
        }
    }

    public static final class LongestCommonSubsequence {
        private final int[][] f;
        private final int maxLengthA;
        private final int maxLengthB;

        public LongestCommonSubsequence(int maxLengthA, int maxLengthB) {
            this.maxLengthA = maxLengthA;
            this.maxLengthB = maxLengthB;
            this.f = new int[maxLengthA + 1][];
            for (int i = 0; i <= maxLengthA; i++) {
                this.f[i] = new int[maxLengthB + 1];
            }
        }

        public int calc(CharSequence a, CharSequence b) {
            if (a.length() > this.maxLengthA || b.length() > this.maxLengthB) {
                throw new IllegalArgumentException("Too large length");
            }
            for (int i = 1; i <= a.length(); i++) {
                for (int j = 1; j <= b.length(); j++) {
                    if (a.charAt(i - 1) == b.charAt(j - 1)) {
                        this.f[i][j] = this.f[i - 1][j - 1] + 1;
                    } else {
                        this.f[i][j] = Math.max(this.f[i - 1][j], this.f[i][j - 1]);
                    }
                }
            }
            return this.f[a.length()][b.length()];
        }
    }
}
