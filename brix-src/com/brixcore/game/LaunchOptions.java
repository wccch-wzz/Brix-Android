package com.brixcore.game;

import com.brixcore.data.Renderer;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class LaunchOptions implements Serializable {
    private boolean debugLog;
    private File gameDir;
    private Integer height;
    private JavaVersion java;
    private Integer maxMemory;
    private Integer minMemory;
    private boolean pojavBigCore;
    private String profileName;
    private Renderer renderer;
    private String serverIp;
    private String uuid;
    private String versionName;
    private String versionType;
    private boolean vulkanDriverSystem;
    private Integer width;
    private final List<String> gameArguments = new ArrayList();
    private final List<String> javaArguments = new ArrayList();

    public File getGameDir() {
        return this.gameDir;
    }

    public JavaVersion getJava() {
        return this.java;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public String getVersionType() {
        return this.versionType;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public List<String> getGameArguments() {
        return Collections.unmodifiableList(this.gameArguments);
    }

    public List<String> getJavaArguments() {
        return Collections.unmodifiableList(this.javaArguments);
    }

    public Integer getMinMemory() {
        return this.minMemory;
    }

    public Integer getMaxMemory() {
        return this.maxMemory;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public boolean isVKDriverSystem() {
        return this.vulkanDriverSystem;
    }

    public boolean isPojavBigCore() {
        return this.pojavBigCore;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean isDebugLog() {
        return this.debugLog;
    }

    public static class Builder {
        private final LaunchOptions options = new LaunchOptions();

        public LaunchOptions create() {
            return this.options;
        }

        public List<String> getGameArguments() {
            return this.options.gameArguments;
        }

        public List<String> getJavaArguments() {
            return this.options.javaArguments;
        }

        public Builder setGameDir(File gameDir) {
            this.options.gameDir = gameDir;
            return this;
        }

        public Builder setJava(JavaVersion java) {
            this.options.java = java;
            return this;
        }

        public Builder setVersionName(String versionName) {
            this.options.versionName = versionName;
            return this;
        }

        public Builder setVersionType(String versionType) {
            this.options.versionType = versionType;
            return this;
        }

        public Builder setProfileName(String profileName) {
            this.options.profileName = profileName;
            return this;
        }

        public Builder setGameArguments(List<String> gameArguments) {
            this.options.gameArguments.clear();
            this.options.gameArguments.addAll(gameArguments);
            return this;
        }

        public Builder setJavaArguments(List<String> javaArguments) {
            this.options.javaArguments.clear();
            this.options.javaArguments.addAll(javaArguments);
            return this;
        }

        public Builder setMinMemory(Integer minMemory) {
            this.options.minMemory = minMemory;
            return this;
        }

        public Builder setMaxMemory(Integer maxMemory) {
            this.options.maxMemory = maxMemory;
            return this;
        }

        public Builder setWidth(Integer width) {
            this.options.width = width;
            return this;
        }

        public Builder setHeight(Integer height) {
            this.options.height = height;
            return this;
        }

        public Builder setServerIp(String serverIp) {
            this.options.serverIp = serverIp;
            return this;
        }

        public Builder setVkDriverSystem(boolean vulkanDriverSystem) {
            this.options.vulkanDriverSystem = vulkanDriverSystem;
            return this;
        }

        public Builder setRenderer(Renderer renderer) {
            this.options.renderer = renderer;
            return this;
        }

        public Builder setPojavBigCore(boolean pojavBigCore) {
            this.options.pojavBigCore = pojavBigCore;
            return this;
        }

        public Builder setUUid(String uuid) {
            this.options.uuid = uuid;
            return this;
        }

        public Builder setDebugLog(boolean debugLog) {
            this.options.debugLog = debugLog;
            return this;
        }
    }
}
