package com.brixcore.game;

import androidx.core.os.EnvironmentCompat;

/* JADX INFO: loaded from: classes2.dex */
public enum ReleaseType {
    RELEASE("release"),
    SNAPSHOT("snapshot"),
    MODIFIED("modified"),
    OLD_BETA("old-beta"),
    OLD_ALPHA("old-alpha"),
    PENDING("pending"),
    UNOBFUSCATED("unobfuscated"),
    UNKNOWN(EnvironmentCompat.MEDIA_UNKNOWN);

    private final String id;

    ReleaseType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
