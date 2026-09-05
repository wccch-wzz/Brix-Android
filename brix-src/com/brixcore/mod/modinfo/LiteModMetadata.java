package com.brixcore.mod.modinfo;

import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.ModManager;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/* JADX INFO: loaded from: classes10.dex */
public final class LiteModMetadata {
    private final String author;
    private final String checkUpdateUrl;
    private final String[] classTransformerClasses;
    private final String description;
    private final String mcversion;
    private final String modpackName;
    private final String modpackVersion;
    private final String name;
    private final String revision;
    private final String updateURI;
    private final String version;

    public LiteModMetadata() {
        this("", "", "", "", "", new String[]{""}, "", "", "", "", "");
    }

    public LiteModMetadata(String name, String version, String mcversion, String revision, String author, String[] classTransformerClasses, String description, String modpackName, String modpackVersion, String checkUpdateUrl, String updateURI) {
        this.name = name;
        this.version = version;
        this.mcversion = mcversion;
        this.revision = revision;
        this.author = author;
        this.classTransformerClasses = classTransformerClasses;
        this.description = description;
        this.modpackName = modpackName;
        this.modpackVersion = modpackVersion;
        this.checkUpdateUrl = checkUpdateUrl;
        this.updateURI = updateURI;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getGameVersion() {
        return this.mcversion;
    }

    public String getRevision() {
        return this.revision;
    }

    public String getAuthor() {
        return this.author;
    }

    public String[] getClassTransformerClasses() {
        return this.classTransformerClasses;
    }

    public String getDescription() {
        return this.description;
    }

    public String getModpackName() {
        return this.modpackName;
    }

    public String getModpackVersion() {
        return this.modpackVersion;
    }

    public String getCheckUpdateUrl() {
        return this.checkUpdateUrl;
    }

    public String getUpdateURI() {
        return this.updateURI;
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs) throws JsonParseException, IOException {
        Path path = fs.getPath("litemod.json", new String[0]);
        if (Files.notExists(path, new LinkOption[0])) {
            throw new IOException("File " + modFile + " is not a LiteLoader mod.");
        }
        InputStream is = Files.newInputStream(path, new OpenOption[0]);
        try {
            LiteModMetadata metadata = (LiteModMetadata) JsonUtils.fromJsonFully(is, LiteModMetadata.class);
            if (is != null) {
                is.close();
            }
            if (metadata == null) {
                throw new IOException("Mod " + modFile + " `litemod.json` is malformed.");
            }
            return new LocalModFile(modManager, modManager.getLocalMod(metadata.getName(), ModLoaderType.LITE_LOADER), modFile, metadata.getName(), new LocalModFile.Description(metadata.getDescription()), metadata.getAuthor(), metadata.getVersion(), metadata.getGameVersion(), metadata.getUpdateURI(), "");
        } catch (Throwable th) {
            if (is == null) {
                throw th;
            }
            try {
                is.close();
                throw th;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
                throw th;
            }
        }
    }
}
