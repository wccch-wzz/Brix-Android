package com.brixcore.util.png;

/* JADX INFO: loaded from: classes12.dex */
public enum PNGFilterType {
    NONE(0),
    SUB(1),
    UP(2),
    AVERAGE(3),
    PAETH(4);

    final int id;

    PNGFilterType(int id) {
        this.id = id;
    }
}
