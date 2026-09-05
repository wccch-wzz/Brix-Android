package com.brixcore.mod.mcbbs;

import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Library;
import com.brixcore.mod.ModAdviser;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackExportInfo;
import com.brixcore.mod.curse.CurseManifest;
import com.brixcore.mod.curse.CurseManifestMinecraft;
import com.brixcore.mod.curse.CurseManifestModLoader;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes7.dex */
public class McbbsModpackExportTask extends Task<Void> {
    public static final ModpackExportInfo.Options OPTION = new ModpackExportInfo.Options().requireFileApi(true).requireUrl().requireForceUpdate().requireMinMemory().requireAuthlibInjectorServer().requireJavaArguments().requireLaunchArguments().requireOrigins();
    private final ModpackExportInfo info;
    private final File modpackFile;
    private final DefaultGameRepository repository;
    private final String version;

    public McbbsModpackExportTask(DefaultGameRepository repository, String version, ModpackExportInfo info, final File modpackFile) {
        this.repository = repository;
        this.version = version;
        this.info = info.validate();
        this.modpackFile = modpackFile;
        onDone().register(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                McbbsModpackExportTask.lambda$new$0(modpackFile, (TaskEvent) obj);
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
        blackList.add(this.version + ".jar");
        blackList.add(this.version + ".json");
        Logging.LOG.info("Compressing game files without some files in blacklist, including files or directories: usernamecache.json, asm, logs, backups, versions, assets, usercache.json, libraries, crash-reports, launcher_profiles.json, NVIDIA, TCNodeTracker");
        Zipper zip = new Zipper(this.modpackFile.toPath());
        try {
            final Path runDirectory = this.repository.getRunDirectory(this.version).toPath();
            final List<McbbsModpackManifest.File> files = new ArrayList<>();
            zip.putDirectory(runDirectory, "overrides", new ExceptionalPredicate() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda4
                @Override // com.brixcore.util.function.ExceptionalPredicate
                public final boolean test(Object obj) {
                    return this.f$0.lambda$execute$1(blackList, runDirectory, files, (String) obj);
                }
            });
            String gameVersion = this.repository.getGameVersion(this.version).orElseThrow(new Supplier() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda7
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.lambda$execute$2();
                }
            });
            LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(this.repository.getResolvedPreservingPatchesVersion(this.version), gameVersion);
            final List<McbbsModpackManifest.Addon> addons = new ArrayList<>();
            addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId(), gameVersion));
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.FORGE.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.CLEANROOM).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.CLEANROOM.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.NEO_FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda10
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.LITELOADER).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda11
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.LITELOADER.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.OPTIFINE).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda12
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FABRIC).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.FABRIC.getPatchId(), (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.QUILT).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addons.add(new McbbsModpackManifest.Addon(LibraryAnalyzer.LibraryType.QUILT.getPatchId(), (String) obj));
                }
            });
            List<Library> libraries = new ArrayList<>();
            List<McbbsModpackManifest.Origin> origins = new ArrayList<>();
            McbbsModpackManifest.Settings settings = new McbbsModpackManifest.Settings();
            McbbsModpackManifest.LaunchInfo launchInfo = new McbbsModpackManifest.LaunchInfo(this.info.getMinMemory(), this.info.getSupportedJavaVersions(), StringUtils.tokenize(this.info.getLaunchArguments()), StringUtils.tokenize(this.info.getJavaArguments()));
            McbbsModpackManifest mcbbsManifest = new McbbsModpackManifest("minecraftModpack", 2, this.info.getName(), this.info.getVersion(), this.info.getAuthor(), this.info.getDescription(), this.info.getFileApi() == null ? null : StringUtils.removeSuffix(this.info.getFileApi(), "/"), this.info.getUrl(), this.info.isForceUpdate(), origins, addons, libraries, files, settings, launchInfo);
            zip.putTextFile(JsonUtils.GSON.toJson(mcbbsManifest), "mcbbs.packmeta");
            final List<CurseManifestModLoader> modLoaders = new ArrayList<>();
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    modLoaders.add(new CurseManifestModLoader("forge-" + ((String) obj), true));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.NEO_FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    modLoaders.add(new CurseManifestModLoader("neoforge-" + ((String) obj), true));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FABRIC).ifPresent(new Consumer() { // from class: com.brixcore.mod.mcbbs.McbbsModpackExportTask$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    modLoaders.add(new CurseManifestModLoader("fabric-" + ((String) obj), true));
                }
            });
            CurseManifest curseManifest = new CurseManifest("minecraftModpack", 1, this.info.getName(), this.info.getVersion(), this.info.getAuthor(), "overrides", new CurseManifestMinecraft(gameVersion, modLoaders), Collections.emptyList());
            zip.putTextFile(JsonUtils.GSON.toJson(curseManifest), "manifest.json");
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
        if (!Modpack.acceptFile(path, blackList, this.info.getWhitelist())) {
            return false;
        }
        Path file = runDirectory.resolve(path);
        if (Files.isRegularFile(file, new LinkOption[0])) {
            String relativePath = runDirectory.relativize(file).normalize().toString().replace(File.separatorChar, IOUtils.DIR_SEPARATOR_UNIX);
            files.add(new McbbsModpackManifest.AddonFile(true, relativePath, DigestUtils.digestToString(CacheRepository.SHA1, file)));
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ IOException lambda$execute$2() {
        return new IOException("Cannot parse the version of " + this.version);
    }
}
