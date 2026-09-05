package com.brixcore.mod;

import com.android.tools.r8.RecordTag;
import com.brixcore.download.DownloadProvider;
import com.brixcore.util.versioning.GameVersionNumber;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes2.dex */
public interface RemoteModRepository {
    public static final String[] DEFAULT_GAME_VERSIONS = GameVersionNumber.Release.getDefaultGameVersions();

    public enum SortOrder {
        ASC,
        DESC
    }

    public enum SortType {
        POPULARITY,
        NAME,
        DATE_CREATED,
        LAST_UPDATED,
        AUTHOR,
        TOTAL_DOWNLOADS
    }

    public enum Type {
        MOD,
        MODPACK,
        RESOURCE_PACK,
        SHADER_PACK,
        WORLD,
        CUSTOMIZATION
    }

    Stream<Category> getCategories() throws IOException;

    RemoteMod getModById(String str) throws IOException;

    RemoteMod.File getModFile(String str, String str2) throws IOException;

    Optional<RemoteMod.Version> getRemoteVersionByLocalFile(LocalModFile localModFile, Path path) throws IOException;

    Stream<RemoteMod.Version> getRemoteVersionsById(String str) throws IOException;

    Type getType();

    SearchResult search(DownloadProvider downloadProvider, String str, Category category, int i, int i2, String str2, SortType sortType, SortOrder sortOrder) throws IOException;

    public static class SearchResult {
        private final Stream<RemoteMod> sortedResults;
        private final int totalPages;
        private final Stream<RemoteMod> unsortedResults;

        public SearchResult(Stream<RemoteMod> sortedResults, Stream<RemoteMod> unsortedResults, int totalPages) {
            this.sortedResults = sortedResults;
            this.unsortedResults = unsortedResults;
            this.totalPages = totalPages;
        }

        public SearchResult(Stream<RemoteMod> sortedResults, int pages) {
            this.sortedResults = sortedResults;
            this.unsortedResults = sortedResults;
            this.totalPages = pages;
        }

        public Stream<RemoteMod> getResults() {
            return this.sortedResults;
        }

        public Stream<RemoteMod> getUnsortedResults() {
            return this.unsortedResults;
        }

        public int getTotalPages() {
            return this.totalPages;
        }
    }

    public static final class Category extends RecordTag {
        private final String id;
        private final Object self;
        private final List<Category> subcategories;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Category)) {
                return false;
            }
            Category category = (Category) obj;
            return Objects.equals(this.self, category.self) && Objects.equals(this.id, category.id) && Objects.equals(this.subcategories, category.subcategories);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.self, this.id, this.subcategories};
        }

        public Category(Object self, String id, List<Category> subcategories) {
            this.self = self;
            this.id = id;
            this.subcategories = subcategories;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return RemoteModRepository$Category$$ExternalSyntheticRecord0.m(this.self, this.id, this.subcategories);
        }

        public String id() {
            return this.id;
        }

        public Object self() {
            return this.self;
        }

        public List<Category> subcategories() {
            return this.subcategories;
        }

        public final String toString() {
            return LocalAddonFile$AddonUpdate$$ExternalSyntheticRecord0.m($record$getFieldsAsObjects(), Category.class, "self;id;subcategories");
        }
    }
}
