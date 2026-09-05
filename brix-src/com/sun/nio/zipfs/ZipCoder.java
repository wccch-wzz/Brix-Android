package com.sun.nio.zipfs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import org.apache.commons.lang3.CharEncoding;

/* JADX INFO: loaded from: classes2.dex */
final class ZipCoder {
    private Charset cs;
    private final ThreadLocal<CharsetDecoder> decTL = new ThreadLocal<>();
    private final ThreadLocal<CharsetEncoder> encTL = new ThreadLocal<>();
    private boolean isutf8;
    private ZipCoder utf8;

    String toString(byte[] ba, int length) {
        CharsetDecoder cd = decoder().reset();
        int len = (int) (length * cd.maxCharsPerByte());
        char[] ca = new char[len];
        if (len == 0) {
            return new String(ca);
        }
        ByteBuffer bb = ByteBuffer.wrap(ba, 0, length);
        CharBuffer cb = CharBuffer.wrap(ca);
        CoderResult cr = cd.decode(bb, cb, true);
        if (!cr.isUnderflow()) {
            throw new IllegalArgumentException(cr.toString());
        }
        CoderResult cr2 = cd.flush(cb);
        if (!cr2.isUnderflow()) {
            throw new IllegalArgumentException(cr2.toString());
        }
        return new String(ca, 0, cb.position());
    }

    String toString(byte[] ba) {
        return toString(ba, ba.length);
    }

    byte[] getBytes(String s) {
        CharsetEncoder ce = encoder().reset();
        char[] ca = s.toCharArray();
        int len = (int) (ca.length * ce.maxBytesPerChar());
        byte[] ba = new byte[len];
        if (len == 0) {
            return ba;
        }
        ByteBuffer bb = ByteBuffer.wrap(ba);
        CharBuffer cb = CharBuffer.wrap(ca);
        CoderResult cr = ce.encode(cb, bb, true);
        if (!cr.isUnderflow()) {
            throw new IllegalArgumentException(cr.toString());
        }
        CoderResult cr2 = ce.flush(bb);
        if (!cr2.isUnderflow()) {
            throw new IllegalArgumentException(cr2.toString());
        }
        if (bb.position() == ba.length) {
            return ba;
        }
        return Arrays.copyOf(ba, bb.position());
    }

    byte[] getBytesUTF8(String s) {
        if (this.isutf8) {
            return getBytes(s);
        }
        if (this.utf8 == null) {
            this.utf8 = new ZipCoder(Charset.forName(CharEncoding.UTF_8));
        }
        return this.utf8.getBytes(s);
    }

    String toStringUTF8(byte[] ba, int len) {
        if (this.isutf8) {
            return toString(ba, len);
        }
        if (this.utf8 == null) {
            this.utf8 = new ZipCoder(Charset.forName(CharEncoding.UTF_8));
        }
        return this.utf8.toString(ba, len);
    }

    boolean isUTF8() {
        return this.isutf8;
    }

    private ZipCoder(Charset cs) {
        this.cs = cs;
        this.isutf8 = cs.name().equals(CharEncoding.UTF_8);
    }

    static ZipCoder get(Charset charset) {
        return new ZipCoder(charset);
    }

    static ZipCoder get(String csn) {
        try {
            return new ZipCoder(Charset.forName(csn));
        } catch (Throwable t) {
            t.printStackTrace();
            return new ZipCoder(Charset.defaultCharset());
        }
    }

    private CharsetDecoder decoder() {
        CharsetDecoder dec = this.decTL.get();
        if (dec == null) {
            CharsetDecoder dec2 = this.cs.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            this.decTL.set(dec2);
            return dec2;
        }
        return dec;
    }

    private CharsetEncoder encoder() {
        CharsetEncoder enc = this.encTL.get();
        if (enc == null) {
            CharsetEncoder enc2 = this.cs.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            this.encTL.set(enc2);
            return enc2;
        }
        return enc;
    }
}
