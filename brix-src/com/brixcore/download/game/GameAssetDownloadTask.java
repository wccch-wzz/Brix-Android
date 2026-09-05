package com.brixcore.download.game;

import com.brixcore.download.AbstractDependencyManager;
import com.brixcore.game.AssetIndex;
import com.brixcore.game.AssetIndexInfo;
import com.brixcore.game.AssetObject;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.Logging;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes9.dex */
public final class GameAssetDownloadTask extends Task<Void> {
    public static final boolean DOWNLOAD_INDEX_FORCIBLY = true;
    public static final boolean DOWNLOAD_INDEX_IF_NECESSARY = false;
    private final Path assetIndexFile;
    private final AssetIndexInfo assetIndexInfo;
    private final AbstractDependencyManager dependencyManager;
    private final boolean integrityCheck;
    private final Version version;
    private final List<Task<?>> dependents = new ArrayList(1);
    private final List<Task<?>> dependencies = new ArrayList();

    public GameAssetDownloadTask(AbstractDependencyManager dependencyManager, Version version, boolean forceDownloadingIndex, boolean integrityCheck) {
        this.dependencyManager = dependencyManager;
        this.version = version.resolve(dependencyManager.getGameRepository());
        this.assetIndexInfo = this.version.getAssetIndex();
        this.assetIndexFile = dependencyManager.getGameRepository().getIndexFile(version.getId(), this.assetIndexInfo.getId());
        this.integrityCheck = integrityCheck;
        setStage("Brix.install.assets");
        this.dependents.add(new GameAssetIndexDownloadTask(dependencyManager, this.version, forceDownloadingIndex));
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        try {
            AssetIndex index = (AssetIndex) JsonUtils.fromNonNullJson(FileUtils.readText(this.assetIndexFile), AssetIndex.class);
            int progress = 0;
            for (AssetObject assetObject : index.getObjects().values()) {
                if (isCancelled()) {
                    throw new InterruptedException();
                }
                Path file = this.dependencyManager.getGameRepository().getAssetObject(this.version.getId(), this.assetIndexInfo.getId(), assetObject);
                boolean download = !Files.isRegularFile(file, new LinkOption[0]);
                if (!download) {
                    try {
                        if (this.integrityCheck && !assetObject.validateChecksum(file, true)) {
                            download = true;
                        }
                    } catch (IOException e) {
                        Logging.LOG.log(Level.WARNING, "Unable to calc hash value of file " + file, (Throwable) e);
                    }
                }
                if (!download) {
                    this.dependencyManager.getCacheRepository().tryCacheFile(file, CacheRepository.SHA1, assetObject.getHash());
                } else {
                    List<URL> urls = this.dependencyManager.getDownloadProvider().getAssetObjectCandidates(assetObject.getLocation());
                    FileDownloadTask task = new FileDownloadTask(urls, file.toFile(), new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, assetObject.getHash()));
                    task.setName(assetObject.getHash());
                    task.setCandidate(this.dependencyManager.getCacheRepository().getCommonDirectory().resolve("assets").resolve("objects").resolve(assetObject.getLocation()));
                    task.setCacheRepository(this.dependencyManager.getCacheRepository());
                    task.setCaching(true);
                    this.dependencies.add(task.withCounter("Brix.install.assets"));
                }
                progress++;
                updateProgress(progress, index.getObjects().size());
            }
            if (!this.dependencies.isEmpty()) {
                getProperties().put("total", Integer.valueOf(this.dependencies.size()));
                notifyPropertiesChanged();
            }
        } catch (JsonParseException | IOException e2) {
            throw new GameAssetIndexDownloadTask.GameAssetIndexMalformedException();
        }
    }
}
