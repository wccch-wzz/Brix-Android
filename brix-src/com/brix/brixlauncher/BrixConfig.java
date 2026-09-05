package com.brix.brixlauncher;

import android.content.Context;

/* JADX INFO: loaded from: classes18.dex */
public class BrixConfig {
    private final String[] commandLine;
    private final Context context;
    private final String gameDir;
    private final String javaPath;
    private final String logDir;
    private final Object renderer;

    public BrixConfig(Context context, String logDir, String javaPath, String gameDir, Object renderer, String[] commandLine) {
        this.context = context;
        this.logDir = logDir;
        this.javaPath = javaPath;
        this.gameDir = gameDir;
        this.renderer = renderer;
        this.commandLine = commandLine;
    }

    public Context getContext() {
        return this.context;
    }

    public String getLogDir() {
        return this.logDir;
    }

    public String getJavaPath() {
        return this.javaPath;
    }

    public String getGameDir() {
        return this.gameDir;
    }

    public Object getRenderer() {
        return this.renderer;
    }

    public String[] getCommandLine() {
        return this.commandLine;
    }

    public void setUseVKDriverSystem(boolean useVKDriverSystem) {
    }

    public void setPojavBigCore(boolean pojavBigCore) {
    }

    public void setInstalledModLoaders(InstalledModLoaders loaders) {
    }

    public void setLwjglVersion(String version) {
    }

    public static class InstalledModLoaders {
        public final boolean cleanroom;
        public final boolean fabric;
        public final boolean forge;
        public final boolean liteloader;
        public final boolean neoForge;
        public final boolean optifine;
        public final boolean quilt;

        public InstalledModLoaders(boolean forge, boolean cleanroom, boolean neoForge, boolean optifine, boolean liteloader, boolean fabric, boolean quilt) {
            this.forge = forge;
            this.cleanroom = cleanroom;
            this.neoForge = neoForge;
            this.optifine = optifine;
            this.liteloader = liteloader;
            this.fabric = fabric;
            this.quilt = quilt;
        }
    }
}
