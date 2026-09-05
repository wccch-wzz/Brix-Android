package com.brixcore.download;

/* JADX INFO: loaded from: classes14.dex */
public abstract class AbstractDependencyManager implements DependencyManager {
    @Override // com.brixcore.download.DependencyManager
    public abstract DefaultCacheRepository getCacheRepository();

    public abstract DownloadProvider getDownloadProvider();

    @Override // com.brixcore.download.DependencyManager
    public VersionList<?> getVersionList(String id) {
        return getDownloadProvider().getVersionListById(id);
    }
}
