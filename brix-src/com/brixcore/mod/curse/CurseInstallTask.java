package com.brixcore.mod.curse;

import com.brixcore.download.DefaultCacheRepository;
import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.MinecraftInstanceTask;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackCompletionException;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackInstallTask;
import com.brixcore.task.Task;
import com.brixcore.task.TaskEvent;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseInstallTask extends Task<Void> {
    private final ModpackConfiguration<CurseManifest> config;
    private final DefaultDependencyManager dependencyManager;
    private final CurseManifest manifest;
    private final Modpack modpack;
    private final String name;
    private final DefaultGameRepository repository;
    private final File run;
    private final File zipFile;
    private final List<Task<?>> dependents = new ArrayList(4);
    private final List<Task<?>> dependencies = new ArrayList(1);

    public CurseInstallTask(DefaultDependencyManager dependencyManager, File zipFile, Modpack modpack, CurseManifest manifest, final String name) {
        this.dependencyManager = dependencyManager;
        this.zipFile = zipFile;
        this.modpack = modpack;
        this.manifest = manifest;
        this.name = name;
        this.repository = dependencyManager.getGameRepository();
        this.run = this.repository.getRunDirectory(name);
        File json = this.repository.getModpackConfiguration(name);
        if (this.repository.hasVersion(name) && !json.exists()) {
            throw new IllegalArgumentException("Version " + name + " already exists.");
        }
        GameBuilder builder = dependencyManager.gameBuilder().name(name).gameVersion(manifest.getMinecraft().getGameVersion());
        for (CurseManifestModLoader modLoader : manifest.getMinecraft().getModLoaders()) {
            if (modLoader.getId().startsWith("forge-")) {
                builder.version(DefaultCacheRepository.LibraryIndex.TYPE_FORGE, modLoader.getId().substring("forge-".length()));
            } else if (modLoader.getId().startsWith("fabric-")) {
                builder.version("fabric", modLoader.getId().substring("fabric-".length()));
            } else if (modLoader.getId().startsWith("neoforge-")) {
                builder.version("neoforge", modLoader.getId().substring("neoforge-".length()));
            }
        }
        this.dependents.add(builder.buildAsync());
        onDone().register(new Consumer() { // from class: com.brixcore.mod.curse.CurseInstallTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$new$0(name, (TaskEvent) obj);
            }
        });
        ModpackConfiguration<CurseManifest> config = null;
        try {
            if (json.exists()) {
                config = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(json), new TypeToken<ModpackConfiguration<CurseManifest>>() { // from class: com.brixcore.mod.curse.CurseInstallTask.1
                }.getType());
                if (!CurseModpackProvider.INSTANCE.getName().equals(config.getType())) {
                    throw new IllegalArgumentException("Version " + name + " is not a Curse modpack. Cannot update this version.");
                }
            }
        } catch (JsonParseException e) {
        } catch (IOException e2) {
        }
        this.config = config;
        this.dependents.add(new ModpackInstallTask(zipFile, this.run, modpack.getEncoding(), Collections.singletonList(manifest.getOverrides()), new Predicate() { // from class: com.brixcore.mod.curse.CurseInstallTask$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CurseInstallTask.lambda$new$1((String) obj);
            }
        }, config).withStage("Brix.modpack"));
        this.dependents.add(new MinecraftInstanceTask(zipFile, modpack.getEncoding(), Collections.singletonList(manifest.getOverrides()), manifest, CurseModpackProvider.INSTANCE, manifest.getName(), manifest.getVersion(), this.repository.getModpackConfiguration(name)).withStage("Brix.modpack"));
        this.dependencies.add(new CurseCompletionTask(dependencyManager, name, manifest));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String name, TaskEvent event) {
        Exception ex = event.getTask().getException();
        if (event.isFailed() && !(ex instanceof ModpackCompletionException)) {
            this.repository.removeVersionFromDisk(name);
        }
    }

    static /* synthetic */ boolean lambda$new$1(String any) {
        return true;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        if (this.config != null) {
            for (final CurseManifestFile oldCurseManifestFile : this.config.getManifest().getFiles()) {
                if (!StringUtils.isBlank(oldCurseManifestFile.getFileName())) {
                    File oldFile = new File(this.run, "mods/" + oldCurseManifestFile.getFileName());
                    if (oldFile.exists()) {
                        Stream<CurseManifestFile> stream = this.manifest.getFiles().stream();
                        Objects.requireNonNull(oldCurseManifestFile);
                        if (stream.noneMatch(new Predicate() { // from class: com.brixcore.mod.curse.CurseInstallTask$$ExternalSyntheticLambda0
                            @Override // java.util.function.Predicate
                            public final boolean test(Object obj) {
                                return oldCurseManifestFile.equals((CurseManifestFile) obj);
                            }
                        }) && !oldFile.delete()) {
                            throw new IOException("Unable to delete mod file " + oldFile);
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        File root = this.repository.getVersionRoot(this.name);
        FileUtils.writeText(new File(root, "manifest.json"), JsonUtils.GSON.toJson(this.manifest));
    }
}
