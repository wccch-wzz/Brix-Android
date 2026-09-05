package com.brixcore.game;

import com.brixcore.task.Task;
import com.brixcore.util.function.ExceptionalRunnable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public interface GameRepository extends VersionProvider {
    Path getActualAssetDirectory(String str, String str2);

    Path getAssetDirectory(String str, String str2);

    AssetIndex getAssetIndex(String str, String str2) throws IOException;

    Path getAssetObject(String str, String str2, AssetObject assetObject);

    Optional<Path> getAssetObject(String str, String str2, String str3) throws IOException;

    Optional<String> getGameVersion(Version version);

    Path getIndexFile(String str, String str2);

    File getLibrariesDirectory(Version version);

    File getLibraryFile(Version version, Library library);

    Path getLoggingObject(String str, String str2, LoggingInfo loggingInfo);

    Path getModsDirectory(String str);

    File getRunDirectory(String str);

    @Override // com.brixcore.game.VersionProvider
    Version getVersion(String str) throws VersionNotFoundException;

    int getVersionCount();

    File getVersionJar(Version version);

    File getVersionRoot(String str);

    Collection<Version> getVersions();

    @Override // com.brixcore.game.VersionProvider
    boolean hasVersion(String str);

    void refreshVersions();

    boolean renameVersion(String str, String str2);

    default Version getResolvedVersion(String id) throws VersionNotFoundException {
        return getVersion(id).resolve(this);
    }

    default Version getResolvedPreservingPatchesVersion(String id) throws VersionNotFoundException {
        return getVersion(id).resolvePreservingPatches(this);
    }

    default Task<Void> refreshVersionsAsync() {
        return Task.runAsync(new ExceptionalRunnable() { // from class: com.brixcore.game.GameRepository$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() {
                this.f$0.refreshVersions();
            }
        });
    }

    default Optional<String> getGameVersion(String versionId) throws VersionNotFoundException {
        return getGameVersion(getVersion(versionId));
    }

    default File getVersionJar(String version) throws VersionNotFoundException {
        return getVersionJar(getVersion(version).resolve(this));
    }

    default Set<String> getClasspath(Version version) {
        Set<String> classpath = new LinkedHashSet<>();
        for (Library library : version.getLibraries()) {
            if (!library.getName().contains("org.lwjgl") && library.appliesToCurrentEnvironment() && !library.isNative()) {
                File f = getLibraryFile(version, library);
                if (f.exists() && f.isFile()) {
                    classpath.add(f.getAbsolutePath());
                }
            }
        }
        return classpath;
    }
}
