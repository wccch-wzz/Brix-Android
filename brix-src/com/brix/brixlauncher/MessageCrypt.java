package com.brix.brixlauncher;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes18.dex */
public class MessageCrypt {
    private static final String ALGORITHM = "AES";
    private static final Base64.Decoder B64_DECODER = Base64.getDecoder();
    private static final SecretKeySpec KEY_SPEC;
    private static final String KEY_STRING = "brix2026chatmsg!@#secureKey32b";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    static {
        byte[] keyBytes = KEY_STRING.getBytes(StandardCharsets.UTF_8);
        byte[] key32 = new byte[32];
        System.arraycopy(keyBytes, 0, key32, 0, Math.min(keyBytes.length, 32));
        KEY_SPEC = new SecretKeySpec(key32, ALGORITHM);
    }

    public static String decrypt(String encryptedBase64) throws Exception {
        byte[] raw = B64_DECODER.decode(encryptedBase64);
        if (raw.length < 17) {
            throw new Exception("密文过短");
        }
        byte[] iv = new byte[16];
        System.arraycopy(raw, 0, iv, 0, 16);
        byte[] ciphertext = new byte[raw.length - 16];
        System.arraycopy(raw, 16, ciphertext, 0, ciphertext.length);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(2, KEY_SPEC, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(ciphertext);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
