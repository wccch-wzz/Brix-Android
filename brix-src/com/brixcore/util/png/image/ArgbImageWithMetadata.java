package com.brixcore.util.png.image;

import com.brixcore.util.png.PNGMetadata;

/* JADX INFO: loaded from: classes8.dex */
final class ArgbImageWithMetadata implements ArgbImage {
    private final PNGMetadata metadata;
    private final ArgbImage source;

    ArgbImageWithMetadata(ArgbImage source, PNGMetadata metadata) {
        this.source = source;
        this.metadata = metadata;
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getWidth() {
        return this.source.getWidth();
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getHeight() {
        return this.source.getHeight();
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public int getArgb(int x, int y) {
        return this.source.getArgb(x, y);
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public PNGMetadata getMetadata() {
        return this.metadata;
    }

    @Override // com.brixcore.util.png.image.ArgbImage
    public ArgbImage withMetadata(PNGMetadata metadata) {
        return new ArgbImageWithMetadata(this.source, metadata);
    }
}
