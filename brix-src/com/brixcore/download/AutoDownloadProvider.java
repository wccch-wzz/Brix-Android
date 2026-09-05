package com.brixcore.download;

import java.net.URL;
import java.util.List;

/* JADX INFO: loaded from: classes14.dex */
public class AutoDownloadProvider implements DownloadProvider {
    private final DownloadProvider fileProvider;
    private final DownloadProvider versionListProvider;

    public AutoDownloadProvider(DownloadProvider versionListProvider, DownloadProvider fileProvider) {
        this.versionListProvider = versionListProvider;
        this.fileProvider = fileProvider;
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getVersionListURL() {
        return this.versionListProvider.getVersionListURL();
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getAssetBaseURL() {
        return this.fileProvider.getAssetBaseURL();
    }

    @Override // com.brixcore.download.DownloadProvider
    public String injectURL(String baseURL) {
        return this.fileProvider.injectURL(baseURL);
    }

    @Override // com.brixcore.download.DownloadProvider
    public List<URL> getAssetObjectCandidates(String assetObjectLocation) {
        return this.fileProvider.getAssetObjectCandidates(assetObjectLocation);
    }

    @Override // com.brixcore.download.DownloadProvider
    public List<URL> injectURLWithCandidates(String baseURL) {
        return this.fileProvider.injectURLWithCandidates(baseURL);
    }

    @Override // com.brixcore.download.DownloadProvider
    public List<URL> injectURLsWithCandidates(List<String> urls) {
        return this.fileProvider.injectURLsWithCandidates(urls);
    }

    @Override // com.brixcore.download.DownloadProvider
    public VersionList<?> getVersionListById(String id) {
        return this.versionListProvider.getVersionListById(id);
    }

    @Override // com.brixcore.download.DownloadProvider
    public int getConcurrency() {
        return this.fileProvider.getConcurrency();
    }
}
