package com.brixcore.download.game;

import com.brixcore.download.AbstractDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.GameRepository;
import com.brixcore.game.Library;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.LibFilter;
import com.brixcore.util.Logging;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.versioning.GameVersionNumber;
import com.brixcore.util.versioning.VersionNumber;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes9.dex */
public final class GameLibrariesTask extends Task<Void> {
    private final List<Task<?>> dependencies;
    private final AbstractDependencyManager dependencyManager;
    private final boolean integrityCheck;
    private final List<Library> libraries;
    private final Version version;

    public GameLibrariesTask(AbstractDependencyManager dependencyManager, Version version, boolean integrityCheck) {
        this(dependencyManager, version, integrityCheck, version.resolve(dependencyManager.getGameRepository()).getLibraries());
    }

    public GameLibrariesTask(AbstractDependencyManager dependencyManager, Version version, boolean integrityCheck, List<Library> libraries) {
        this.dependencies = new ArrayList();
        this.dependencyManager = dependencyManager;
        this.version = LibFilter.filter(version);
        this.integrityCheck = integrityCheck;
        this.libraries = LibFilter.filterLibs(libraries, true);
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependencies() {
        return this.dependencies;
    }

    public static boolean shouldDownloadLibrary(GameRepository gameRepository, Version version, Library library, boolean integrityCheck) {
        File file = gameRepository.getLibraryFile(version, library);
        Path jar = file.toPath();
        if (!file.isFile()) {
            return true;
        }
        if (!integrityCheck) {
            return false;
        }
        try {
            if (!library.getDownload().validateChecksum(jar, true)) {
                return true;
            }
            if (library.getChecksums() != null && !library.getChecksums().isEmpty() && !LibraryDownloadTask.checksumValid(file.toPath(), library.getChecksums())) {
                return true;
            }
            if (FileUtils.getExtension(file).equals("jar")) {
                try {
                    FileDownloadTask.ZIP_INTEGRITY_CHECK_HANDLER.checkIntegrity(jar, jar);
                } catch (IOException e) {
                    return true;
                }
            }
        } catch (IOException e2) {
            Logging.LOG.log(Level.WARNING, "Unable to calc hash value of file " + jar, (Throwable) e2);
        }
        return false;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws IOException {
        String forgeVersion;
        GameRepository gameRepository = this.dependencyManager.getGameRepository();
        for (Library library : this.libraries) {
            if (library.appliesToCurrentEnvironment()) {
                File file = gameRepository.getLibraryFile(this.version, library);
                if ("optifine".equals(library.getGroupId()) && file.exists() && GameVersionNumber.asGameVersion(gameRepository.getGameVersion(this.version)).compareTo("1.20.4") == 0 && (forgeVersion = LibraryAnalyzer.analyze(this.version, "1.20.4").getVersion(LibraryAnalyzer.LibraryType.FORGE).orElse(null)) != null && LibraryAnalyzer.FORGE_OPTIFINE_BROKEN_RANGE.contains(VersionNumber.asVersion(forgeVersion))) {
                    try {
                        FileSystem fs2 = CompressingUtils.createWritableZipFileSystem(file.toPath());
                        try {
                            Files.deleteIfExists(fs2.getPath("/META-INF/mods.toml", new String[0]));
                            if (fs2 != null) {
                                fs2.close();
                            }
                        } catch (Throwable th) {
                            if (fs2 != null) {
                                try {
                                    fs2.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e) {
                        throw new IOException("Cannot fix optifine", e);
                    }
                }
                if (shouldDownloadLibrary(gameRepository, this.version, library, this.integrityCheck) && (library.hasDownloadURL() || !"optifine".equals(library.getGroupId()))) {
                    this.dependencies.add(new LibraryDownloadTask(this.dependencyManager, file.toPath(), library));
                } else {
                    this.dependencyManager.getCacheRepository().tryCacheLibrary(library, file.toPath());
                }
            }
        }
    }
}
