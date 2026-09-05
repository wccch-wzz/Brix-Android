package com.brixcore.download.neoforge;

import com.android.tools.r8.RecordTag;
import com.brixcore.auth.microsoft.MicrosoftService$MinecraftLicense$$ExternalSyntheticRecord0;
import com.brixcore.download.DownloadProvider;
import com.brixcore.download.VersionList;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.function.ExceptionalSupplier;
import com.brixcore.util.io.HttpRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes5.dex */
public final class NeoForgeOfficialVersionList extends VersionList<NeoForgeRemoteVersion> {
    private static final String META_URL = "https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/neoforge";
    private static final String OLD_URL = "https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/forge";
    private final DownloadProvider downloadProvider;

    public NeoForgeOfficialVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return true;
    }

    @Override // com.brixcore.download.VersionList
    public Optional<NeoForgeRemoteVersion> getVersion(String gameVersion, String remoteVersion) {
        if (gameVersion.equals("1.20.1")) {
            remoteVersion = NeoForgeRemoteVersion.normalize(remoteVersion);
        }
        return super.getVersion(gameVersion, remoteVersion);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ OfficialAPIResult[] lambda$refreshAsync$0() throws Exception {
        return new OfficialAPIResult[]{(OfficialAPIResult) HttpRequest.GET(this.downloadProvider.injectURL(OLD_URL)).getJson(OfficialAPIResult.class), (OfficialAPIResult) HttpRequest.GET(this.downloadProvider.injectURL(META_URL)).getJson(OfficialAPIResult.class)};
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.supplyAsync(Lang.wrap(new ExceptionalSupplier() { // from class: com.brixcore.download.neoforge.NeoForgeOfficialVersionList$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$refreshAsync$0();
            }
        })).thenAccept(new Consumer() { // from class: com.brixcore.download.neoforge.NeoForgeOfficialVersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$1((NeoForgeOfficialVersionList.OfficialAPIResult[]) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$1(OfficialAPIResult[] officialAPIResultArr) {
        String strSubstring;
        String strSubstring2;
        String strSubstring3;
        this.lock.writeLock().lock();
        try {
            this.versions.clear();
            for (String str : officialAPIResultArr[0].versions) {
                this.versions.put("1.20.1", new NeoForgeRemoteVersion("1.20.1", NeoForgeRemoteVersion.normalize(str), Collections.singletonList("https://maven.neoforged.net/releases/net/neoforged/forge/" + str + "/forge-" + str + "-installer.jar")));
            }
            for (String str2 : officialAPIResultArr[1].versions) {
                try {
                    int iIndexOf = str2.indexOf(46);
                    int iIndexOf2 = str2.indexOf(46, iIndexOf + 1);
                    if (iIndexOf < 0 || iIndexOf2 < 0) {
                        Logging.LOG.warning("Unsupported NeoForge version: " + str2);
                    } else {
                        int i = Integer.parseInt(str2.substring(0, iIndexOf));
                        if (i == 0) {
                            strSubstring = str2.substring(iIndexOf + 1, iIndexOf2);
                        } else if (i >= 26) {
                            int iIndexOf3 = str2.indexOf(46, iIndexOf2 + 1);
                            if (iIndexOf3 < 0) {
                                Logging.LOG.warning("Unsupported NeoForge version: " + str2);
                            } else {
                                if (Integer.parseInt(str2.substring(iIndexOf2 + 1, iIndexOf3)) == 0) {
                                    strSubstring3 = str2.substring(0, iIndexOf2);
                                } else {
                                    strSubstring3 = str2.substring(0, iIndexOf3);
                                }
                                int iIndexOf4 = str2.indexOf(43);
                                if (iIndexOf4 < 0) {
                                    strSubstring = strSubstring3;
                                } else {
                                    strSubstring = strSubstring3 + "-" + str2.substring(iIndexOf4 + 1);
                                }
                            }
                        } else {
                            if (Integer.parseInt(str2.substring(iIndexOf + 1, iIndexOf2)) == 0) {
                                strSubstring2 = str2.substring(0, iIndexOf);
                            } else {
                                strSubstring2 = str2.substring(0, iIndexOf2);
                            }
                            strSubstring = "1." + strSubstring2;
                        }
                        this.versions.put(strSubstring, new NeoForgeRemoteVersion(strSubstring, NeoForgeRemoteVersion.normalize(str2), Collections.singletonList("https://maven.neoforged.net/releases/net/neoforged/neoforge/" + str2 + "/neoforge-" + str2 + "-installer.jar")));
                    }
                } catch (RuntimeException e) {
                    Logging.LOG.warning(String.format("Cannot parse NeoForge version %s for cracking its mc version. ", str2) + e);
                }
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class OfficialAPIResult extends RecordTag {
        private final boolean isSnapshot;
        private final List<String> versions;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof OfficialAPIResult)) {
                return false;
            }
            OfficialAPIResult officialAPIResult = (OfficialAPIResult) obj;
            return this.isSnapshot == officialAPIResult.isSnapshot && Objects.equals(this.versions, officialAPIResult.versions);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Boolean.valueOf(this.isSnapshot), this.versions};
        }

        private OfficialAPIResult(boolean isSnapshot, List<String> versions) {
            this.isSnapshot = isSnapshot;
            this.versions = versions;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return NeoForgeOfficialVersionList$OfficialAPIResult$$ExternalSyntheticRecord0.m(this.isSnapshot, this.versions);
        }

        public boolean isSnapshot() {
            return this.isSnapshot;
        }

        public final String toString() {
            return MicrosoftService$MinecraftLicense$$ExternalSyntheticRecord0.m($record$getFieldsAsObjects(), OfficialAPIResult.class, "isSnapshot;versions");
        }

        public List<String> versions() {
            return this.versions;
        }
    }
}
