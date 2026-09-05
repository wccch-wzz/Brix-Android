package com.brixcore.mod;

import com.brixcore.mod.curse.CurseForgeRemoteModRepository;
import com.brixcore.mod.modrinth.ModrinthRemoteModRepository;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.io.NetworkUtils;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes2.dex */
public class RemoteMod {
    private static RemoteMod EMPTY = null;
    private final String author;
    private final List<String> categories;
    private final IMod data;
    private final String description;
    private final int downloadCount;
    private final String iconUrl;
    private String modID;
    private final String pageUrl;
    private final String slug;
    private final String title;

    public enum DependencyType {
        REQUIRED,
        OPTIONAL,
        TOOL,
        INCLUDE,
        EMBEDDED,
        INCOMPATIBLE,
        BROKEN
    }

    public interface IMod {
        List<RemoteMod> loadDependencies(RemoteModRepository remoteModRepository) throws IOException;

        List<Screenshot> loadScreenshots(RemoteModRepository remoteModRepository) throws IOException;

        Stream<Version> loadVersions(RemoteModRepository remoteModRepository) throws IOException;
    }

    public interface IVersion {
        Type getType();
    }

    public enum VersionType {
        Release,
        Beta,
        Alpha
    }

    public static void registerEmptyRemoteMod(RemoteMod empty) {
        EMPTY = empty;
    }

    public static RemoteMod getEmptyRemoteMod() {
        if (EMPTY == null) {
            throw new NullPointerException();
        }
        return EMPTY;
    }

    public RemoteMod(String slug, String author, String title, String description, List<String> categories, String pageUrl, String iconUrl, IMod data, int downloadCount, String modID) {
        this.slug = slug;
        this.author = author;
        this.title = title;
        this.description = description;
        this.categories = categories;
        this.pageUrl = pageUrl;
        this.iconUrl = iconUrl;
        this.data = data;
        this.downloadCount = downloadCount;
        this.modID = modID;
    }

