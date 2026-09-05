package com.brixcore.mod.modinfo;

import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.ModManager;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

/* JADX INFO: loaded from: classes10.dex */
public final class ForgeOldModMetadata {
    private final String author;
    private final String[] authorList;
    private final String[] authors;
    private final String credits;
    private final String description;
    private final String logoFile;
    private final String mcversion;

    @SerializedName("modid")
    private final String modId;
    private final String name;
    private final String updateUrl;
    private final String url;
    private final String version;

    public ForgeOldModMetadata() {
        this("", "", "", "", "", "", "", "", "", "", new String[0], new String[0]);
    }

    public ForgeOldModMetadata(String modId, String name, String description, String author, String version, String logoFile, String mcversion, String url, String updateUrl, String credits, String[] authorList, String[] authors) {
        this.modId = modId;
        this.name = name;
        this.description = description;
        this.author = author;
        this.version = version;
        this.logoFile = logoFile;
        this.mcversion = mcversion;
        this.url = url;
        this.updateUrl = updateUrl;
        this.credits = credits;
        this.authorList = authorList;
        this.authors = authors;
    }

    public String getModId() {
        return this.modId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getVersion() {
        return this.version;
    }

    public String getLogoFile() {
        return this.logoFile;
    }

    public String getGameVersion() {
        return this.mcversion;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUpdateUrl() {
        return this.updateUrl;
    }

    public String getCredits() {
        return this.credits;
    }

    public String[] getAuthorList() {
        return this.authorList;
    }

    public String[] getAuthors() {
        return this.authors;
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0145 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs) throws Throwable {
        BufferedReader reader;
        List<ForgeOldModMetadata> modList;
        String authors;
        Path mcmod = fs.getPath("mcmod.info", new String[0]);
        if (Files.notExists(mcmod, new LinkOption[0])) {
            throw new IOException("File " + modFile + " is not a Forge mod.");
        }
        reader = Files.newBufferedReader(mcmod);
        try {
            JsonReader jsonReader = new JsonReader(reader);
            try {
                JsonToken firstToken = jsonReader.peek();
                if (firstToken == JsonToken.BEGIN_ARRAY) {
                    modList = (List) JsonUtils.GSON.fromJson(jsonReader, JsonUtils.listTypeOf(ForgeOldModMetadata.class));
                } else {
                    try {
                        if (firstToken != JsonToken.BEGIN_OBJECT) {
                            throw new JsonParseException("Unexpected first token: " + firstToken);
                        }
                        ForgeOldModMetadataLst list = (ForgeOldModMetadataLst) JsonUtils.GSON.fromJson(jsonReader, ForgeOldModMetadataLst.class);
                        if (list == null) {
                            throw new IOException("Mod " + modFile + " `mcmod.info` is malformed");
                        }
                        List<ForgeOldModMetadata> modList2 = list.modList();
                        modList = modList2;
                    } catch (Throwable th) {
                        th = th;
                        Throwable th2 = th;
                        try {
                            jsonReader.close();
                            throw th2;
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                            throw th2;
                        }
                    }
                }
                jsonReader.close();
                if (reader != null) {
                    reader.close();
                }
                if (modList == null || modList.isEmpty()) {
                    throw new IOException("Mod " + modFile + " `mcmod.info` is malformed");
                }
                ForgeOldModMetadata metadata = modList.get(0);
                String authors2 = metadata.getAuthor();
                if (StringUtils.isBlank(authors2) && metadata.getAuthors().length > 0) {
                    authors2 = String.join(", ", metadata.getAuthors());
                }
                if (StringUtils.isBlank(authors2) && metadata.getAuthorList().length > 0) {
                    authors2 = String.join(", ", metadata.getAuthorList());
                }
                if (!StringUtils.isBlank(authors2)) {
                    authors = authors2;
                } else {
                    authors = metadata.getCredits();
                }
                return new LocalModFile(modManager, modManager.getLocalMod(metadata.getModId(), ModLoaderType.FORGE), modFile, metadata.getName(), new LocalModFile.Description(metadata.getDescription()), authors, metadata.getVersion(), metadata.getGameVersion(), StringUtils.isBlank(metadata.getUrl()) ? metadata.getUpdateUrl() : metadata.url, metadata.getLogoFile());
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Throwable th5) {
            th = th5;
        }
        Throwable th6 = th;
        if (reader == null) {
            throw th6;
        }
        try {
            reader.close();
            throw th6;
        } catch (Throwable th7) {
            th6.addSuppressed(th7);
            throw th6;
        }
    }
}
