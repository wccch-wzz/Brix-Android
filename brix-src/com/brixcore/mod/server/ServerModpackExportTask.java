package com.brixcore.mod.server;

import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModAdviser;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackExportInfo;
import com.brixcore.task.Task;
import com.brixcore.task.TaskEvent;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.function.ExceptionalPredicate;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.Zipper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ServerModpackExportTask extends Task<Void> {
    public static final ModpackExportInfo.Options OPTION = new ModpackExportInfo.Options().requireFileApi(false);
    private final ModpackExportInfo exportInfo;
    private final File modpackFile;
    private final DefaultGameRepository repository;
    private final String versionId;

    public ServerModpackExportTask(DefaultGameRepository repository, String version, ModpackExportInfo exportInfo, final File modpackFile) {
        this.repository = repository;
        this.versionId = version;
        this.exportInfo = exportInfo.validate();
        this.modpackFile = modpackFile;
        onDone().register(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ServerModpackExportTask.lambda$new$0(modpackFile, (TaskEvent) obj);
            }
        });
    }

    static /* synthetic */ void lambda$new$0(File modpackFile, TaskEvent event) {
        if (event.isFailed()) {
            modpackFile.delete();
        }
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        final ArrayList<String> blackList = new ArrayList<>(ModAdviser.MODPACK_BLACK_LIST);
        blackList.add(this.versionId + ".jar");
        blackList.add(this.versionId + ".json");
        Logging.LOG.info("Compressing game files without some files in blacklist, including files or directories: usernamecache.json, asm, logs, backups, versions, assets, usercache.json, libraries, crash-reports, launcher_profiles.json, NVIDIA, TCNodeTracker");
        Zipper zip = new Zipper(this.modpackFile.toPath());
        try {
            final Path runDirectory = this.repository.getRunDirectory(this.versionId).toPath();
            final List<ModpackConfiguration.FileInformation> files = new ArrayList<>();
            zip.putDirectory(runDirectory, "overrides", new ExceptionalPredicate() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.function.ExceptionalPredicate
                public final boolean test(Object obj) {
                    return this.f$0.lambda$execute$1(blackList, runDirectory, files, (String) obj);
                }
            });
            String gameVersion = this.repository.getGameVersion(this.versionId).orElseThrow(new Supplier() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.lambda$execute$2();
                }
            });
            LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(this.repository.getResolvedPreservingPatchesVersion(this.versionId), gameVersion);
            final List<ServerModpackManifest.Addon> addons = new ArrayList<>();
            addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId(), gameVersion));
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.FORGE.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.NEO_FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.LITELOADER).ifPresent(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.LITELOADER.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.OPTIFINE).ifPresent(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FABRIC).ifPresent(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.FABRIC.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.QUILT).ifPresent(new Consumer() { // from class: com.brixcore.mod.server.ServerModpackExportTask$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new ServerModpackManifest.Addon(LibraryAnalyzer.LibraryType.QUILT.getPatchId(), (String) obj));
                }
            });
            ServerModpackManifest manifest = new ServerModpackManifest(this.exportInfo.getName(), this.exportInfo.getAuthor(), this.exportInfo.getVersion(), this.exportInfo.getDescription(), StringUtils.removeSuffix(this.exportInfo.getFileApi(), "/"), files, addons);
            zip.putTextFile(JsonUtils.GSON.toJson(manifest), "server-manifest.json");
            zip.close();
        } catch (Throwable th) {
            try {
                zip.close();
                throw th;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$execute$1(ArrayList blackList, Path runDirectory, List files, String path) throws IOException {
        if (!Modpack.acceptFile(path, blackList, this.exportInfo.getWhitelist())) {
            return false;
        }
        Path file = runDirectory.resolve(path);
        if (Files.isRegularFile(file, new LinkOption[0])) {
            String relativePath = runDirectory.relativize(file).normalize().toString().replace(File.separatorChar, IOUtils.DIR_SEPARATOR_UNIX);
            files.add(new ModpackConfiguration.FileInformation(relativePath, DigestUtils.digestToString(CacheRepository.SHA1, file)));
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ IOException lambda$execute$2() {
        return new IOException("Cannot parse the version of " + this.versionId);
    }
}
