package com.brixcore.util.png.image;

import com.brixcore.util.png.PNGMetadata;

/* JADX INFO: loaded from: classes8.dex */
public interface ArgbImage {
    int getArgb(int i, int i2);

    int getHeight();

    int getWidth();

    default PNGMetadata getMetadata() {
        return null;
    }

    default ArgbImage withMetadata(PNGMetadata metadata) {
        return new ArgbImageWithMetadata(this, metadata);
    }

    default ArgbImage withDefaultMetadata() {
        return withMetadata(new PNGMetadata().setAuthor().setCreationTime());
    }
}
