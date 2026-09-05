package com.brixcore.download.optifine;

import com.brixcore.download.VersionList;
import com.brixcore.util.StringUtils;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.versioning.VersionNumber;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes11.dex */
public final class OptiFineBMCLVersionList extends VersionList<OptiFineRemoteVersion> {
    private final String apiRoot;

    public OptiFineBMCLVersionList(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return true;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return HttpRequest.GET(this.apiRoot + "/optifine/versionlist").getJsonAsync(new TypeToken<List<OptiFineVersion>>() { // from class: com.brixcore.download.optifine.OptiFineBMCLVersionList.1
        }.getType()).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.optifine.OptiFineBMCLVersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$0((List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0(List list) {
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            HashSet hashSet = new HashSet();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                OptiFineVersion optiFineVersion = (OptiFineVersion) it.next();
                String str = optiFineVersion.type + "_" + optiFineVersion.patch;
                String str2 = this.apiRoot + "/optifine/" + optiFineVersion.gameVersion + "/" + optiFineVersion.type + "/" + optiFineVersion.patch;
                if (hashSet.add(str2)) {
                    boolean z = optiFineVersion.patch != null && (optiFineVersion.patch.startsWith("pre") || optiFineVersion.patch.startsWith("alpha"));
                    if (!StringUtils.isBlank(optiFineVersion.gameVersion)) {
                        String strNormalize = VersionNumber.normalize(optiFineVersion.gameVersion);
                        this.versions.put(strNormalize, new OptiFineRemoteVersion(strNormalize, str, Collections.singletonList(str2), z));
                    }
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private static final class OptiFineVersion {

        @SerializedName("mcversion")
        private final String gameVersion;

        @SerializedName("patch")
        private final String patch;

        @SerializedName("type")
        private final String type;

        public OptiFineVersion(String type, String patch, String gameVersion) {
            this.type = type;
            this.patch = patch;
            this.gameVersion = gameVersion;
        }
    }
}
