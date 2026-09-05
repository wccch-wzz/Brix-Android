package com.brixcore.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes11.dex */
public final class DigestUtils {
    private static final int STREAM_BUFFER_LENGTH = 1024;
    private static final ThreadLocal<byte[]> threadLocalBuffer = ThreadLocal.withInitial(new Supplier() { // from class: com.brixcore.util.DigestUtils$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return DigestUtils.lambda$static$0();
        }
    });

    private DigestUtils() {
    }

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static byte[] digest(String algorithm, byte[] data) {
        return getDigest(algorithm).digest(data);
    }

    public static byte[] digest(String algorithm, Path path) throws IOException {
        InputStream is = Files.newInputStream(path, new OpenOption[0]);
        try {
            byte[] bArrDigest = digest(algorithm, is);
            if (is != null) {
                is.close();
            }
            return bArrDigest;
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static byte[] digest(String algorithm, InputStream data) throws IOException {
        return digest(getDigest(algorithm), data);
    }

    public static byte[] digest(MessageDigest digest, InputStream data) throws IOException {
        return updateDigest(digest, data).digest();
    }

    public static String digestToString(String algorithm, byte[] data) throws IOException {
        return Hex.encodeHex(digest(algorithm, data));
    }

    public static String digestToString(String algorithm, Path path) throws IOException {
        return Hex.encodeHex(digest(algorithm, path));
    }

    public static String digestToString(String algorithm, InputStream data) throws IOException {
        return Hex.encodeHex(digest(algorithm, data));
    }

    static /* synthetic */ byte[] lambda$static$0() {
        return new byte[1024];
    }

    public static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = threadLocalBuffer.get();
        int read = data.read(buffer, 0, 1024);
        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer, 0, 1024);
        }
        return digest;
    }
}
