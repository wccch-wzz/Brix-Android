package com.github.junrar.rarfile;

/* JADX INFO: loaded from: classes.dex */
public enum RARVersion {
    OLD,
    V4,
    V5;

    public static boolean isOldFormat(RARVersion version) {
        return version == OLD;
    }
}
