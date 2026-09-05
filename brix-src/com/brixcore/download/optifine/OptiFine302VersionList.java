package com.brixcore.download.optifine;

import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.versioning.VersionNumber;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes11.dex */
public final class OptiFine302VersionList extends com.brixcore.download.VersionList<OptiFineRemoteVersion> {
    private final String versionListURL;

    public OptiFine302VersionList(String versionListURL) {
        this.versionListURL = versionListURL;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return true;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return HttpRequest.GET(this.versionListURL).getJsonAsync(new TypeToken<VersionList>() { // from class: com.brixcore.download.optifine.OptiFine302VersionList.1
        }.getType()).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.optifine.OptiFine302VersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$1((OptiFine302VersionList.VersionList) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$1(VersionList versionList) {
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            for (final OptiFineVersion optiFineVersion : versionList.versions) {
                String strNormalize = VersionNumber.normalize(optiFineVersion.gameVersion);
                this.versions.put(strNormalize, new OptiFineRemoteVersion(strNormalize, optiFineVersion.version, (List) versionList.downloadBases.stream().map(new Function() { // from class: com.brixcore.download.optifine.OptiFine302VersionList$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return OptiFine302VersionList.lambda$refreshAsync$0(optiFineVersion, (String) obj);
                    }
                }).collect(Collectors.toList()), optiFineVersion.fileName.startsWith("pre")));
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }

    static /* synthetic */ String lambda$refreshAsync$0(OptiFineVersion element, String u) {
        return u + element.fileName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class VersionList {

        @SerializedName("download")
        private final List<String> downloadBases;

        @SerializedName("file")
        private final List<OptiFineVersion> versions;

        public VersionList(List<OptiFineVersion> versions, List<String> downloadBases) {
            this.versions = versions;
            this.downloadBases = downloadBases;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class OptiFineVersion {

        @SerializedName("filename")
        private final String fileName;

        @SerializedName("mcversion")
        private final String gameVersion;

        @SerializedName("name")
        private final String version;

        public OptiFineVersion(String version, String fileName, String gameVersion) {
            this.version = version;
            this.fileName = fileName;
            this.gameVersion = gameVersion;
        }
    }
}
