package com.brixcore.download.game;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.versioning.GameVersionNumber;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes9.dex */
public final class GameVerificationFixTask extends Task<Void> {
    private final List<Task<?>> dependencies = new ArrayList();
    private final DefaultDependencyManager dependencyManager;
    private final String gameVersion;
    private final Version version;

    public GameVerificationFixTask(DefaultDependencyManager dependencyManager, String gameVersion, Version version) {
        this.dependencyManager = dependencyManager;
        this.gameVersion = gameVersion;
        this.version = version;
        if (!version.isResolved()) {
            throw new IllegalArgumentException("GameVerificationFixTask requires a resolved game version");
        }
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws IOException {
        File jar = this.dependencyManager.getGameRepository().getVersionJar(this.version);
        LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(this.version, this.gameVersion);
        if (jar.exists() && GameVersionNumber.compare(this.gameVersion, "1.6") < 0 && analyzer.has(LibraryAnalyzer.LibraryType.FORGE)) {
            FileSystem fs = CompressingUtils.createWritableZipFileSystem(jar.toPath(), StandardCharsets.UTF_8);
            try {
                Files.deleteIfExists(fs.getPath("META-INF/MOJANG_C.DSA", new String[0]));
                Files.deleteIfExists(fs.getPath("META-INF/MOJANG_C.SF", new String[0]));
                if (fs != null) {
                    fs.close();
                }
            } catch (Throwable th) {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }
}
