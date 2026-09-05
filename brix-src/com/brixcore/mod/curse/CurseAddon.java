package com.brixcore.mod.curse;

import com.android.tools.r8.RecordTag;
import com.brixcore.download.DefaultCacheRepository;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.RemoteMod;
import com.brixcore.mod.RemoteModRepository;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.versioning.GameVersionNumber;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseAddon extends RecordTag implements RemoteMod.IMod {
    public static final Map<Integer, RemoteMod.DependencyType> RELATION_TYPE = Lang.mapOf(Pair.pair(1, RemoteMod.DependencyType.EMBEDDED), Pair.pair(2, RemoteMod.DependencyType.OPTIONAL), Pair.pair(3, RemoteMod.DependencyType.REQUIRED), Pair.pair(4, RemoteMod.DependencyType.TOOL), Pair.pair(5, RemoteMod.DependencyType.INCOMPATIBLE), Pair.pair(6, RemoteMod.DependencyType.INCLUDE));
    private final boolean allowModDistribution;
    private final List<Author> authors;
    private final List<Category> categories;
    private final int classId;
    private final Instant dateCreated;
    private final Instant dateModified;
    private final Instant dateReleased;
    private final int downloadCount;
    private final int gameId;
    private final int gamePopularityRank;
    private final int id;
    private final boolean isAvailable;
    private final boolean isFeatured;
    private final List<LatestFileIndex> latestFileIndices;
    private final List<LatestFile> latestFiles;
    private final Links links;
    private final Logo logo;
    private final int mainFileId;
    private final String name;
    private final int primaryCategoryId;
    private final List<Screenshot> screenshots;
    private final String slug;
    private final int status;
    private final String summary;
    private final int thumbsUpCount;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof CurseAddon)) {
            return false;
        }
        CurseAddon curseAddon = (CurseAddon) obj;
        return this.isFeatured == curseAddon.isFeatured && this.allowModDistribution == curseAddon.allowModDistribution && this.isAvailable == curseAddon.isAvailable && this.id == curseAddon.id && this.gameId == curseAddon.gameId && this.status == curseAddon.status && this.downloadCount == curseAddon.downloadCount && this.primaryCategoryId == curseAddon.primaryCategoryId && this.classId == curseAddon.classId && this.mainFileId == curseAddon.mainFileId && this.gamePopularityRank == curseAddon.gamePopularityRank && this.thumbsUpCount == curseAddon.thumbsUpCount && Objects.equals(this.name, curseAddon.name) && Objects.equals(this.slug, curseAddon.slug) && Objects.equals(this.links, curseAddon.links) && Objects.equals(this.summary, curseAddon.summary) && Objects.equals(this.categories, curseAddon.categories) && Objects.equals(this.authors, curseAddon.authors) && Objects.equals(this.logo, curseAddon.logo) && Objects.equals(this.latestFiles, curseAddon.latestFiles) && Objects.equals(this.latestFileIndices, curseAddon.latestFileIndices) && Objects.equals(this.dateCreated, curseAddon.dateCreated) && Objects.equals(this.dateModified, curseAddon.dateModified) && Objects.equals(this.dateReleased, curseAddon.dateReleased) && Objects.equals(this.screenshots, curseAddon.screenshots);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{Integer.valueOf(this.id), Integer.valueOf(this.gameId), this.name, this.slug, this.links, this.summary, Integer.valueOf(this.status), Integer.valueOf(this.downloadCount), Boolean.valueOf(this.isFeatured), Integer.valueOf(this.primaryCategoryId), this.categories, Integer.valueOf(this.classId), this.authors, this.logo, Integer.valueOf(this.mainFileId), this.latestFiles, this.latestFileIndices, this.dateCreated, this.dateModified, this.dateReleased, Boolean.valueOf(this.allowModDistribution), Integer.valueOf(this.gamePopularityRank), Boolean.valueOf(this.isAvailable), Integer.valueOf(this.thumbsUpCount), this.screenshots};
    }

    public CurseAddon(int id, int gameId, String name, String slug, Links links, String summary, int status, int downloadCount, boolean isFeatured, int primaryCategoryId, List<Category> categories, int classId, List<Author> authors, Logo logo, int mainFileId, List<LatestFile> latestFiles, List<LatestFileIndex> latestFileIndices, Instant dateCreated, Instant dateModified, Instant dateReleased, boolean allowModDistribution, int gamePopularityRank, boolean isAvailable, int thumbsUpCount, List<Screenshot> screenshots) {
        this.id = id;
        this.gameId = gameId;
        this.name = name;
        this.slug = slug;
        this.links = links;
        this.summary = summary;
        this.status = status;
        this.downloadCount = downloadCount;
        this.isFeatured = isFeatured;
        this.primaryCategoryId = primaryCategoryId;
        this.categories = categories;
        this.classId = classId;
        this.authors = authors;
        this.logo = logo;
        this.mainFileId = mainFileId;
        this.latestFiles = latestFiles;
        this.latestFileIndices = latestFileIndices;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.dateReleased = dateReleased;
        this.allowModDistribution = allowModDistribution;
        this.gamePopularityRank = gamePopularityRank;
        this.isAvailable = isAvailable;
        this.thumbsUpCount = thumbsUpCount;
        this.screenshots = screenshots;
    }

    public boolean allowModDistribution() {
        return this.allowModDistribution;
    }

    public List<Author> authors() {
        return this.authors;
    }

    public List<Category> categories() {
        return this.categories;
    }

    public int classId() {
        return this.classId;
    }

    public Instant dateCreated() {
        return this.dateCreated;
    }

    public Instant dateModified() {
        return this.dateModified;
    }

    public Instant dateReleased() {
        return this.dateReleased;
    }

    public int downloadCount() {
        return this.downloadCount;
    }

    public final boolean equals(Object o) {
        return $record$equals(o);
    }

    public int gameId() {
        return this.gameId;
    }

    public int gamePopularityRank() {
        return this.gamePopularityRank;
    }

    public final int hashCode() {
        return CurseAddon$$ExternalSyntheticRecord0.m(this.isFeatured, this.allowModDistribution, this.isAvailable, this.id, this.gameId, this.status, this.downloadCount, this.primaryCategoryId, this.classId, this.mainFileId, this.gamePopularityRank, this.thumbsUpCount, this.name, this.slug, this.links, this.summary, this.categories, this.authors, this.logo, this.latestFiles, this.latestFileIndices, this.dateCreated, this.dateModified, this.dateReleased, this.screenshots);
    }

    public int id() {
        return this.id;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public boolean isFeatured() {
        return this.isFeatured;
    }

    public List<LatestFileIndex> latestFileIndices() {
        return this.latestFileIndices;
    }

    public List<LatestFile> latestFiles() {
        return this.latestFiles;
    }

    public Links links() {
        return this.links;
    }

    public Logo logo() {
        return this.logo;
    }

    public int mainFileId() {
        return this.mainFileId;
    }

    public String name() {
        return this.name;
    }

    public int primaryCategoryId() {
        return this.primaryCategoryId;
    }

    public List<Screenshot> screenshots() {
        return this.screenshots;
    }

    public String slug() {
        return this.slug;
    }

    public int status() {
        return this.status;
    }

    public String summary() {
        return this.summary;
    }

    public int thumbsUpCount() {
        return this.thumbsUpCount;
    }

    public final String toString() {
        return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), CurseAddon.class, "id;gameId;name;slug;links;summary;status;downloadCount;isFeatured;primaryCategoryId;categories;classId;authors;logo;mainFileId;latestFiles;latestFileIndices;dateCreated;dateModified;dateReleased;allowModDistribution;gamePopularityRank;isAvailable;thumbsUpCount;screenshots");
    }

    @Override // com.brixcore.mod.RemoteMod.IMod
    public List<RemoteMod> loadDependencies(RemoteModRepository modRepository) throws IOException {
        Set<Integer> dependencies = (Set) this.latestFiles.stream().flatMap(new Function() { // from class: com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CurseAddon.LatestFile) obj).dependencies().stream();
            }
        }).filter(new Predicate() { // from class: com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CurseAddon.lambda$loadDependencies$1((CurseAddon.Dependency) obj);
            }
        }).map(new Function() { // from class: com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((CurseAddon.Dependency) obj).modId());
            }
        }).collect(Collectors.toSet());
        List<RemoteMod> mods = new ArrayList<>();
        Iterator<Integer> it = dependencies.iterator();
        while (it.hasNext()) {
            int dependencyId = it.next().intValue();
            mods.add(modRepository.getModById(Integer.toString(dependencyId)));
        }
        return mods;
    }

    static /* synthetic */ boolean lambda$loadDependencies$1(Dependency dep) {
        return dep.relationType() == 3;
    }

    @Override // com.brixcore.mod.RemoteMod.IMod
    public Stream<RemoteMod.Version> loadVersions(RemoteModRepository modRepository) throws IOException {
        return modRepository.getRemoteVersionsById(Integer.toString(this.id));
    }

    @Override // com.brixcore.mod.RemoteMod.IMod
    public List<RemoteMod.Screenshot> loadScreenshots(RemoteModRepository modRepository) {
        List<RemoteMod.Screenshot> screenshotList = new ArrayList<>();
        for (Screenshot screenshot : this.screenshots) {
            screenshotList.add(new RemoteMod.Screenshot(screenshot.url, screenshot.title, screenshot.description));
        }
        return screenshotList;
    }

    public RemoteMod toMod() {
        String iconUrl = (String) Optional.ofNullable(this.logo).map(new Function() { // from class: com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CurseAddon.Logo) obj).thumbnailUrl();
            }
        }).orElse("");
        return new RemoteMod(this.slug, "", this.name, this.summary, (List) this.categories.stream().map(new Function() { // from class: com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.toString(((CurseAddon.Category) obj).getId());
            }
        }).collect(Collectors.toList()), this.links.websiteUrl, iconUrl, this, this.downloadCount, String.valueOf(this.id));
    }

    public static final class Links extends RecordTag {
        private final String issuesUrl;
        private final String sourceUrl;
        private final String websiteUrl;
        private final String wikiUrl;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Links)) {
                return false;
            }
            Links links = (Links) obj;
            return Objects.equals(this.websiteUrl, links.websiteUrl) && Objects.equals(this.wikiUrl, links.wikiUrl) && Objects.equals(this.issuesUrl, links.issuesUrl) && Objects.equals(this.sourceUrl, links.sourceUrl);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.websiteUrl, this.wikiUrl, this.issuesUrl, this.sourceUrl};
        }

        public Links(String websiteUrl, String wikiUrl, String issuesUrl, String sourceUrl) {
            this.websiteUrl = websiteUrl;
            this.wikiUrl = wikiUrl;
            this.issuesUrl = issuesUrl;
            this.sourceUrl = sourceUrl;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$Links$$ExternalSyntheticRecord0.m(this.websiteUrl, this.wikiUrl, this.issuesUrl, this.sourceUrl);
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Links.class, "websiteUrl;wikiUrl;issuesUrl;sourceUrl");
        }

        public String websiteUrl() {
            return this.websiteUrl;
        }

        public String wikiUrl() {
            return this.wikiUrl;
        }

        public String issuesUrl() {
            return this.issuesUrl;
        }

        public String sourceUrl() {
            return this.sourceUrl;
        }
    }

    public static final class Author extends RecordTag {
        private final int id;
        private final String name;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Author)) {
                return false;
            }
            Author author = (Author) obj;
            return this.id == author.id && Objects.equals(this.name, author.name) && Objects.equals(this.url, author.url);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.id), this.name, this.url};
        }

        public Author(int id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$Author$$ExternalSyntheticRecord0.m(this.id, this.name, this.url);
        }

        public int id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Author.class, "id;name;url");
        }

        public String url() {
            return this.url;
        }
    }

    public static final class Logo extends RecordTag {
        private final String description;
        private final int id;
        private final int modId;
        private final String thumbnailUrl;
        private final String title;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Logo)) {
                return false;
            }
            Logo logo = (Logo) obj;
            return this.id == logo.id && this.modId == logo.modId && Objects.equals(this.title, logo.title) && Objects.equals(this.description, logo.description) && Objects.equals(this.thumbnailUrl, logo.thumbnailUrl) && Objects.equals(this.url, logo.url);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.id), Integer.valueOf(this.modId), this.title, this.description, this.thumbnailUrl, this.url};
        }

        public Logo(int id, int modId, String title, String description, String thumbnailUrl, String url) {
            this.id = id;
            this.modId = modId;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = thumbnailUrl;
            this.url = url;
        }

        public String description() {
            return this.description;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$Logo$$ExternalSyntheticRecord0.m(this.id, this.modId, this.title, this.description, this.thumbnailUrl, this.url);
        }

        public int id() {
            return this.id;
        }

        public int modId() {
            return this.modId;
        }

        public String thumbnailUrl() {
            return this.thumbnailUrl;
        }

        public String title() {
            return this.title;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Logo.class, "id;modId;title;description;thumbnailUrl;url");
        }

        public String url() {
            return this.url;
        }
    }

    public static final class Attachment extends RecordTag {
        private final String description;
        private final int id;
        private final boolean isDefault;
        private final int projectId;
        private final int status;
        private final String thumbnailUrl;
        private final String title;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Attachment)) {
                return false;
            }
            Attachment attachment = (Attachment) obj;
            return this.isDefault == attachment.isDefault && this.id == attachment.id && this.projectId == attachment.projectId && this.status == attachment.status && Objects.equals(this.description, attachment.description) && Objects.equals(this.thumbnailUrl, attachment.thumbnailUrl) && Objects.equals(this.title, attachment.title) && Objects.equals(this.url, attachment.url);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.id), Integer.valueOf(this.projectId), this.description, Boolean.valueOf(this.isDefault), this.thumbnailUrl, this.title, this.url, Integer.valueOf(this.status)};
        }

        public Attachment(int id, int projectId, String description, boolean isDefault, String thumbnailUrl, String title, String url, int status) {
            this.id = id;
            this.projectId = projectId;
            this.description = description;
            this.isDefault = isDefault;
            this.thumbnailUrl = thumbnailUrl;
            this.title = title;
            this.url = url;
            this.status = status;
        }

        public String description() {
            return this.description;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$Attachment$$ExternalSyntheticRecord0.m(this.isDefault, this.id, this.projectId, this.status, this.description, this.thumbnailUrl, this.title, this.url);
        }

        public int id() {
            return this.id;
        }

        public boolean isDefault() {
            return this.isDefault;
        }

        public int projectId() {
            return this.projectId;
        }

        public int status() {
            return this.status;
        }

        public String thumbnailUrl() {
            return this.thumbnailUrl;
        }

        public String title() {
            return this.title;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Attachment.class, "id;projectId;description;isDefault;thumbnailUrl;title;url;status");
        }

        public String url() {
            return this.url;
        }
    }

    public static final class Dependency extends RecordTag {
        private final int modId;
        private final int relationType;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Dependency)) {
                return false;
            }
            Dependency dependency = (Dependency) obj;
            return this.modId == dependency.modId && this.relationType == dependency.relationType;
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.modId), Integer.valueOf(this.relationType)};
        }

        public Dependency(int modId, int relationType) {
            this.modId = modId;
            this.relationType = relationType;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$Dependency$$ExternalSyntheticRecord0.m(this.modId, this.relationType);
        }

        public int modId() {
            return this.modId;
        }

        public int relationType() {
            return this.relationType;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Dependency.class, "modId;relationType");
        }

        public Dependency() {
            this(0, 1);
        }
    }

    public static final class LatestFileHash extends RecordTag {
        private final int algo;
        private final String value;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof LatestFileHash)) {
                return false;
            }
            LatestFileHash latestFileHash = (LatestFileHash) obj;
            return this.algo == latestFileHash.algo && Objects.equals(this.value, latestFileHash.value);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.value, Integer.valueOf(this.algo)};
        }

        public LatestFileHash(String value, int algo) {
            this.value = value;
            this.algo = algo;
        }

        public int algo() {
            return this.algo;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$LatestFileHash$$ExternalSyntheticRecord0.m(this.algo, this.value);
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), LatestFileHash.class, "value;algo");
        }

        public String value() {
            return this.value;
        }
    }

    public static final class LatestFile extends RecordTag implements RemoteMod.IVersion {
        private final int alternateFileId;
        private final List<Dependency> dependencies;
        private final String displayName;
        private final int downloadCount;
        private final String downloadUrl;
        private final Instant fileDate;
        private final long fileFingerprint;
        private final int fileLength;
        private final String fileName;
        private final int fileStatus;
        private final int gameId;
        private final List<String> gameVersions;
        private final List<LatestFileHash> hashes;
        private final int id;
        private final boolean isAvailable;
        private final boolean isServerPack;
        private final int modId;
        private final int releaseType;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof LatestFile)) {
                return false;
            }
            LatestFile latestFile = (LatestFile) obj;
            return this.isAvailable == latestFile.isAvailable && this.isServerPack == latestFile.isServerPack && this.id == latestFile.id && this.gameId == latestFile.gameId && this.modId == latestFile.modId && this.releaseType == latestFile.releaseType && this.fileStatus == latestFile.fileStatus && this.fileLength == latestFile.fileLength && this.downloadCount == latestFile.downloadCount && this.alternateFileId == latestFile.alternateFileId && this.fileFingerprint == latestFile.fileFingerprint && Objects.equals(this.displayName, latestFile.displayName) && Objects.equals(this.fileName, latestFile.fileName) && Objects.equals(this.hashes, latestFile.hashes) && Objects.equals(this.fileDate, latestFile.fileDate) && Objects.equals(this.downloadUrl, latestFile.downloadUrl) && Objects.equals(this.gameVersions, latestFile.gameVersions) && Objects.equals(this.dependencies, latestFile.dependencies);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.id), Integer.valueOf(this.gameId), Integer.valueOf(this.modId), Boolean.valueOf(this.isAvailable), this.displayName, this.fileName, Integer.valueOf(this.releaseType), Integer.valueOf(this.fileStatus), this.hashes, this.fileDate, Integer.valueOf(this.fileLength), Integer.valueOf(this.downloadCount), this.downloadUrl, this.gameVersions, this.dependencies, Integer.valueOf(this.alternateFileId), Boolean.valueOf(this.isServerPack), Long.valueOf(this.fileFingerprint)};
        }

        public LatestFile(int id, int gameId, int modId, boolean isAvailable, String displayName, String fileName, int releaseType, int fileStatus, List<LatestFileHash> hashes, Instant fileDate, int fileLength, int downloadCount, String downloadUrl, List<String> gameVersions, List<Dependency> dependencies, int alternateFileId, boolean isServerPack, long fileFingerprint) {
            this.id = id;
            this.gameId = gameId;
            this.modId = modId;
            this.isAvailable = isAvailable;
            this.displayName = displayName;
            this.fileName = fileName;
            this.releaseType = releaseType;
            this.fileStatus = fileStatus;
            this.hashes = hashes;
            this.fileDate = fileDate;
            this.fileLength = fileLength;
            this.downloadCount = downloadCount;
            this.downloadUrl = downloadUrl;
            this.gameVersions = gameVersions;
            this.dependencies = dependencies;
            this.alternateFileId = alternateFileId;
            this.isServerPack = isServerPack;
            this.fileFingerprint = fileFingerprint;
        }

        public int alternateFileId() {
            return this.alternateFileId;
        }

        public List<Dependency> dependencies() {
            return this.dependencies;
        }

        public String displayName() {
            return this.displayName;
        }

        public int downloadCount() {
            return this.downloadCount;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public Instant fileDate() {
            return this.fileDate;
        }

        public long fileFingerprint() {
            return this.fileFingerprint;
        }

        public int fileLength() {
            return this.fileLength;
        }

        public String fileName() {
            return this.fileName;
        }

        public int fileStatus() {
            return this.fileStatus;
        }

        public int gameId() {
            return this.gameId;
        }

        public List<String> gameVersions() {
            return this.gameVersions;
        }

        public final int hashCode() {
            return CurseAddon$LatestFile$$ExternalSyntheticRecord1.m(this.isAvailable, this.isServerPack, this.id, this.gameId, this.modId, this.releaseType, this.fileStatus, this.fileLength, this.downloadCount, this.alternateFileId, this.fileFingerprint, this.displayName, this.fileName, this.hashes, this.fileDate, this.downloadUrl, this.gameVersions, this.dependencies);
        }

        public List<LatestFileHash> hashes() {
            return this.hashes;
        }

        public int id() {
            return this.id;
        }

        public boolean isAvailable() {
            return this.isAvailable;
        }

        public boolean isServerPack() {
            return this.isServerPack;
        }

        public int modId() {
            return this.modId;
        }

        public int releaseType() {
            return this.releaseType;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), LatestFile.class, "id;gameId;modId;isAvailable;displayName;fileName;releaseType;fileStatus;hashes;fileDate;fileLength;downloadCount;downloadUrl;gameVersions;dependencies;alternateFileId;isServerPack;fileFingerprint");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$downloadUrl$0() {
            return String.format(Locale.getDefault(), "https://edge.forgecdn.net/files/%d/%d/%s", Integer.valueOf(this.id / 1000), Integer.valueOf(this.id % 1000), this.fileName);
        }

        public String downloadUrl() {
            return (String) CurseAddon$LatestFile$$ExternalSyntheticBackport0.m(this.downloadUrl, new Supplier() { // from class: com.brixcore.mod.curse.CurseAddon$LatestFile$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    return this.f$0.lambda$downloadUrl$0();
                }
            });
        }

        @Override // com.brixcore.mod.RemoteMod.IVersion
        public RemoteMod.Type getType() {
            return RemoteMod.Type.CURSEFORGE;
        }

        public RemoteMod.Version toVersion() {
            RemoteMod.VersionType versionType;
            switch (releaseType()) {
                case 2:
                    versionType = RemoteMod.VersionType.Beta;
                    break;
                case 3:
                    versionType = RemoteMod.VersionType.Alpha;
                    break;
                default:
                    versionType = RemoteMod.VersionType.Release;
                    break;
            }
            RemoteMod.VersionType versionType2 = versionType;
            return new RemoteMod.Version(this, Integer.toString(this.modId), displayName(), fileName(), null, fileDate(), versionType2, new RemoteMod.File(Collections.emptyMap(), downloadUrl(), fileName()), (List) this.dependencies.stream().map(new Function() { // from class: com.brixcore.mod.curse.CurseAddon$LatestFile$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return CurseAddon.LatestFile.lambda$toVersion$1((CurseAddon.Dependency) obj);
                }
            }).distinct().filter(new Predicate() { // from class: com.brixcore.mod.curse.CurseAddon$LatestFile$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((RemoteMod.Dependency) obj);
                }
            }).collect(Collectors.toList()), (List) this.gameVersions.stream().filter(new Predicate() { // from class: com.brixcore.mod.curse.CurseAddon$LatestFile$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return GameVersionNumber.isKnown((String) obj);
                }
            }).collect(Collectors.toList()), (List) this.gameVersions.stream().flatMap(new Function() { // from class: com.brixcore.mod.curse.CurseAddon$LatestFile$$ExternalSyntheticLambda6
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return CurseAddon.LatestFile.lambda$toVersion$2((String) obj);
                }
            }).collect(Collectors.toList()));
        }

        static /* synthetic */ RemoteMod.Dependency lambda$toVersion$1(Dependency dependency) {
            if (!CurseAddon.RELATION_TYPE.containsKey(Integer.valueOf(dependency.relationType()))) {
                throw new IllegalStateException("Broken datas.");
            }
            return RemoteMod.Dependency.ofGeneral(CurseAddon.RELATION_TYPE.get(Integer.valueOf(dependency.relationType())), CurseForgeRemoteModRepository.MODS, Integer.toString(dependency.modId()));
        }

        static /* synthetic */ Stream lambda$toVersion$2(String version) {
            if ("fabric".equalsIgnoreCase(version)) {
                return Stream.of(ModLoaderType.FABRIC);
            }
            if (DefaultCacheRepository.LibraryIndex.TYPE_FORGE.equalsIgnoreCase(version)) {
                return Stream.of(ModLoaderType.FORGE);
            }
            if ("quilt".equalsIgnoreCase(version)) {
                return Stream.of(ModLoaderType.QUILT);
            }
            if ("neoforge".equalsIgnoreCase(version)) {
                return Stream.of(ModLoaderType.NEO_FORGED);
            }
            return Stream.empty();
        }
    }

    public static final class LatestFileIndex extends RecordTag {
        private final int fileId;
        private final String filename;
        private final String gameVersion;
        private final int gameVersionTypeId;
        private final int modLoader;
        private final int releaseType;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof LatestFileIndex)) {
                return false;
            }
            LatestFileIndex latestFileIndex = (LatestFileIndex) obj;
            return this.fileId == latestFileIndex.fileId && this.releaseType == latestFileIndex.releaseType && this.gameVersionTypeId == latestFileIndex.gameVersionTypeId && this.modLoader == latestFileIndex.modLoader && Objects.equals(this.gameVersion, latestFileIndex.gameVersion) && Objects.equals(this.filename, latestFileIndex.filename);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.gameVersion, Integer.valueOf(this.fileId), this.filename, Integer.valueOf(this.releaseType), Integer.valueOf(this.gameVersionTypeId), Integer.valueOf(this.modLoader)};
        }

        public LatestFileIndex(String gameVersion, int fileId, String filename, int releaseType, int gameVersionTypeId, int modLoader) {
            this.gameVersion = gameVersion;
            this.fileId = fileId;
            this.filename = filename;
            this.releaseType = releaseType;
            this.gameVersionTypeId = gameVersionTypeId;
            this.modLoader = modLoader;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public int fileId() {
            return this.fileId;
        }

        public String filename() {
            return this.filename;
        }

        public String gameVersion() {
            return this.gameVersion;
        }

        public final int hashCode() {
            return CurseAddon$LatestFileIndex$$ExternalSyntheticRecord0.m(this.fileId, this.releaseType, this.gameVersionTypeId, this.modLoader, this.gameVersion, this.filename);
        }

        public int modLoader() {
            return this.modLoader;
        }

        public int releaseType() {
            return this.releaseType;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), LatestFileIndex.class, "gameVersion;fileId;filename;releaseType;gameVersionTypeId;modLoader");
        }

        public int gameVersionTypeId() {
            return this.gameVersionTypeId;
        }
    }

    public static class Category {
        private final int classId;
        private final Instant dateModified;
        private final int gameId;
        private final String iconUrl;
        private final int id;
        private final boolean isClass;
        private final String name;
        private final int parentCategoryId;
        private final String slug;
        private final transient List<Category> subcategories;
        private final String url;

        public Category() {
            this(0, 0, "", "", "", "", Instant.now(), false, 0, 0);
        }

        public Category(int id, int gameId, String name, String slug, String url, String iconUrl, Instant dateModified, boolean isClass, int classId, int parentCategoryId) {
            this.id = id;
            this.gameId = gameId;
            this.name = name;
            this.slug = slug;
            this.url = url;
            this.iconUrl = iconUrl;
            this.dateModified = dateModified;
            this.isClass = isClass;
            this.classId = classId;
            this.parentCategoryId = parentCategoryId;
            this.subcategories = new ArrayList();
        }

        public int getId() {
            return this.id;
        }

        public int getGameId() {
            return this.gameId;
        }

        public String getName() {
            return this.name;
        }

        public String getSlug() {
            return this.slug;
        }

        public String getUrl() {
            return this.url;
        }

        public String getIconUrl() {
            return this.iconUrl;
        }

        public Instant getDateModified() {
            return this.dateModified;
        }

        public boolean isClass() {
            return this.isClass;
        }

        public int getClassId() {
            return this.classId;
        }

        public int getParentCategoryId() {
            return this.parentCategoryId;
        }

        public List<Category> getSubcategories() {
            return this.subcategories;
        }

        public RemoteModRepository.Category toCategory() {
            return new RemoteModRepository.Category(this, Integer.toString(this.id), (List) getSubcategories().stream().map(new CurseAddon$Category$$ExternalSyntheticLambda0()).collect(Collectors.toList()));
        }
    }

    public static class Screenshot {
        private final String description;
        private final int id;
        private final int modid;
        private final String thumbnailUrl;
        private final String title;
        private final String url;

        public Screenshot() {
            this.id = 0;
            this.modid = 0;
            this.title = "";
            this.description = "";
            this.thumbnailUrl = "";
            this.url = "";
        }

        public Screenshot(int id, int modid, String title, String description, String thumbnailUrl, String url) {
            this.id = id;
            this.modid = modid;
            this.title = title;
            this.description = description;
            this.thumbnailUrl = thumbnailUrl;
            this.url = url;
        }
    }
}
