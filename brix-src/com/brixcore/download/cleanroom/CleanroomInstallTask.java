package com.brixcore.download.cleanroom;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.UnsupportedInstallationException;
import com.brixcore.download.VersionMismatchException;
import com.brixcore.download.forge.ForgeNewInstallProfile;
import com.brixcore.download.forge.ForgeNewInstallTask;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/* JADX INFO: loaded from: classes5.dex */
public final class CleanroomInstallTask extends Task<Version> {
    private final DefaultDependencyManager dependencyManager;
    private FileDownloadTask dependent;
    private Path installer;
    private final CleanroomRemoteVersion remote;
    private String selfVersion;
    private Task<Version> task;
    private final Version version;

    public CleanroomInstallTask(DefaultDependencyManager dependencyManager, Version version, CleanroomRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remote = remoteVersion;
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    public CleanroomInstallTask(DefaultDependencyManager dependencyManager, Version version, String selfVersion, Path installer) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.selfVersion = selfVersion;
        this.remote = null;
        this.installer = installer;
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        if (this.installer == null) {
            this.installer = Files.createTempFile("cleanroom-installer", ".jar", new FileAttribute[0]);
            this.dependent = new FileDownloadTask(this.dependencyManager.getDownloadProvider().injectURLsWithCandidates(this.remote.getUrls()), this.installer.toFile(), (FileDownloadTask.IntegrityCheck) null);
            this.dependent.setCacheRepository(this.dependencyManager.getCacheRepository());
            this.dependent.setCaching(true);
            this.dependent.addIntegrityCheckHandler(FileDownloadTask.ZIP_INTEGRITY_CHECK_HANDLER);
        }
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        Files.deleteIfExists(this.installer);
        setResult(this.task.getResult());
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependent == null ? Collections.emptySet() : Collections.singleton(this.dependent);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return Collections.singleton(this.task);
    }

    @Override // com.brixcore.task.Task
    public void execute() throws VersionMismatchException, UnsupportedInstallationException, IOException {
        if (this.selfVersion == null) {
            this.task = new ForgeNewInstallTask(this.dependencyManager, this.version, this.remote.getSelfVersion(), this.installer).thenApplyAsync(new ExceptionalFunction() { // from class: com.brixcore.download.cleanroom.CleanroomInstallTask$$ExternalSyntheticLambda1
                @Override // com.brixcore.util.function.ExceptionalFunction
                public final Object apply(Object obj) {
                    return ((Version) obj).setId(LibraryAnalyzer.LibraryType.CLEANROOM.getPatchId());
                }
            });
        } else {
            this.task = new ForgeNewInstallTask(this.dependencyManager, this.version, this.selfVersion, this.installer).thenApplyAsync(new ExceptionalFunction() { // from class: com.brixcore.download.cleanroom.CleanroomInstallTask$$ExternalSyntheticLambda2
                @Override // com.brixcore.util.function.ExceptionalFunction
                public final Object apply(Object obj) {
                    return ((Version) obj).setId(LibraryAnalyzer.LibraryType.CLEANROOM.getPatchId());
                }
            });
        }
    }

    public static Task<Version> install(DefaultDependencyManager dependencyManager, Version version, Path installer) throws VersionMismatchException, IOException {
        Optional<String> gameVersion = dependencyManager.getGameRepository().getGameVersion(version);
        if (CleanroomInstallTask$$ExternalSyntheticBackport0.m(gameVersion)) {
            throw new IOException();
        }
        FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(installer);
        try {
            String installProfileText = FileUtils.readText(fs.getPath("install_profile.json", new String[0]));
            Map<?, ?> installProfile = (Map) JsonUtils.fromNonNullJson(installProfileText, Map.class);
            if (LibraryAnalyzer.LibraryType.CLEANROOM.getPatchId().equals(installProfile.get("profile"))) {
                ForgeNewInstallProfile profile = (ForgeNewInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeNewInstallProfile.class);
                if (!gameVersion.get().equals(profile.getMinecraft())) {
                    throw new VersionMismatchException(profile.getMinecraft(), gameVersion.get());
                }
                CleanroomInstallTask cleanroomInstallTask = new CleanroomInstallTask(dependencyManager, version, modifyVersion(profile.getVersion()), installer);
                if (fs != null) {
                    fs.close();
                }
                return cleanroomInstallTask;
            }
            throw new IOException();
        } catch (Throwable th) {
            if (fs != null) {
                try {
                    fs.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static String modifyVersion(String version) {
        return version.replace("cleanroom-", "");
    }
}
