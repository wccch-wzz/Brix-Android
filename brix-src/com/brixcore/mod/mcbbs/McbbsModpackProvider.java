package com.brixcore.mod.mcbbs;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.LaunchOptions;
import com.brixcore.mod.MismatchedModpackTypeException;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.mod.ModpackUpdateTask;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes7.dex */
public final class McbbsModpackProvider implements ModpackProvider {
    public static final McbbsModpackProvider INSTANCE = new McbbsModpackProvider();

    @Override // com.brixcore.mod.ModpackProvider
    public String getName() {
        return "Mcbbs";
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        return new McbbsModpackCompletionTask(dependencyManager, version);
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createUpdateTask(DefaultDependencyManager dependencyManager, String name, File zipFile, Modpack modpack) throws MismatchedModpackTypeException {
        if (modpack.getManifest() instanceof McbbsModpackManifest) {
            return new ModpackUpdateTask(dependencyManager.getGameRepository(), name, new McbbsModpackLocalInstallTask(dependencyManager, zipFile, modpack, (McbbsModpackManifest) modpack.getManifest(), name));
        }
        throw new MismatchedModpackTypeException(getName(), modpack.getManifest().getProvider().getName());
    }

    @Override // com.brixcore.mod.ModpackProvider
    public void injectLaunchOptions(String modpackConfigurationJson, LaunchOptions.Builder builder) {
        ModpackConfiguration<McbbsModpackManifest> config = (ModpackConfiguration) JsonUtils.GSON.fromJson(modpackConfigurationJson, new TypeToken<ModpackConfiguration<McbbsModpackManifest>>() { // from class: com.brixcore.mod.mcbbs.McbbsModpackProvider.1
        }.getType());
        if (!getName().equals(config.getType())) {
            throw new IllegalArgumentException("Incorrect manifest type, actual=" + config.getType() + ", expected=" + getName());
        }
        config.getManifest().injectLaunchOptions(builder);
    }

    private static Modpack fromManifestFile(InputStream json, Charset encoding) throws JsonParseException, IOException {
        McbbsModpackManifest manifest = (McbbsModpackManifest) JsonUtils.fromNonNullJsonFully(json, McbbsModpackManifest.class);
        return manifest.toModpack(encoding);
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Modpack readManifest(ZipFile zip, Path file, Charset encoding) throws JsonParseException, IOException {
        ZipArchiveEntry mcbbsPackMeta = zip.getEntry("mcbbs.packmeta");
        if (mcbbsPackMeta != null) {
            return fromManifestFile(zip.getInputStream(mcbbsPackMeta), encoding);
        }
        ZipArchiveEntry manifestJson = zip.getEntry("manifest.json");
        if (manifestJson != null) {
            return fromManifestFile(zip.getInputStream(manifestJson), encoding);
        }
        throw new IOException("`mcbbs.packmeta` or `manifest.json` cannot be found");
    }
}
