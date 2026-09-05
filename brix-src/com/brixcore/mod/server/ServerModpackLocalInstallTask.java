package com.brixcore.mod.server;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.DefaultGameRepository;
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
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes2.dex */
public class ServerModpackLocalInstallTask extends Task<Void> {
    private final List<Task<?>> dependencies = new ArrayList();
    private final List<Task<?>> dependents = new ArrayList(4);
    private final ServerModpackManifest manifest;
    private final Modpack modpack;
    private final String name;
    private final DefaultGameRepository repository;
    private final File zipFile;

    public ServerModpackLocalInstallTask(DefaultDependencyManager dependencyManager, File zipFile, Modpack modpack, ServerModpackManifest manifest, final String name) {
        ModpackConfiguration<ServerModpackManifest> config;
        this.zipFile = zipFile;
        this.modpack = modpack;
        this.manifest = manifest;
        this.name = name;
        this.repository = dependencyManager.getGameRepository();
        File run = this.repository.getRunDirectory(name);
        File json = this.repository.getModpackConfiguration(name);
        if (this.repository.hasVersion(name) && !json.exists()) {
            throw new IllegalArgumentException("Version " + name + " already exists.");
        }
        GameBuilder builder = dependencyManager.gameBuilder().name(name);
        for (ServerModpackManifest.Addon addon : manifest.getAddons()) {
            builder.version(addon.getId(), addon.getVersion());
        }
        this.dependents.add(builder.buildAsync());
        onDone().register(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackLocalInstallTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$new$0(name, (TaskEvent) obj);
            }
        });
        ModpackConfiguration<ServerModpackManifest> config2 = null;
        try {
            if (json.exists()) {
                config2 = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(json), new TypeToken<ModpackConfiguration<ServerModpackManifest>>() { // from class: com.brixcore.mod.server.ServerModpackLocalInstallTask.1
                }.getType());
                if (!ServerModpackProvider.INSTANCE.getName().equals(config2.getType())) {
                    throw new IllegalArgumentException("Version " + name + " is not a Server modpack. Cannot update this version.");
                }
            }
            config = config2;
        } catch (JsonParseException | IOException e) {
            config = config2;
        }
        this.dependents.add(new ModpackInstallTask(zipFile, run, modpack.getEncoding(), Collections.singletonList("/overrides"), new Predicate() { // from class: com.brixcore.mod.server.ServerModpackLocalInstallTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ServerModpackLocalInstallTask.lambda$new$1((String) obj);
            }
        }, config).withStage("Brix.modpack"));
        this.dependents.add(new MinecraftInstanceTask(zipFile, modpack.getEncoding(), Collections.singletonList("/overrides"), manifest, ServerModpackProvider.INSTANCE, modpack.getName(), modpack.getVersion(), this.repository.getModpackConfiguration(name)).withStage("Brix.modpack"));
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
    }
}
