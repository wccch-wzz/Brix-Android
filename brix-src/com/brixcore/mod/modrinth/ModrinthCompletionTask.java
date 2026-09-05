package com.brixcore.mod.modrinth;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.ModpackCompletionException;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.Logging;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes14.dex */
public class ModrinthCompletionTask extends Task<Void> {
    private final AtomicBoolean allNameKnown;
    private final List<Task<?>> dependencies;
    private final DefaultDependencyManager dependency;
    private final AtomicInteger finished;
    private ModrinthManifest manifest;
    private final ModManager modManager;
    private final AtomicBoolean notFound;
    private final DefaultGameRepository repository;
    private final String version;

    public ModrinthCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        this(dependencyManager, version, null);
    }

    public ModrinthCompletionTask(DefaultDependencyManager dependencyManager, String version, ModrinthManifest manifest) {
        this.dependencies = new ArrayList();
        this.allNameKnown = new AtomicBoolean(true);
        this.finished = new AtomicInteger(0);
        this.notFound = new AtomicBoolean(false);
        this.dependency = dependencyManager;
        this.repository = dependencyManager.getGameRepository();
        this.modManager = this.repository.getModManager(version);
        this.version = version;
        this.manifest = manifest;
        if (manifest == null) {
            try {
                File manifestFile = new File(this.repository.getVersionRoot(version), "modrinth.index.json");
                if (manifestFile.exists()) {
                    this.manifest = (ModrinthManifest) JsonUtils.GSON.fromJson(FileUtils.readText(manifestFile), ModrinthManifest.class);
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read Modrinth modpack manifest.json", (Throwable) e);
            }
        }
        setStage("Brix.modpack.download");
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
    public void execute() throws Exception {
        if (this.manifest == null) {
            return;
        }
        Path runDirectory = this.repository.getRunDirectory(this.version).toPath().toAbsolutePath().normalize();
        Path modsDirectory = runDirectory.resolve("mods");
        for (ModrinthManifest.File file : this.manifest.getFiles()) {
            if (file.getEnv() == null || !file.getEnv().getOrDefault("client", "required").equals("unsupported")) {
                if (file.getDownloads().isEmpty()) {
                    continue;
                } else {
                    Path filePath = runDirectory.resolve(file.getPath()).toAbsolutePath().normalize();
                    if (!filePath.startsWith(runDirectory)) {
                        throw new ModpackCompletionException("Unsecure path: " + file.getPath());
                    }
                    if (!Files.exists(filePath, new LinkOption[0]) && (!modsDirectory.equals(filePath.getParent()) || !this.modManager.hasSimpleMod(FileUtils.getName(filePath)))) {
                        FileDownloadTask task = new FileDownloadTask(file.getDownloads(), filePath.toFile());
                        task.setCacheRepository(this.dependency.getCacheRepository());
                        task.setCaching(true);
                        this.dependencies.add(task.withCounter("Brix.modpack.download"));
                    }
                }
            }
        }
        if (!this.dependencies.isEmpty()) {
            getProperties().put("total", Integer.valueOf(this.dependencies.size()));
            notifyPropertiesChanged();
        }
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        if (this.notFound.get()) {
            throw new ModpackCompletionException(new FileNotFoundException());
        }
        if (!this.allNameKnown.get() || !isDependenciesSucceeded()) {
            throw new ModpackCompletionException();
        }
    }
}
