package com.brixcore.util.platform;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes7.dex */
public enum OperatingSystem {
    WINDOWS("windows"),
    LINUX("linux"),
    OSX("osx"),
    UNKNOWN("universal");

    private static final Pattern ILLEGAL_CHARS_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]");
    public static final Charset NATIVE_CHARSET;
    private final String checkedName;

    /* JADX WARN: Code duplicated, block: B:17:0x0082 A[Catch: UnsupportedCharsetException -> 0x0086, TRY_LEAVE, TryCatch #0 {UnsupportedCharsetException -> 0x0086, blocks: (B:4:0x004a, B:6:0x0054, B:7:0x0059, B:9:0x005d, B:12:0x0062, B:14:0x006e, B:16:0x007a, B:17:0x0082), top: B:24:0x004a }] */
    static {
        String nativeEncoding = System.getProperty("native.encoding");
        Charset nativeCharset = Charset.defaultCharset();
        if (nativeEncoding != null) {
            try {
                if (!nativeEncoding.equalsIgnoreCase(nativeCharset.name())) {
                    nativeCharset = Charset.forName(nativeEncoding);
                }
                if (nativeCharset == StandardCharsets.UTF_8 && nativeCharset != StandardCharsets.US_ASCII) {
                    if ("GBK".equalsIgnoreCase(nativeCharset.name()) || "GB2312".equalsIgnoreCase(nativeCharset.name())) {
                        nativeCharset = Charset.forName("GB18030");
                    }
                }
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        } else {
            nativeCharset = nativeCharset == StandardCharsets.UTF_8 ? StandardCharsets.UTF_8 : StandardCharsets.UTF_8;
        }
        NATIVE_CHARSET = nativeCharset;
    }

    OperatingSystem(String checkedName) {
        this.checkedName = checkedName;
    }

    public String getCheckedName() {
        return this.checkedName;
    }

    public static boolean isNameValid(String name) {
        if (!name.isEmpty() && !name.equals(".") && name.indexOf(47) == -1 && name.indexOf(0) == -1) {
            return !ILLEGAL_CHARS_PATTERN.matcher(name).find();
        }
        return false;
    }
}
