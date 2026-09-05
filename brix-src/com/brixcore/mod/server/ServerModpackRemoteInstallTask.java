package com.brixcore.mod.server;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModpackConfiguration;
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

/* JADX INFO: loaded from: classes2.dex */
public class ServerModpackRemoteInstallTask extends Task<Void> {
    public static final String MODPACK_TYPE = "Server";
    private final DefaultDependencyManager dependency;
    private final ServerModpackManifest manifest;
    private final String name;
    private final DefaultGameRepository repository;
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final List<Task<?>> dependents = new ArrayList(1);

    public ServerModpackRemoteInstallTask(DefaultDependencyManager dependencyManager, ServerModpackManifest manifest, final String name) {
        this.name = name;
        this.dependency = dependencyManager;
        this.repository = dependencyManager.getGameRepository();
        this.manifest = manifest;
        File json = this.repository.getModpackConfiguration(name);
        if (this.repository.hasVersion(name) && !json.exists()) {
            throw new IllegalArgumentException("Version " + name + " already exists.");
        }
        GameBuilder builder = dependencyManager.gameBuilder().name(name);
        for (ServerModpackManifest.Addon addon : manifest.getAddons()) {
            builder.version(addon.getId(), addon.getVersion());
        }
        this.dependents.add(builder.buildAsync());
        onDone().register(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackRemoteInstallTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$new$0(name, (TaskEvent) obj);
            }
        });
        try {
            if (json.exists()) {
                ModpackConfiguration<ServerModpackManifest> config = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(json), new TypeToken<ModpackConfiguration<ServerModpackManifest>>() { // from class: com.brixcore.mod.server.ServerModpackRemoteInstallTask.1
                }.getType());
                if (!"Server".equals(config.getType())) {
                    throw new IllegalArgumentException("Version " + name + " is not a Server modpack. Cannot update this version.");
                }
            }
        } catch (JsonParseException e) {
        } catch (IOException e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String name, TaskEvent event) {
        if (event.isFailed()) {
            this.repository.removeVersionFromDisk(name);
        }
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
        this.dependencies.add(new ServerModpackCompletionTask(this.dependency, this.name, new ModpackConfiguration(this.manifest, "Server", this.manifest.getName(), this.manifest.getVersion(), Collections.emptyList())));
    }
}
