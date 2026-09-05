package com.brixcore.download;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes14.dex */
public class AdaptedDownloadProvider implements DownloadProvider {
    private List<DownloadProvider> downloadProviderCandidates;

    public void setDownloadProviderCandidates(List<DownloadProvider> downloadProviderCandidates) {
        this.downloadProviderCandidates = new ArrayList(downloadProviderCandidates);
    }

    public DownloadProvider getPreferredDownloadProvider() {
        List<DownloadProvider> d = this.downloadProviderCandidates;
        if (d == null || d.isEmpty()) {
            throw new IllegalStateException("No download provider candidate");
        }
        return d.get(0);
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getVersionListURL() {
        return getPreferredDownloadProvider().getVersionListURL();
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getAssetBaseURL() {
        return getPreferredDownloadProvider().getAssetBaseURL();
    }

    @Override // com.brixcore.download.DownloadProvider
    public String injectURL(String baseURL) {
        return getPreferredDownloadProvider().injectURL(baseURL);
    }

    @Override // com.brixcore.download.DownloadProvider
    public List<URL> getAssetObjectCandidates(final String assetObjectLocation) {
        return (List) this.downloadProviderCandidates.stream().flatMap(new Function() { // from class: com.brixcore.download.AdaptedDownloadProvider$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((DownloadProvider) obj).getAssetObjectCandidates(assetObjectLocation).stream();
            }
        }).collect(Collectors.toList());
    }

    @Override // com.brixcore.download.DownloadProvider
    public List<URL> injectURLWithCandidates(final String baseURL) {
        return (List) this.downloadProviderCandidates.stream().flatMap(new Function() { // from class: com.brixcore.download.AdaptedDownloadProvider$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((DownloadProvider) obj).injectURLWithCandidates(baseURL).stream();
            }
        }).collect(Collectors.toList());
    }

    @Override // com.brixcore.download.DownloadProvider
    public List<URL> injectURLsWithCandidates(final List<String> urls) {
        return (List) this.downloadProviderCandidates.stream().flatMap(new Function() { // from class: com.brixcore.download.AdaptedDownloadProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((DownloadProvider) obj).injectURLsWithCandidates(urls).stream();
            }
        }).collect(Collectors.toList());
    }

    @Override // com.brixcore.download.DownloadProvider
    public VersionList<?> getVersionListById(String id) {
        return getPreferredDownloadProvider().getVersionListById(id);
    }

    @Override // com.brixcore.download.DownloadProvider
    public int getConcurrency() {
        return getPreferredDownloadProvider().getConcurrency();
    }
}
