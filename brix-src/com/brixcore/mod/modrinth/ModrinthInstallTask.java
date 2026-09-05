package com.brixcore.mod.modrinth;

import com.brixcore.download.DefaultCacheRepository;
import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.MinecraftInstanceTask;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackCompletionException;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackInstallTask;
import com.brixcore.mod.curse.CurseManifest;
import com.brixcore.task.Task;
import com.brixcore.task.TaskEvent;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes14.dex */
public class ModrinthInstallTask extends Task<Void> {
    private final ModpackConfiguration<ModrinthManifest> config;
    private final DefaultDependencyManager dependencyManager;
    private final ModrinthManifest manifest;
    private final Modpack modpack;
    private final String name;
    private final DefaultGameRepository repository;
    private final File run;
    private final File zipFile;
    private final List<Task<?>> dependents = new ArrayList(4);
    private final List<Task<?>> dependencies = new ArrayList(1);

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:30:0x00d7  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v10 */
    /* JADX WARN: Type inference failed for: r10v11 */
    /* JADX WARN: Type inference failed for: r10v14 */
    /* JADX WARN: Type inference failed for: r10v2 */
    /* JADX WARN: Type inference failed for: r10v5 */
    /* JADX WARN: Type inference failed for: r10v7 */
    /* JADX WARN: Type inference failed for: r17v3 */
    public ModrinthInstallTask(DefaultDependencyManager defaultDependencyManager, File file, Modpack modpack, ModrinthManifest modrinthManifest, final String str) {
        boolean z = true;
        this.dependencyManager = defaultDependencyManager;
        this.zipFile = file;
        this.modpack = modpack;
        this.manifest = modrinthManifest;
        this.name = str;
        this.repository = defaultDependencyManager.getGameRepository();
        this.run = this.repository.getRunDirectory(str);
        File modpackConfiguration = this.repository.getModpackConfiguration(str);
        if (this.repository.hasVersion(str) && !modpackConfiguration.exists()) {
            throw new IllegalArgumentException("Version " + str + " already exists.");
        }
        GameBuilder gameBuilderGameVersion = defaultDependencyManager.gameBuilder().name(str).gameVersion(modrinthManifest.getGameVersion());
        Iterator<Map.Entry<String, String>> it = modrinthManifest.getDependencies().entrySet().iterator();
        while (true) {
            ?? r10 = 2;
            if (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                String key = next.getKey();
                boolean z2 = z;
                switch (key.hashCode()) {
                    case -1040676293:
                        if (!key.equals("fabric-loader")) {
                            r10 = -1;
                        } else {
                            r10 = 3;
                        }
                        break;
                    case 97618791:
                        if (!key.equals(DefaultCacheRepository.LibraryIndex.TYPE_FORGE)) {
                            r10 = -1;
                        } else {
                            r10 = z2;
                        }
                        break;
                    case 695073197:
                        if (!key.equals("minecraft")) {
                            r10 = -1;
                        } else {
                            r10 = 0;
                        }
                        break;
                    case 1154621647:
                        if (!key.equals("neoforge")) {
                            r10 = -1;
                        }
                        break;
                    case 1308875475:
                        if (!key.equals("quilt-loader")) {
                            r10 = -1;
                        } else {
                            r10 = 4;
                        }
                        break;
                    default:
                        r10 = -1;
                        break;
                }
                switch (r10) {
                    case 0:
                        break;
                    case 1:
                        gameBuilderGameVersion.version(DefaultCacheRepository.LibraryIndex.TYPE_FORGE, next.getValue());
                        break;
                    case 2:
                        gameBuilderGameVersion.version("neoforge", next.getValue());
                        break;
                    case 3:
                        gameBuilderGameVersion.version("fabric", next.getValue());
                        break;
                    case 4:
                        gameBuilderGameVersion.version("quilt", next.getValue());
                        break;
                    default:
                        throw new IllegalStateException("Unsupported mod loader " + next.getKey());
                }
                z = z2;
            } else {
                boolean z3 = z;
                this.dependents.add(gameBuilderGameVersion.buildAsync());
                onDone().register(new Consumer() { // from class: com.brixcore.mod.modrinth.ModrinthInstallTask$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        this.f$0.lambda$new$0(str, (TaskEvent) obj);
                    }
                });
                ModpackConfiguration<ModrinthManifest> modpackConfiguration2 = null;
                try {
                    if (modpackConfiguration.exists()) {
                        modpackConfiguration2 = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(modpackConfiguration), new TypeToken<ModpackConfiguration<CurseManifest>>() { // from class: com.brixcore.mod.modrinth.ModrinthInstallTask.1
                        }.getType());
                        if (!ModrinthModpackProvider.INSTANCE.getName().equals(modpackConfiguration2.getType())) {
                            throw new IllegalArgumentException("Version " + str + " is not a Modrinth modpack. Cannot update this version.");
                        }
                    }
                } catch (JsonParseException e) {
                } catch (IOException e2) {
                }
                this.config = modpackConfiguration2;
                String[] strArr = new String[2];
                strArr[0] = "/client-overrides";
                strArr[z3 ? 1 : 0] = "/overrides";
                List listAsList = Arrays.asList(strArr);
                this.dependents.add(new ModpackInstallTask(file, this.run, modpack.getEncoding(), listAsList, new Predicate() { // from class: com.brixcore.mod.modrinth.ModrinthInstallTask$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return ModrinthInstallTask.lambda$new$1((String) obj);
                    }
                }, modpackConfiguration2).withStage("Brix.modpack"));
                this.dependents.add(new MinecraftInstanceTask(file, modpack.getEncoding(), listAsList, modrinthManifest, ModrinthModpackProvider.INSTANCE, modrinthManifest.getName(), modrinthManifest.getVersionId(), this.repository.getModpackConfiguration(str)).withStage("Brix.modpack"));
                this.dependencies.add(new ModrinthCompletionTask(defaultDependencyManager, str, modrinthManifest));
                return;
            }
        }
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
            for (final ModrinthManifest.File oldManifestFile : this.config.getManifest().getFiles()) {
                Path oldFile = this.run.toPath().resolve(oldManifestFile.getPath());
                if (Files.exists(oldFile, new LinkOption[0])) {
                    Stream<ModrinthManifest.File> stream = this.manifest.getFiles().stream();
                    Objects.requireNonNull(oldManifestFile);
                    if (stream.noneMatch(new Predicate() { // from class: com.brixcore.mod.modrinth.ModrinthInstallTask$$ExternalSyntheticLambda2
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            return oldManifestFile.equals((ModrinthManifest.File) obj);
                        }
                    })) {
                        Files.deleteIfExists(oldFile);
                    }
                }
            }
        }
        File root = this.repository.getVersionRoot(this.name);
        FileUtils.writeText(new File(root, "modrinth.index.json"), JsonUtils.GSON.toJson(this.manifest));
    }
}
