package com.brixcore.mod.multimc;

import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.mod.ModAdviser;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackExportInfo;
import com.brixcore.task.Task;
import com.brixcore.task.TaskEvent;
import com.brixcore.util.Logging;
import com.brixcore.util.function.ExceptionalPredicate;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.Zipper;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes10.dex */
public class MultiMCModpackExportTask extends Task<Void> {
    public static final ModpackExportInfo.Options OPTION = new ModpackExportInfo.Options().requireMinMemory();
    private final MultiMCInstanceConfiguration configuration;
    private final File output;
    private final DefaultGameRepository repository;
    private final String versionId;
    private final List<String> whitelist;

    public MultiMCModpackExportTask(DefaultGameRepository repository, String versionId, List<String> whitelist, MultiMCInstanceConfiguration configuration, final File output) {
        this.repository = repository;
        this.versionId = versionId;
        this.whitelist = whitelist;
        this.configuration = configuration;
        this.output = output;
        onDone().register(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MultiMCModpackExportTask.lambda$new$0(output, (TaskEvent) obj);
            }
        });
    }

    static /* synthetic */ void lambda$new$0(File output, TaskEvent event) {
        if (event.isFailed()) {
            output.delete();
        }
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        final ArrayList<String> blackList = new ArrayList<>(ModAdviser.MODPACK_BLACK_LIST);
        blackList.add(this.versionId + ".jar");
        blackList.add(this.versionId + ".json");
        Logging.LOG.info("Compressing game files without some files in blacklist, including files or directories: usernamecache.json, asm, logs, backups, versions, assets, usercache.json, libraries, crash-reports, launcher_profiles.json, NVIDIA, TCNodeTracker");
        Zipper zip = new Zipper(this.output.toPath());
        try {
            zip.putDirectory(this.repository.getRunDirectory(this.versionId).toPath(), ".minecraft", new ExceptionalPredicate() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda0
                @Override // com.brixcore.util.function.ExceptionalPredicate
                public final boolean test(Object obj) {
                    return this.f$0.lambda$execute$1(blackList, (String) obj);
                }
            });
            String gameVersion = this.repository.getGameVersion(this.versionId).orElseThrow(new Supplier() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.lambda$execute$2();
                }
            });
            LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(this.repository.getResolvedPreservingPatchesVersion(this.versionId), gameVersion);
            final List<MultiMCManifest.MultiMCManifestComponent> components = new ArrayList<>();
            components.add(new MultiMCManifest.MultiMCManifestComponent(true, false, "net.minecraft", gameVersion));
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    components.add(new MultiMCManifest.MultiMCManifestComponent(false, false, "net.minecraftforge", (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.NEO_FORGE).ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    components.add(new MultiMCManifest.MultiMCManifestComponent(false, false, "net.neoforged", (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.LITELOADER).ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    components.add(new MultiMCManifest.MultiMCManifestComponent(false, false, "com.mumfrey.liteloader", (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.FABRIC).ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    components.add(new MultiMCManifest.MultiMCManifestComponent(false, false, "net.fabricmc.fabric-loader", (String) obj));
                }
            });
            analyzer.getVersion(LibraryAnalyzer.LibraryType.QUILT).ifPresent(new Consumer() { // from class: com.brixcore.mod.multimc.MultiMCModpackExportTask$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    components.add(new MultiMCManifest.MultiMCManifestComponent(false, false, "org.quiltmc.quilt-loader", (String) obj));
                }
            });
            MultiMCManifest mmcPack = new MultiMCManifest(1, components);
            zip.putTextFile(JsonUtils.GSON.toJson(mmcPack), "mmc-pack.json");
            StringWriter writer = new StringWriter();
            this.configuration.toProperties().store(writer, "Auto generated by Hello Minecraft! Launcher");
            zip.putTextFile(writer.toString(), "instance.cfg");
            zip.putTextFile("", ".packignore");
            zip.close();
        } catch (Throwable th) {
            try {
                zip.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$execute$1(ArrayList blackList, String path) throws IOException {
        return Modpack.acceptFile(path, blackList, this.whitelist);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ IOException lambda$execute$2() {
        return new IOException("Cannot parse the version of " + this.versionId);
    }
}
