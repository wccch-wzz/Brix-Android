package com.brixcore.util.png.image;

/* JADX INFO: loaded from: classes8.dex */
public final class ArgbImageBuffer implements ArgbImage {
    private final int[] colors;
    private final int height;
    private final int width;

    public ArgbImageBuffer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.colors = new int[width * height];
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getWidth() {
        return this.width;
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getHeight() {
        return this.height;
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getArgb(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IllegalArgumentException();
        }
        return this.colors[(this.width * y) + x];
    }

    public void setArgb(int x, int y, int color) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IllegalArgumentException();
        }
        this.colors[(this.width * y) + x] = color;
    }

    public void setArgb(int x, int y, int a, int r, int g, int b) {
        setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
    }

    public void setRgb(int x, int y, int r, int g, int b) {
        setArgb(x, y, 255, r, g, b);
    }
}
