package com.brixcore.download.forge;

import com.brixcore.download.DownloadProvider;
import com.brixcore.download.VersionList;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.versioning.VersionNumber;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeVersionList extends VersionList<ForgeRemoteVersion> {
    public static final String FORGE_LIST = "https://hmcl.glavo.site/metadata/forge/";
    private final DownloadProvider downloadProvider;

    public ForgeVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return false;
    }

    private static String toLookupVersion(String gameVersion) {
        return "1.7.10-pre4".equals(gameVersion) ? "1.7.10_pre4" : gameVersion;
    }

    private static String fromLookupVersion(String lookupVersion) {
        return "1.7.10_pre4".equals(lookupVersion) ? "1.7.10-pre4" : lookupVersion;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return HttpRequest.GET(FORGE_LIST).getJsonAsync(ForgeVersionRoot.class).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.forge.ForgeVersionList$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$0((ForgeVersionRoot) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$0(ForgeVersionRoot forgeVersionRoot) {
        String str;
        Iterator<Map.Entry<String, int[]>> it;
        String str2 = "-";
        this.lock.writeLock().lock();
        if (forgeVersionRoot != null) {
            try {
                this.versions.clear();
                Iterator<Map.Entry<String, int[]>> it2 = forgeVersionRoot.getGameVersions().entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<String, int[]> next = it2.next();
                    String strFromLookupVersion = fromLookupVersion(VersionNumber.normalize(next.getKey()));
                    int[] value = next.getValue();
                    int length = value.length;
                    int i = 0;
                    while (i < length) {
                        ForgeVersion forgeVersion = forgeVersionRoot.getNumber().get(Integer.valueOf(value[i]));
                        if (forgeVersion == null) {
                            str = str2;
                            it = it2;
                        } else {
                            String[][] files = forgeVersion.getFiles();
                            int length2 = files.length;
                            String str3 = null;
                            int i2 = 0;
                            while (i2 < length2) {
                                String[] strArr = files[i2];
                                int i3 = i2;
                                if (strArr.length <= 1 || !"installer".equals(strArr[1])) {
                                    it2 = it2;
                                } else {
                                    String str4 = forgeVersion.getGameVersion() + str2 + forgeVersion.getVersion() + (StringUtils.isNotBlank(forgeVersion.getBranch()) ? str2 + forgeVersion.getBranch() : "");
                                    str3 = forgeVersionRoot.getWebPath() + str4 + "/" + (forgeVersionRoot.getArtifact() + str2 + str4 + str2 + strArr[1] + "." + strArr[0]);
                                }
                                i2 = i3 + 1;
                                str2 = str2;
                                it2 = it2;
                            }
                            str = str2;
                            it = it2;
                            if (str3 != null) {
                                Instant instantOfEpochSecond = null;
                                if (forgeVersion.getModified() != null) {
                                    try {
                                        instantOfEpochSecond = Instant.ofEpochSecond(Long.parseLong(forgeVersion.getModified()));
                                    } catch (NumberFormatException e) {
                                        Logging.LOG.log(Level.WARNING, "Failed to parse instant " + forgeVersion.getModified(), (Throwable) e);
                                    }
                                }
                                this.versions.put(strFromLookupVersion, new ForgeRemoteVersion(toLookupVersion(forgeVersion.getGameVersion()), forgeVersion.getVersion(), instantOfEpochSecond, Collections.singletonList(str3)));
                            }
                        }
                        i++;
                        str2 = str;
                        it2 = it;
                    }
                }
                this.lock.writeLock().unlock();
                return;
            } catch (Throwable th) {
                this.lock.writeLock().unlock();
                throw th;
            }
        }
        this.lock.writeLock().unlock();
    }
}
