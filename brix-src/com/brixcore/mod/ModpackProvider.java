package com.brixcore.mod;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.LaunchOptions;
import com.brixcore.task.Task;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipFile;

/* JADX INFO: loaded from: classes2.dex */
public interface ModpackProvider {
    Task<?> createCompletionTask(DefaultDependencyManager defaultDependencyManager, String str);

    Task<?> createUpdateTask(DefaultDependencyManager defaultDependencyManager, String str, File file, Modpack modpack) throws MismatchedModpackTypeException;

    String getName();

    Modpack readManifest(ZipFile zipFile, Path path, Charset charset) throws JsonParseException, IOException;

    default void injectLaunchOptions(String modpackConfigurationJson, LaunchOptions.Builder builder) {
    }
}
