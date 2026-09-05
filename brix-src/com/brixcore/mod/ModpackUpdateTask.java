package com.brixcore.mod;

import com.brixcore.game.DefaultGameRepository;
import com.brixcore.task.Task;
import com.brixcore.util.io.FileUtils;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/* JADX INFO: loaded from: classes2.dex */
public class ModpackUpdateTask extends Task<Void> {
    private final Path backupFolder;
    private final String id;
    private final DefaultGameRepository repository;
    private final Task<?> updateTask;

    public ModpackUpdateTask(DefaultGameRepository repository, String id, Task<?> updateTask) {
        int num;
        this.repository = repository;
        this.id = id;
        this.updateTask = updateTask;
        Path backup = repository.getBaseDirectory().toPath().resolve("backup");
        do {
            num = (int) (Math.random() * 1.0E7d);
        } while (Files.exists(backup.resolve(id + "-" + num), new LinkOption[0]));
        this.backupFolder = backup.resolve(id + "-" + num);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return Collections.singleton(this.updateTask);
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        FileUtils.copyDirectory(this.repository.getVersionRoot(this.id).toPath(), this.backupFolder);
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        if (!isDependenciesSucceeded()) {
            this.repository.removeVersionFromDisk(this.id);
            FileUtils.copyDirectory(this.backupFolder, this.repository.getVersionRoot(this.id).toPath());
            this.repository.refreshVersionsAsync().start();
        }
    }
}
