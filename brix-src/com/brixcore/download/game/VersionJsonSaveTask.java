package com.brixcore.download.game;

import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.FileUtils;
import java.io.File;

/* JADX INFO: loaded from: classes9.dex */
public final class VersionJsonSaveTask extends Task<Version> {
    private final DefaultGameRepository repository;
    private final Version version;

    public VersionJsonSaveTask(DefaultGameRepository repository, Version version) {
        this.repository = repository;
        this.version = version;
        setSignificance(Task.TaskSignificance.MODERATE);
        setResult(version);
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        File json = this.repository.getVersionJson(this.version.getId()).getAbsoluteFile();
        FileUtils.writeText(json, JsonUtils.GSON.toJson(this.version));
    }
}
