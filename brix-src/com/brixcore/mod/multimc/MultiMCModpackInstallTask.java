package com.brixcore.mod.multimc;

import com.brixcore.download.DefaultCacheRepository;
import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.Arguments;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Version;
import com.brixcore.mod.MinecraftInstanceTask;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackInstallTask;
import com.brixcore.task.Task;
import com.brixcore.task.TaskEvent;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes10.dex */
public final class MultiMCModpackInstallTask extends Task<Void> {
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final List<Task<?>> dependents = new ArrayList(4);
    private final MultiMCInstanceConfiguration manifest;
    private final Modpack modpack;
    private final String name;
    private final DefaultGameRepository repository;
    private final File zipFile;

    public MultiMCModpackInstallTask(DefaultDependencyManager dependencyManager, File zipFile, Modpack modpack, MultiMCInstanceConfiguration manifest, final String name) {
        this.zipFile = zipFile;
        this.modpack = modpack;
        this.manifest = manifest;
        this.name = name;
        this.repository = dependencyManager.getGameRepository();
        File json = this.repository.getModpackConfiguration(name);
        if (this.repository.hasVersion(name) && !json.exists()) {
            throw new IllegalArgumentException("Version " + name + " already exists.");
        }
        final GameBuilder builder = dependencyManager.gameBuilder().name(name).gameVersion(manifest.getGameVersion());
        if (manifest.getMmcPack() != null) {
            Optional<MultiMCManifest.MultiMCManifestComponent> forge = manifest.getMmcPack().getComponents().stream().filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((MultiMCManifest.MultiMCManifestComponent) obj).getUid().equals("net.minecraftforge");
                }
            }).findAny();
            forge.ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MultiMCModpackInstallTask.lambda$new$1(builder, (MultiMCManifest.MultiMCManifestComponent) obj);
                }
            });
            Optional<MultiMCManifest.MultiMCManifestComponent> neoForge = manifest.getMmcPack().getComponents().stream().filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((MultiMCManifest.MultiMCManifestComponent) obj).getUid().equals("net.neoforged");
                }
            }).findAny();
            neoForge.ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MultiMCModpackInstallTask.lambda$new$3(builder, (MultiMCManifest.MultiMCManifestComponent) obj);
                }
            });
            Optional<MultiMCManifest.MultiMCManifestComponent> liteLoader = manifest.getMmcPack().getComponents().stream().filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((MultiMCManifest.MultiMCManifestComponent) obj).getUid().equals("com.mumfrey.liteloader");
                }
            }).findAny();
            liteLoader.ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MultiMCModpackInstallTask.lambda$new$5(builder, (MultiMCManifest.MultiMCManifestComponent) obj);
                }
            });
            Optional<MultiMCManifest.MultiMCManifestComponent> fabric = manifest.getMmcPack().getComponents().stream().filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((MultiMCManifest.MultiMCManifestComponent) obj).getUid().equals("net.fabricmc.fabric-loader");
                }
            }).findAny();
            fabric.ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda11
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MultiMCModpackInstallTask.lambda$new$7(builder, (MultiMCManifest.MultiMCManifestComponent) obj);
                }
            });
            Optional<MultiMCManifest.MultiMCManifestComponent> quilt = manifest.getMmcPack().getComponents().stream().filter(new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((MultiMCManifest.MultiMCManifestComponent) obj).getUid().equals("org.quiltmc.quilt-loader");
                }
            }).findAny();
            quilt.ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MultiMCModpackInstallTask.lambda$new$9(builder, (MultiMCManifest.MultiMCManifestComponent) obj);
                }
            });
        }
        this.dependents.add(builder.buildAsync());
        onDone().register(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$new$10(name, (TaskEvent) obj);
            }
        });
    }

    static /* synthetic */ void lambda$new$1(GameBuilder builder, MultiMCManifest.MultiMCManifestComponent c) {
        if (c.getVersion() != null) {
            builder.version(DefaultCacheRepository.LibraryIndex.TYPE_FORGE, c.getVersion());
        }
    }

    static /* synthetic */ void lambda$new$3(GameBuilder builder, MultiMCManifest.MultiMCManifestComponent c) {
        if (c.getVersion() != null) {
            builder.version("neoforge", c.getVersion());
        }
    }

    static /* synthetic */ void lambda$new$5(GameBuilder builder, MultiMCManifest.MultiMCManifestComponent c) {
        if (c.getVersion() != null) {
            builder.version("liteloader", c.getVersion());
        }
    }

    static /* synthetic */ void lambda$new$7(GameBuilder builder, MultiMCManifest.MultiMCManifestComponent c) {
        if (c.getVersion() != null) {
            builder.version("fabric", c.getVersion());
        }
    }

    static /* synthetic */ void lambda$new$9(GameBuilder builder, MultiMCManifest.MultiMCManifestComponent c) {
        if (c.getVersion() != null) {
            builder.version("quilt", c.getVersion());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10(String name, TaskEvent event) {
        if (event.isFailed()) {
            this.repository.removeVersionFromDisk(name);
        }
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        ModpackConfiguration<MultiMCInstanceConfiguration> config;
        String subDirectory = "/.minecraft";
        File run = this.repository.getRunDirectory(this.name);
        File json = this.repository.getModpackConfiguration(this.name);
        ModpackConfiguration<MultiMCInstanceConfiguration> config2 = null;
        try {
            if (json.exists()) {
                config2 = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(json), new TypeToken<ModpackConfiguration<MultiMCInstanceConfiguration>>() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask.1
                }.getType());
                if (!MultiMCModpackProvider.INSTANCE.getName().equals(config2.getType())) {
                    throw new IllegalArgumentException("Version " + this.name + " is not a MultiMC modpack. Cannot update this version.");
                }
            }
            config = config2;
        } catch (JsonParseException | IOException e) {
            config = config2;
        }
        FileSystem fs = CompressingUtils.readonly(this.zipFile.toPath()).setEncoding(this.modpack.getEncoding()).build();
        try {
            if (!Files.exists(fs.getPath("/.minecraft", new String[0]), new LinkOption[0])) {
                if (!Files.exists(fs.getPath("/minecraft", new String[0]), new LinkOption[0])) {
                    if (Files.exists(fs.getPath("/" + this.manifest.getName() + "/.minecraft", new String[0]), new LinkOption[0])) {
                        subDirectory = "/" + this.manifest.getName() + "/.minecraft";
                    } else {
                        subDirectory = Files.exists(fs.getPath(new StringBuilder().append("/").append(this.manifest.getName()).append("/minecraft").toString(), new String[0]), new LinkOption[0]) ? "/" + this.manifest.getName() + "/minecraft" : "/" + this.manifest.getName() + "/.minecraft";
                    }
                } else {
                    subDirectory = "/minecraft";
                }
            }
            if (fs != null) {
                fs.close();
            }
            this.dependents.add(new ModpackInstallTask(this.zipFile, run, this.modpack.getEncoding(), Collections.singletonList(subDirectory), new Predicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackInstallTask$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return MultiMCModpackInstallTask.lambda$preExecute$11((String) obj);
                }
            }, config).withStage("Brix.modpack"));
            this.dependents.add(new MinecraftInstanceTask(this.zipFile, this.modpack.getEncoding(), Collections.singletonList(subDirectory), this.manifest, MultiMCModpackProvider.INSTANCE, this.manifest.getName(), null, this.repository.getModpackConfiguration(this.name)).withStage("Brix.modpack"));
        } catch (Throwable th) {
            if (fs == null) {
                throw th;
            }
            try {
                fs.close();
                throw th;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
                throw th;
            }
        }
    }

    static /* synthetic */ boolean lambda$preExecute$11(String any) {
        return true;
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        int i;
        int i2;
        Version version = this.repository.readVersionJson(this.name);
        FileSystem fs = CompressingUtils.readonly(this.zipFile.toPath()).setAutoDetectEncoding(true).build();
        try {
            int i3 = 0;
            Path root = MultiMCModpackProvider.getRootPath(fs.getPath("/", new String[0]));
            Path patches = root.resolve("patches");
            if (!Files.exists(patches, new LinkOption[0])) {
                i = 0;
            } else {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(patches);
                try {
                    for (Path patchJson : directoryStream) {
                        if (!patchJson.toString().endsWith(".json")) {
                            i2 = i3;
                        } else {
                            MultiMCInstancePatch multiMCPatch = (MultiMCInstancePatch) JsonUtils.GSON.fromJson(FileUtils.readText(patchJson), MultiMCInstancePatch.class);
                            List<String> arguments = new ArrayList<>();
                            for (String arg : multiMCPatch.getTweakers()) {
                                arguments.add("--tweakClass");
                                arguments.add(arg);
                                i3 = i3;
                            }
                            i2 = i3;
                            Version patch = new Version(multiMCPatch.getName(), multiMCPatch.getVersion(), 1, new Arguments().addGameArguments(arguments), multiMCPatch.getMainClass(), multiMCPatch.getLibraries());
                            Version[] versionArr = new Version[1];
                            versionArr[i2] = patch;
                            version = version.addPatch(versionArr);
                        }
                        i3 = i2;
                    }
                    i = i3;
                    if (directoryStream != null) {
                        directoryStream.close();
                    }
                } catch (Throwable th) {
                    if (directoryStream == null) {
                        throw th;
                    }
                    try {
                        directoryStream.close();
                        throw th;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        throw th;
                    }
                }
            }
            Path libraries = root.resolve("libraries");
            if (Files.exists(libraries, new LinkOption[i])) {
                FileUtils.copyDirectory(libraries, this.repository.getVersionRoot(this.name).toPath().resolve("libraries"));
            }
            Path jarmods = root.resolve("jarmods");
            if (Files.exists(jarmods, new LinkOption[0])) {
                FileUtils.copyDirectory(jarmods, this.repository.getVersionRoot(this.name).toPath().resolve("jarmods"));
            }
            String iconKey = this.manifest.getIconKey();
            if (iconKey != null) {
                Path iconFile = root.resolve(iconKey + ".png");
                if (Files.exists(iconFile, new LinkOption[0])) {
                    FileUtils.copyFile(iconFile, this.repository.getVersionRoot(this.name).toPath().resolve("icon.png"));
                }
            }
            if (fs != null) {
                fs.close();
            }
            this.dependencies.add(this.repository.saveAsync(version));
        } catch (Throwable th3) {
            if (fs == null) {
                throw th3;
            }
            try {
                fs.close();
                throw th3;
            } catch (Throwable th4) {
                th3.addSuppressed(th4);
                throw th3;
            }
        }
    }
}
