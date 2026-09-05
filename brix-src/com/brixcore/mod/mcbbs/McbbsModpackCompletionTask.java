package com.brixcore.mod.mcbbs;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.ModpackCompletionException;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.curse.CurseMetaMod;
import com.brixcore.task.CompletableFutureTask;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.GetTask;
import com.brixcore.task.Task;
import com.brixcore.task.TaskCompletableFuture;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.function.ExceptionalBiConsumer;
import com.brixcore.util.function.ExceptionalConsumer;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.function.ExceptionalRunnable;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes7.dex */
public class McbbsModpackCompletionTask extends CompletableFutureTask<Void> {
    private final AtomicBoolean allNameKnown;
    private ModpackConfiguration<McbbsModpackManifest> configuration;
    private final File configurationFile;
    private final List<Task<?>> dependencies;
    private final DefaultDependencyManager dependency;
    private final AtomicInteger finished;
    private McbbsModpackManifest manifest;
    private final ModManager modManager;
    private final AtomicBoolean notFound;
    private final DefaultGameRepository repository;
    private final String version;

    public McbbsModpackCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        this(dependencyManager, version, null);
    }

    public McbbsModpackCompletionTask(DefaultDependencyManager dependencyManager, String version, ModpackConfiguration<McbbsModpackManifest> configuration) {
        this.dependencies = new ArrayList();
        this.allNameKnown = new AtomicBoolean(true);
        this.finished = new AtomicInteger(0);
        this.notFound = new AtomicBoolean(false);
        this.dependency = dependencyManager;
        this.repository = dependencyManager.getGameRepository();
        this.modManager = this.repository.getModManager(version);
        this.version = version;
        this.configurationFile = this.repository.getModpackConfiguration(version);
        this.configuration = configuration;
        setStage("Brix.modpack.download");
    }

    @Override // com.brixcore.task.CompletableFutureTask
    public CompletableFuture<Void> getFuture(final TaskCompletableFuture executor) {
        return breakable(CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda5
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() throws Exception {
                this.f$0.lambda$getFuture$0();
            }
        })).thenComposeAsync(new Function() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$getFuture$7(executor, (Void) obj);
            }
        }).thenComposeAsync((Function<? super U, ? extends CompletionStage<U>>) new Function() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$getFuture$11(executor, (Void) obj);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getFuture$0() throws Exception {
        if (this.configuration == null) {
            try {
                this.configuration = (ModpackConfiguration) JsonUtils.fromNonNullJson(FileUtils.readText(this.configurationFile), new TypeToken<ModpackConfiguration<McbbsModpackManifest>>() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask.1
                }.getType());
            } catch (JsonParseException | IOException e) {
                throw new IOException("Malformed modpack configuration");
            }
        }
        this.manifest = this.configuration.getManifest();
        if (this.manifest == null) {
            throw new CompletableFutureTask.CustomException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$getFuture$7(final TaskCompletableFuture executor, Void unused) {
        return breakable(CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda12
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() throws Exception {
                this.f$0.lambda$getFuture$1();
            }
        })).thenComposeAsync(Lang.wrap(new ExceptionalFunction() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda1
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$getFuture$2(executor, (Void) obj);
            }
        })).thenComposeAsync((Function<? super U, ? extends CompletionStage<U>>) Lang.wrap(new ExceptionalFunction() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda2
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$getFuture$3(executor, (String) obj);
            }
        })).thenAcceptAsync(Lang.wrapConsumer(new ExceptionalConsumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda3
            @Override // com.brixcore.util.function.ExceptionalConsumer
            public final void accept(Object obj) throws Exception {
                this.f$0.lambda$getFuture$6(obj);
            }
        })));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getFuture$1() throws Exception {
        if (StringUtils.isBlank(this.manifest.getFileApi())) {
            throw new CompletableFutureTask.CustomException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$getFuture$2(TaskCompletableFuture executor, Void unused1) throws Exception {
        return executor.one(new GetTask(new URL(this.manifest.getFileApi() + "/manifest.json")));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 3 */
    public /* synthetic */ CompletableFuture lambda$getFuture$3(TaskCompletableFuture executor, String remoteManifestJson) throws Exception {
        try {
            McbbsModpackManifest remoteManifest = (McbbsModpackManifest) JsonUtils.fromNonNullJson(remoteManifestJson, McbbsModpackManifest.class);
            this.repository.getVersionRoot(this.version).toPath();
            Map<McbbsModpackManifest.File, McbbsModpackManifest.File> localFiles = (Map) this.manifest.getFiles().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
            List<McbbsModpackManifest.File> newFiles = new ArrayList<>(remoteManifest.getFiles().size());
            List<Task<?>> tasks = new ArrayList<>();
            for (McbbsModpackManifest.File file : remoteManifest.getFiles()) {
                Path actualPath = getFilePath(file);
                McbbsModpackManifest.File oldFile = localFiles.remove(file);
                boolean download = false;
                if (oldFile == null) {
                    download = true;
                } else if (actualPath != null) {
                    if (!Files.exists(actualPath, new LinkOption[0])) {
                        download = true;
                    } else if (getFileHash(file) != null) {
                        String fileHash = DigestUtils.digestToString(CacheRepository.SHA1, actualPath);
                        String oldHash = getFileHash(oldFile);
                        String newHash = getFileHash(file);
                        if (oldHash == null) {
                            download = true;
                        } else if (!Objects.equals(fileHash, newHash) && (file.isForce() || Objects.equals(oldHash, fileHash))) {
                            download = true;
                        }
                    }
                }
                if (download) {
                    tasks.add(downloadFile(remoteManifest, file));
                }
                newFiles.add(mergeFile(oldFile, file));
            }
            Iterator<McbbsModpackManifest.File> it = localFiles.keySet().iterator();
            while (it.hasNext()) {
                Path actualPath2 = getFilePath(it.next());
                if (actualPath2 != null && Files.exists(actualPath2, new LinkOption[0])) {
                    Files.deleteIfExists(actualPath2);
                }
            }
            this.manifest = remoteManifest.setFiles(newFiles);
            return executor.all((Collection) tasks.stream().filter(new Predicate() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((Task) obj);
                }
            }).collect(Collectors.toList()));
        } catch (JsonParseException e) {
            throw new IOException("Unable to parse server manifest.json from " + this.manifest.getFileApi(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getFuture$6(Object unused1) throws Exception {
        File manifestFile = this.repository.getModpackConfiguration(this.version);
        FileUtils.writeText(manifestFile, JsonUtils.GSON.toJson(new ModpackConfiguration(this.manifest, this.configuration.getType(), this.manifest.getName(), this.manifest.getVersion(), (List) this.manifest.getFiles().stream().flatMap(new Function() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return McbbsModpackCompletionTask.lambda$getFuture$4((McbbsModpackManifest.File) obj);
            }
        }).map(new Function() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return McbbsModpackCompletionTask.lambda$getFuture$5((McbbsModpackManifest.AddonFile) obj);
            }
        }).collect(Collectors.toList()))));
    }

    static /* synthetic */ Stream lambda$getFuture$4(McbbsModpackManifest.File file) {
        if (file instanceof McbbsModpackManifest.AddonFile) {
            return Stream.of((McbbsModpackManifest.AddonFile) file);
        }
        return Stream.empty();
    }

    static /* synthetic */ ModpackConfiguration.FileInformation lambda$getFuture$5(McbbsModpackManifest.AddonFile file) {
        return new ModpackConfiguration.FileInformation(file.getPath(), file.getHash());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$getFuture$11(final TaskCompletableFuture executor, Void unused) {
        final AtomicBoolean allNameKnown = new AtomicBoolean(true);
        final AtomicInteger finished = new AtomicInteger(0);
        final AtomicBoolean notFound = new AtomicBoolean(false);
        return breakable(CompletableFuture.completedFuture(null).thenComposeAsync(Lang.wrap(new ExceptionalFunction() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda10
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$getFuture$9(finished, notFound, allNameKnown, executor, obj);
            }
        })).whenComplete(Lang.wrap(new ExceptionalBiConsumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda11
            @Override // com.brixcore.util.function.ExceptionalBiConsumer
            public final void accept(Object obj, Object obj2) throws Exception {
                McbbsModpackCompletionTask.lambda$getFuture$10(notFound, allNameKnown, obj, (Throwable) obj2);
            }
        })));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$getFuture$9(final AtomicInteger finished, final AtomicBoolean notFound, final AtomicBoolean allNameKnown, TaskCompletableFuture executor, Object unused1) throws Exception {
        List<Task<?>> dependencies = new ArrayList<>();
        McbbsModpackManifest newManifest = this.manifest.setFiles((List) this.manifest.getFiles().parallelStream().map(new Function() { // from class: com.brixcore.mod.mcbbs.McbbsModpackCompletionTask$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$getFuture$8(finished, notFound, allNameKnown, (McbbsModpackManifest.File) obj);
            }
        }).collect(Collectors.toList()));
        this.manifest = newManifest;
        this.configuration = this.configuration.setManifest(newManifest);
        FileUtils.writeText(this.configurationFile, JsonUtils.GSON.toJson(this.configuration));
        for (McbbsModpackManifest.File file : newManifest.getFiles()) {
            if (file instanceof McbbsModpackManifest.CurseFile) {
                McbbsModpackManifest.CurseFile curseFile = (McbbsModpackManifest.CurseFile) file;
                if (StringUtils.isNotBlank(curseFile.getFileName()) && !this.modManager.hasSimpleMod(curseFile.getFileName())) {
                    FileDownloadTask task = new FileDownloadTask(curseFile.getUrl(), this.modManager.getSimpleModPath(curseFile.getFileName()).toFile());
                    task.setCacheRepository(this.dependency.getCacheRepository());
                    task.setCaching(true);
                    dependencies.add(task.withCounter("Brix.modpack.download"));
                }
            }
        }
        if (!dependencies.isEmpty()) {
            getProperties().put("total", Integer.valueOf(dependencies.size()));
            notifyPropertiesChanged();
        }
        return executor.all(dependencies);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ McbbsModpackManifest.File lambda$getFuture$8(AtomicInteger finished, AtomicBoolean notFound, AtomicBoolean allNameKnown, McbbsModpackManifest.File rawFile) {
        updateProgress(finished.incrementAndGet(), this.manifest.getFiles().size());
        if (rawFile instanceof McbbsModpackManifest.CurseFile) {
            McbbsModpackManifest.CurseFile file = (McbbsModpackManifest.CurseFile) rawFile;
            if (StringUtils.isBlank(file.getFileName())) {
                try {
                    return file.withFileName(NetworkUtils.detectFileName(file.getUrl()));
                } catch (IOException e) {
                    try {
                        String result = NetworkUtils.doGet(NetworkUtils.toURL(String.format("https://cursemeta.dries007.net/%d/%d.json", Integer.valueOf(file.getProjectID()), Integer.valueOf(file.getFileID()))));
                        CurseMetaMod mod = (CurseMetaMod) JsonUtils.fromNonNullJson(result, CurseMetaMod.class);
                        return file.withFileName(mod.getFileNameOnDisk()).withURL(mod.getDownloadURL());
                    } catch (JsonParseException e2) {
                        e2 = e2;
                        try {
                            String result2 = NetworkUtils.doGet(NetworkUtils.toURL(String.format("https://addons-ecs.forgesvc.net/api/v2/addon/%d/file/%d", Integer.valueOf(file.getProjectID()), Integer.valueOf(file.getFileID()))));
                            CurseMetaMod mod2 = (CurseMetaMod) JsonUtils.fromNonNullJson(result2, CurseMetaMod.class);
                            return file.withFileName(mod2.getFileName()).withURL(mod2.getDownloadURL());
                        } catch (JsonParseException e3) {
                            e3 = e3;
                            Logging.LOG.log(Level.WARNING, "Unable to fetch the file name of URL: " + file.getUrl(), (Throwable) e);
                            Logging.LOG.log(Level.WARNING, "Unable to fetch the file name of URL: " + file.getUrl(), (Throwable) e2);
                            Logging.LOG.log(Level.WARNING, "Unable to fetch the file name of URL: " + file.getUrl(), (Throwable) e3);
                            allNameKnown.set(false);
                            return file;
                        } catch (FileNotFoundException fof) {
                            Logging.LOG.log(Level.WARNING, "Could not query forgesvc for deleted mods: " + file.getUrl(), (Throwable) fof);
                            notFound.set(true);
                            return file;
                        } catch (IOException e4) {
                            e3 = e4;
                            Logging.LOG.log(Level.WARNING, "Unable to fetch the file name of URL: " + file.getUrl(), (Throwable) e);
                            Logging.LOG.log(Level.WARNING, "Unable to fetch the file name of URL: " + file.getUrl(), (Throwable) e2);
                            Logging.LOG.log(Level.WARNING, "Unable to fetch the file name of URL: " + file.getUrl(), (Throwable) e3);
                            allNameKnown.set(false);
                            return file;
                        }
                    } catch (FileNotFoundException fof2) {
                        Logging.LOG.log(Level.WARNING, "Could not query cursemeta for deleted mods: " + file.getUrl(), (Throwable) fof2);
                        notFound.set(true);
                        return file;
                    } catch (IOException e5) {
                        e2 = e5;
                        String result3 = NetworkUtils.doGet(NetworkUtils.toURL(String.format("https://addons-ecs.forgesvc.net/api/v2/addon/%d/file/%d", Integer.valueOf(file.getProjectID()), Integer.valueOf(file.getFileID()))));
                        CurseMetaMod mod3 = (CurseMetaMod) JsonUtils.fromNonNullJson(result3, CurseMetaMod.class);
                        return file.withFileName(mod3.getFileName()).withURL(mod3.getDownloadURL());
                    }
                }
            }
            return file;
        }
        return rawFile;
    }

    static /* synthetic */ void lambda$getFuture$10(AtomicBoolean notFound, AtomicBoolean allNameKnown, Object unused1, Throwable ex) throws Exception {
        if (notFound.get()) {
            throw new ModpackCompletionException(new FileNotFoundException());
        }
        if (!allNameKnown.get() || ex != null) {
            throw new ModpackCompletionException();
        }
    }

    private Path getFilePath(McbbsModpackManifest.File file) {
        if (file instanceof McbbsModpackManifest.AddonFile) {
            return this.modManager.getRepository().getRunDirectory(this.modManager.getInstanceId()).toPath().resolve(((McbbsModpackManifest.AddonFile) file).getPath());
        }
        if (file instanceof McbbsModpackManifest.CurseFile) {
            String fileName = ((McbbsModpackManifest.CurseFile) file).getFileName();
            if (fileName == null) {
                return null;
            }
            return this.modManager.getSimpleModPath(fileName);
        }
        throw new IllegalArgumentException();
    }

    private String getFileHash(McbbsModpackManifest.File file) {
        if (file instanceof McbbsModpackManifest.AddonFile) {
            return ((McbbsModpackManifest.AddonFile) file).getHash();
        }
        return null;
    }

    private Task<?> downloadFile(McbbsModpackManifest remoteManifest, McbbsModpackManifest.File file) throws IOException {
        if (file instanceof McbbsModpackManifest.AddonFile) {
            McbbsModpackManifest.AddonFile addonFile = (McbbsModpackManifest.AddonFile) file;
            return new FileDownloadTask(new URL(remoteManifest.getFileApi() + "/overrides/" + NetworkUtils.encodeLocation(addonFile.getPath())), this.modManager.getSimpleModPath(addonFile.getPath()).toFile(), addonFile.getHash() != null ? new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, addonFile.getHash()) : null);
        }
        if (file instanceof McbbsModpackManifest.CurseFile) {
            return null;
        }
        throw new IllegalArgumentException();
    }

    private McbbsModpackManifest.File mergeFile(McbbsModpackManifest.File oldFile, McbbsModpackManifest.File newFile) {
        if (newFile instanceof McbbsModpackManifest.AddonFile) {
            return newFile;
        }
        if (newFile instanceof McbbsModpackManifest.CurseFile) {
            return oldFile != null ? oldFile : newFile;
        }
        throw new IllegalArgumentException();
    }
}
