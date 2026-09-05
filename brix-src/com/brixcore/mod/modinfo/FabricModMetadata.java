package com.brixcore.mod.modinfo;

import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.ModManager;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes10.dex */
public final class FabricModMetadata {
    private final List<FabricModAuthor> authors;
    private final Map<String, String> contact;
    private final String description;
    private final String icon;
    private final String id;
    private final String name;
    private final String version;

    public FabricModMetadata() {
        this("", "", "", "", "", Collections.emptyList(), Collections.emptyMap());
    }

    public FabricModMetadata(String id, String name, String version, String icon, String description, List<FabricModAuthor> authors, Map<String, String> contact) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.icon = icon;
        this.description = description;
        this.authors = authors;
        this.contact = contact;
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs) throws JsonParseException, IOException {
        Path mcmod = fs.getPath("fabric.mod.json", new String[0]);
        if (Files.notExists(mcmod, new LinkOption[0])) {
            throw new IOException("File " + modFile + " is not a Fabric mod.");
        }
        InputStream is = Files.newInputStream(mcmod, new OpenOption[0]);
        try {
            FabricModMetadata metadata = (FabricModMetadata) JsonUtils.fromNonNullJsonFully(is, FabricModMetadata.class);
            if (is != null) {
                is.close();
            }
            String authors = metadata.authors == null ? "" : (String) metadata.authors.stream().map(new Function() { // from class: com.brixcore.mod.modinfo.FabricModMetadata$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((FabricModMetadata.FabricModAuthor) obj).name;
                }
            }).collect(Collectors.joining(", "));
            return new LocalModFile(modManager, modManager.getLocalMod(metadata.id, ModLoaderType.FABRIC), modFile, metadata.name, new LocalModFile.Description(metadata.description), authors, metadata.version, "", metadata.contact != null ? metadata.contact.getOrDefault("homepage", "") : "", metadata.icon);
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

    @JsonAdapter(FabricModAuthorSerializer.class)
    public static final class FabricModAuthor {
        private final String name;

        public FabricModAuthor() {
            this("");
        }

        public FabricModAuthor(String name) {
            this.name = name;
        }
    }

    public static final class FabricModAuthorSerializer implements JsonSerializer<FabricModAuthor>, JsonDeserializer<FabricModAuthor> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.JsonDeserializer
        public FabricModAuthor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.isJsonPrimitive() ? new FabricModAuthor(json.getAsString()) : new FabricModAuthor(json.getAsJsonObject().getAsJsonPrimitive("name").getAsString());
        }

        @Override // com.google.gson.JsonSerializer
        public JsonElement serialize(FabricModAuthor src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.name);
        }
    }
}
