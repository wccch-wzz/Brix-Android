package com.brixcore.download.forge;

import com.brixcore.download.ArtifactMalformedException;
import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.Library;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: classes3.dex */
public class ForgeOldInstallTask extends Task<Version> {
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final DefaultDependencyManager dependencyManager;
    private final Path installer;
    private final String selfVersion;
    private final Version version;

    ForgeOldInstallTask(DefaultDependencyManager dependencyManager, Version version, String selfVersion, Path installer) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.installer = installer;
        this.selfVersion = selfVersion;
        setSignificance(Task.TaskSignificance.MAJOR);
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        try {
            ZipFile zipFile = new ZipFile(this.installer.toFile());
            try {
                InputStream stream = zipFile.getInputStream(zipFile.getEntry("install_profile.json"));
                if (stream == null) {
                    throw new ArtifactMalformedException("Malformed forge installer file, install_profile.json does not exist.");
                }
                ForgeInstallProfile installProfile = (ForgeInstallProfile) JsonUtils.fromNonNullJsonFully(stream, ForgeInstallProfile.class);
                Library forgeLibrary = new Library(installProfile.getInstall().getPath());
                File forgeFile = this.dependencyManager.getGameRepository().getLibraryFile(this.version, forgeLibrary);
                if (!FileUtils.makeFile(forgeFile)) {
                    throw new IOException("Cannot make directory " + forgeFile.getParent());
                }
                ZipEntry forgeEntry = zipFile.getEntry(installProfile.getInstall().getFilePath());
                InputStream is = zipFile.getInputStream(forgeEntry);
                try {
                    OutputStream os = new FileOutputStream(forgeFile);
                    try {
                        IOUtils.copyTo(is, os);
                        os.close();
                        if (is != null) {
                            is.close();
                        }
                        setResult(installProfile.getVersionInfo().setPriority(30000).setId(LibraryAnalyzer.LibraryType.FORGE.getPatchId()).setVersion(this.selfVersion));
                        this.dependencies.add(this.dependencyManager.checkLibraryCompletionAsync(installProfile.getVersionInfo(), true));
                        zipFile.close();
                        return;
                    } catch (Throwable th) {
                        try {
                            os.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            } catch (Throwable th5) {
                try {
                    zipFile.close();
                } catch (Throwable th6) {
                    th5.addSuppressed(th6);
                }
                throw th5;
            }
        } catch (ZipException ex) {
            throw new ArtifactMalformedException("Malformed forge installer file", ex);
        }
        throw new ArtifactMalformedException("Malformed forge installer file", ex);
    }
}
