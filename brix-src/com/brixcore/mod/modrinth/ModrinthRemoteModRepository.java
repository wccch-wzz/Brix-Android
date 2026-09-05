package com.brixcore.mod.modrinth;

import com.android.tools.r8.RecordTag;
import com.brixcore.auth.offline.Skin$$ExternalSyntheticRecord1;
import com.brixcore.auth.offline.Skin$LoadedSkin$$ExternalSyntheticRecord0;
import com.brixcore.auth.offline.YggdrasilServer$$ExternalSyntheticLambda2;
import com.brixcore.download.DefaultCacheRepository;
import com.brixcore.download.DownloadProvider;
import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.RemoteMod;
import com.brixcore.mod.RemoteModRepository;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.NetworkUtils;
import com.brixcore.util.io.ResponseCodeException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes14.dex */
public final class ModrinthRemoteModRepository implements RemoteModRepository {
    private static final String PREFIX = "https://api.modrinth.com";
    private final String projectType;
    public static final ModrinthRemoteModRepository MODS = new ModrinthRemoteModRepository("mod");
    public static final ModrinthRemoteModRepository MODPACKS = new ModrinthRemoteModRepository("modpack");
    public static final ModrinthRemoteModRepository RESOURCE_PACKS = new ModrinthRemoteModRepository("resourcepack");
    public static final ModrinthRemoteModRepository SHADER_PACKS = new ModrinthRemoteModRepository("shader");

