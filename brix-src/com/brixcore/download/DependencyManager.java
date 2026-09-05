package com.brixcore.download;

import com.brixcore.game.GameRepository;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;

/* JADX INFO: loaded from: classes14.dex */
public interface DependencyManager {
    Task<?> checkGameCompletionAsync(Version version, boolean z);

    Task<?> checkLibraryCompletionAsync(Version version, boolean z);

    Task<?> checkPatchCompletionAsync(Version version, boolean z);

    GameBuilder gameBuilder();

    CacheRepository getCacheRepository();

    GameRepository getGameRepository();

    VersionList<?> getVersionList(String str);

    Task<?> installLibraryAsync(Version version, RemoteVersion remoteVersion);

    Task<?> installLibraryAsync(String str, Version version, String str2, String str3);
}
