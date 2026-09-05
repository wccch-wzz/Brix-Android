package com.brixcore.mod;

import com.brixcore.game.GameRepository;
import com.brixcore.mod.LocalAddonFile;
import com.brixcore.util.StringUtils;
import com.brixcore.util.io.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
public abstract class LocalAddonManager<T extends LocalAddonFile> {
    public static final String DISABLED_EXTENSION = ".disabled";
    public static final String OLD_EXTENSION = ".old";
    protected final String id;
    protected final GameRepository repository;
    protected final ReentrantLock lock = new ReentrantLock();
    protected final Set<T> localFiles = new LinkedHashSet();

    public abstract Comparator<T> getComparator();

    public abstract Path getDirectory();

    public abstract void refresh() throws IOException;

    public static String getLocalAddonName(Path file) {
        return StringUtils.removeSuffix(FileUtils.getName(file), ".disabled", ".old");
    }

    public LocalAddonManager(GameRepository gameRepository, String versionId) {
        this.repository = gameRepository;
        this.id = versionId;
    }

    public GameRepository getRepository() {
        return this.repository;
    }

    public String getInstanceId() {
        return this.id;
    }

    public List<T> getLocalFiles() throws IOException {
        this.lock.lock();
        try {
            return (List) this.localFiles.stream().sorted(getComparator()).collect(Collectors.toList());
        } finally {
            this.lock.unlock();
        }
    }

    public Path setOld(T modFile, boolean old) throws IOException {
        Path newPath;
        this.lock.lock();
        try {
            if (old) {
                newPath = backupFile(modFile.getFile());
                this.localFiles.remove(modFile);
            } else {
                Path newPath2 = modFile.getFile();
                newPath = restoreFile(newPath2);
                this.localFiles.add(modFile);
            }
            this.lock.unlock();
            return newPath;
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    private Path backupFile(Path file) throws IOException {
        Path newPath = file.resolveSibling(StringUtils.addSuffix(StringUtils.removeSuffix(FileUtils.getName(file), ".disabled"), ".old"));
        if (Files.exists(file, new LinkOption[0])) {
            Files.move(file, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return newPath;
    }

    private Path restoreFile(Path file) throws IOException {
        Path newPath = file.resolveSibling(StringUtils.removeSuffix(FileUtils.getName(file), ".old"));
        if (Files.exists(file, new LinkOption[0])) {
            Files.move(file, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return newPath;
    }
}
