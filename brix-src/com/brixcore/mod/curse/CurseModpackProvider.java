package com.brixcore.mod.curse;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.mod.MismatchedModpackTypeException;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.mod.ModpackUpdateTask;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.IOUtils;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseModpackProvider implements ModpackProvider {
    public static final CurseModpackProvider INSTANCE = new CurseModpackProvider();

    @Override // com.brixcore.mod.ModpackProvider
    public String getName() {
        return "Curse";
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        return new CurseCompletionTask(dependencyManager, version);
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createUpdateTask(DefaultDependencyManager dependencyManager, String name, File zipFile, Modpack modpack) throws MismatchedModpackTypeException {
        if (modpack.getManifest() instanceof CurseManifest) {
            return new ModpackUpdateTask(dependencyManager.getGameRepository(), name, new CurseInstallTask(dependencyManager, zipFile, modpack, (CurseManifest) modpack.getManifest(), name));
        }
        throw new MismatchedModpackTypeException(getName(), modpack.getManifest().getProvider().getName());
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Modpack readManifest(ZipFile zip, Path file, Charset encoding) throws JsonParseException, IOException {
        String description;
        final CurseManifest manifest = (CurseManifest) JsonUtils.fromNonNullJson(CompressingUtils.readTextZipEntry(zip, "manifest.json"), CurseManifest.class);
        String description2 = "No description";
        try {
            ZipArchiveEntry modlist = zip.getEntry("modlist.html");
            if (modlist != null) {
                description2 = IOUtils.readFullyAsString(zip.getInputStream(modlist));
            }
            description = description2;
        } catch (Throwable th) {
            description = "No description";
        }
        return new Modpack(manifest.getName(), manifest.getAuthor(), manifest.getVersion(), manifest.getMinecraft().getGameVersion(), description, encoding, manifest) { // from class: com.brixcore.mod.curse.CurseModpackProvider.1
            @Override // com.brixcore.mod.Modpack
            public Task<?> getInstallTask(DefaultDependencyManager dependencyManager, File zipFile, String name) {
                return new CurseInstallTask(dependencyManager, zipFile, this, manifest, name);
            }
        };
    }
}
