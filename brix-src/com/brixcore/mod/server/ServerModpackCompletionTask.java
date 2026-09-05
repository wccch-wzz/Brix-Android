package com.brixcore.mod.server;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.GameBuilder;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.GetTask;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
public class ServerModpackCompletionTask extends Task<Void> {
    private final List<Task<?>> dependencies;
    private final DefaultDependencyManager dependencyManager;
    private GetTask dependent;
    private ModpackConfiguration<ServerModpackManifest> manifest;
    private ServerModpackManifest remoteManifest;
    private final DefaultGameRepository repository;
    private final String version;

    public ServerModpackCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        this(dependencyManager, version, null);
    }

    public ServerModpackCompletionTask(DefaultDependencyManager dependencyManager, String version, ModpackConfiguration<ServerModpackManifest> manifest) {
        this.dependencies = new ArrayList();
        this.dependencyManager = dependencyManager;
        this.repository = dependencyManager.getGameRepository();
        this.version = version;
        if (manifest == null) {
            try {
                File manifestFile = this.repository.getModpackConfiguration(version);
                if (manifestFile.exists()) {
                    this.manifest = (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(manifestFile), new TypeToken<ModpackConfiguration<ServerModpackManifest>>() { // from class: com.brixcore.mod.server.ServerModpackCompletionTask.1
                    }.getType());
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read Server modpack manifest.json", (Throwable) e);
            }
        } else {
            this.manifest = manifest;
        }
        setStage("Brix.modpack.download");
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        if (this.manifest == null || StringUtils.isBlank(this.manifest.getManifest().getFileApi())) {
            return;
        }
        this.dependent = new GetTask(new URL(this.manifest.getManifest().getFileApi() + "/server-manifest.json"));
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependent == null ? Collections.emptySet() : Collections.singleton(this.dependent);
    }

    private Map<String, String> toMap(Collection<ServerModpackManifest.Addon> addons) {
        return (Map) addons.stream().collect(Collectors.toMap(new Function() { // from class: com.brixcore.mod.server.ServerModpackCompletionTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ServerModpackManifest.Addon) obj).getId();
            }
        }, new Function() { // from class: com.brixcore.mod.server.ServerModpackCompletionTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ServerModpackManifest.Addon) obj).getVersion();
            }
        }));
    }

    /* JADX WARN: Code duplicated, block: B:43:0x01bd  */
    /* JADX WARN: Code duplicated, block: B:44:0x020f  */
    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        Map<String, String> oldAddons;
        boolean download;
        if (this.manifest == null || StringUtils.isBlank(this.manifest.getManifest().getFileApi())) {
            return;
        }
        try {
            this.remoteManifest = (ServerModpackManifest) JsonUtils.fromNonNullJson(this.dependent.getResult(), ServerModpackManifest.class);
            Map<String, String> oldAddons2 = toMap(this.manifest.getManifest().getAddons());
            Map<String, String> newAddons = toMap(this.remoteManifest.getAddons());
            if (!Objects.equals(oldAddons2, newAddons)) {
                GameBuilder builder = this.dependencyManager.gameBuilder().name(this.version);
                for (ServerModpackManifest.Addon addon : this.remoteManifest.getAddons()) {
                    builder.version(addon.getId(), addon.getVersion());
                }
                this.dependencies.add(builder.buildAsync());
            }
            Path rootPath = this.repository.getVersionRoot(this.version).toPath().toAbsolutePath().normalize();
            Map<String, ModpackConfiguration.FileInformation> files = (Map) this.manifest.getManifest().getFiles().stream().collect(Collectors.toMap(new Function() { // from class: com.brixcore.mod.server.ServerModpackCompletionTask$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((ModpackConfiguration.FileInformation) obj).getPath();
                }
            }, Function.identity()));
            Set<String> remoteFiles = (Set) this.remoteManifest.getFiles().stream().map(new Function() { // from class: com.brixcore.mod.server.ServerModpackCompletionTask$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((ModpackConfiguration.FileInformation) obj).getPath();
                }
            }).collect(Collectors.toSet());
            Path runDirectory = this.repository.getRunDirectory(this.version).toPath().toAbsolutePath().normalize();
            Path modsDirectory = runDirectory.resolve("mods");
            int total = 0;
            for (ModpackConfiguration.FileInformation file : this.remoteManifest.getFiles()) {
                Path actualPath = rootPath.resolve(file.getPath()).toAbsolutePath().normalize();
                String fileName = actualPath.getFileName().toString();
                if (!actualPath.startsWith(rootPath)) {
                    throw new IOException("Unsecure path: " + file.getPath());
                }
                if (!files.containsKey(file.getPath())) {
                    oldAddons = oldAddons2;
                } else {
                    if (!modsDirectory.equals(actualPath.getParent())) {
                        oldAddons = oldAddons2;
                    } else {
                        oldAddons = oldAddons2;
                        if (!Files.notExists(actualPath.resolveSibling(fileName + ".disabled"), new LinkOption[0]) || !Files.notExists(actualPath.resolveSibling(fileName + ".old"), new LinkOption[0])) {
                        }
                        if (download) {
                            total++;
                            this.dependencies.add(new FileDownloadTask(new URL(this.remoteManifest.getFileApi() + "/overrides/" + NetworkUtils.encodeLocation(file.getPath())), actualPath.toFile(), new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, file.getHash())).withCounter("Brix.modpack.download"));
                        }
                        oldAddons2 = oldAddons;
                        newAddons = newAddons;
                        files = files;
                        runDirectory = runDirectory;
                    }
                    if (!Files.exists(actualPath, new LinkOption[0])) {
                        download = true;
                    } else {
                        String fileHash = DigestUtils.digestToString(CacheRepository.SHA1, actualPath);
                        String oldHash = files.get(file.getPath()).getHash();
                        download = !Objects.equals(oldHash, file.getHash()) && Objects.equals(oldHash, fileHash);
                    }
                    if (download) {
                        total++;
                        this.dependencies.add(new FileDownloadTask(new URL(this.remoteManifest.getFileApi() + "/overrides/" + NetworkUtils.encodeLocation(file.getPath())), actualPath.toFile(), new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, file.getHash())).withCounter("Brix.modpack.download"));
                    }
                    oldAddons2 = oldAddons;
                    newAddons = newAddons;
                    files = files;
                    runDirectory = runDirectory;
                }
                download = true;
                if (download) {
                    total++;
                    this.dependencies.add(new FileDownloadTask(new URL(this.remoteManifest.getFileApi() + "/overrides/" + NetworkUtils.encodeLocation(file.getPath())), actualPath.toFile(), new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, file.getHash())).withCounter("Brix.modpack.download"));
                }
                oldAddons2 = oldAddons;
                newAddons = newAddons;
                files = files;
                runDirectory = runDirectory;
            }
            for (ModpackConfiguration.FileInformation file2 : this.manifest.getManifest().getFiles()) {
                Path actualPath2 = rootPath.resolve(file2.getPath());
                if (Files.exists(actualPath2, new LinkOption[0]) && !remoteFiles.contains(file2.getPath())) {
                    Files.deleteIfExists(actualPath2);
                }
            }
            getProperties().put("total", Integer.valueOf(this.dependencies.size()));
            notifyPropertiesChanged();
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        if (this.manifest == null || StringUtils.isBlank(this.manifest.getManifest().getFileApi())) {
            return;
        }
        File manifestFile = this.repository.getModpackConfiguration(this.version);
        FileUtils.writeText(manifestFile, JsonUtils.GSON.toJson(new ModpackConfiguration(this.remoteManifest, this.manifest.getType(), this.manifest.getName(), this.manifest.getVersion(), this.remoteManifest.getFiles())));
    }
}
