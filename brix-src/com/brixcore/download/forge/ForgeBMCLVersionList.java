package com.brixcore.download.forge;

import com.brixcore.download.VersionList;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes3.dex */
public final class ForgeBMCLVersionList extends VersionList<ForgeRemoteVersion> {
    private final String apiRoot;

    public ForgeBMCLVersionList(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override // com.brixcore.download.VersionList
    public boolean hasType() {
        return false;
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> loadAsync() {
        throw new UnsupportedOperationException("ForgeBMCLVersionList does not support loading the entire Forge remote version list.");
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync() {
        throw new UnsupportedOperationException("ForgeBMCLVersionList does not support loading the entire Forge remote version list.");
    }

    private static String toLookupVersion(String gameVersion) {
        return "1.7.10-pre4".equals(gameVersion) ? "1.7.10_pre4" : gameVersion;
    }

    private static String fromLookupVersion(String lookupVersion) {
        return "1.7.10_pre4".equals(lookupVersion) ? "1.7.10-pre4" : lookupVersion;
    }

    private static String toLookupBranch(String gameVersion, String branch) {
        if ("1.7.10-pre4".equals(gameVersion)) {
            return "prerelease";
        }
        return (String) Lang.requireNonNullElse(branch, "");
    }

    @Override // com.brixcore.download.VersionList
    public CompletableFuture<?> refreshAsync(final String gameVersion) {
        final String lookupVersion = toLookupVersion(gameVersion);
        return CompletableFuture.completedFuture(null).thenApplyAsync(Lang.wrap(new ExceptionalFunction() { // from class: com.brixcore.download.forge.ForgeBMCLVersionList$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$refreshAsync$0(lookupVersion, obj);
            }
        })).thenAcceptAsync(new Consumer() { // from class: com.brixcore.download.forge.ForgeBMCLVersionList$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$refreshAsync$1(gameVersion, lookupVersion, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$refreshAsync$0(String lookupVersion, Object unused) throws Exception {
        return (List) HttpRequest.GET(this.apiRoot + "/forge/minecraft/" + lookupVersion).getJson(new TypeToken<List<ForgeVersion>>() { // from class: com.brixcore.download.forge.ForgeBMCLVersionList.1
        }.getType());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public /* synthetic */ void lambda$refreshAsync$1(String str, String str2, List list) {
        String str3 = str2;
        String str4 = "forge-";
        this.lock.writeLock().lock();
        try {
            this.versions.clear(str);
            if (list != null) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    ForgeVersion forgeVersion = (ForgeVersion) it.next();
                    if (forgeVersion != null) {
                        ArrayList arrayList = new ArrayList();
                        Iterator<ForgeVersion.File> it2 = forgeVersion.getFiles().iterator();
                        while (it2.hasNext()) {
                            ForgeVersion.File next = it2.next();
                            if (!"installer".equals(next.getCategory()) || !"jar".equals(next.getFormat())) {
                                it2 = it2;
                            } else {
                                String lookupBranch = toLookupBranch(str, forgeVersion.getBranch());
                                String str5 = str3 + "-" + forgeVersion.getVersion() + (lookupBranch.isEmpty() ? "" : '-' + lookupBranch);
                                String str6 = str4 + str5 + "-" + next.getCategory() + "." + next.getFormat();
                                String str7 = str4 + str5 + "-" + str3 + "-" + next.getCategory() + "." + next.getFormat();
                                arrayList.add("https://files.minecraftforge.net/maven/net/minecraftforge/forge/" + str5 + "/" + str6);
                                arrayList.add("https://files.minecraftforge.net/maven/net/minecraftforge/forge/" + str5 + "-" + str3 + "/" + str7);
                                arrayList.add(NetworkUtils.withQuery("https://bmclapi2.bangbang93.com/forge/download", Lang.mapOf(Pair.pair("mcversion", forgeVersion.getGameVersion()), Pair.pair("version", forgeVersion.getVersion()), Pair.pair("branch", lookupBranch), Pair.pair("category", next.getCategory()), Pair.pair("format", next.getFormat()))));
                            }
                            str3 = str2;
                            it2 = it2;
                            str4 = str4;
                        }
                        String str8 = str4;
                        if (arrayList.isEmpty()) {
                            str3 = str2;
                            str4 = str8;
                        } else {
                            Instant instant = null;
                            if (forgeVersion.getModified() != null) {
                                try {
                                    instant = Instant.parse(forgeVersion.getModified());
                                } catch (DateTimeParseException e) {
                                    Logging.LOG.log(Level.WARNING, "Failed to parse instant " + forgeVersion.getModified(), (Throwable) e);
                                }
                            }
                            this.versions.put(str, new ForgeRemoteVersion(fromLookupVersion(forgeVersion.getGameVersion()), forgeVersion.getVersion(), instant, arrayList));
                            str3 = str2;
                            str4 = str8;
                        }
                    }
                }
                this.lock.writeLock().unlock();
                return;
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
            throw th;
        }
    }

    @Override // com.brixcore.download.VersionList
    public Optional<ForgeRemoteVersion> getVersion(String gameVersion, String remoteVersion) {
        return super.getVersion(gameVersion, StringUtils.substringAfter(remoteVersion, "-", remoteVersion));
    }

    public static final class ForgeVersion implements Validation {
        private final String branch;
        private final int build;
        private final List<File> files;
        private final String mcversion;
        private final String modified;
        private final String version;

        public ForgeVersion() {
            this(null, 0, "", null, "", Collections.emptyList());
        }

        public ForgeVersion(String branch, int build, String mcversion, String modified, String version, List<File> files) {
            this.branch = branch;
            this.build = build;
            this.mcversion = mcversion;
            this.modified = modified;
            this.version = version;
            this.files = files;
        }

        public String getBranch() {
            return this.branch;
        }

        public int getBuild() {
            return this.build;
        }

        public String getGameVersion() {
            return this.mcversion;
        }

        public String getModified() {
            return this.modified;
        }

        public String getVersion() {
            return this.version;
        }

        public List<File> getFiles() {
            return this.files;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException {
            if (this.files == null) {
                throw new JsonParseException("ForgeVersion files cannot be null");
            }
            if (this.version == null) {
                throw new JsonParseException("ForgeVersion version cannot be null");
            }
            if (this.mcversion == null) {
                throw new JsonParseException("ForgeVersion mcversion cannot be null");
            }
        }

        public static final class File {
            private final String category;
            private final String format;
            private final String hash;

            public File() {
                this("", "", "");
            }

            public File(String format, String category, String hash) {
                this.format = format;
                this.category = category;
                this.hash = hash;
            }

            public String getFormat() {
                return this.format;
            }

            public String getCategory() {
                return this.category;
            }

            public String getHash() {
                return this.hash;
            }
        }
    }
}