    public String getSlug() {
        return this.slug;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public String getPageUrl() {
        return this.pageUrl;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public IMod getData() {
        return this.data;
    }

    public int getDownloadCount() {
        return this.downloadCount;
    }

    public String getModID() {
        return this.modID;
    }

    public void setModID(String modID) {
        this.modID = modID;
    }

    public static final class Dependency {
        private static Dependency BROKEN_DEPENDENCY = null;
        private final String id;
        private transient RemoteMod remoteMod = null;
        private final RemoteModRepository remoteModRepository;
        private final DependencyType type;

        private Dependency(DependencyType type, RemoteModRepository remoteModRepository, String modid) {
            this.type = type;
            this.remoteModRepository = remoteModRepository;
            this.id = modid;
        }

        public static Dependency ofGeneral(DependencyType type, RemoteModRepository remoteModRepository, String modid) {
            if (type == DependencyType.BROKEN) {
                return ofBroken();
            }
            return new Dependency(type, remoteModRepository, modid);
        }

        public static Dependency ofBroken() {
            if (BROKEN_DEPENDENCY == null) {
                BROKEN_DEPENDENCY = new Dependency(DependencyType.BROKEN, null, null);
            }
            return BROKEN_DEPENDENCY;
        }

        public DependencyType getType() {
            return this.type;
        }

        public RemoteModRepository getRemoteModRepository() {
            return this.remoteModRepository;
        }

        public String getId() {
            return this.id;
        }

        public RemoteMod load() throws IOException {
            if (this.remoteMod == null) {
                if (this.type == DependencyType.BROKEN) {
                    this.remoteMod = RemoteMod.getEmptyRemoteMod();
                } else {
                    this.remoteMod = this.remoteModRepository.getModById(this.id);
                }
            }
            return this.remoteMod;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Dependency that = (Dependency) o;
            if (this.type != that.type || !this.remoteModRepository.equals(that.remoteModRepository)) {
                return false;
            }
            return this.id.equals(that.id);
        }

        public int hashCode() {
            int result = this.type.hashCode();
            return (((result * 31) + this.remoteModRepository.hashCode()) * 31) + this.id.hashCode();
        }
    }

    public enum Type {
        CURSEFORGE(CurseForgeRemoteModRepository.MODS),
        MODRINTH(ModrinthRemoteModRepository.MODS);

        private final RemoteModRepository remoteModRepository;

        public RemoteModRepository getRemoteModRepository() {
            return this.remoteModRepository;
        }

        Type(RemoteModRepository remoteModRepository) {
            this.remoteModRepository = remoteModRepository;
        }
    }

    public static class Version {
        private final String changelog;
        private final Instant datePublished;
        private final List<Dependency> dependencies;
        private final File file;
        private final List<String> gameVersions;
        private final List<ModLoaderType> loaders;
        private final String modid;
        private final String name;
        private final IVersion self;
        private final String version;
        private final VersionType versionType;

        public Version(IVersion self, String modid, String name, String version, String changelog, Instant datePublished, VersionType versionType, File file, List<Dependency> dependencies, List<String> gameVersions, List<ModLoaderType> loaders) {
            this.self = self;
            this.modid = modid;
            this.name = name;
            this.version = version;
            this.changelog = changelog;
            this.datePublished = datePublished;
            this.versionType = versionType;
            this.file = file;
            this.dependencies = dependencies;
            this.gameVersions = gameVersions;
            this.loaders = loaders;
        }

        public IVersion getSelf() {
            return this.self;
        }

        public String getModid() {
            return this.modid;
        }

        public String getName() {
            return this.name;
        }

        public String getVersion() {
            return this.version;
        }

        public String getChangelog() {
            return this.changelog;
        }

        public Instant getDatePublished() {
            return this.datePublished;
        }

        public VersionType getVersionType() {
            return this.versionType;
        }

        public File getFile() {
            return this.file;
        }

        public List<Dependency> getDependencies() {
            return this.dependencies;
        }

        public List<String> getGameVersions() {
            return this.gameVersions;
        }

        public List<ModLoaderType> getLoaders() {
            return this.loaders;
        }
    }

    public static class File {
        private final String filename;
        private final Map<String, String> hashes;
        private final String url;

        public File(Map<String, String> hashes, String url, String filename) {
            this.hashes = hashes;
            this.url = url;
            this.filename = filename;
        }

        public Map<String, String> getHashes() {
            return this.hashes;
        }

        public FileDownloadTask.IntegrityCheck getIntegrityCheck() {
            if (this.hashes.containsKey("md5")) {
                return new FileDownloadTask.IntegrityCheck("MD5", this.hashes.get("md5"));
            }
            if (this.hashes.containsKey("sha1")) {
                return new FileDownloadTask.IntegrityCheck(CacheRepository.SHA1, this.hashes.get("sha1"));
            }
            if (this.hashes.containsKey("sha256")) {
                return new FileDownloadTask.IntegrityCheck("SHA-256", this.hashes.get("sha256"));
            }
            if (this.hashes.containsKey("sha512")) {
                return new FileDownloadTask.IntegrityCheck("SHA-512", this.hashes.get("sha512"));
            }
            return null;
        }

        public String getUrl() {
            return NetworkUtils.encodeLocation(this.url);
        }

        public String getFilename() {
            return this.filename;
        }
    }

    public static class Screenshot {
        private final String description;
        private final String imageUrl;
        private final String title;

        public Screenshot(String imageUrl, String title, String description) {
            this.imageUrl = imageUrl;
            this.title = title;
            this.description = description;
        }

        public String getImageUrl() {
            return this.imageUrl;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String toString() {
            return "Screenshot{imageUrl='" + this.imageUrl + "', title='" + this.title + "', description=" + this.description + '}';
        }
    }
}
