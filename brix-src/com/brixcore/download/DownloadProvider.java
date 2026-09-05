package com.brixcore.download;

import com.brixcore.util.io.NetworkUtils;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes14.dex */
public interface DownloadProvider {
    String getAssetBaseURL();

    int getConcurrency();

    VersionList<?> getVersionListById(String str);

    String getVersionListURL();

    String injectURL(String str);

    default List<URL> getAssetObjectCandidates(String assetObjectLocation) {
        return Collections.singletonList(NetworkUtils.toURL(getAssetBaseURL() + assetObjectLocation));
    }

    default List<URL> injectURLWithCandidates(String baseURL) {
        return Collections.singletonList(NetworkUtils.toURL(injectURL(baseURL)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* synthetic */ default Stream lambda$injectURLsWithCandidates$0(String url) {
        return injectURLWithCandidates(url).stream();
    }

    default List<URL> injectURLsWithCandidates(List<String> urls) {
        return (List) urls.stream().flatMap(new Function() { // from class: com.brixcore.download.DownloadProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$injectURLsWithCandidates$0((String) obj);
            }
        }).collect(Collectors.toList());
    }
}
