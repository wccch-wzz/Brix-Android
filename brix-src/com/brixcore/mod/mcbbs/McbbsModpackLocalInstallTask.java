package com.brixcore.mod.mcbbs;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Version;
import com.brixcore.mod.MinecraftInstanceTask;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackInstallTask;
import com.brixcore.task.Task;
import com.brixcore.task.TaskEvent;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes7.dex */
public class McbbsModpackLocalInstallTask extends Task<Void> {
    private static final String PATCH_NAME = "mcbbs";
    private final DefaultDependencyManager dependencyManager;
    private final MinecraftInstanceTask<McbbsModpackManifest> instanceTask;
    private final McbbsModpackManifest manifest;
    private final Modpack modpack;
    private final String name;
    private final DefaultGameRepository repository;
    private final boolean update;
    private final File zipFile;
    private final List<Task<?>> dependencies = new ArrayList(2);
    private final List<Task<?>> dependents = new ArrayList(4);

    public McbbsModpackLocalInstallTask(DefaultDependencyManager dependencyManager, File zipFile, Modpack modpack, McbbsModpackManifest manifest, final String name) {
        ModpackConfiguration<McbbsModpackManifest> config;
        this.dependencyManager = dependencyManager;
        this.zipFile = zipFile;
        this.modpack = modpack;
        this.manifest = manifest;
        this.name = name;
        this.repository = dependencyManager.getGameRepository();
        File run = this.repository.getRunDirectory(name);
        File json = this.repository.getModpackConfiguration(name);
        if (!this.repository.hasVersion(name) || json.exists()) {
            this.update = this.repository.hasVersion(name);
            GameBuilder builder = dependencyManager.gameBuilder().name(name);
            for (McbbsModpackManifest.Addon addon : manifest.getAddons()) {
                builder.version(addon.getId(), addon.getVersion());
            }
            this.dependents.add(builder.buildAsync());
            onDone().register(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackLocalInstallTask$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$new$0(name, (TaskEvent) obj);
                }
            });
            ModpackConfiguration<McbbsModpackManifest> config2 = null;
            try {
                if (json.exists()) {
                    config2 = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(json), new TypeToken<ModpackConfiguration<McbbsModpackManifest>>() { // from class: com.brixcore.mod.mcbbs.McbbsModpackLocalInstallTask.1
                    }.getType());
                    if (!McbbsModpackProvider.INSTANCE.getName().equals(config2.getType())) {
                        throw new IllegalArgumentException("Version " + name + " is not a Mcbbs modpack. Cannot update this version.");
                    }
                }
                config = config2;
            } catch (JsonParseException | IOException e) {
                config = config2;
            }
            this.dependents.add(new ModpackInstallTask(zipFile, run, modpack.getEncoding(), Collections.singletonList("/overrides"), new Predicate() { // from class: com.brixcore.mod.mcbbs.McbbsModpackLocalInstallTask$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return McbbsModpackLocalInstallTask.lambda$new$1((String) obj);
                }
            }, config).withStage("Brix.modpack"));
            this.instanceTask = new MinecraftInstanceTask<>(zipFile, modpack.getEncoding(), Collections.singletonList("/overrides"), manifest, McbbsModpackProvider.INSTANCE, modpack.getName(), modpack.getVersion(), this.repository.getModpackConfiguration(name));
            this.dependents.add(this.instanceTask.withStage("Brix.modpack"));
            return;
        }
        throw new IllegalArgumentException("Version " + name + " already exists.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String name, TaskEvent event) {
        if (event.isFailed()) {
            this.repository.removeVersionFromDisk(name);
        }
    }

    static /* synthetic */ boolean lambda$new$1(String any) {
        return true;
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        Version version = this.repository.readVersionJson(this.name);
        Optional<Version> mcbbsPatch = version.getPatches().stream().filter(new Predicate() { // from class: com.brixcore.mod.mcbbs.McbbsModpackLocalInstallTask$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return McbbsModpackLocalInstallTask.PATCH_NAME.equals(((Version) obj).getId());
            }
        }).findFirst();
        if (!this.update) {
            Version patch = new Version(PATCH_NAME).setLibraries(this.manifest.getLibraries());
            this.dependencies.add(this.repository.saveAsync(version.addPatch(patch)));
        } else if (mcbbsPatch.isPresent()) {
            Version patch2 = mcbbsPatch.get().setLibraries(this.manifest.getLibraries());
            this.dependencies.add(this.repository.saveAsync(version.addPatch(patch2)));
        }
        this.dependencies.add(new McbbsModpackCompletionTask(this.dependencyManager, this.name, (ModpackConfiguration) this.instanceTask.getResult()));
    }
}
