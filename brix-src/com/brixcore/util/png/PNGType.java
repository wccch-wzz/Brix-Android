package com.brixcore.util.png;

/* JADX INFO: loaded from: classes12.dex */
public enum PNGType {
    GRAYSCALE(0, 1),
    RGB(2, 3),
    PALETTE(3, 1),
    GRAYSCALE_ALPHA(4, 2),
    RGBA(6, 4);

    final int cpp;
    final int id;

    PNGType(int id, int cpp) {
        this.id = id;
        this.cpp = cpp;
    }
}
