package com.github.junrar.unpack.ppm;

/* JADX INFO: loaded from: classes.dex */
public enum BlockTypes {
    BLOCK_LZ(0),
    BLOCK_PPM(1);

    private final int blockType;

    BlockTypes(int blockType) {
        this.blockType = blockType;
    }

    public int getBlockType() {
        return this.blockType;
    }

    public boolean equals(int blockType) {
        return this.blockType == blockType;
    }

    public static BlockTypes findBlockType(int blockType) {
        if (BLOCK_LZ.equals(blockType)) {
            return BLOCK_LZ;
        }
        if (BLOCK_PPM.equals(blockType)) {
            return BLOCK_PPM;
        }
        return null;
    }
}
