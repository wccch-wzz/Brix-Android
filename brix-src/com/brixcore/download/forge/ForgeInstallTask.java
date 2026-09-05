package com.brixcore.download.forge;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.DependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.UnsupportedInstallationException;
import com.brixcore.download.VersionMismatchException;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.versioning.GameVersionNumber;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeInstallTask extends Task<Version> {
    private Task<Version> dependency;
    private final DefaultDependencyManager dependencyManager;
    private FileDownloadTask dependent;
    private Path installer;
    private final ForgeRemoteVersion remote;
    private final Version version;

    public ForgeInstallTask(DefaultDependencyManager dependencyManager, Version version, ForgeRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remote = remoteVersion;
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        this.installer = Files.createTempFile("forge-installer", ".jar", new FileAttribute[0]);
        this.dependent = new FileDownloadTask(this.dependencyManager.getDownloadProvider().injectURLsWithCandidates(this.remote.getUrls()), this.installer.toFile(), (FileDownloadTask.IntegrityCheck) null);
        this.dependent.setCacheRepository(this.dependencyManager.getCacheRepository());
        this.dependent.setCaching(true);
        this.dependent.addIntegrityCheckHandler(FileDownloadTask.ZIP_INTEGRITY_CHECK_HANDLER);
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        Files.deleteIfExists(this.installer);
        setResult(this.dependency.getResult());
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return Collections.singleton(this.dependent);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return Collections.singleton(this.dependency);
    }

    @Override // com.brixcore.task.Task
    public void execute() throws VersionMismatchException, UnsupportedInstallationException, IOException {
        String originalMainClass = this.version.resolve(this.dependencyManager.getGameRepository()).getMainClass();
        if (GameVersionNumber.compare("1.13", this.remote.getGameVersion()) <= 0 && !LibraryAnalyzer.FORGE_OPTIFINE_MAIN.contains(originalMainClass)) {
            throw new UnsupportedInstallationException(1);
        }
        if (detectForgeInstallerType(this.dependencyManager, this.version, this.installer)) {
            this.dependency = new ForgeNewInstallTask(this.dependencyManager, this.version, this.remote.getSelfVersion(), this.installer);
        } else {
            this.dependency = new ForgeOldInstallTask(this.dependencyManager, this.version, this.remote.getSelfVersion(), this.installer);
        }
    }

    public static boolean detectForgeInstallerType(DependencyManager dependencyManager, Version version, Path installer) throws VersionMismatchException, IOException {
        Optional<String> gameVersion = dependencyManager.getGameRepository().getGameVersion(version);
        if (!gameVersion.isPresent()) {
            throw new IOException();
        }
        FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(installer);
        try {
            String installProfileText = FileUtils.readText(fs.getPath("install_profile.json", new String[0]));
            Map<?, ?> installProfile = (Map) JsonUtils.fromNonNullJson(installProfileText, Map.class);
            if (installProfile.containsKey("spec")) {
                ForgeNewInstallProfile profile = (ForgeNewInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeNewInstallProfile.class);
                if (!gameVersion.get().equals(profile.getMinecraft())) {
                    throw new VersionMismatchException(profile.getMinecraft(), gameVersion.get());
                }
                if (fs != null) {
                    fs.close();
                    return true;
                }
                return true;
            }
            if (installProfile.containsKey("install") && installProfile.containsKey("versionInfo")) {
                ForgeInstallProfile profile2 = (ForgeInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeInstallProfile.class);
                if (!gameVersion.get().equals(profile2.getInstall().getMinecraft())) {
                    throw new VersionMismatchException(profile2.getInstall().getMinecraft(), gameVersion.get());
                }
                if (fs != null) {
                    fs.close();
                }
                return false;
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

    public static Task<Version> install(DefaultDependencyManager dependencyManager, Version version, Path installer) throws VersionMismatchException, IOException {
        Optional<String> gameVersion = dependencyManager.getGameRepository().getGameVersion(version);
        if (!gameVersion.isPresent()) {
            throw new IOException();
        }
        FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(installer);
        try {
            String installProfileText = FileUtils.readText(fs.getPath("install_profile.json", new String[0]));
            Map<?, ?> installProfile = (Map) JsonUtils.fromNonNullJson(installProfileText, Map.class);
            if (installProfile.containsKey("spec")) {
                ForgeNewInstallProfile profile = (ForgeNewInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeNewInstallProfile.class);
                if (!gameVersion.get().equals(profile.getMinecraft())) {
                    throw new VersionMismatchException(profile.getMinecraft(), gameVersion.get());
                }
                ForgeNewInstallTask forgeNewInstallTask = new ForgeNewInstallTask(dependencyManager, version, modifyVersion(gameVersion.get(), profile.getVersion()), installer);
                if (fs != null) {
                    fs.close();
                }
                return forgeNewInstallTask;
            }
            if (installProfile.containsKey("install") && installProfile.containsKey("versionInfo")) {
                ForgeInstallProfile profile2 = (ForgeInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeInstallProfile.class);
                if (!gameVersion.get().equals(profile2.getInstall().getMinecraft())) {
                    throw new VersionMismatchException(profile2.getInstall().getMinecraft(), gameVersion.get());
                }
                ForgeOldInstallTask forgeOldInstallTask = new ForgeOldInstallTask(dependencyManager, version, modifyVersion(gameVersion.get(), profile2.getInstall().getPath().getVersion().replaceAll("(?i)forge", "")), installer);
                if (fs != null) {
                    fs.close();
                }
                return forgeOldInstallTask;
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

    private static String modifyVersion(String gameVersion, String version) {
        return StringUtils.removeSuffix(StringUtils.removePrefix(StringUtils.removeSuffix(StringUtils.removePrefix(version.replace(gameVersion, "").trim(), "-"), "-"), "_"), "_");
    }
}
