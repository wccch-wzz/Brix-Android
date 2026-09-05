package com.brixcore.mod;

import com.brixcore.mod.mcbbs.McbbsModpackManifest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ModpackExportInfo {
    private String authlibInjectorServer;
    private String author;
    private String description;
    private String fileApi;
    private boolean forceUpdate;
    private String javaArguments;
    private String launchArguments;
    private int minMemory;
    private String name;
    private boolean packWithLauncher;
    private List<Integer> supportedJavaVersions;
    private String url;
    private String version;
    private final List<String> whitelist = new ArrayList();
    private List<McbbsModpackManifest.Origin> origins = new ArrayList();

    public List<String> getWhitelist() {
        return this.whitelist;
    }

    public ModpackExportInfo setWhitelist(List<String> whitelist) {
        this.whitelist.clear();
        this.whitelist.addAll(whitelist);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ModpackExportInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public ModpackExportInfo setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getVersion() {
        return this.version;
    }

    public ModpackExportInfo setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public ModpackExportInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getFileApi() {
        return this.fileApi;
    }

    public ModpackExportInfo setFileApi(String fileApi) {
        this.fileApi = fileApi;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public ModpackExportInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isForceUpdate() {
        return this.forceUpdate;
    }

    public ModpackExportInfo setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        return this;
    }

    public boolean isPackWithLauncher() {
        return this.packWithLauncher;
    }

    public ModpackExportInfo setPackWithLauncher(boolean packWithLauncher) {
        this.packWithLauncher = packWithLauncher;
        return this;
    }

    public int getMinMemory() {
        return this.minMemory;
    }

    public ModpackExportInfo setMinMemory(int minMemory) {
        this.minMemory = minMemory;
        return this;
    }

    public List<Integer> getSupportedJavaVersions() {
        return this.supportedJavaVersions;
    }

    public ModpackExportInfo setSupportedJavaVersions(List<Integer> supportedJavaVersions) {
        this.supportedJavaVersions = supportedJavaVersions;
        return this;
    }

    public String getLaunchArguments() {
        return this.launchArguments;
    }

    public ModpackExportInfo setLaunchArguments(String launchArguments) {
        this.launchArguments = launchArguments;
        return this;
    }

    public String getJavaArguments() {
        return this.javaArguments;
    }

    public ModpackExportInfo setJavaArguments(String javaArguments) {
        this.javaArguments = javaArguments;
        return this;
    }

    public String getAuthlibInjectorServer() {
        return this.authlibInjectorServer;
    }

    public ModpackExportInfo setAuthlibInjectorServer(String authlibInjectorServer) {
        this.authlibInjectorServer = authlibInjectorServer;
        return this;
    }

    public List<McbbsModpackManifest.Origin> getOrigins() {
        return Collections.unmodifiableList(this.origins);
    }

    public ModpackExportInfo setOrigins(List<McbbsModpackManifest.Origin> origins) {
        this.origins.clear();
        this.origins.addAll(origins);
        return this;
    }

    public ModpackExportInfo validate() throws NullPointerException {
        return this;
    }

    public static class Options {
        private boolean requireAuthlibInjectorServer;
        private boolean requireFileApi;
        private boolean requireForceUpdate;
        private boolean requireJavaArguments;
        private boolean requireLaunchArguments;
        private boolean requireMinMemory;
        private boolean requireOrigins;
        private boolean requireUrl;
        private boolean validateFileApi;

        public boolean isRequireUrl() {
            return this.requireUrl;
        }

        public boolean isRequireForceUpdate() {
            return this.requireForceUpdate;
        }

        public boolean isRequireFileApi() {
            return this.requireFileApi;
        }

        public boolean isValidateFileApi() {
            return this.validateFileApi;
        }

        public boolean isRequireMinMemory() {
            return this.requireMinMemory;
        }

        public boolean isRequireAuthlibInjectorServer() {
            return this.requireAuthlibInjectorServer;
        }

        public boolean isRequireLaunchArguments() {
            return this.requireLaunchArguments;
        }

        public boolean isRequireJavaArguments() {
            return this.requireJavaArguments;
        }

        public boolean isRequireOrigins() {
            return this.requireOrigins;
        }

        public Options requireUrl() {
            this.requireUrl = true;
            return this;
        }

        public Options requireForceUpdate() {
            this.requireForceUpdate = true;
            return this;
        }

        public Options requireFileApi(boolean optional) {
            this.requireFileApi = true;
            this.validateFileApi = !optional;
            return this;
        }

        public Options requireMinMemory() {
            this.requireMinMemory = true;
            return this;
        }

        public Options requireAuthlibInjectorServer() {
            this.requireAuthlibInjectorServer = true;
            return this;
        }

        public Options requireLaunchArguments() {
            this.requireLaunchArguments = true;
            return this;
        }

        public Options requireJavaArguments() {
            this.requireJavaArguments = true;
            return this;
        }

        public Options requireOrigins() {
            this.requireOrigins = true;
            return this;
        }
    }
}
