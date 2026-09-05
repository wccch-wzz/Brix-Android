package com.brixcore.mod.curse;

import com.android.tools.r8.RecordTag;
import com.brixcore.R;
import com.brixcore.download.DownloadProvider;
import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.RemoteMod;
import com.brixcore.mod.RemoteModRepository;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.MurmurHash2;
import com.brixcore.util.Pair;
import com.brixcore.util.io.HttpRequest;
import com.brixcore.util.io.NetworkUtils;
import com.brixcore.utils.BrixPath;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes10.dex */
public final class CurseForgeRemoteModRepository implements RemoteModRepository {
    private static final String PREFIX = "https://api.curseforge.com";
    public static final int SECTION_ADDONS = 4559;
    public static final int SECTION_BUKKIT_PLUGIN = 5;
    public static final int SECTION_MOD = 6;
    public static final int SECTION_RESOURCE_PACK = 12;
    public static final int SECTION_UNKNOWN1 = 4944;
    public static final int SECTION_UNKNOWN2 = 4979;
    public static final int SECTION_UNKNOWN3 = 4984;
    public static final int SECTION_WORLD = 17;
    private static final int WORD_PERFECT_MATCH_WEIGHT = 5;
    private final int section;
    private final RemoteModRepository.Type type;
    private static final String apiKey = BrixPath.CONTEXT.getString(R.string.curse_api_key);
    public static final CurseForgeRemoteModRepository MODS = new CurseForgeRemoteModRepository(RemoteModRepository.Type.MOD, 6);
    public static final int SECTION_MODPACK = 4471;
    public static final CurseForgeRemoteModRepository MODPACKS = new CurseForgeRemoteModRepository(RemoteModRepository.Type.MODPACK, SECTION_MODPACK);
    public static final CurseForgeRemoteModRepository RESOURCE_PACKS = new CurseForgeRemoteModRepository(RemoteModRepository.Type.RESOURCE_PACK, 12);
    public static final CurseForgeRemoteModRepository WORLDS = new CurseForgeRemoteModRepository(RemoteModRepository.Type.WORLD, 17);
    public static final int SECTION_CUSTOMIZATION = 4546;
    public static final CurseForgeRemoteModRepository CUSTOMIZATIONS = new CurseForgeRemoteModRepository(RemoteModRepository.Type.CUSTOMIZATION, SECTION_CUSTOMIZATION);
    public static final int SECTION_SHADER_PACK = 6552;
    public static final CurseForgeRemoteModRepository SHADER_PACKS = new CurseForgeRemoteModRepository(RemoteModRepository.Type.SHADER_PACK, SECTION_SHADER_PACK);

    private static <R extends HttpRequest> R withApiKey(R request) {
        if (request.getUrl().startsWith(PREFIX) && !apiKey.isEmpty()) {
            request.header("X-API-KEY", apiKey);
        }
        return request;
    }

    public static boolean isAvailable() {
        return !apiKey.equals("null");
    }

