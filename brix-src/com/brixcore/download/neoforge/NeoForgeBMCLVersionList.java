package com.brixcore.download.neoforge;

import com.brixcore.download.VersionList;
import com.brixcore.util.Lang;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.HttpRequest;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes5.dex */
public final class NeoForgeBMCLVersionList extends VersionList<NeoForgeRemoteVersion> {
    private final String apiRoot;

    public NeoForgeBMCLVersionList(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return true;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> loadAsync() {
        throw new UnsupportedOperationException("NeoForgeBMCLVersionList does not support loading the entire NeoForge remote version list.");
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        throw new UnsupportedOperationException("NeoForgeBMCLVersionList does not support loading the entire NeoForge remote version list.");
    }

    @Override // com.brixcore.download.VersionList
    public Optional<NeoForgeRemoteVersion> getVersion(String gameVersion, String remoteVersion) {
        if (gameVersion.equals("1.20.1")) {
            remoteVersion = NeoForgeRemoteVersion.normalize(remoteVersion);
        }
        return super.getVersion(gameVersion, remoteVersion);
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync(final String gameVersion) {
        return CompletableFuture.completedFuture(null).thenApplyAsync(Lang.wrap(new ExceptionalFunction() { // from class: com.brixcore.download.neoforge.NeoForgeBMCLVersionList$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$refreshAsync$0(gameVersion, (Void) obj);
            }
        })).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.neoforge.NeoForgeBMCLVersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$1(gameVersion, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$refreshAsync$0(String gameVersion, Void unused) throws Exception {
        return (List) HttpRequest.GET(this.apiRoot + "/neoforge/list/" + gameVersion).getJson(new TypeToken<List<NeoForgeVersion>>() { // from class: com.brixcore.download.neoforge.NeoForgeBMCLVersionList.1
        }.getType());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$1(String str, List list) {
        this.lock.writeLock().lock();
        try {
            this.versions.clear(str);
            Iterator it = list.iterator();
            while (it.hasNext()) {
                NeoForgeVersion neoForgeVersion = (NeoForgeVersion) it.next();
                this.versions.put(str, new NeoForgeRemoteVersion(neoForgeVersion.mcVersion, NeoForgeRemoteVersion.normalize(neoForgeVersion.version), Collections.singletonList(this.apiRoot + "/neoforge/version/" + neoForgeVersion.version + "/download/installer.jar")));
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }

    private static final class NeoForgeVersion implements Validation {

        @SerializedName("mcversion")
        private final String mcVersion;
        private final String rawVersion;
        private final String version;

        public NeoForgeVersion(String rawVersion, String version, String mcVersion) {
            this.rawVersion = rawVersion;
            this.version = version;
            this.mcVersion = mcVersion;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException {
            if (this.rawVersion == null) {
                throw new JsonParseException("NeoForgeVersion rawVersion cannot be null.");
            }
            if (this.version == null) {
                throw new JsonParseException("NeoForgeVersion version cannot be null.");
            }
            if (this.mcVersion == null) {
                throw new JsonParseException("NeoForgeVersion mcversion cannot be null.");
            }
        }
    }
}
