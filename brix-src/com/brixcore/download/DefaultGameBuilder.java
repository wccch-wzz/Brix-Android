package com.brixcore.download;

import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.function.ExceptionalFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes14.dex */
public class DefaultGameBuilder extends GameBuilder {
    private final DefaultDependencyManager dependencyManager;

    public DefaultGameBuilder(DefaultDependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    public DefaultDependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    @Override // com.brixcore.download.GameBuilder
    public Task<?> buildAsync() {
        List<String> stages = new ArrayList<>();
        Task<Version> libraryTask = Task.supplyAsync(new Callable() { // from class: com.brixcore.download.DefaultGameBuilder$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return this.f$0.lambda$buildAsync$0();
            }
        });
        Task taskThenComposeAsync = libraryTask.thenComposeAsync((ExceptionalFunction<Version, Task<U>, E>) libraryTaskHelper(this.gameVersion, "game", this.gameVersion));
        stages.add("Brix.install.game:" + this.gameVersion);
        stages.add("Brix.install.assets");
        for (Map.Entry<String, String> entry : this.toolVersions.entrySet()) {
            taskThenComposeAsync = taskThenComposeAsync.thenComposeAsync(libraryTaskHelper(this.gameVersion, entry.getKey(), entry.getValue()));
            stages.add(String.format("Brix.install.%s:%s", entry.getKey(), entry.getValue()));
        }
        for (final RemoteVersion remoteVersion : this.remoteVersions) {
            taskThenComposeAsync = taskThenComposeAsync.thenComposeAsync(new ExceptionalFunction() { // from class: com.brixcore.download.DefaultGameBuilder$$ExternalSyntheticLambda2
                @Override // com.brixcore.util.function.ExceptionalFunction
                public final Object apply(Object obj) {
                    return this.f$0.lambda$buildAsync$1(remoteVersion, (Version) obj);
                }
            });
            stages.add(String.format("Brix.install.%s:%s", remoteVersion.getLibraryId(), remoteVersion.getSelfVersion()));
        }
        final DefaultGameRepository gameRepository = this.dependencyManager.getGameRepository();
        Objects.requireNonNull(gameRepository);
        return taskThenComposeAsync.thenComposeAsync(new ExceptionalFunction() { // from class: com.brixcore.download.DefaultGameBuilder$$ExternalSyntheticLambda3
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return gameRepository.saveAsync((Version) obj);
            }
        }).whenComplete(new Task.FinalizedCallback() { // from class: com.brixcore.download.DefaultGameBuilder$$ExternalSyntheticLambda4
            @Override // com.brixcore.task.Task.FinalizedCallback
            public final void execute(Exception exc) throws Exception {
                this.f$0.lambda$buildAsync$2(exc);
            }
        }).withStagesHint(stages);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Version lambda$buildAsync$0() throws Exception {
        return new Version(this.name);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$buildAsync$1(RemoteVersion remoteVersion, Version version) throws RuntimeException {
        return this.dependencyManager.installLibraryAsync(version, remoteVersion);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$buildAsync$2(Exception exception) throws Exception {
        if (exception != null) {
            this.dependencyManager.getGameRepository().removeVersionFromDisk(this.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$libraryTaskHelper$3(String gameVersion, String libraryId, String libraryVersion, Version version) throws Exception {
        return this.dependencyManager.installLibraryAsync(gameVersion, version, libraryId, libraryVersion);
    }

    private ExceptionalFunction<Version, Task<Version>, ?> libraryTaskHelper(final String gameVersion, final String libraryId, final String libraryVersion) {
        return new ExceptionalFunction() { // from class: com.brixcore.download.DefaultGameBuilder$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$libraryTaskHelper$3(gameVersion, libraryId, libraryVersion, (Version) obj);
            }
        };
    }
}
