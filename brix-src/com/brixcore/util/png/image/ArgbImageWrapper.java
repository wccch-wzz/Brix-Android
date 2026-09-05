package com.brixcore.util.png.image;

import java.util.Objects;

/* JADX INFO: loaded from: classes8.dex */
public abstract class ArgbImageWrapper<T> implements ArgbImage {
    protected final int height;
    protected final T image;
    protected final int width;

    @FunctionalInterface
    public interface ColorExtractor<T> {
        int getArgb(T t, int i, int i2);
    }

    protected ArgbImageWrapper(T image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid picture size");
        }
    }

    public T getImage() {
        return this.image;
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getWidth() {
        return this.width;
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getHeight() {
        return this.height;
    }

    public static <T> ArgbImageWrapper<T> of(T image, int width, int height, final ColorExtractor<T> extractor) {
        Objects.requireNonNull(extractor);
        return new ArgbImageWrapper<T>(image, width, height) { // from class: com.brixcore.util.png.image.ArgbImageWrapper.1
            @Override // com.brixcore.util.png.image.ArgbImage
            public int getArgb(int x, int y) {
                return extractor.getArgb(this.image, x, y);
            }
        };
    }
}
