package com.brixcore.mod.server;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.mod.Modpack;
import com.brixcore.mod.ModpackConfiguration;
import com.brixcore.mod.ModpackManifest;
import com.brixcore.mod.ModpackProvider;
import com.brixcore.task.Task;
import com.brixcore.util.gson.TolerableValidationException;
import com.brixcore.util.gson.Validation;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes2.dex */
public class ServerModpackManifest implements ModpackManifest, Validation {
    private final List<Addon> addons;
    private final String author;
    private final String description;
    private final String fileApi;
    private final List<ModpackConfiguration.FileInformation> files;
    private final String name;
    private final String version;

    public ServerModpackManifest() {
        this("", "", "", "", "", Collections.emptyList(), Collections.emptyList());
    }

    public ServerModpackManifest(String name, String author, String version, String description, String fileApi, List<ModpackConfiguration.FileInformation> files, List<Addon> addons) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
        this.fileApi = fileApi;
        this.files = files;
        this.addons = addons;
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFileApi() {
        return this.fileApi;
    }

    public List<ModpackConfiguration.FileInformation> getFiles() {
        return this.files;
    }

    public List<Addon> getAddons() {
        return this.addons;
    }

    @Override // com.brixcore.mod.ModpackManifest
    public ModpackProvider getProvider() {
        return ServerModpackProvider.INSTANCE;
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException, TolerableValidationException {
        if (this.fileApi == null) {
            throw new JsonParseException("ServerModpackManifest.fileApi cannot be blank");
        }
        if (this.files == null) {
            throw new JsonParseException("ServerModpackManifest.files cannot be null");
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

    public Modpack toModpack(Charset encoding) throws IOException {
        String gameVersion = this.addons.stream().filter(new Predicate() { // from class: com.brixcore.mod.server.ServerModpackManifest$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return LibraryAnalyzer.LibraryType.MINECRAFT.getPatchId().equals(((ServerModpackManifest.Addon) obj).id);
            }
        }).findAny().orElseThrow(new Supplier() { // from class: com.brixcore.mod.server.ServerModpackManifest$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return ServerModpackManifest.lambda$toModpack$1();
            }
        }).getVersion();
        return new Modpack(this.name, this.author, this.version, gameVersion, this.description, encoding, this) { // from class: com.brixcore.mod.server.ServerModpackManifest.1
            @Override // com.brixcore.mod.Modpack
            public Task<?> getInstallTask(DefaultDependencyManager dependencyManager, File zipFile, String name) {
                return new ServerModpackLocalInstallTask(dependencyManager, zipFile, this, ServerModpackManifest.this, name);
            }
        };
    }

    static /* synthetic */ IOException lambda$toModpack$1() {
        return new IOException("Cannot find game version");
    }
}
