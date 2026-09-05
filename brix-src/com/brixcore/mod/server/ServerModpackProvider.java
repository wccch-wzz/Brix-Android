package com.brixcore.mod.server;

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

/* JADX INFO: loaded from: classes2.dex */
public final class ServerModpackProvider implements ModpackProvider {
    public static final ServerModpackProvider INSTANCE = new ServerModpackProvider();

    @Override // com.brixcore.mod.ModpackProvider
    public String getName() {
        return "Server";
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        return new ServerModpackCompletionTask(dependencyManager, version);
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Task<?> createUpdateTask(DefaultDependencyManager dependencyManager, String name, File zipFile, Modpack modpack) throws MismatchedModpackTypeException {
        if (modpack.getManifest() instanceof ServerModpackManifest) {
            return new ModpackUpdateTask(dependencyManager.getGameRepository(), name, new ServerModpackLocalInstallTask(dependencyManager, zipFile, modpack, (ServerModpackManifest) modpack.getManifest(), name));
        }
        throw new MismatchedModpackTypeException(getName(), modpack.getManifest().getProvider().getName());
    }

    @Override // com.brixcore.mod.ModpackProvider
    public Modpack readManifest(ZipFile zip, Path file, Charset encoding) throws JsonParseException, IOException {
        String json = CompressingUtils.readTextZipEntry(zip, "server-manifest.json");
        ServerModpackManifest manifest = (ServerModpackManifest) JsonUtils.fromNonNullJson(json, ServerModpackManifest.class);
        return manifest.toModpack(encoding);
    }
}
