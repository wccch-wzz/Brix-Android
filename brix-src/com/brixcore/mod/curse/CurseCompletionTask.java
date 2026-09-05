package com.brixcore.mod.curse;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.ModpackCompletionException;
import com.brixcore.mod.RemoteMod;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseCompletionTask extends Task<Void> {
    private final AtomicBoolean allNameKnown;
    private List<Task<?>> dependencies;
    private final DefaultDependencyManager dependency;
    private final AtomicInteger finished;
    private CurseManifest manifest;
    private final ModManager modManager;
    private final AtomicBoolean notFound;
    private final DefaultGameRepository repository;
    private final String version;

    public CurseCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        this(dependencyManager, version, null);
    }

    public CurseCompletionTask(DefaultDependencyManager dependencyManager, String version, CurseManifest manifest) {
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
                File manifestFile = new File(this.repository.getVersionRoot(version), "manifest.json");
                if (manifestFile.exists()) {
                    this.manifest = (CurseManifest) JsonUtils.GSON.fromJson(FileUtils.readText(manifestFile), CurseManifest.class);
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read CurseForge modpack manifest.json", (Throwable) e);
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
        File root = this.repository.getVersionRoot(this.version);
        final CurseManifest newManifest = this.manifest.setFiles((List) this.manifest.getFiles().parallelStream().map(new Function() { // from class: com.brixcore.mod.curse.CurseCompletionTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$execute$0((CurseManifestFile) obj);
            }
        }).collect(Collectors.toList()));
        FileUtils.writeText(new File(root, "manifest.json"), JsonUtils.GSON.toJson(newManifest));
        File versionRoot = this.repository.getVersionRoot(this.modManager.getInstanceId());
        final File resourcePacksRoot = new File(versionRoot, "resourcepacks");
        final File shaderPacksRoot = new File(versionRoot, "shaderpacks");
        this.finished.set(0);
        this.dependencies = (List) ((Stream) newManifest.getFiles().stream().parallel()).filter(new Predicate() { // from class: com.brixcore.mod.curse.CurseCompletionTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CurseCompletionTask.lambda$execute$1((CurseManifestFile) obj);
            }
        }).flatMap(new Function() { // from class: com.brixcore.mod.curse.CurseCompletionTask$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$execute$2(resourcePacksRoot, shaderPacksRoot, newManifest, (CurseManifestFile) obj);
            }
        }).collect(Collectors.toList());
        if (!this.dependencies.isEmpty()) {
            getProperties().put("total", Integer.valueOf(this.dependencies.size()));
            notifyPropertiesChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CurseManifestFile lambda$execute$0(CurseManifestFile file) {
        updateProgress(this.finished.incrementAndGet(), this.manifest.getFiles().size());
        if (StringUtils.isBlank(file.getFileName()) || file.getUrl() == null) {
            try {
                RemoteMod.File remoteFile = CurseForgeRemoteModRepository.MODS.getModFile(Integer.toString(file.getProjectID()), Integer.toString(file.getFileID()));
                return file.withFileName(remoteFile.getFilename()).withURL(remoteFile.getUrl());
            } catch (JsonParseException e) {
                e = e;
                Logging.LOG.log(Level.WARNING, "Unable to fetch the file name projectID=" + file.getProjectID() + ", fileID=" + file.getFileID(), (Throwable) e);
                this.allNameKnown.set(false);
                return file;
            } catch (FileNotFoundException fof) {
                Logging.LOG.log(Level.WARNING, "Could not query api.curseforge.com for deleted mods: " + file.getProjectID() + ", " + file.getFileID(), (Throwable) fof);
                this.notFound.set(true);
                return file;
            } catch (IOException e2) {
                e = e2;
                Logging.LOG.log(Level.WARNING, "Unable to fetch the file name projectID=" + file.getProjectID() + ", fileID=" + file.getFileID(), (Throwable) e);
                this.allNameKnown.set(false);
                return file;
            }
        }
        return file;
    }

    static /* synthetic */ boolean lambda$execute$1(CurseManifestFile f) {
        return f.getFileName() != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Stream lambda$execute$2(File resourcePacksRoot, File shaderPacksRoot, CurseManifest newManifest, CurseManifestFile f) {
        try {
            File path = guessFilePath(f, resourcePacksRoot, shaderPacksRoot);
            if (path == null) {
                return Stream.empty();
            }
            FileDownloadTask task = new FileDownloadTask(f.getUrl(), path);
            task.setCacheRepository(this.dependency.getCacheRepository());
            task.setCaching(true);
            return Stream.of(task.withCounter("Brix.modpack.download"));
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Could not query api.curseforge.com for mod: " + f.getProjectID() + ", " + f.getFileID(), (Throwable) e);
            return Stream.empty();
        } finally {
            updateProgress(this.finished.incrementAndGet(), newManifest.getFiles().size());
        }
    }

    private File guessFilePath(CurseManifestFile file, File resourcePacksRoot, File shaderPacksRoot) throws IOException {
        RemoteMod mod = CurseForgeRemoteModRepository.MODS.getModById(Integer.toString(file.getProjectID()));
        int classID = ((CurseAddon) mod.getData()).classId();
        String fileName = file.getFileName();
        switch (classID) {
            case 12:
            case CurseForgeRemoteModRepository.SECTION_SHADER_PACK /* 6552 */:
                File res = new File(classID == 12 ? resourcePacksRoot : shaderPacksRoot, fileName);
                if (res.exists()) {
                    return null;
                }
                return res;
            default:
                if (this.modManager.hasSimpleMod(fileName)) {
                    return null;
                }
                return this.modManager.getSimpleModPath(fileName).toFile();
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