    public CurseForgeRemoteModRepository(RemoteModRepository.Type type, int section) {
        this.type = type;
        this.section = section;
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteModRepository.Type getType() {
        return this.type;
    }

    private int toModsSearchSortField(RemoteModRepository.SortType sort) {
        switch (sort) {
            case DATE_CREATED:
                return 1;
            case POPULARITY:
                return 2;
            case LAST_UPDATED:
                return 3;
            case NAME:
                return 4;
            case AUTHOR:
                return 5;
            case TOTAL_DOWNLOADS:
                return 6;
            default:
                return 8;
        }
    }

    private String toSortOrder(RemoteModRepository.SortOrder sortOrder) {
        switch (sortOrder) {
            case ASC:
                return "asc";
            case DESC:
                return "desc";
            default:
                return "asc";
        }
    }

    private int calculateTotalPages(Response<List<CurseAddon>> response, int pageSize) {
        return (int) Math.ceil(((double) Math.min(((Response) response).pagination.totalCount, 10000)) / ((double) pageSize));
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteModRepository.SearchResult search(DownloadProvider downloadProvider, String gameVersion, RemoteModRepository.Category category, int pageOffset, int pageSize, String searchFilter, RemoteModRepository.SortType sortType, RemoteModRepository.SortOrder sortOrder) throws IOException {
        int categoryId = category != null ? ((CurseAddon.Category) category.self()).getId() : 0;
        LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("gameId", "432");
        query.put("classId", Integer.toString(this.section));
        if (categoryId != 0) {
            query.put("categoryId", Integer.toString(categoryId));
        }
        query.put("gameVersion", gameVersion);
        query.put("searchFilter", searchFilter);
        query.put("sortField", Integer.toString(toModsSearchSortField(sortType)));
        query.put("sortOrder", toSortOrder(sortOrder));
        query.put("index", Integer.toString(pageOffset * pageSize));
        query.put("pageSize", Integer.toString(pageSize));
        List<URL> candidates = downloadProvider.injectURLWithCandidates(NetworkUtils.withQuery("https://api.curseforge.com/v1/mods/search", query));
        IOException exception = null;
        Response<List<CurseAddon>> response = null;
        for (URL candidate : candidates) {
            int categoryId2 = categoryId;
            Logging.LOG.info("Fetching " + candidate);
            try {
                response = (Response) ((HttpRequest.HttpGetRequest) withApiKey(HttpRequest.GET(candidate.toString()))).getJson(new TypeToken<Response<List<CurseAddon>>>() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository.1
                }.getType());
                if (!searchFilter.isEmpty()) {
                    break;
                }
                return new RemoteModRepository.SearchResult(response.data().stream().map(new Function() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda1
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((CurseAddon) obj).toMod();
                    }
                }), calculateTotalPages(response, pageSize));
            } catch (IOException e) {
                LinkedHashMap<String, String> query2 = query;
                Logging.LOG.warning("Failed to search addons: " + candidate + StringUtils.LF + e);
                if (candidates.size() == 1) {
                    exception = e;
                } else {
                    if (exception == null) {
                        exception = new IOException("Failed to search addons");
                    }
                    exception.addSuppressed(e);
                }
                categoryId = categoryId2;
                query = query2;
            }
        }
        if (response == null) {
            if (exception != null) {
                throw exception;
            }
            throw new IOException("No candidates found");
        }
        final String lowerCaseSearchFilter = searchFilter.toLowerCase(Locale.ROOT);
        final Map<String, Integer> searchFilterWords = new HashMap<>();
        for (String s : com.brixcore.util.StringUtils.tokenize(lowerCaseSearchFilter)) {
            searchFilterWords.merge(s, 1, new BiFunction() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda2
                @Override // java.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    return Integer.valueOf(Integer.sum(((Integer) obj).intValue(), ((Integer) obj2).intValue()));
                }
            });
        }
        final com.brixcore.util.StringUtils.LevCalculator levCalculator = new com.brixcore.util.StringUtils.LevCalculator();
        return new RemoteModRepository.SearchResult(response.data().stream().map(new Function() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CurseAddon) obj).toMod();
            }
        }).map(new Function() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return CurseForgeRemoteModRepository.lambda$search$0(levCalculator, lowerCaseSearchFilter, searchFilterWords, (RemoteMod) obj);
            }
        }).sorted(Comparator.comparingInt(new ToIntFunction() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda4
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((Integer) ((Pair) obj).getValue()).intValue();
            }
        })).map(new Function() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (RemoteMod) ((Pair) obj).getKey();
            }
        }), response.data().stream().map(new Function() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CurseAddon) obj).toMod();
            }
        }), calculateTotalPages(response, pageSize));
    }

    static /* synthetic */ Pair lambda$search$0(com.brixcore.util.StringUtils.LevCalculator levCalculator, String lowerCaseSearchFilter, Map searchFilterWords, RemoteMod remoteMod) {
        String lowerCaseResult = remoteMod.getTitle().toLowerCase();
        int diff = levCalculator.calc(lowerCaseSearchFilter, lowerCaseResult);
        for (String s : com.brixcore.util.StringUtils.tokenize(lowerCaseResult)) {
            if (searchFilterWords.containsKey(s)) {
                diff -= (((Integer) searchFilterWords.get(s)).intValue() * 5) * s.length();
            }
        }
        return Pair.pair(remoteMod, Integer.valueOf(diff));
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public Optional<RemoteMod.Version> getRemoteVersionByLocalFile(LocalModFile localModFile, Path file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream stream = Files.newInputStream(file, new OpenOption[0]);
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int len = stream.read(buf, 0, buf.length);
                if (len == -1) {
                    break;
                }
                for (int i = 0; i < len; i++) {
                    byte b = buf[i];
                    if (b != 9 && b != 10 && b != 13 && b != 32) {
                        baos.write(b);
                    }
                }
            }
            if (stream != null) {
                stream.close();
            }
            long hash = Integer.toUnsignedLong(MurmurHash2.hash32(baos.toByteArray(), baos.size(), 1));
            Response<FingerprintMatchesResult> response = (Response) ((HttpRequest.HttpPostRequest) withApiKey(HttpRequest.POST("https://api.curseforge.com/v1/fingerprints/432"))).json(Lang.mapOf(Pair.pair("fingerprints", Collections.singletonList(Long.valueOf(hash))))).getJson(new TypeToken<Response<FingerprintMatchesResult>>() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository.2
            }.getType());
            if (response.data().exactMatches() != null && !response.data().exactMatches().isEmpty()) {
                return Optional.of(response.data().exactMatches().get(0).file().toVersion());
            }
            return Optional.empty();
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteMod getModById(String id) throws IOException {
        Response<CurseAddon> response = (Response) ((HttpRequest.HttpGetRequest) withApiKey(HttpRequest.GET("https://api.curseforge.com/v1/mods/" + id))).getJson(new TypeToken<Response<CurseAddon>>() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository.3
        }.getType());
        return ((CurseAddon) ((Response) response).data).toMod();
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public RemoteMod.File getModFile(String modId, String fileId) throws IOException {
        Response<CurseAddon.LatestFile> response = (Response) ((HttpRequest.HttpGetRequest) withApiKey(HttpRequest.GET(String.format("%s/v1/mods/%s/files/%s", PREFIX, modId, fileId)))).getJson(new TypeToken<Response<CurseAddon.LatestFile>>() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository.4
        }.getType());
        return response.data().toVersion().getFile();
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public Stream<RemoteMod.Version> getRemoteVersionsById(String id) throws IOException {
        Response<List<CurseAddon.LatestFile>> response = (Response) ((HttpRequest.HttpGetRequest) withApiKey(HttpRequest.GET("https://api.curseforge.com/v1/mods/" + id + "/files", Pair.pair("pageSize", "10000")))).getJson(new TypeToken<Response<List<CurseAddon.LatestFile>>>() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository.5
        }.getType());
        return response.data().stream().map(new Function() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CurseAddon.LatestFile) obj).toVersion();
            }
        });
    }

    public List<CurseAddon.Category> getCategoriesImpl() throws IOException {
        Response<List<CurseAddon.Category>> categories = (Response) ((HttpRequest.HttpGetRequest) withApiKey(HttpRequest.GET("https://api.curseforge.com/v1/categories", Pair.pair("gameId", "432")))).getJson(new TypeToken<Response<List<CurseAddon.Category>>>() { // from class: com.brixcore.mod.curse.CurseForgeRemoteModRepository.6
        }.getType());
        return reorganizeCategories(categories.data(), this.section);
    }

    @Override // com.brixcore.mod.RemoteModRepository
    public Stream<RemoteModRepository.Category> getCategories() throws IOException {
        return getCategoriesImpl().stream().map(new CurseAddon$Category$$ExternalSyntheticLambda0());
    }

    private List<CurseAddon.Category> reorganizeCategories(List<CurseAddon.Category> categories, int rootId) {
        List<CurseAddon.Category> result = new ArrayList<>();
        Map<Integer, CurseAddon.Category> categoryMap = new HashMap<>();
        for (CurseAddon.Category category : categories) {
            categoryMap.put(Integer.valueOf(category.getId()), category);
        }
        for (CurseAddon.Category category2 : categories) {
            if (category2.getParentCategoryId() == rootId) {
                result.add(category2);
            } else {
                CurseAddon.Category parentCategory = categoryMap.get(Integer.valueOf(category2.getParentCategoryId()));
                if (parentCategory != null) {
                    parentCategory.getSubcategories().add(category2);
                }
            }
        }
        return result;
    }

    public static final class Pagination extends RecordTag {
        private final int index;
        private final int pageSize;
        private final int resultCount;
        private final int totalCount;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Pagination)) {
                return false;
            }
            Pagination pagination = (Pagination) obj;
            return this.index == pagination.index && this.pageSize == pagination.pageSize && this.resultCount == pagination.resultCount && this.totalCount == pagination.totalCount;
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.index), Integer.valueOf(this.pageSize), Integer.valueOf(this.resultCount), Integer.valueOf(this.totalCount)};
        }

        public Pagination(int index, int pageSize, int resultCount, int totalCount) {
            this.index = index;
            this.pageSize = pageSize;
            this.resultCount = resultCount;
            this.totalCount = totalCount;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseForgeRemoteModRepository$Pagination$$ExternalSyntheticRecord0.m(this.index, this.pageSize, this.resultCount, this.totalCount);
        }

        public int index() {
            return this.index;
        }

        public int pageSize() {
            return this.pageSize;
        }

        public int resultCount() {
            return this.resultCount;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Pagination.class, "index;pageSize;resultCount;totalCount");
        }

        public int totalCount() {
            return this.totalCount;
        }
    }

    public static final class Response<T> extends RecordTag {
        private final T data;
        private final Pagination pagination;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Response)) {
                return false;
            }
            Response response = (Response) obj;
            return Objects.equals(this.data, response.data) && Objects.equals(this.pagination, response.pagination);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.data, this.pagination};
        }

        public Response(T data, Pagination pagination) {
            this.data = data;
            this.pagination = pagination;
        }

        public T data() {
            return this.data;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseForgeRemoteModRepository$Response$$ExternalSyntheticRecord0.m(this.data, this.pagination);
        }

        public Pagination pagination() {
            return this.pagination;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Response.class, "data;pagination");
        }
    }

    private static final class FingerprintMatchesResult extends RecordTag {
        private final List<Long> exactFingerprints;
        private final List<FingerprintMatch> exactMatches;
        private final boolean isCacheBuilt;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof FingerprintMatchesResult)) {
                return false;
            }
            FingerprintMatchesResult fingerprintMatchesResult = (FingerprintMatchesResult) obj;
            return this.isCacheBuilt == fingerprintMatchesResult.isCacheBuilt && Objects.equals(this.exactMatches, fingerprintMatchesResult.exactMatches) && Objects.equals(this.exactFingerprints, fingerprintMatchesResult.exactFingerprints);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Boolean.valueOf(this.isCacheBuilt), this.exactMatches, this.exactFingerprints};
        }

        private FingerprintMatchesResult(boolean isCacheBuilt, List<FingerprintMatch> exactMatches, List<Long> exactFingerprints) {
            this.isCacheBuilt = isCacheBuilt;
            this.exactMatches = exactMatches;
            this.exactFingerprints = exactFingerprints;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public List<Long> exactFingerprints() {
            return this.exactFingerprints;
        }

        public List<FingerprintMatch> exactMatches() {
            return this.exactMatches;
        }

        public final int hashCode() {
            return CurseForgeRemoteModRepository$FingerprintMatchesResult$$ExternalSyntheticRecord0.m(this.isCacheBuilt, this.exactMatches, this.exactFingerprints);
        }

        public boolean isCacheBuilt() {
            return this.isCacheBuilt;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), FingerprintMatchesResult.class, "isCacheBuilt;exactMatches;exactFingerprints");
        }
    }

    private static final class FingerprintMatch extends RecordTag {
        private final CurseAddon.LatestFile file;
        private final int id;
        private final List<CurseAddon.LatestFile> latestFiles;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof FingerprintMatch)) {
                return false;
            }
            FingerprintMatch fingerprintMatch = (FingerprintMatch) obj;
            return this.id == fingerprintMatch.id && Objects.equals(this.file, fingerprintMatch.file) && Objects.equals(this.latestFiles, fingerprintMatch.latestFiles);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.id), this.file, this.latestFiles};
        }

        private FingerprintMatch(int id, CurseAddon.LatestFile file, List<CurseAddon.LatestFile> latestFiles) {
            this.id = id;
            this.file = file;
            this.latestFiles = latestFiles;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public CurseAddon.LatestFile file() {
            return this.file;
        }

        public final int hashCode() {
            return CurseAddon$Author$$ExternalSyntheticRecord0.m(this.id, this.file, this.latestFiles);
        }

        public int id() {
            return this.id;
        }

        public List<CurseAddon.LatestFile> latestFiles() {
            return this.latestFiles;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), FingerprintMatch.class, "id;file;latestFiles");
        }
    }
}
