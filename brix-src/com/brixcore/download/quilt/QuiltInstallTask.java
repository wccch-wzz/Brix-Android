package com.brixcore.download.quilt;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.UnsupportedInstallationException;
import com.brixcore.game.Arguments;
import com.brixcore.game.Artifact;
import com.brixcore.game.Library;
import com.brixcore.game.Version;
import com.brixcore.task.GetTask;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes5.dex */
public final class QuiltInstallTask extends Task<Version> {
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final DefaultDependencyManager dependencyManager;
    private final GetTask launchMetaTask;
    private final QuiltRemoteVersion remote;
    private final Version version;

    public QuiltInstallTask(DefaultDependencyManager dependencyManager, Version version, QuiltRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remote = remoteVersion;
        this.launchMetaTask = new GetTask(dependencyManager.getDownloadProvider().injectURLsWithCandidates(remoteVersion.getUrls()));
        this.launchMetaTask.setCacheRepository(dependencyManager.getCacheRepository());
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        if (!Objects.equals(LibraryAnalyzer.VANILLA_MAIN, this.version.resolve(this.dependencyManager.getGameRepository()).getMainClass())) {
            throw new UnsupportedInstallationException(3);
        }
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return Collections.singleton(this.launchMetaTask);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public boolean isRelyingOnDependencies() {
        return false;
    }

    @Override // com.brixcore.task.Task
    public void execute() {
        setResult(getPatch((QuiltInfo) JsonUtils.GSON.fromJson(this.launchMetaTask.getResult(), QuiltInfo.class), this.remote.getGameVersion(), this.remote.getSelfVersion()));
        this.dependencies.add(this.dependencyManager.checkLibraryCompletionAsync(getResult(), true));
    }

    private Version getPatch(QuiltInfo quiltInfo, String gameVersion, String loaderVersion) {
        String mainClass;
        Arguments arguments;
        JsonObject launcherMeta = quiltInfo.launcherMeta;
        Arguments arguments2 = new Arguments();
        if (!launcherMeta.get("mainClass").isJsonObject()) {
            mainClass = launcherMeta.get("mainClass").getAsString();
        } else {
            mainClass = launcherMeta.get("mainClass").getAsJsonObject().get("client").getAsString();
        }
        if (!launcherMeta.has("launchwrapper")) {
            arguments = arguments2;
        } else {
            String clientTweaker = launcherMeta.get("launchwrapper").getAsJsonObject().get("tweakers").getAsJsonObject().get("client").getAsJsonArray().get(0).getAsString();
            arguments = arguments2.addGameArguments("--tweakClass", clientTweaker);
        }
        JsonObject librariesObject = launcherMeta.getAsJsonObject("libraries");
        List<Library> libraries = new ArrayList<>();
        String[] strArr = {"common", "server"};
        for (int i = 0; i < 2; i++) {
            String side = strArr[i];
            for (JsonElement element : librariesObject.getAsJsonArray(side)) {
                libraries.add((Library) JsonUtils.GSON.fromJson(element, Library.class));
            }
        }
        libraries.add(new Library(Artifact.fromDescriptor(quiltInfo.intermediary.maven), getMavenRepositoryByGroup(quiltInfo.intermediary.maven), null));
        libraries.add(new Library(Artifact.fromDescriptor(quiltInfo.loader.maven), getMavenRepositoryByGroup(quiltInfo.loader.maven), null));
        return new Version(LibraryAnalyzer.LibraryType.QUILT.getPatchId(), loaderVersion, 30000, arguments, mainClass, libraries);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:11:0x0024  */
    private static String getMavenRepositoryByGroup(String maven) {
        Artifact artifact = Artifact.fromDescriptor(maven);
        switch (artifact.getGroup()) {
            case "net.fabricmc":
                return "https://maven.fabricmc.net/";
            case "org.quiltmc":
                return "https://maven.quiltmc.org/repository/release/";
            default:
                return "https://maven.fabricmc.net/";
        }
    }

    public static class QuiltInfo {
        private final IntermediaryInfo hashed;
        private final IntermediaryInfo intermediary;
        private final JsonObject launcherMeta;
        private final LoaderInfo loader;

        public QuiltInfo(LoaderInfo loader, IntermediaryInfo hashed, IntermediaryInfo intermediary, JsonObject launcherMeta) {
            this.loader = loader;
            this.hashed = hashed;
            this.intermediary = intermediary;
            this.launcherMeta = launcherMeta;
        }

        public LoaderInfo getLoader() {
            return this.loader;
        }

        public IntermediaryInfo getHashed() {
            return this.hashed;
        }

        public IntermediaryInfo getIntermediary() {
            return this.intermediary;
        }

        public JsonObject getLauncherMeta() {
            return this.launcherMeta;
        }
    }

    public static class LoaderInfo {
        private final int build;
        private final String maven;
        private final String separator;
        private final boolean stable;
        private final String version;

        public LoaderInfo(String separator, int build, String maven, String version, boolean stable) {
            this.separator = separator;
            this.build = build;
            this.maven = maven;
            this.version = version;
            this.stable = stable;
        }

        public String getSeparator() {
            return this.separator;
        }

        public int getBuild() {
            return this.build;
        }

        public String getMaven() {
            return this.maven;
        }

        public String getVersion() {
            return this.version;
        }

        public boolean isStable() {
            return this.stable;
        }
    }

    public static class IntermediaryInfo {
        private final String maven;
        private final String version;

        public IntermediaryInfo(String maven, String version) {
            this.maven = maven;
            this.version = version;
        }

        public String getMaven() {
            return this.maven;
        }

        public String getVersion() {
            return this.version;
        }
    }
}
