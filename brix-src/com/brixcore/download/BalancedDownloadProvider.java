package com.brixcore.download;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/* JADX INFO: loaded from: classes14.dex */
public final class BalancedDownloadProvider implements DownloadProvider {
    private final DownloadProvider[] candidates;
    private final Map<String, VersionList<?>> versionLists = new HashMap();

    public BalancedDownloadProvider(DownloadProvider... candidates) {
        this.candidates = candidates;
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getVersionListURL() {
        throw new UnsupportedOperationException();
    }

    @Override // com.brixcore.download.DownloadProvider
    public String getAssetBaseURL() {
        throw new UnsupportedOperationException();
    }

    @Override // com.brixcore.download.DownloadProvider
    public String injectURL(String baseURL) {
        throw new UnsupportedOperationException();
    }

    @Override // com.brixcore.download.DownloadProvider
    public VersionList<?> getVersionListById(String id) {
        return this.versionLists.computeIfAbsent(id, new Function() { // from class: com.brixcore.download.BalancedDownloadProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$getVersionListById$0((String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ VersionList lambda$getVersionListById$0(String value) {
        VersionList<?>[] lists = new VersionList[this.candidates.length];
        for (int i = 0; i < this.candidates.length; i++) {
            lists[i] = this.candidates[i].getVersionListById(value);
        }
        return new MultipleSourceVersionList(lists);
    }

    @Override // com.brixcore.download.DownloadProvider
    public int getConcurrency() {
        throw new UnsupportedOperationException();
    }
}
