package com.brixcore.util.skin;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes10.dex */
public final class NormalizedSkin {
    private final Bitmap normalizedTexture;
    private final boolean oldFormat;
    private final int scale;
    private final Bitmap texture;

    private static void copyImage(Bitmap src, Bitmap dst, int sx, int sy, int dx, int dy, int w, int h, boolean flipHorizontal) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = src.getPixel(sx + x, sy + y);
                dst.setPixel((flipHorizontal ? (w - x) - 1 : x) + dx, dy + y, pixel);
            }
        }
    }

    public NormalizedSkin(Bitmap texture) throws InvalidSkinException {
        this.texture = texture;
        int w = texture.getWidth();
        int h = texture.getHeight();
        if (w % 64 != 0) {
            throw new InvalidSkinException("Invalid size " + w + "x" + h);
        }
        if (w == h) {
            this.oldFormat = false;
        } else {
            if (w != h * 2) {
                throw new InvalidSkinException("Invalid size " + w + "x" + h);
            }
            this.oldFormat = true;
        }
        this.scale = w / 64;
        this.normalizedTexture = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
        copyImage(texture, this.normalizedTexture, 0, 0, 0, 0, w, h, false);
        if (this.oldFormat) {
            convertOldSkin();
        }
    }

    private void convertOldSkin() {
        copyImageRelative(4, 16, 20, 48, 4, 4, true);
        copyImageRelative(8, 16, 24, 48, 4, 4, true);
        copyImageRelative(0, 20, 24, 52, 4, 12, true);
        copyImageRelative(4, 20, 20, 52, 4, 12, true);
        copyImageRelative(8, 20, 16, 52, 4, 12, true);
        copyImageRelative(12, 20, 28, 52, 4, 12, true);
        copyImageRelative(44, 16, 36, 48, 4, 4, true);
        copyImageRelative(48, 16, 40, 48, 4, 4, true);
        copyImageRelative(40, 20, 40, 52, 4, 12, true);
        copyImageRelative(44, 20, 36, 52, 4, 12, true);
        copyImageRelative(48, 20, 32, 52, 4, 12, true);
        copyImageRelative(52, 20, 44, 52, 4, 12, true);
    }

    private void copyImageRelative(int sx, int sy, int dx, int dy, int w, int h, boolean flipHorizontal) {
        copyImage(this.normalizedTexture, this.normalizedTexture, this.scale * sx, this.scale * sy, this.scale * dx, this.scale * dy, this.scale * w, this.scale * h, flipHorizontal);
    }

    public Bitmap getOriginalTexture() {
        return this.texture;
    }

    public Bitmap getNormalizedTexture() {
        return this.normalizedTexture;
    }

    public int getScale() {
        return this.scale;
    }

    public boolean isOldFormat() {
        return this.oldFormat;
    }

    public boolean isSlim() {
        return hasTransparencyRelative(50, 16, 2, 4) || hasTransparencyRelative(54, 20, 2, 12) || hasTransparencyRelative(42, 48, 2, 4) || hasTransparencyRelative(46, 52, 2, 12) || (isAreaBlackRelative(50, 16, 2, 4) && isAreaBlackRelative(54, 20, 2, 12) && isAreaBlackRelative(42, 48, 2, 4) && isAreaBlackRelative(46, 52, 2, 12));
    }

    private boolean hasTransparencyRelative(int x0, int y0, int w, int h) {
        int x1 = x0 * this.scale;
        int y1 = y0 * this.scale;
        int w2 = w * this.scale;
        int h2 = h * this.scale;
        for (int y = 0; y < h2; y++) {
            for (int x = 0; x < w2; x++) {
                int pixel = this.normalizedTexture.getPixel(x1 + x, y1 + y);
                if ((pixel >>> 24) != 255) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAreaBlackRelative(int x0, int y0, int w, int h) {
        int x1 = x0 * this.scale;
        int y1 = y0 * this.scale;
        int w2 = w * this.scale;
        int h2 = h * this.scale;
        for (int y = 0; y < h2; y++) {
            for (int x = 0; x < w2; x++) {
                int pixel = this.normalizedTexture.getPixel(x1 + x, y1 + y);
                if (pixel != -16777216) {
                    return false;
                }
            }
        }
        return true;
    }
}
