package com.brixcore.auth.offline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.brixcore.util.Hex;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes14.dex */
public final class Texture {
    private static final Map<String, Texture> textures = new HashMap();
    private final String hash;
    private final Bitmap image;

    public Texture(String hash, Bitmap image) {
        this.hash = (String) Objects.requireNonNull(hash);
        this.image = (Bitmap) Objects.requireNonNull(image);
    }

    public String getHash() {
        return this.hash;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public static boolean hasTexture(String hash) {
        return textures.containsKey(hash);
    }

    public static Texture getTexture(String hash) {
        return textures.get(hash);
    }

    private static String computeTextureHash(Bitmap img) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            int width = img.getWidth();
            int height = img.getHeight();
            byte[] buf = new byte[4096];
            putInt(buf, 0, width);
            putInt(buf, 4, height);
            int pos = 8;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    putInt(buf, pos, img.getPixel(x, y));
                    if (buf[pos + 0] == 0) {
                        buf[pos + 3] = 0;
                        buf[pos + 2] = 0;
                        buf[pos + 1] = 0;
                    }
                    pos += 4;
                    if (pos == buf.length) {
                        pos = 0;
                        digest.update(buf, 0, buf.length);
                    }
                }
            }
            if (pos > 0) {
                digest.update(buf, 0, pos);
            }
            return Hex.encodeHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void putInt(byte[] array, int offset, int x) {
        array[offset + 0] = (byte) ((x >> 24) & 255);
        array[offset + 1] = (byte) ((x >> 16) & 255);
        array[offset + 2] = (byte) ((x >> 8) & 255);
        array[offset + 3] = (byte) ((x >> 0) & 255);
    }

    public static Texture loadTexture(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        try {
            Bitmap img = BitmapFactory.decodeStream(in);
            if (in != null) {
                in.close();
            }
            return loadTexture(img);
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static Texture loadTexture(Bitmap image) {
        if (image == null) {
            return null;
        }
        String hash = computeTextureHash(image);
        Texture existent = textures.get(hash);
        if (existent != null) {
            return existent;
        }
        Texture texture = new Texture(hash, image);
        Texture existent2 = textures.putIfAbsent(hash, texture);
        if (existent2 != null) {
            return existent2;
        }
        return texture;
    }
}
