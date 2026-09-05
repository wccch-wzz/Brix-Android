package com.brixcore.download.quilt;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public final class QuiltAPIInstallTask extends Task<Version> {
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final DefaultDependencyManager dependencyManager;
    private final QuiltAPIRemoteVersion remote;
    private final Version version;

    public QuiltAPIInstallTask(DefaultDependencyManager dependencyManager, Version version, QuiltAPIRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remote = remoteVersion;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public boolean isRelyingOnDependencies() {
        return false;
    }

    @Override // com.brixcore.task.Task
    public void execute() throws IOException {
        this.dependencies.add(new FileDownloadTask(new URL(this.remote.getVersion().getFile().getUrl()), this.dependencyManager.getGameRepository().getRunDirectory(this.version.getId()).toPath().resolve("mods").resolve("quilt-api-" + this.remote.getVersion().getVersion() + ".jar").toFile(), this.remote.getVersion().getFile().getIntegrityCheck()));
    }
}
