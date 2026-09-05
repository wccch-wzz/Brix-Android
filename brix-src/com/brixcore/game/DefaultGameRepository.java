package com.brixcore.game;

import com.brixcore.download.MaintainTask;
import com.brixcore.download.game.VersionJsonSaveTask;
import com.brixcore.event.Event;
import com.brixcore.event.EventBus;
import com.brixcore.event.GameJsonParseFailedEvent;
import com.brixcore.event.RefreshedVersionsEvent;
import com.brixcore.event.RemoveVersionEvent;
import com.brixcore.event.RenameVersionEvent;
import com.brixcore.game.tlauncher.TLauncherVersion;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.task.Task;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.ToStringBuilder;
import com.brixcore.util.function.ExceptionalSupplier;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes2.dex */
public class DefaultGameRepository implements GameRepository {
    private File baseDirectory;
    private final ConcurrentHashMap<File, Optional<String>> gameVersions = new ConcurrentHashMap<>();
    protected Map<String, Version> versions;

    public DefaultGameRepository(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public File getBaseDirectory() {
        return this.baseDirectory;
    }

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override // com.brixcore.game.GameRepository, com.brixcore.game.VersionProvider
    public boolean hasVersion(String id) {
        return id != null && this.versions.containsKey(id);
    }

    @Override // com.brixcore.game.GameRepository, com.brixcore.game.VersionProvider
    public Version getVersion(String id) {
        if (!hasVersion(id)) {
            throw new VersionNotFoundException("Version '" + id + "' does not exist in " + this.versions.keySet() + ".");
        }
        return this.versions.get(id);
    }

    @Override // com.brixcore.game.GameRepository
    public int getVersionCount() {
        return this.versions.size();
    }

    @Override // com.brixcore.game.GameRepository
    public Collection<Version> getVersions() {
        return this.versions.values();
    }

    @Override // com.brixcore.game.GameRepository
    public File getLibrariesDirectory(Version version) {
        return new File(getBaseDirectory(), "libraries");
    }

    @Override // com.brixcore.game.GameRepository
    public File getLibraryFile(Version version, Library lib) {
        if ("local".equals(lib.getHint()) && lib.getFileName() != null) {
            return new File(getVersionRoot(version.getId()), "libraries/" + lib.getFileName());
        }
        return new File(getLibrariesDirectory(version), lib.getPath());
    }

    public Path getArtifactFile(Version version, Artifact artifact) {
        return artifact.getPath(getBaseDirectory().toPath().resolve("libraries"));
    }

    public GameDirectoryType getGameDirectoryType(String id) {
        return GameDirectoryType.ROOT_FOLDER;
    }

    @Override // com.brixcore.game.GameRepository
    public File getRunDirectory(String id) {
        switch (getGameDirectoryType(id)) {
            case VERSION_FOLDER:
                return getVersionRoot(id);
            case ROOT_FOLDER:
                return getBaseDirectory();
            default:
                throw new IllegalStateException();
        }
    }

    @Override // com.brixcore.game.GameRepository
    public File getVersionJar(Version version) {
        Version v = version.resolve(this);
        String id = (String) Optional.ofNullable(v.getJar()).orElse(v.getId());
        return new File(getVersionRoot(id), id + ".jar");
    }

    @Override // com.brixcore.game.GameRepository
    public Optional<String> getGameVersion(final Version version) {
        return this.gameVersions.computeIfAbsent(getVersionJar(version), new Function() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DefaultGameRepository.lambda$getGameVersion$0(version, (File) obj);
            }
        });
    }

    static /* synthetic */ Optional lambda$getGameVersion$0(Version version, File versionJar) {
        Optional<String> gameVersion = GameVersion.minecraftVersion(versionJar);
        if (!gameVersion.isPresent()) {
            Logging.LOG.warning("Cannot find out game version of " + version.getId() + ", primary jar: " + versionJar.toString() + ", jar exists: " + versionJar.exists());
        }
        return gameVersion;
    }

    @Override // com.brixcore.game.GameRepository
    public Path getModsDirectory(String id) {
        return getRunDirectory(id).toPath().resolve("mods");
    }

    @Override // com.brixcore.game.GameRepository
    public File getVersionRoot(String id) {
        return new File(getBaseDirectory(), "versions/" + id);
    }

    public File getVersionJson(String id) {
        return new File(getVersionRoot(id), id + ".json");
    }

    public Version readVersionJson(String id) throws JsonParseException, IOException {
        return readVersionJson(getVersionJson(id));
    }

    public Version readVersionJson(File file) throws JsonParseException, IOException {
        String jsonText = FileUtils.readText(file);
        try {
            return ((TLauncherVersion) JsonUtils.fromNonNullJson(jsonText, TLauncherVersion.class)).toVersion();
        } catch (JsonParseException e) {
            try {
                return (Version) JsonUtils.fromNonNullJson(jsonText, Version.class);
            } catch (JsonParseException e2) {
                Logging.LOG.warning("Cannot parse version json: " + file.toString() + StringUtils.LF + jsonText);
                throw new JsonParseException("Version json incorrect");
            }
        }
    }

    @Override // com.brixcore.game.GameRepository
    public boolean renameVersion(String from, String to) {
        boolean z = false;
        if (EventBus.EVENT_BUS.fireEvent(new RenameVersionEvent(this, from, to)) == Event.Result.DENY) {
            return false;
        }
        try {
            try {
                Version fromVersion = getVersion(from);
                final Path fromDir = getVersionRoot(from).toPath();
                final Path toDir = getVersionRoot(to).toPath();
                Files.move(fromDir, toDir, new CopyOption[0]);
                final Path fromJson = toDir.resolve(from + ".json");
                final Path fromJar = toDir.resolve(from + ".jar");
                final Path toJson = toDir.resolve(to + ".json");
                final Path toJar = toDir.resolve(to + ".jar");
                boolean hasJarFile = Files.exists(fromJar, new LinkOption[0]);
                try {
                    try {
                        Files.move(fromJson, toJson, new CopyOption[0]);
                        if (hasJarFile) {
                            Files.move(fromJar, toJar, new CopyOption[0]);
                        }
                        if (fromVersion.getId().equals(fromVersion.getJar())) {
                            fromVersion = fromVersion.setJar(null);
                        }
                        FileUtils.writeText(toJson.toFile(), JsonUtils.GSON.toJson(fromVersion.setId(to)));
                        for (Version version : getVersions()) {
                            if (from.equals(version.getInheritsFrom())) {
                                File json = getVersionJson(version.getId()).getAbsoluteFile();
                                FileUtils.writeText(json, JsonUtils.GSON.toJson(version.setInheritsFrom(to)));
                            }
                            z = z;
                        }
                        return true;
                    } catch (IOException e) {
                        Lang.ignoringException(new ExceptionalSupplier() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda1
                            @Override // com.brixcore.util.function.ExceptionalSupplier
                            public final Object get() {
                                return Files.move(toJson, fromJson, new CopyOption[0]);
                            }
                        });
                        if (hasJarFile) {
                            Lang.ignoringException(new ExceptionalSupplier() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda2
                                @Override // com.brixcore.util.function.ExceptionalSupplier
                                public final Object get() {
                                    return Files.move(toJar, fromJar, new CopyOption[0]);
                                }
                            });
                        }
                        Lang.ignoringException(new ExceptionalSupplier() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda3
                            @Override // com.brixcore.util.function.ExceptionalSupplier
                            public final Object get() {
                                return Files.move(toDir, fromDir, new CopyOption[0]);
                            }
                        });
                        throw e;
                    }
                } catch (VersionNotFoundException e2) {
                    e = e2;
                    Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
                    return z;
                } catch (JsonParseException e3) {
                    e = e3;
                    Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
                    return z;
                } catch (IOException e4) {
                    e = e4;
                    Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
                    return z;
                } catch (InvalidPathException e5) {
                    e = e5;
                    Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
                    return z;
                }
            } catch (IOException e6) {
                e = e6;
                boolean z2 = z;
                Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
                return z2;
            }
        } catch (VersionNotFoundException e7) {
            e = e7;
            boolean z3 = z;
            Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
            return z3;
        } catch (JsonParseException e8) {
            e = e8;
            boolean z4 = z;
            Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
            return z4;
        } catch (InvalidPathException e9) {
            e = e9;
            boolean z5 = z;
            Logging.LOG.log(Level.WARNING, "Unable to rename version " + from + " to " + to, (Throwable) e);
            return z5;
        }
    }

    public boolean removeVersionFromDisk(String id) {
        if (EventBus.EVENT_BUS.fireEvent(new RemoveVersionEvent(this, id)) == Event.Result.DENY) {
            return false;
        }
        if (!this.versions.containsKey(id)) {
            return FileUtils.deleteDirectoryQuietly(getVersionRoot(id));
        }
        File file = getVersionRoot(id);
        if (!file.exists()) {
            return true;
        }
        File removedFile = new File(file.getAbsoluteFile().getParentFile(), file.getName() + "_removed");
        if (!file.renameTo(removedFile)) {
            return false;
        }
        try {
            this.versions.remove(id);
            List<File> jsons = FileUtils.listFilesByExtension(removedFile, "json");
            jsons.forEach(new Consumer() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DefaultGameRepository.lambda$removeVersionFromDisk$4((File) obj);
                }
            });
            try {
                FileUtils.deleteDirectory(removedFile);
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Unable to remove version folder: " + file, (Throwable) e);
            }
            refreshVersionsAsync().start();
            return true;
        } catch (Throwable th) {
            refreshVersionsAsync().start();
            throw th;
        }
    }

    static /* synthetic */ void lambda$removeVersionFromDisk$4(File f) {
        if (!f.delete()) {
            Logging.LOG.warning("Unable to delete file " + f);
        }
    }

    protected void refreshVersionsImpl() {
        Map<String, Version> versions = new TreeMap<>();
        if (ClassicVersion.hasClassicVersion(getBaseDirectory())) {
            Version version = new ClassicVersion();
            versions.put(version.getId(), version);
        }
        final SimpleVersionProvider provider = new SimpleVersionProvider();
        File[] files = new File(getBaseDirectory(), "versions").listFiles();
        if (files != null) {
            Stream streamFlatMap = ((Stream) Arrays.stream(files).parallel()).filter(new Predicate() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((File) obj).isDirectory();
                }
            }).flatMap(new Function() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return this.f$0.lambda$refreshVersionsImpl$5((File) obj);
                }
            });
            Objects.requireNonNull(provider);
            streamFlatMap.forEachOrdered(new Consumer() { // from class: com.brixcore.game.DefaultGameRepository$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    provider.addVersion((Version) obj);
                }
            });
        }
        for (Version version2 : provider.getVersionMap().values()) {
            try {
                Version resolved = version2.resolve(provider);
                if (resolved.appliesToCurrentEnvironment()) {
                    versions.put(version2.getId(), version2);
                }
            } catch (VersionNotFoundException e) {
                Logging.LOG.log(Level.WARNING, "Ignoring version " + version2.getId() + " because it inherits from a nonexistent version.");
            }
        }
        this.gameVersions.clear();
        this.versions = versions;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Stream lambda$refreshVersionsImpl$5(File dir) {
        Version version;
        String id = dir.getName();
        File json = new File(dir, id + ".json");
        if (!json.exists()) {
            List<File> jsons = FileUtils.listFilesByExtension(dir, "json");
            if (jsons.size() == 1) {
                Logging.LOG.info("Renaming json file " + jsons.get(0) + " to " + json);
                if (!jsons.get(0).renameTo(json)) {
                    Logging.LOG.warning("Cannot rename json file, ignoring version " + id);
                    return Stream.empty();
                }
                File jar = new File(dir, FileUtils.getNameWithoutExtension(jsons.get(0)) + ".jar");
                if (jar.exists() && !jar.renameTo(new File(dir, id + ".jar"))) {
                    Logging.LOG.warning("Cannot rename jar file, ignoring version " + id);
                    return Stream.empty();
                }
            } else {
                Logging.LOG.info("No available json file found, ignoring version " + id);
                return Stream.empty();
            }
        }
        try {
            version = readVersionJson(json);
        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Malformed version json " + id, (Throwable) e);
            if (EventBus.EVENT_BUS.fireEvent(new GameJsonParseFailedEvent(this, json, id)) != Event.Result.ALLOW) {
                return Stream.empty();
            }
            try {
                Version version2 = readVersionJson(json);
                version = version2;
            } catch (Exception e2) {
                Logging.LOG.log(Level.SEVERE, "User corrected version json is still malformed", (Throwable) e2);
                return Stream.empty();
            }
        }
        if (!id.equals(version.getId())) {
            version._setId(id);
        }
        return Stream.of(version);
    }

    @Override // com.brixcore.game.GameRepository
    public void refreshVersions() {
        refreshVersionsImpl();
        EventBus.EVENT_BUS.fireEvent(new RefreshedVersionsEvent(this));
    }

    @Override // com.brixcore.game.GameRepository
    public AssetIndex getAssetIndex(String version, String assetId) throws IOException {
        try {
            return (AssetIndex) Objects.requireNonNull((AssetIndex) JsonUtils.GSON.fromJson(FileUtils.readText(getIndexFile(version, assetId)), AssetIndex.class));
        } catch (JsonParseException | NullPointerException e) {
            throw new IOException("Asset index file malformed", e);
        }
    }

    @Override // com.brixcore.game.GameRepository
    public Path getActualAssetDirectory(String version, String assetId) {
        try {
            return reconstructAssets(version, assetId);
        } catch (JsonParseException | IOException e) {
            Logging.LOG.log(Level.SEVERE, "Unable to reconstruct asset directory", (Throwable) e);
            return getAssetDirectory(version, assetId);
        }
    }

    @Override // com.brixcore.game.GameRepository
    public Path getAssetDirectory(String version, String assetId) {
        return getBaseDirectory().toPath().resolve("assets");
    }

    @Override // com.brixcore.game.GameRepository
    public Optional<Path> getAssetObject(String version, String assetId, String name) throws IOException {
        try {
            AssetObject assetObject = getAssetIndex(version, assetId).getObjects().get(name);
            return assetObject == null ? Optional.empty() : Optional.of(getAssetObject(version, assetId, assetObject));
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOException("Unrecognized asset object " + name + " in asset " + assetId + " of version " + version, e2);
        }
    }

    @Override // com.brixcore.game.GameRepository
    public Path getAssetObject(String version, String assetId, AssetObject obj) {
        return getAssetObject(version, getAssetDirectory(version, assetId), obj);
    }

    public Path getAssetObject(String version, Path assetDir, AssetObject obj) {
        return assetDir.resolve("objects").resolve(obj.getLocation());
    }

    @Override // com.brixcore.game.GameRepository
    public Path getIndexFile(String version, String assetId) {
        return getAssetDirectory(version, assetId).resolve("indexes").resolve(assetId + ".json");
    }

    @Override // com.brixcore.game.GameRepository
    public Path getLoggingObject(String version, String assetId, LoggingInfo loggingInfo) {
        return getAssetDirectory(version, assetId).resolve("log_configs").resolve(loggingInfo.getFile().getId());
    }

    protected Path reconstructAssets(String version, String assetId) throws JsonParseException, IOException {
        Path target = getAssetDirectory(version, assetId);
        Path indexFile = getIndexFile(version, assetId);
        Path virtualRoot = target.resolve("virtual").resolve(assetId);
        if (!Files.isRegularFile(indexFile, new LinkOption[0])) {
            return target;
        }
        String assetIndexContent = FileUtils.readText(indexFile);
        AssetIndex index = (AssetIndex) JsonUtils.GSON.fromJson(assetIndexContent, AssetIndex.class);
        if (index == null) {
            return target;
        }
        if (index.isVirtual()) {
            Path resourcesDir = getRunDirectory(version).toPath().resolve("resources");
            int cnt = 0;
            int tot = index.getObjects().entrySet().size();
            for (Map.Entry<String, AssetObject> entry : index.getObjects().entrySet()) {
                Path target2 = virtualRoot.resolve(entry.getKey());
                Path original = getAssetObject(version, target, entry.getValue());
                Path assetsDir = target;
                if (Files.exists(original, new LinkOption[0])) {
                    cnt++;
                    if (!Files.isRegularFile(target2, new LinkOption[0])) {
                        FileUtils.copyFile(original, target2);
                    }
                    if (index.needMapToResources()) {
                        Path target3 = resourcesDir.resolve(entry.getKey());
                        if (!Files.isRegularFile(target3, new LinkOption[0])) {
                            FileUtils.copyFile(original, target3);
                        }
                    }
                }
                target = assetsDir;
            }
            Path assetsDir2 = target;
            if (cnt * 10 < tot) {
                return assetsDir2;
            }
            return virtualRoot;
        }
        return target;
    }

    public Task<Version> saveAsync(Version version) {
        this.gameVersions.remove(getVersionJar(version));
        if (version.isResolvedPreservingPatches()) {
            return new VersionJsonSaveTask(this, MaintainTask.maintainPreservingPatches(this, version));
        }
        return new VersionJsonSaveTask(this, version);
    }

    public boolean isLoaded() {
        return this.versions != null;
    }

    public File getModpackConfiguration(String version) {
        return new File(getVersionRoot(version), "modpack.json");
    }

    public <M> ModpackConfiguration<M> readModpackConfiguration(String version) throws VersionNotFoundException, IOException {
        if (!hasVersion(version)) {
            throw new VersionNotFoundException(version);
        }
        File file = getModpackConfiguration(version);
        if (file.exists()) {
            return (ModpackConfiguration) JsonUtils.GSON.fromJson(FileUtils.readText(file), new TypeToken<ModpackConfiguration<M>>() { // from class: com.brixcore.game.DefaultGameRepository.1
            }.getType());
        }
        return null;
    }

    public boolean isModpack(String version) {
        return getModpackConfiguration(version).exists();
    }

    public ModManager getModManager(String version) {
        return new ModManager(this, version);
    }

    public String toString() {
        return new ToStringBuilder(this).append("versions", this.versions == null ? null : this.versions.keySet()).append("baseDirectory", this.baseDirectory).toString();
    }
}
