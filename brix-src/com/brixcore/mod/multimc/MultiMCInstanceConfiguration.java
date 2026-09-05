package com.brixcore.mod.multimc;

import com.brixcore.mod.ModpackManifest;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.util.Lang;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes10.dex */
public final class MultiMCInstanceConfiguration implements ModpackManifest {
    private final boolean autoCloseConsole;
    private final boolean fullscreen;
    private final String gameVersion;
    private final Integer height;
    private final String iconKey;
    private final String instanceType;
    private final String javaPath;
    private final String jvmArgs;
    private final Integer maxMemory;
    private final Integer minMemory;
    private final MultiMCManifest mmcPack;
    private final String name;
    private final String notes;
    private final boolean overrideCommands;
    private final boolean overrideConsole;
    private final boolean overrideJavaArgs;
    private final boolean overrideJavaLocation;
    private final boolean overrideMemory;
    private final boolean overrideWindow;
    private final Integer permGen;
    private final String postExitCommand;
    private final String preLaunchCommand;
    private final boolean showConsole;
    private final boolean showConsoleOnError;
    private final Integer width;
    private final String wrapperCommand;

    MultiMCInstanceConfiguration(String defaultName, InputStream contentStream, MultiMCManifest mmcPack) throws IOException {
        Properties p = new Properties();
        p.load(new InputStreamReader(contentStream, StandardCharsets.UTF_8));
        this.mmcPack = mmcPack;
        this.instanceType = readValue(p, "InstanceType");
        this.autoCloseConsole = Boolean.parseBoolean(readValue(p, "AutoCloseConsole"));
        this.gameVersion = mmcPack != null ? mmcPack.getComponents().stream().filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCInstanceConfiguration$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return "net.minecraft".equals(((MultiMCManifest.MultiMCManifestComponent) obj).getUid());
            }
        }).findAny().orElseThrow(new Supplier() { // from class: com.brixcore.mod.multimc.MultiMCInstanceConfiguration$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return MultiMCInstanceConfiguration.lambda$new$1();
            }
        }).getVersion() : readValue(p, "IntendedVersion");
        this.javaPath = readValue(p, "JavaPath");
        this.jvmArgs = readValue(p, "JvmArgs");
        this.fullscreen = Boolean.parseBoolean(readValue(p, "LaunchMaximized"));
        this.maxMemory = Lang.toIntOrNull(readValue(p, "MaxMemAlloc"));
        this.minMemory = Lang.toIntOrNull(readValue(p, "MinMemAlloc"));
        this.height = Lang.toIntOrNull(readValue(p, "MinecraftWinHeight"));
        this.width = Lang.toIntOrNull(readValue(p, "MinecraftWinWidth"));
        this.overrideCommands = Boolean.parseBoolean(readValue(p, "OverrideCommands"));
        this.overrideConsole = Boolean.parseBoolean(readValue(p, "OverrideConsole"));
        this.overrideJavaArgs = Boolean.parseBoolean(readValue(p, "OverrideJavaArgs"));
        this.overrideJavaLocation = Boolean.parseBoolean(readValue(p, "OverrideJavaLocation"));
        this.overrideMemory = Boolean.parseBoolean(readValue(p, "OverrideMemory"));
        this.overrideWindow = Boolean.parseBoolean(readValue(p, "OverrideWindow"));
        this.permGen = Lang.toIntOrNull(readValue(p, "PermGen"));
        this.postExitCommand = readValue(p, "PostExitCommand");
        this.preLaunchCommand = readValue(p, "PreLaunchCommand");
        this.showConsole = Boolean.parseBoolean(readValue(p, "ShowConsole"));
        this.showConsoleOnError = Boolean.parseBoolean(readValue(p, "ShowConsoleOnError"));
        this.wrapperCommand = readValue(p, "WrapperCommand");
        this.name = defaultName;
        this.notes = (String) Optional.ofNullable(readValue(p, "notes")).orElse("");
        this.iconKey = readValue(p, "iconKey");
    }

    static /* synthetic */ IOException lambda$new$1() {
        return new IOException("Malformed mmc-pack.json");
    }

    private String readValue(Properties p, String key) {
        String value = p.getProperty(key);
        if (value == null) {
            return null;
        }
        int l = value.length();
        if (l >= 2 && value.charAt(0) == '\"' && value.charAt(l - 1) == ':') {
            return value.substring(0, l - 1);
        }
        return value;
    }

    public MultiMCInstanceConfiguration(String instanceType, String name, String gameVersion, Integer permGen, String wrapperCommand, String preLaunchCommand, String postExitCommand, String notes, String javaPath, String jvmArgs, boolean fullscreen, Integer width, Integer height, Integer maxMemory, Integer minMemory, boolean showConsole, boolean showConsoleOnError, boolean autoCloseConsole, boolean overrideMemory, boolean overrideJavaLocation, boolean overrideJavaArgs, boolean overrideConsole, boolean overrideCommands, boolean overrideWindow, String iconKey) {
        this.instanceType = instanceType;
        this.name = name;
        this.gameVersion = gameVersion;
        this.permGen = permGen;
        this.wrapperCommand = wrapperCommand;
        this.preLaunchCommand = preLaunchCommand;
        this.postExitCommand = postExitCommand;
        this.notes = notes;
        this.javaPath = javaPath;
        this.jvmArgs = jvmArgs;
        this.fullscreen = fullscreen;
        this.width = width;
        this.height = height;
        this.maxMemory = maxMemory;
        this.minMemory = minMemory;
        this.showConsole = showConsole;
        this.showConsoleOnError = showConsoleOnError;
        this.autoCloseConsole = autoCloseConsole;
        this.overrideMemory = overrideMemory;
        this.overrideJavaLocation = overrideJavaLocation;
        this.overrideJavaArgs = overrideJavaArgs;
        this.overrideConsole = overrideConsole;
        this.overrideCommands = overrideCommands;
        this.overrideWindow = overrideWindow;
        this.mmcPack = null;
        this.iconKey = iconKey;
    }

    public String getInstanceType() {
        return this.instanceType;
    }

    public String getName() {
        return this.name;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public Integer getPermGen() {
        return this.permGen;
    }

    public String getWrapperCommand() {
        return this.wrapperCommand;
    }

    public String getPreLaunchCommand() {
        return this.preLaunchCommand;
    }

    public String getPostExitCommand() {
        return this.postExitCommand;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getJavaPath() {
        return this.javaPath;
    }

    public String getJvmArgs() {
        return this.jvmArgs;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public Integer getMaxMemory() {
        return this.maxMemory;
    }

    public Integer getMinMemory() {
        return this.minMemory;
    }

    public boolean isShowConsole() {
        return this.showConsole;
    }

    public boolean isShowConsoleOnError() {
        return this.showConsoleOnError;
    }

    public boolean isAutoCloseConsole() {
        return this.autoCloseConsole;
    }

    public boolean isOverrideMemory() {
        return this.overrideMemory;
    }

    public boolean isOverrideJavaLocation() {
        return this.overrideJavaLocation;
    }

    public boolean isOverrideJavaArgs() {
        return this.overrideJavaArgs;
    }

    public boolean isOverrideConsole() {
        return this.overrideConsole;
    }

    public boolean isOverrideCommands() {
        return this.overrideCommands;
    }

    public boolean isOverrideWindow() {
        return this.overrideWindow;
    }

    public String getIconKey() {
        return this.iconKey;
    }

    public Properties toProperties() {
        Properties p = new Properties();
        if (this.instanceType != null) {
            p.setProperty("InstanceType", this.instanceType);
        }
        p.setProperty("AutoCloseConsole", Boolean.toString(this.autoCloseConsole));
        if (this.gameVersion != null) {
            p.setProperty("IntendedVersion", this.gameVersion);
        }
        if (this.javaPath != null) {
            p.setProperty("JavaPath", this.javaPath);
        }
        if (this.jvmArgs != null) {
            p.setProperty("JvmArgs", this.jvmArgs);
        }
        p.setProperty("LaunchMaximized", Boolean.toString(this.fullscreen));
        if (this.maxMemory != null) {
            p.setProperty("MaxMemAlloc", Integer.toString(this.maxMemory.intValue()));
        }
        if (this.minMemory != null) {
            p.setProperty("MinMemAlloc", Integer.toString(this.minMemory.intValue()));
        }
        if (this.height != null) {
            p.setProperty("MinecraftWinHeight", Integer.toString(this.height.intValue()));
        }
        if (this.width != null) {
            p.setProperty("MinecraftWinWidth", Integer.toString(this.width.intValue()));
        }
        p.setProperty("OverrideCommands", Boolean.toString(this.overrideCommands));
        p.setProperty("OverrideConsole", Boolean.toString(this.overrideConsole));
        p.setProperty("OverrideJavaArgs", Boolean.toString(this.overrideJavaArgs));
        p.setProperty("OverrideJavaLocation", Boolean.toString(this.overrideJavaLocation));
        p.setProperty("OverrideMemory", Boolean.toString(this.overrideMemory));
        p.setProperty("OverrideWindow", Boolean.toString(this.overrideWindow));
        if (this.permGen != null) {
            p.setProperty("PermGen", Integer.toString(this.permGen.intValue()));
        }
        if (this.postExitCommand != null) {
            p.setProperty("PostExitCommand", this.postExitCommand);
        }
        if (this.preLaunchCommand != null) {
            p.setProperty("PreLaunchCommand", this.preLaunchCommand);
        }
        p.setProperty("ShowConsole", Boolean.toString(this.showConsole));
        p.setProperty("ShowConsoleOnError", Boolean.toString(this.showConsoleOnError));
        if (this.wrapperCommand != null) {
            p.setProperty("WrapperCommand", this.wrapperCommand);
        }
        if (this.name != null) {
            p.setProperty("name", this.name);
        }
        if (this.notes != null) {
            p.setProperty("notes", this.notes);
        }
        if (this.iconKey != null) {
            p.setProperty("iconKey", this.iconKey);
        }
        return p;
    }

    public MultiMCManifest getMmcPack() {
        return this.mmcPack;
    }

    @Override // com.brixcore.mod.ModpackManifest
    public ModpackProvider getProvider() {
        return MultiMCModpackProvider.INSTANCE;
    }
}
