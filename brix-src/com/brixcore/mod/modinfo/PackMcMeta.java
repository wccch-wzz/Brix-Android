package com.brixcore.mod.modinfo;

import com.android.tools.r8.RecordTag;
import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticRecord1;
import com.brixcore.mod.curse.CurseAddon$Dependency$$ExternalSyntheticRecord0;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes10.dex */
public final class PackMcMeta extends RecordTag implements Validation {

    @SerializedName("pack")
    private final PackInfo pack;

    private /* synthetic */ boolean $record$equals(Object obj) {
        return (obj instanceof PackMcMeta) && Objects.equals(this.pack, ((PackMcMeta) obj).pack);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.pack};
    }

    public PackMcMeta(PackInfo pack) {
        this.pack = pack;
    }

    public final boolean equals(Object o) {
        return $record$equals(o);
    }

    public final int hashCode() {
        return Objects.hashCode(this.pack);
    }

    @SerializedName("pack")
    public PackInfo pack() {
        return this.pack;
    }

    public final String toString() {
        return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), PackMcMeta.class, "pack");
    }

    @Override // com.brixcore.util.gson.Validation
    public void validate() throws JsonParseException {
        if (this.pack == null) {
            throw new JsonParseException("pack cannot be null");
        }
    }

    @JsonAdapter(PackInfoDeserializer.class)
    public static final class PackInfo extends RecordTag {

        @SerializedName("description")
        private final LocalModFile.Description description;

        @SerializedName("max_format")
        private final PackVersion maxPackVersion;

        @SerializedName("min_format")
        private final PackVersion minPackVersion;

        @SerializedName("pack_format")
        private final int packFormat;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof PackInfo)) {
                return false;
            }
            PackInfo packInfo = (PackInfo) obj;
            return this.packFormat == packInfo.packFormat && Objects.equals(this.minPackVersion, packInfo.minPackVersion) && Objects.equals(this.maxPackVersion, packInfo.maxPackVersion) && Objects.equals(this.description, packInfo.description);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.packFormat), this.minPackVersion, this.maxPackVersion, this.description};
        }

        @SerializedName("description")
        public LocalModFile.Description description() {
            return this.description;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return PackMcMeta$PackInfo$$ExternalSyntheticRecord0.m(this.packFormat, this.minPackVersion, this.maxPackVersion, this.description);
        }

        @SerializedName("max_format")
        public PackVersion maxPackVersion() {
            return this.maxPackVersion;
        }

        @SerializedName("min_format")
        public PackVersion minPackVersion() {
            return this.minPackVersion;
        }

        @SerializedName("pack_format")
        public int packFormat() {
            return this.packFormat;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), PackInfo.class, "packFormat;minPackVersion;maxPackVersion;description");
        }

        public PackInfo(int packFormat, PackVersion minPackVersion, PackVersion maxPackVersion, LocalModFile.Description description) {
            this.packFormat = packFormat;
            this.minPackVersion = minPackVersion;
            this.maxPackVersion = maxPackVersion;
            this.description = description;
        }

        public PackVersion getEffectiveMinVersion() {
            return !this.minPackVersion.isUnspecified() ? this.minPackVersion : new PackVersion(this.packFormat, 0);
        }

        public PackVersion getEffectiveMaxVersion() {
            return !this.maxPackVersion.isUnspecified() ? this.maxPackVersion : new PackVersion(this.packFormat, 0);
        }
    }

    public static final class PackVersion extends RecordTag implements Comparable<PackVersion> {
        public static final PackVersion UNSPECIFIED = new PackVersion(0, 0);
        private final int majorVersion;
        private final int minorVersion;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof PackVersion)) {
                return false;
            }
            PackVersion packVersion = (PackVersion) obj;
            return this.majorVersion == packVersion.majorVersion && this.minorVersion == packVersion.minorVersion;
        }

        public PackVersion(int majorVersion, int minorVersion) {
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return CurseAddon$Dependency$$ExternalSyntheticRecord0.m(this.majorVersion, this.minorVersion);
        }

        public int majorVersion() {
            return this.majorVersion;
        }

        public int minorVersion() {
            return this.minorVersion;
        }

        public String toString() {
            return this.minorVersion != 0 ? this.majorVersion + "." + this.minorVersion : String.valueOf(this.majorVersion);
        }

        @Override // java.lang.Comparable
        public int compareTo(PackVersion other) {
            int majorCompare = Integer.compare(this.majorVersion, other.majorVersion);
            if (majorCompare != 0) {
                return majorCompare;
            }
            return Integer.compare(this.minorVersion, other.minorVersion);
        }

        public boolean isUnspecified() {
            return equals(UNSPECIFIED);
        }

        public static PackVersion fromJson(JsonElement element) throws JsonParseException {
            if (element == null || element.isJsonNull()) {
                return UNSPECIFIED;
            }
            try {
                if (element instanceof JsonPrimitive) {
                    JsonPrimitive primitive = (JsonPrimitive) element;
                    if (primitive.isNumber()) {
                        return new PackVersion(element.getAsInt(), 0);
                    }
                }
                if (element instanceof JsonArray) {
                    JsonArray jsonArray = (JsonArray) element;
                    if (jsonArray.size() == 1 && (jsonArray.get(0) instanceof JsonPrimitive)) {
                        return new PackVersion(jsonArray.get(0).getAsInt(), 0);
                    }
                    if (jsonArray.size() == 2 && (jsonArray.get(0) instanceof JsonPrimitive) && (jsonArray.get(1) instanceof JsonPrimitive)) {
                        return new PackVersion(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt());
                    }
                    Logging.LOG.warning("Datapack version array must have 1 or 2 elements, but got " + jsonArray.size());
                }
            } catch (NumberFormatException e) {
                Logging.LOG.warning("Failed to parse datapack version component as a number. Value: " + element + StringUtils.LF + e);
            }
            return UNSPECIFIED;
        }
    }

    public static final class PackInfoDeserializer implements JsonDeserializer<PackInfo> {
        private List<LocalModFile.Description.Part> pairToPart(List<Pair<String, String>> lists, String color) {
            List<LocalModFile.Description.Part> parts = new ArrayList<>();
            for (Pair<String, String> list : lists) {
                parts.add(new LocalModFile.Description.Part(list.getKey(), list.getValue().isEmpty() ? color : list.getValue()));
            }
            return parts;
        }

        private void parseComponent(JsonElement element, List<LocalModFile.Description.Part> parts, String parentColor) throws JsonParseException {
            if (parentColor == null) {
                parentColor = "";
            }
            String color = parentColor;
            if (element instanceof JsonPrimitive) {
                JsonPrimitive primitive = (JsonPrimitive) element;
                parts.addAll(pairToPart(com.brixcore.util.StringUtils.parseMinecraftColorCodes(primitive.getAsString()), color));
                return;
            }
            if (element instanceof JsonObject) {
                JsonObject jsonObj = (JsonObject) element;
                JsonElement jsonElement = jsonObj.get("color");
                if (jsonElement instanceof JsonPrimitive) {
                    JsonPrimitive primitive2 = (JsonPrimitive) jsonElement;
                    color = primitive2.getAsString();
                }
                JsonElement jsonElement2 = jsonObj.get("text");
                if (jsonElement2 instanceof JsonPrimitive) {
                    JsonPrimitive primitive3 = (JsonPrimitive) jsonElement2;
                    parts.addAll(pairToPart(com.brixcore.util.StringUtils.parseMinecraftColorCodes(primitive3.getAsString()), color));
                }
                JsonElement jsonElement3 = jsonObj.get("extra");
                if (jsonElement3 instanceof JsonArray) {
                    parseComponent((JsonArray) jsonElement3, parts, color);
                    return;
                }
                return;
            }
            if (element instanceof JsonArray) {
                JsonArray jsonArray = (JsonArray) element;
                if (!jsonArray.isEmpty()) {
                    JsonElement jsonElement4 = jsonArray.get(0);
                    if (jsonElement4 instanceof JsonObject) {
                        JsonElement jsonElement5 = ((JsonObject) jsonElement4).get("color");
                        if (jsonElement5 instanceof JsonPrimitive) {
                            JsonPrimitive primitive4 = (JsonPrimitive) jsonElement5;
                            color = primitive4.getAsString();
                        }
                    }
                }
                for (JsonElement childElement : jsonArray) {
                    parseComponent(childElement, parts, color);
                }
                return;
            }
            Logging.LOG.warning("Skipping unsupported element in description. Expected a string, object, or array, but got type " + element.getClass().getSimpleName() + ". Value: " + element);
        }

        private List<LocalModFile.Description.Part> parseDescription(JsonElement json) throws JsonParseException {
            List<LocalModFile.Description.Part> parts = new ArrayList<>();
            if (json == null || json.isJsonNull()) {
                return parts;
            }
            try {
                parseComponent(json, parts, "");
            } catch (JsonParseException | IllegalStateException e) {
                parts.clear();
                Logging.LOG.warning("An unexpected error occurred while parsing a description component. The description may be incomplete.\n" + e);
            }
            return parts;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        /* JADX WARN: Code duplicated, block: B:7:0x001b  */
        @Override // com.google.gson.JsonDeserializer
        public PackInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int packFormat;
            JsonObject packInfo = json.getAsJsonObject();
            JsonElement jsonElement = packInfo.get("pack_format");
            if (jsonElement instanceof JsonPrimitive) {
                JsonPrimitive primitive = (JsonPrimitive) jsonElement;
                if (primitive.isNumber()) {
                    packFormat = primitive.getAsInt();
                } else {
                    packFormat = 0;
                }
            } else {
                packFormat = 0;
            }
            PackVersion minVersion = PackVersion.fromJson(packInfo.get("min_format"));
            PackVersion maxVersion = PackVersion.fromJson(packInfo.get("max_format"));
            List<LocalModFile.Description.Part> parts = parseDescription(packInfo.get("description"));
            return new PackInfo(packFormat, minVersion, maxVersion, new LocalModFile.Description(parts));
        }
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs) throws JsonParseException, IOException {
        Path mcmod = fs.getPath("pack.mcmeta", new String[0]);
        if (Files.notExists(mcmod, new LinkOption[0])) {
            throw new IOException("File " + modFile + " is not a resource pack.");
        }
        InputStream is = Files.newInputStream(mcmod, new OpenOption[0]);
        try {
            PackMcMeta metadata = (PackMcMeta) JsonUtils.fromNonNullJsonFully(is, PackMcMeta.class);
            if (is != null) {
                is.close();
            }
            return new LocalModFile(modManager, modManager.getLocalMod(FileUtils.getNameWithoutExtension(modFile), ModLoaderType.PACK), modFile, FileUtils.getNameWithoutExtension(modFile), metadata.pack.description, "", "", "", "", "");
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
