package com.brixcore.mod.modrinth;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.mod.MismatchedModpackTypeException;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.mod.ModpackUpdateTask;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes14.dex */
public final class ModrinthModpackProvider implements ModpackProvider {
    public static final ModrinthModpackProvider INSTANCE = new ModrinthModpackProvider();

    @Override // com.brixcore.mod.ModpackProvider
    public String getName() {
        return "Modrinth";
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        return new ModrinthCompletionTask(dependencyManager, version);
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createUpdateTask(DefaultDependencyManager dependencyManager, String name, File zipFile, Modpack modpack) throws MismatchedModpackTypeException {
        if (modpack.getManifest() instanceof ModrinthManifest) {
            return new ModpackUpdateTask(dependencyManager.getGameRepository(), name, new ModrinthInstallTask(dependencyManager, zipFile, modpack, (ModrinthManifest) modpack.getManifest(), name));
        }
        throw new MismatchedModpackTypeException(getName(), modpack.getManifest().getProvider().getName());
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Modpack readManifest(ZipFile zip, Path file, Charset encoding) throws JsonParseException, IOException {
        final ModrinthManifest manifest = (ModrinthManifest) JsonUtils.fromNonNullJson(CompressingUtils.readTextZipEntry(zip, "modrinth.index.json"), ModrinthManifest.class);
        return new Modpack(manifest.getName(), "", manifest.getVersionId(), manifest.getGameVersion(), manifest.getSummary(), encoding, manifest) { // from class: com.brixcore.mod.modrinth.ModrinthModpackProvider.1
            @Override // com.brixcore.mod.Modpack
            public Task<?> getInstallTask(DefaultDependencyManager dependencyManager, File zipFile, String name) {
                return new ModrinthInstallTask(dependencyManager, zipFile, this, manifest, name);
            }
        };
    }
}
