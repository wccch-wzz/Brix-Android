package com.brixcore.mod;

import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.task.Task;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public abstract class Modpack {
    private String author;
    private String description;
    private transient Charset encoding;
    private String gameVersion;
    private ModpackManifest manifest;
    private String name;
    private String version;

    public abstract Task<?> getInstallTask(DefaultDependencyManager defaultDependencyManager, File file, String str);

    public Modpack() {
        this("", null, null, null, null, null, null);
    }

    public Modpack(String name, String author, String version, String gameVersion, String description, Charset encoding, ModpackManifest manifest) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.gameVersion = gameVersion;
        this.description = description;
        this.encoding = encoding;
        this.manifest = manifest;
    }

    public String getName() {
        return this.name;
    }

    public Modpack setName(String name) {
        this.name = name;
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public Modpack setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getVersion() {
        return this.version;
    }

    public Modpack setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public Modpack setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Modpack setDescription(String description) {
        this.description = description;
        return this;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public Modpack setEncoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public ModpackManifest getManifest() {
        return this.manifest;
    }

    public Modpack setManifest(ModpackManifest manifest) {
        this.manifest = manifest;
        return this;
    }

    public static boolean acceptFile(String path, List<String> blackList, List<String> whiteList) {
        if (path.isEmpty()) {
            return true;
        }
        if (ModAdviser.match(blackList, path, false)) {
            return false;
        }
        if (whiteList == null || whiteList.isEmpty()) {
            return true;
        }
        for (String s : whiteList) {
            if (path.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
