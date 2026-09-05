package com.brixcore.mod.mcbbs;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.LaunchOptions;
import com.brixcore.game.Library;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackManifest;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.task.Task;
import com.brixcore.util.gson.JsonSubtype;
import com.brixcore.util.gson.JsonType;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.NetworkUtils;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes7.dex */
public class McbbsModpackManifest implements ModpackManifest, Validation {
    public static final String MANIFEST_TYPE = "minecraftModpack";
    private final List<Addon> addons;
    private final String author;
    private final String description;
    private final String fileApi;
    private final List<File> files;
    private final boolean forceUpdate;
    private final LaunchInfo launchInfo;
    private final List<Library> libraries;
    private final String manifestType;
    private final int manifestVersion;
    private final String name;

    @SerializedName("origin")
    private final List<Origin> origins;
    private final Settings settings;
    private final String url;
    private final String version;

    public McbbsModpackManifest() {
        this("minecraftModpack", 1, "", "", "", "", null, "", false, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), new Settings(), new LaunchInfo());
    }

    public McbbsModpackManifest(String manifestType, int manifestVersion, String name, String version, String author, String description, String fileApi, String url, boolean forceUpdate, List<Origin> origins, List<Addon> addons, List<Library> libraries, List<File> files, Settings settings, LaunchInfo launchInfo) {
        this.manifestType = manifestType;
        this.manifestVersion = manifestVersion;
        this.name = name;
        this.version = version;
        this.author = author;
        this.description = description;
        this.fileApi = fileApi;
        this.url = url;
        this.forceUpdate = forceUpdate;
        this.origins = origins;
        this.addons = addons;
        this.libraries = libraries;
        this.files = files;
        this.settings = settings;
        this.launchInfo = launchInfo;
    }

    public String getManifestType() {
        return this.manifestType;
    }

    public int getManifestVersion() {
        return this.manifestVersion;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFileApi() {
        return this.fileApi;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isForceUpdate() {
        return this.forceUpdate;
    }

    public List<Origin> getOrigins() {
        return this.origins;
    }

    public List<Addon> getAddons() {
        return this.addons;
    }

    public List<Library> getLibraries() {
        return this.libraries;
    }

    public List<File> getFiles() {
        return this.files;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public LaunchInfo getLaunchInfo() {
        return this.launchInfo;
    }

    public McbbsModpackManifest setFiles(List<File> files) {
        return new McbbsModpackManifest(this.manifestType, this.manifestVersion, this.name, this.version, this.author, this.description, this.fileApi, this.url, this.forceUpdate, this.origins, this.addons, this.libraries, files, this.settings, this.launchInfo);
    }

    @Override // com.brixcore.mod.ModpackManifest
    public ModpackProvider getProvider() {
        return McbbsModpackProvider.INSTANCE;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        if (!"minecraftModpack".equals(this.manifestType)) {
            throw new JsonParseException("McbbsModpackManifest.manifestType must be 'minecraftModpack'");
        }
        if (this.files == null) {
            throw new JsonParseException("McbbsModpackManifest.files cannot be null");
        }
        if (this.addons == null) {
            throw new JsonParseException("McbbsModpackManifest.addons cannot be null");
        }
    }

    public static final class Origin {
        private final int id;
        private final String type;

        public Origin() {
            this("", 0);
        }

        public Origin(String type, int id) {
            this.type = type;
            this.id = id;
        }

        public String getType() {
            return this.type;
        }

        public int getId() {
            return this.id;
        }
    }

    public static final class Addon {
        private final String id;
        private final String version;

        public Addon() {
            this("", "");
        }

        public Addon(String id, String version) {
            this.id = id;
            this.version = version;
        }

        public String getId() {
            return this.id;
        }

        public String getVersion() {
            return this.version;
        }
    }

    public static final class Settings {

        @SerializedName("install_mods")
        private final boolean installMods;

        @SerializedName("install_resourcepack")
        private final boolean installResourcepack;

        public Settings() {
            this(true, true);
        }

        public Settings(boolean installMods, boolean installResourcepack) {
            this.installMods = installMods;
            this.installResourcepack = installResourcepack;
        }

        public boolean isInstallMods() {
            return this.installMods;
        }

        public boolean isInstallResourcepack() {
            return this.installResourcepack;
        }
    }

    @JsonType(property = "type", subtypes = {@JsonSubtype(clazz = AddonFile.class, name = "addon"), @JsonSubtype(clazz = CurseFile.class, name = "curse")})
    public static abstract class File implements Validation {
        protected final boolean force;

        public File(boolean force) {
            this.force = force;
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
        }

        public boolean isForce() {
            return this.force;
        }
    }

    public static final class AddonFile extends File {
        private final String hash;
        private final String path;

        public AddonFile(boolean force, String path, String hash) {
            super(force);
            this.path = (String) Objects.requireNonNull(path);
            this.hash = (String) Objects.requireNonNull(hash);
        }

        public String getPath() {
            return this.path;
        }

        public String getHash() {
            return this.hash;
        }

        @Override // com.brixcore.mod.mcbbs.McbbsModpackManifest.File, com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            super.validate();
            Validation.requireNonNull(this.path, "AddonFile.path cannot be null");
            Validation.requireNonNull(this.hash, "AddonFile.hash cannot be null");
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AddonFile addonFile = (AddonFile) o;
            return this.path.equals(addonFile.path);
        }

        public int hashCode() {
            return Objects.hash(this.path);
        }
    }

    public static final class CurseFile extends File {
        private final int fileID;
        private final String fileName;
        private final int projectID;
        private final String url;

        public CurseFile() {
            this(false, 0, 0, "", "");
        }

        public CurseFile(boolean force, int projectID, int fileID, String fileName, String url) {
            super(force);
            this.projectID = projectID;
            this.fileID = fileID;
            this.fileName = fileName;
            this.url = url;
        }

        public int getProjectID() {
            return this.projectID;
        }

        public int getFileID() {
            return this.fileID;
        }

        public String getFileName() {
            return this.fileName;
        }

        public URL getUrl() {
            return this.url == null ? NetworkUtils.toURL("https://www.curseforge.com/minecraft/mc-mods/" + this.projectID + "/download/" + this.fileID + "/file") : NetworkUtils.toURL(NetworkUtils.encodeLocation(this.url));
        }

        public CurseFile withFileName(String fileName) {
            return new CurseFile(this.force, this.projectID, this.fileID, fileName, this.url);
        }

        public CurseFile withURL(String url) {
            return new CurseFile(this.force, this.projectID, this.fileID, this.fileName, url);
        }

        @Override // com.brixcore.mod.mcbbs.McbbsModpackManifest.File, com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException, TolerableValidationException {
            super.validate();
            if (this.projectID == 0 || this.fileID == 0) {
                throw new JsonParseException("CurseFile.{projectID|fileID} cannot be empty.");
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CurseFile curseFile = (CurseFile) o;
            if (this.projectID == curseFile.projectID && this.fileID == curseFile.fileID) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.projectID), Integer.valueOf(this.fileID));
        }
    }

    public static final class LaunchInfo {

        @SerializedName("javaArgument")
        private final List<String> javaArguments;

        @SerializedName("launchArgument")
        private final List<String> launchArguments;
        private final int minMemory;
        private final List<Integer> supportJava;

        public LaunchInfo() {
            this(0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }

        public LaunchInfo(int minMemory, List<Integer> supportJava, List<String> launchArguments, List<String> javaArguments) {
            this.minMemory = minMemory;
            this.supportJava = supportJava;
            this.launchArguments = launchArguments;
            this.javaArguments = javaArguments;
        }

        public int getMinMemory() {
            return this.minMemory;
        }

        public List<Integer> getSupportJava() {
            return this.supportJava;
        }

        public List<String> getLaunchArguments() {
            return (List) Optional.ofNullable(this.launchArguments).orElseGet(new McbbsModpackManifest$LaunchInfo$$ExternalSyntheticLambda0());
        }

        public List<String> getJavaArguments() {
            return (List) Optional.ofNullable(this.javaArguments).orElseGet(new McbbsModpackManifest$LaunchInfo$$ExternalSyntheticLambda0());
        }
    }

    public static class ServerInfo {
        private final String authlibInjectorServer;

        public ServerInfo() {
            this(null);
        }

        public ServerInfo(String authlibInjectorServer) {
            this.authlibInjectorServer = authlibInjectorServer;
        }

        public String getAuthlibInjectorServer() {
            return this.authlibInjectorServer;
        }
    }

    public Modpack toModpack(Charset encoding) throws IOException {
        String gameVersion = this.addons.stream().filter(new Predicate() { // from class: com.brixcore.mod.mcbbs.McbbsModpackManifest$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId().equals(((McbbsModpackManifest.Addon) obj).id);
            }
        }).findAny().orElseThrow(new Supplier() { // from class: com.brixcore.mod.mcbbs.McbbsModpackManifest$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return McbbsModpackManifest.lambda$toModpack$1();
            }
        }).getVersion();
        return new Modpack(this.name, this.author, this.version, gameVersion, this.description, encoding, this) { // from class: com.brixcore.mod.mcbbs.McbbsModpackManifest.1
            @Override // com.brixcore.mod.Modpack
            public Task<?> getInstallTask(DefaultDependencyManager dependencyManager, java.io.File zipFile, String name) {
                return new McbbsModpackLocalInstallTask(dependencyManager, zipFile, this, McbbsModpackManifest.this, name);
            }
        };
    }

    static /* synthetic */ IOException lambda$toModpack$1() {
        return new IOException("Cannot find game version");
    }

    public void injectLaunchOptions(LaunchOptions.Builder launchOptions) {
        launchOptions.getGameArguments().addAll(this.launchInfo.getLaunchArguments());
        launchOptions.getJavaArguments().addAll(this.launchInfo.getJavaArguments());
    }
}
