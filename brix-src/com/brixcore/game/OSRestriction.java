package com.brixcore.game;

import com.brixcore.util.platform.OperatingSystem;

/* JADX INFO: loaded from: classes2.dex */
public final class OSRestriction {
    private final String arch;
    private final OperatingSystem name;
    private final String version;

    public OSRestriction() {
        this(OperatingSystem.UNKNOWN);
    }

    public OSRestriction(OperatingSystem name) {
        this(name, null);
    }

    public OSRestriction(OperatingSystem name, String version) {
        this(name, version, null);
    }

    public OSRestriction(OperatingSystem name, String version, String arch) {
        this.name = name;
        this.version = version;
        this.arch = arch;
    }

    public OperatingSystem getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getArch() {
        return this.arch;
    }

    public boolean allow() {
        return false;
    }
}
