package com.brixcore.download.game;

import com.brixcore.download.AbstractDependencyManager;
import com.brixcore.game.AssetIndex;
import com.brixcore.game.AssetIndexInfo;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes9.dex */
public final class GameAssetIndexDownloadTask extends Task<Void> {
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final AbstractDependencyManager dependencyManager;
    private final boolean forceDownloading;
    private final Version version;

    public static class GameAssetIndexMalformedException extends IOException {
    }

    public GameAssetIndexDownloadTask(AbstractDependencyManager dependencyManager, Version version, boolean forceDownloading) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.forceDownloading = forceDownloading;
        setSignificance(Task.TaskSignificance.MODERATE);
    }

    @Override // com.brixcore.task.Task
    public List<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() {
        AssetIndexInfo assetIndexInfo = this.version.getAssetIndex();
        Path assetIndexFile = this.dependencyManager.getGameRepository().getIndexFile(this.version.getId(), assetIndexInfo.getId());
        boolean verifyHashCode = StringUtils.isNotBlank(assetIndexInfo.getSha1()) && assetIndexInfo.getUrl().contains(assetIndexInfo.getSha1());
        if (Files.exists(assetIndexFile, new LinkOption[0]) && !this.forceDownloading) {
            if (verifyHashCode) {
                try {
                    String actualSum = DigestUtils.digestToString(CacheRepository.SHA1, assetIndexFile);
                    if (actualSum.equalsIgnoreCase(assetIndexInfo.getSha1())) {
                        return;
                    }
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Failed to calculate sha1sum of file " + assetIndexInfo, (Throwable) e);
                }
            } else {
                try {
                    JsonUtils.fromNonNullJson(FileUtils.readText(assetIndexFile), AssetIndex.class);
                    return;
                } catch (JsonParseException e2) {
                } catch (IOException e3) {
                }
            }
        }
        FileDownloadTask task = new FileDownloadTask(this.dependencyManager.getDownloadProvider().injectURLWithCandidates(assetIndexInfo.getUrl()), assetIndexFile.toFile(), verifyHashCode ? new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, assetIndexInfo.getSha1()) : null);
        task.setCacheRepository(this.dependencyManager.getCacheRepository());
        this.dependencies.add(task);
    }
}