    private ModrinthRemoteModRepository(String projectType) {
        this.projectType = projectType;
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteModRepository.Type getType() {
        return RemoteModRepository.Type.MOD;
    }

    private static String convertSortType(RemoteModRepository.SortType sortType) {
        switch (sortType) {
            case DATE_CREATED:
                return "newest";
            case POPULARITY:
            case NAME:
            case AUTHOR:
                return "relevance";
            case LAST_UPDATED:
                return "updated";
            case TOTAL_DOWNLOADS:
                return "downloads";
            default:
                throw new IllegalArgumentException("Unsupported sort type " + sortType);
        }
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteModRepository.SearchResult search(DownloadProvider downloadProvider, String gameVersion, RemoteModRepository.Category category, int pageOffset, int pageSize, String searchFilter, RemoteModRepository.SortType sort, RemoteModRepository.SortOrder sortOrder) throws IOException {
        ModrinthRemoteModRepository modrinthRemoteModRepository = this;
        List<List<String>> facets = new ArrayList<>();
        facets.add(Collections.singletonList("project_type:" + modrinthRemoteModRepository.projectType));
        if (StringUtils.isNotBlank(gameVersion)) {
            facets.add(Collections.singletonList("versions:" + gameVersion));
        }
        if (category != null && StringUtils.isNotBlank(category.id())) {
            facets.add(Collections.singletonList("categories:" + category.id()));
        }
        Map<String, String> query = Lang.mapOf(Pair.pair("query", searchFilter), Pair.pair("facets", JsonUtils.UGLY_GSON.toJson(facets)), Pair.pair("offset", Integer.toString(pageOffset * pageSize)), Pair.pair("limit", Integer.toString(pageSize)), Pair.pair("index", convertSortType(sort)));
        List<URL> candidates = downloadProvider.injectURLWithCandidates(NetworkUtils.withQuery("https://api.modrinth.com/v2/search", query));
        IOException exception = null;
        for (URL candidate : candidates) {
            try {
                Logging.LOG.info("Fetching " + candidate);
                Response<ProjectSearchResult> response = (Response) HttpRequest.GET(candidate.toString()).getJson(new TypeToken<Response<ProjectSearchResult>>() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository.1
                }.getType());
                return new RemoteModRepository.SearchResult(response.getHits().stream().map(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$$ExternalSyntheticLambda3
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((ModrinthRemoteModRepository.ProjectSearchResult) obj).toMod();
                    }
                }), (int) Math.ceil(((double) ((Response) response).totalHits) / ((double) pageSize)));
            } catch (IOException e) {
                Logging.LOG.warning("Failed to search addons: " + candidate + org.apache.commons.lang3.StringUtils.LF + e);
                if (candidates.size() == 1) {
                    exception = e;
                } else {
                    if (exception == null) {
                        exception = new IOException("Failed to search addons");
                    }
                    exception.addSuppressed(e);
                }
                modrinthRemoteModRepository = this;
            }
        }
        if (exception != null) {
            throw exception;
        }
        throw new IOException("No candidates found");
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public Optional<RemoteMod.Version> getRemoteVersionByLocalFile(LocalModFile localModFile, Path file) throws IOException {
        String sha1 = DigestUtils.digestToString(CacheRepository.SHA1, file);
        try {
            ProjectVersion mod = (ProjectVersion) HttpRequest.GET("https://api.modrinth.com/v2/version_file/" + sha1, Pair.pair("algorithm", "sha1")).getJson(ProjectVersion.class);
            return mod.toVersion();
        } catch (ResponseCodeException e) {
            if (e.getResponseCode() == 404) {
                return Optional.empty();
            }
            throw e;
        }
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteMod getModById(String id) throws IOException {
        Project project = (Project) HttpRequest.GET("https://api.modrinth.com/v2/project/" + StringUtils.removePrefix(id, "local-")).getJson(Project.class);
        return project.toMod();
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteMod.File getModFile(String modId, String fileId) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public Stream<RemoteMod.Version> getRemoteVersionsById(String id) throws IOException {
        List<ProjectVersion> versions = (List) HttpRequest.GET("https://api.modrinth.com/v2/project/" + StringUtils.removePrefix(id, "local-") + "/version").getJson(new TypeToken<List<ProjectVersion>>() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository.2
        }.getType());
        return versions.stream().map(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ModrinthRemoteModRepository.ProjectVersion) obj).toVersion();
            }
        }).flatMap(new YggdrasilServer$$ExternalSyntheticLambda2());
    }

    public List<Category> getCategoriesImpl() throws IOException {
        List<Category> categories = (List) HttpRequest.GET("https://api.modrinth.com/v2/tag/category").getJson(new TypeToken<List<Category>>() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository.3
        }.getType());
        return (List) categories.stream().filter(new Predicate() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$getCategoriesImpl$0((ModrinthRemoteModRepository.Category) obj);
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getCategoriesImpl$0(Category category) {
        return category.projectType().equals(this.projectType);
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public Stream<RemoteModRepository.Category> getCategories() throws IOException {
        return getCategoriesImpl().stream().map(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ModrinthRemoteModRepository.Category) obj).toCategory();
            }
        });
    }

    public static final class Category extends RecordTag {
        private final String icon;
        private final String name;

        @SerializedName("project_type")
        private final String projectType;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Category)) {
                return false;
            }
            Category category = (Category) obj;
            return Objects.equals(this.icon, category.icon) && Objects.equals(this.name, category.name) && Objects.equals(this.projectType, category.projectType);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.icon, this.name, this.projectType};
        }

        public Category(String icon, String name, String projectType) {
            this.icon = icon;
            this.name = name;
            this.projectType = projectType;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return Skin$LoadedSkin$$ExternalSyntheticRecord0.m(this.icon, this.name, this.projectType);
        }

        public String icon() {
            return this.icon;
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Category.class, "icon;name;projectType");
        }

        public Category() {
            this("", "", "");
        }

        public String projectType() {
            return this.projectType;
        }

        public RemoteModRepository.Category toCategory() {
            return new RemoteModRepository.Category(this, this.name, Collections.emptyList());
        }
    }

    public static final class Project extends RecordTag implements RemoteMod.IMod {
        private final String body;
        private final List<String> categories;
        private final String description;
        private final int downloads;

        @SerializedName("icon_url")
        private final String iconUrl;
        private final String id;

        @SerializedName("project_type")
        private final String projectType;
        private final Instant published;

        @SerializedName("gallery")
        private final List<Screenshot> screenshots;
        private final String slug;
        private final String team;
        private final String title;
        private final Instant updated;
        private final List<String> versions;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Project)) {
                return false;
            }
            Project project = (Project) obj;
            return this.downloads == project.downloads && Objects.equals(this.slug, project.slug) && Objects.equals(this.title, project.title) && Objects.equals(this.description, project.description) && Objects.equals(this.categories, project.categories) && Objects.equals(this.body, project.body) && Objects.equals(this.projectType, project.projectType) && Objects.equals(this.iconUrl, project.iconUrl) && Objects.equals(this.id, project.id) && Objects.equals(this.team, project.team) && Objects.equals(this.published, project.published) && Objects.equals(this.updated, project.updated) && Objects.equals(this.versions, project.versions) && Objects.equals(this.screenshots, project.screenshots);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.slug, this.title, this.description, this.categories, this.body, this.projectType, Integer.valueOf(this.downloads), this.iconUrl, this.id, this.team, this.published, this.updated, this.versions, this.screenshots};
        }

        public Project(String slug, String title, String description, List<String> categories, String body, String projectType, int downloads, String iconUrl, String id, String team, Instant published, Instant updated, List<String> versions, List<Screenshot> screenshots) {
            this.slug = slug;
            this.title = title;
            this.description = description;
            this.categories = categories;
            this.body = body;
            this.projectType = projectType;
            this.downloads = downloads;
            this.iconUrl = iconUrl;
            this.id = id;
            this.team = team;
            this.published = published;
            this.updated = updated;
            this.versions = versions;
            this.screenshots = screenshots;
        }

        public String body() {
            return this.body;
        }

        public List<String> categories() {
            return this.categories;
        }

        public String description() {
            return this.description;
        }

        public int downloads() {
            return this.downloads;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return ModrinthRemoteModRepository$Project$$ExternalSyntheticRecord0.m(this.downloads, this.slug, this.title, this.description, this.categories, this.body, this.projectType, this.iconUrl, this.id, this.team, this.published, this.updated, this.versions, this.screenshots);
        }

        public String id() {
            return this.id;
        }

        public Instant published() {
            return this.published;
        }

        public String slug() {
            return this.slug;
        }

        public String team() {
            return this.team;
        }

        public String title() {
            return this.title;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Project.class, "slug;title;description;categories;body;projectType;downloads;iconUrl;id;team;published;updated;versions;screenshots");
        }

        public Instant updated() {
            return this.updated;
        }

        public List<String> versions() {
            return this.versions;
        }

        public String projectType() {
            return this.projectType;
        }

        public String iconUrl() {
            return this.iconUrl;
        }

        public List<Screenshot> screenshots() {
            return this.screenshots;
        }

        @Override // com.brixcore.mod.RemoteMod.IMod
        public List<RemoteMod> loadDependencies(RemoteModRepository modRepository) throws IOException {
            Set<RemoteMod.Dependency> dependencies = (Set) modRepository.getRemoteVersionsById(id()).flatMap(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$Project$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((RemoteMod.Version) obj).getDependencies().stream();
                }
            }).collect(Collectors.toSet());
            List<RemoteMod> mods = new ArrayList<>();
            for (RemoteMod.Dependency dependency : dependencies) {
                mods.add(dependency.load());
            }
            return mods;
        }

        @Override // com.brixcore.mod.RemoteMod.IMod
        public Stream<RemoteMod.Version> loadVersions(RemoteModRepository modRepository) throws IOException {
            return modRepository.getRemoteVersionsById(id());
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
            return new RemoteMod(this.slug, "", this.title, this.description, this.categories, String.format("https://modrinth.com/%s/%s", this.projectType, this.id), this.iconUrl, this, this.downloads, this.id);
        }
    }

    public static final class Dependency extends RecordTag {

        @SerializedName("dependency_type")
        private final String dependencyType;

        @SerializedName("project_id")
        private final String projectId;

        @SerializedName("version_id")
        private final String versionId;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Dependency)) {
                return false;
            }
            Dependency dependency = (Dependency) obj;
            return Objects.equals(this.versionId, dependency.versionId) && Objects.equals(this.projectId, dependency.projectId) && Objects.equals(this.dependencyType, dependency.dependencyType);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.versionId, this.projectId, this.dependencyType};
        }

        public Dependency(String versionId, String projectId, String dependencyType) {
            this.versionId = versionId;
            this.projectId = projectId;
            this.dependencyType = dependencyType;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return Skin$LoadedSkin$$ExternalSyntheticRecord0.m(this.versionId, this.projectId, this.dependencyType);
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Dependency.class, "versionId;projectId;dependencyType");
        }

        public String versionId() {
            return this.versionId;
        }

        public String projectId() {
            return this.projectId;
        }

        public String dependencyType() {
            return this.dependencyType;
        }
    }

    public static final class ProjectVersion extends RecordTag implements RemoteMod.IVersion {
        private static final Map<String, RemoteMod.DependencyType> DEPENDENCY_TYPE = Lang.mapOf(Pair.pair("required", RemoteMod.DependencyType.REQUIRED), Pair.pair("optional", RemoteMod.DependencyType.OPTIONAL), Pair.pair("embedded", RemoteMod.DependencyType.EMBEDDED), Pair.pair("incompatible", RemoteMod.DependencyType.INCOMPATIBLE));

        @SerializedName("author_id")
        private final String authorId;
        private final String changelog;

        @SerializedName("changelog_url")
        private final String changelogUrl;

        @SerializedName("date_published")
        private final Instant datePublished;
        private final List<Dependency> dependencies;
        private final int downloads;
        private final boolean featured;
        private final List<ProjectVersionFile> files;

        @SerializedName("game_versions")
        private final List<String> gameVersions;
        private final String id;
        private final List<String> loaders;
        private final String name;

        @SerializedName("project_id")
        private final String projectId;

        @SerializedName("version_number")
        private final String versionNumber;

        @SerializedName("version_type")
        private final String versionType;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof ProjectVersion)) {
                return false;
            }
            ProjectVersion projectVersion = (ProjectVersion) obj;
            return this.featured == projectVersion.featured && this.downloads == projectVersion.downloads && Objects.equals(this.name, projectVersion.name) && Objects.equals(this.versionNumber, projectVersion.versionNumber) && Objects.equals(this.changelog, projectVersion.changelog) && Objects.equals(this.dependencies, projectVersion.dependencies) && Objects.equals(this.gameVersions, projectVersion.gameVersions) && Objects.equals(this.versionType, projectVersion.versionType) && Objects.equals(this.loaders, projectVersion.loaders) && Objects.equals(this.id, projectVersion.id) && Objects.equals(this.projectId, projectVersion.projectId) && Objects.equals(this.authorId, projectVersion.authorId) && Objects.equals(this.datePublished, projectVersion.datePublished) && Objects.equals(this.changelogUrl, projectVersion.changelogUrl) && Objects.equals(this.files, projectVersion.files);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, this.versionNumber, this.changelog, this.dependencies, this.gameVersions, this.versionType, this.loaders, Boolean.valueOf(this.featured), this.id, this.projectId, this.authorId, this.datePublished, Integer.valueOf(this.downloads), this.changelogUrl, this.files};
        }

        public ProjectVersion(String name, String versionNumber, String changelog, List<Dependency> dependencies, List<String> gameVersions, String versionType, List<String> loaders, boolean featured, String id, String projectId, String authorId, Instant datePublished, int downloads, String changelogUrl, List<ProjectVersionFile> files) {
            this.name = name;
            this.versionNumber = versionNumber;
            this.changelog = changelog;
            this.dependencies = dependencies;
            this.gameVersions = gameVersions;
            this.versionType = versionType;
            this.loaders = loaders;
            this.featured = featured;
            this.id = id;
            this.projectId = projectId;
            this.authorId = authorId;
            this.datePublished = datePublished;
            this.downloads = downloads;
            this.changelogUrl = changelogUrl;
            this.files = files;
        }

        public String changelog() {
            return this.changelog;
        }

        public List<Dependency> dependencies() {
            return this.dependencies;
        }

        public int downloads() {
            return this.downloads;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public boolean featured() {
            return this.featured;
        }

        public List<ProjectVersionFile> files() {
            return this.files;
        }

        public final int hashCode() {
            return ModrinthRemoteModRepository$ProjectVersion$$ExternalSyntheticRecord0.m(this.featured, this.downloads, this.name, this.versionNumber, this.changelog, this.dependencies, this.gameVersions, this.versionType, this.loaders, this.id, this.projectId, this.authorId, this.datePublished, this.changelogUrl, this.files);
        }

        public String id() {
            return this.id;
        }

        public List<String> loaders() {
            return this.loaders;
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ProjectVersion.class, "name;versionNumber;changelog;dependencies;gameVersions;versionType;loaders;featured;id;projectId;authorId;datePublished;downloads;changelogUrl;files");
        }

        public String versionNumber() {
            return this.versionNumber;
        }

        public List<String> gameVersions() {
            return this.gameVersions;
        }

        public String versionType() {
            return this.versionType;
        }

        public String projectId() {
            return this.projectId;
        }

        public String authorId() {
            return this.authorId;
        }

        public Instant datePublished() {
            return this.datePublished;
        }

        public String changelogUrl() {
            return this.changelogUrl;
        }

        @Override // com.brixcore.mod.RemoteMod.IVersion
        public RemoteMod.Type getType() {
            return RemoteMod.Type.MODRINTH;
        }

        public Optional<RemoteMod.Version> toVersion() {
            RemoteMod.VersionType type;
            if ("release".equals(this.versionType)) {
                type = RemoteMod.VersionType.Release;
            } else if ("beta".equals(this.versionType)) {
                type = RemoteMod.VersionType.Beta;
            } else if ("alpha".equals(this.versionType)) {
                type = RemoteMod.VersionType.Alpha;
            } else {
                RemoteMod.VersionType type2 = RemoteMod.VersionType.Release;
                type = type2;
            }
            if (this.files.size() == 0) {
                return Optional.empty();
            }
            return Optional.of(new RemoteMod.Version(this, this.projectId, this.name, this.versionNumber, this.changelog, this.datePublished, type, this.files.get(0).toFile(), (List) this.dependencies.stream().map(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$ProjectVersion$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ModrinthRemoteModRepository.ProjectVersion.lambda$toVersion$0((ModrinthRemoteModRepository.Dependency) obj);
                }
            }).filter(new Predicate() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$ProjectVersion$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((RemoteMod.Dependency) obj);
                }
            }).collect(Collectors.toList()), this.gameVersions, (List) this.loaders.stream().flatMap(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$ProjectVersion$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ModrinthRemoteModRepository.ProjectVersion.lambda$toVersion$1((String) obj);
                }
            }).collect(Collectors.toList())));
        }

        static /* synthetic */ RemoteMod.Dependency lambda$toVersion$0(Dependency dependency) {
            if (dependency.projectId == null) {
                return RemoteMod.Dependency.ofBroken();
            }
            if (!DEPENDENCY_TYPE.containsKey(dependency.dependencyType)) {
                throw new IllegalStateException("Broken datas");
            }
            return RemoteMod.Dependency.ofGeneral(DEPENDENCY_TYPE.get(dependency.dependencyType), ModrinthRemoteModRepository.MODS, dependency.projectId);
        }

        static /* synthetic */ Stream lambda$toVersion$1(String loader) {
            if ("fabric".equalsIgnoreCase(loader)) {
                return Stream.of(ModLoaderType.FABRIC);
            }
            if (DefaultCacheRepository.LibraryIndex.TYPE_FORGE.equalsIgnoreCase(loader)) {
                return Stream.of(ModLoaderType.FORGE);
            }
            if ("neoforge".equalsIgnoreCase(loader)) {
                return Stream.of(ModLoaderType.NEO_FORGED);
            }
            if ("quilt".equalsIgnoreCase(loader)) {
                return Stream.of(ModLoaderType.QUILT);
            }
            if ("liteloader".equalsIgnoreCase(loader)) {
                return Stream.of(ModLoaderType.LITE_LOADER);
            }
            return Stream.empty();
        }
    }

    public static final class ProjectVersionFile extends RecordTag {
        private final String filename;
        private final Map<String, String> hashes;
        private final boolean primary;
        private final int size;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof ProjectVersionFile)) {
                return false;
            }
            ProjectVersionFile projectVersionFile = (ProjectVersionFile) obj;
            return this.primary == projectVersionFile.primary && this.size == projectVersionFile.size && Objects.equals(this.hashes, projectVersionFile.hashes) && Objects.equals(this.url, projectVersionFile.url) && Objects.equals(this.filename, projectVersionFile.filename);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.hashes, this.url, this.filename, Boolean.valueOf(this.primary), Integer.valueOf(this.size)};
        }

        public ProjectVersionFile(Map<String, String> hashes, String url, String filename, boolean primary, int size) {
            this.hashes = hashes;
            this.url = url;
            this.filename = filename;
            this.primary = primary;
            this.size = size;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public String filename() {
            return this.filename;
        }

        public final int hashCode() {
            return ModrinthRemoteModRepository$ProjectVersionFile$$ExternalSyntheticRecord0.m(this.primary, this.size, this.hashes, this.url, this.filename);
        }

        public Map<String, String> hashes() {
            return this.hashes;
        }

        public boolean primary() {
            return this.primary;
        }

        public int size() {
            return this.size;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ProjectVersionFile.class, "hashes;url;filename;primary;size");
        }

        public String url() {
            return this.url;
        }

        public RemoteMod.File toFile() {
            return new RemoteMod.File(this.hashes, this.url, this.filename);
        }
    }

    public static final class ProjectSearchResult extends RecordTag implements RemoteMod.IMod {
        private final String author;
        private final List<String> categories;

        @SerializedName("date_created")
        private final Instant dateCreated;

        @SerializedName("date_modified")
        private final Instant dateModified;
        private final String description;
        private final int downloads;

        @SerializedName("icon_url")
        private final String iconUrl;

        @SerializedName("latest_version")
        private final String latestVersion;

        @SerializedName("project_id")
        private final String projectId;

        @SerializedName("project_type")
        private final String projectType;
        private final String slug;
        private final String title;
        private final List<String> versions;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof ProjectSearchResult)) {
                return false;
            }
            ProjectSearchResult projectSearchResult = (ProjectSearchResult) obj;
            return this.downloads == projectSearchResult.downloads && Objects.equals(this.slug, projectSearchResult.slug) && Objects.equals(this.title, projectSearchResult.title) && Objects.equals(this.description, projectSearchResult.description) && Objects.equals(this.categories, projectSearchResult.categories) && Objects.equals(this.projectType, projectSearchResult.projectType) && Objects.equals(this.iconUrl, projectSearchResult.iconUrl) && Objects.equals(this.projectId, projectSearchResult.projectId) && Objects.equals(this.author, projectSearchResult.author) && Objects.equals(this.versions, projectSearchResult.versions) && Objects.equals(this.dateCreated, projectSearchResult.dateCreated) && Objects.equals(this.dateModified, projectSearchResult.dateModified) && Objects.equals(this.latestVersion, projectSearchResult.latestVersion);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.slug, this.title, this.description, this.categories, this.projectType, Integer.valueOf(this.downloads), this.iconUrl, this.projectId, this.author, this.versions, this.dateCreated, this.dateModified, this.latestVersion};
        }

        public ProjectSearchResult(String slug, String title, String description, List<String> categories, String projectType, int downloads, String iconUrl, String projectId, String author, List<String> versions, Instant dateCreated, Instant dateModified, String latestVersion) {
            this.slug = slug;
            this.title = title;
            this.description = description;
            this.categories = categories;
            this.projectType = projectType;
            this.downloads = downloads;
            this.iconUrl = iconUrl;
            this.projectId = projectId;
            this.author = author;
            this.versions = versions;
            this.dateCreated = dateCreated;
            this.dateModified = dateModified;
            this.latestVersion = latestVersion;
        }

        public String author() {
            return this.author;
        }

        public List<String> categories() {
            return this.categories;
        }

        public String description() {
            return this.description;
        }

        public int downloads() {
            return this.downloads;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return ModrinthRemoteModRepository$ProjectSearchResult$$ExternalSyntheticRecord0.m(this.downloads, this.slug, this.title, this.description, this.categories, this.projectType, this.iconUrl, this.projectId, this.author, this.versions, this.dateCreated, this.dateModified, this.latestVersion);
        }

        public String slug() {
            return this.slug;
        }

        public String title() {
            return this.title;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ProjectSearchResult.class, "slug;title;description;categories;projectType;downloads;iconUrl;projectId;author;versions;dateCreated;dateModified;latestVersion");
        }

        public List<String> versions() {
            return this.versions;
        }

        public String projectType() {
            return this.projectType;
        }

        public String iconUrl() {
            return this.iconUrl;
        }

        public String projectId() {
            return this.projectId;
        }

        public Instant dateCreated() {
            return this.dateCreated;
        }

        public Instant dateModified() {
            return this.dateModified;
        }

        public String latestVersion() {
            return this.latestVersion;
        }

        @Override // com.brixcore.mod.RemoteMod.IMod
        public List<RemoteMod> loadDependencies(RemoteModRepository modRepository) throws IOException {
            Set<RemoteMod.Dependency> dependencies = (Set) modRepository.getRemoteVersionsById(projectId()).flatMap(new Function() { // from class: com.brixcore.mod.modrinth.ModrinthRemoteModRepository$ProjectSearchResult$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((RemoteMod.Version) obj).getDependencies().stream();
                }
            }).collect(Collectors.toSet());
            List<RemoteMod> mods = new ArrayList<>();
            for (RemoteMod.Dependency dependency : dependencies) {
                mods.add(dependency.load());
            }
            return mods;
        }

        @Override // com.brixcore.mod.RemoteMod.IMod
        public Stream<RemoteMod.Version> loadVersions(RemoteModRepository modRepository) throws IOException {
            return modRepository.getRemoteVersionsById(projectId());
        }

        @Override // com.brixcore.mod.RemoteMod.IMod
        public List<RemoteMod.Screenshot> loadScreenshots(RemoteModRepository modRepository) throws IOException {
            return modRepository.getModById(projectId()).getData().loadScreenshots(modRepository);
        }

        public RemoteMod toMod() {
            return new RemoteMod(this.slug, this.author, this.title, this.description, this.categories, String.format("https://modrinth.com/%s/%s", this.projectType, this.projectId), this.iconUrl, this, this.downloads, this.projectId);
        }
    }

    public static class Response<T> {
        private final List<T> hits;
        private final int limit;
        private final int offset;

        @SerializedName("total_hits")
        private final int totalHits;

        public Response() {
            this(0, 0, Collections.emptyList());
        }

        public Response(int offset, int limit, List<T> hits) {
            this.offset = offset;
            this.limit = limit;
            this.totalHits = hits.size();
            this.hits = hits;
        }

        public int getOffset() {
            return this.offset;
        }

        public int getLimit() {
            return this.limit;
        }

        public int getTotalHits() {
            return this.totalHits;
        }

        public List<T> getHits() {
            return this.hits;
        }
    }

    public static final class Screenshot extends RecordTag {
        private final Instant created;
        private final String description;
        private final boolean featured;
        private final int ordering;

        @SerializedName("raw_url")
        private final String rawUrl;
        private final String title;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Screenshot)) {
                return false;
            }
            Screenshot screenshot = (Screenshot) obj;
            return this.featured == screenshot.featured && this.ordering == screenshot.ordering && Objects.equals(this.url, screenshot.url) && Objects.equals(this.rawUrl, screenshot.rawUrl) && Objects.equals(this.title, screenshot.title) && Objects.equals(this.description, screenshot.description) && Objects.equals(this.created, screenshot.created);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.url, this.rawUrl, Boolean.valueOf(this.featured), this.title, this.description, this.created, Integer.valueOf(this.ordering)};
        }

        public Screenshot(String url, String rawUrl, boolean featured, String title, String description, Instant created, int ordering) {
            this.url = url;
            this.rawUrl = rawUrl;
            this.featured = featured;
            this.title = title;
            this.description = description;
            this.created = created;
            this.ordering = ordering;
        }

        public Instant created() {
            return this.created;
        }

        public String description() {
            return this.description;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public boolean featured() {
            return this.featured;
        }

        public final int hashCode() {
            return ModrinthRemoteModRepository$Screenshot$$ExternalSyntheticRecord0.m(this.featured, this.ordering, this.url, this.rawUrl, this.title, this.description, this.created);
        }

        public int ordering() {
            return this.ordering;
        }

        public String title() {
            return this.title;
        }

        public final String toString() {
            return Skin$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Screenshot.class, "url;rawUrl;featured;title;description;created;ordering");
        }

        public String url() {
            return this.url;
        }

        public String rawUrl() {
            return this.rawUrl;
        }
    }
}
