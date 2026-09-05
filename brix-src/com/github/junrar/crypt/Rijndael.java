package com.github.junrar.crypt;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class Rijndael {
    public static Cipher buildDecipherer(String password, byte[] salt) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (password == null) {
            throw new InvalidAlgorithmParameterException("password should be specified");
        }
        byte[] AESInit = new byte[16];
        byte[] AESKey = new byte[16];
        int rawLength = password.length() * 2;
        byte[] rawpsw = new byte[rawLength + 8];
        byte[] pwd = password.getBytes();
        for (int i = 0; i < password.length(); i++) {
            rawpsw[i * 2] = pwd[i];
            rawpsw[(i * 2) + 1] = 0;
        }
        int i2 = salt.length;
        System.arraycopy(salt, 0, rawpsw, rawLength, i2);
        MessageDigest sha = MessageDigest.getInstance("sha-1");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i3 = 0; i3 < 262144; i3++) {
            bout.write(rawpsw);
            bout.write((byte) i3);
            bout.write((byte) (i3 >>> 8));
            bout.write((byte) (i3 >>> 16));
            if (i3 % 16384 == 0) {
                byte[] input = bout.toByteArray();
                sha.update(input);
                AESInit[i3 / 16384] = sha.digest()[19];
            }
        }
        sha.update(bout.toByteArray());
        byte[] digest = sha.digest();
        int i4 = 0;
        while (true) {
            if (i4 < 4) {
                int j = 0;
                for (int i5 = 4; j < i5; i5 = 4) {
                    AESKey[(i4 * 4) + j] = (byte) (((digest[(i4 * 4) + 3] & UByte.MAX_VALUE) | ((((digest[i4 * 4] * 16777216) & ViewCompat.MEASURED_STATE_MASK) | ((digest[(i4 * 4) + 1] * 65536) & 16711680)) | ((digest[(i4 * 4) + 2] * 256) & MotionEventCompat.ACTION_POINTER_INDEX_MASK))) >>> (j * 8));
                    j++;
                }
                i4++;
            } else {
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(2, new SecretKeySpec(AESKey, "AES"), new IvParameterSpec(AESInit));
                return cipher;
            }
        }
    }
}
