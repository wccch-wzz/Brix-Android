package com.brixcore.download;

/* JADX INFO: loaded from: classes14.dex */
public class UnsupportedInstallationException extends Exception {
    public static final int FABRIC_NOT_COMPATIBLE_WITH_FORGE = 3;
    public static final int FORGE_1_17_OPTIFINE_H1_PRE2 = 2;
    public static final int UNSUPPORTED_LAUNCH_WRAPPER = 1;
    private final int reason;

    public UnsupportedInstallationException(int reason) {
        this.reason = reason;
    }

    public int getReason() {
        return this.reason;
    }
}
