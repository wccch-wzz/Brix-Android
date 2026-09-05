package com.brixcore.download.neoforge;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.VersionMismatchException;
import com.brixcore.download.forge.ForgeNewInstallProfile;
import com.brixcore.download.forge.ForgeNewInstallTask;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.StringUtils;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/* JADX INFO: loaded from: classes5.dex */
public final class NeoForgeInstallTask extends Task<Version> {
    private Task<Version> dependency;
    private final DefaultDependencyManager dependencyManager;
    private FileDownloadTask dependent;
    private Path installer = null;
    private final NeoForgeRemoteVersion remoteVersion;
    private final Version version;

    public NeoForgeInstallTask(DefaultDependencyManager dependencyManager, Version version, NeoForgeRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remoteVersion = remoteVersion;
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        this.installer = Files.createTempFile("neoforge-installer", ".jar", new FileAttribute[0]);
        this.dependent = new FileDownloadTask(this.dependencyManager.getDownloadProvider().injectURLsWithCandidates(this.remoteVersion.getUrls()), this.installer.toFile(), (FileDownloadTask.IntegrityCheck) null);
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
    public Collection<? extends Task<?>> getDependents() {
        return Collections.singleton(this.dependent);
    }

    @Override // com.brixcore.task.Task
    public Collection<? extends Task<?>> getDependencies() {
        return Collections.singleton(this.dependency);
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        this.dependency = install(this.dependencyManager, this.version, this.installer);
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
            if (!LibraryAnalyzer.LibraryType.FORGE.getPatchId().equals(installProfile.get("profile")) || (!Files.exists(fs.getPath("META-INF/NEOFORGE.RSA", new String[0]), new LinkOption[0]) && !installProfileText.contains("neoforge"))) {
                if (!LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId().equals(installProfile.get("profile")) && !"NeoForge".equals(installProfile.get("profile"))) {
                    throw new IOException();
                }
                ForgeNewInstallProfile profile = (ForgeNewInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeNewInstallProfile.class);
                if (!gameVersion.get().equals(profile.getMinecraft())) {
                    throw new VersionMismatchException(profile.getMinecraft(), gameVersion.get());
                }
                NeoForgeOldInstallTask neoForgeOldInstallTask = new NeoForgeOldInstallTask(dependencyManager, version, modifyNeoForgeNewVersion(profile.getVersion()), installer);
                if (fs != null) {
                    fs.close();
                }
                return neoForgeOldInstallTask;
            }
            ForgeNewInstallProfile profile2 = (ForgeNewInstallProfile) JsonUtils.fromNonNullJson(installProfileText, ForgeNewInstallProfile.class);
            if (!gameVersion.get().equals(profile2.getMinecraft())) {
                throw new VersionMismatchException(profile2.getMinecraft(), gameVersion.get());
            }
            Task taskThenApplyAsync = new ForgeNewInstallTask(dependencyManager, version, modifyNeoForgeOldVersion(gameVersion.get(), profile2.getVersion()), installer).thenApplyAsync(new ExceptionalFunction() { // from class: com.brixcore.download.neoforge.NeoForgeInstallTask$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.function.ExceptionalFunction
                public final Object apply(Object obj) {
                    return NeoForgeInstallTask.lambda$install$0((Version) obj);
                }
            });
            if (fs != null) {
                fs.close();
            }
            return taskThenApplyAsync;
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

    static /* synthetic */ Version lambda$install$0(Version neoForgeVersion) throws IOException {
        if (!neoForgeVersion.getId().equals(LibraryAnalyzer.LibraryType.FORGE.getPatchId()) || neoForgeVersion.getVersion() == null) {
            throw new IOException("Invalid neoforge version.");
        }
        return neoForgeVersion.setId(LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId()).setVersion(StringUtils.removePrefix(neoForgeVersion.getVersion().replace(LibraryAnalyzer.LibraryType.FORGE.getPatchId(), ""), "-"));
    }

    private static String modifyNeoForgeOldVersion(String gameVersion, String version) {
        return StringUtils.removeSuffix(StringUtils.removePrefix(StringUtils.removeSuffix(StringUtils.removePrefix(version.replace(gameVersion, "").trim(), "-"), "-"), "_"), "_");
    }

    private static String modifyNeoForgeNewVersion(String version) {
        return StringUtils.removePrefix(version.replace("neoforge", ""), "-");
    }
}
