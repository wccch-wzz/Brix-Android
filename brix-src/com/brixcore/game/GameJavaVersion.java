package com.brixcore.game;

/* JADX INFO: loaded from: classes2.dex */
public class GameJavaVersion {
    private final String component;
    private final int majorVersion;
    public static final GameJavaVersion JAVA_17 = new GameJavaVersion("java-runtime-beta", 17);
    public static final GameJavaVersion JAVA_16 = new GameJavaVersion("java-runtime-alpha", 16);
    public static final GameJavaVersion JAVA_8 = new GameJavaVersion("jre-legacy", 8);

    public GameJavaVersion() {
        this("", 0);
    }

    public GameJavaVersion(String component, int majorVersion) {
        this.component = component;
        this.majorVersion = majorVersion;
    }

    public String getComponent() {
        return this.component;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }
}
