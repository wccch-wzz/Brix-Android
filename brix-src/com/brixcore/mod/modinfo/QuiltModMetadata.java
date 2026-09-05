package com.brixcore.mod.modinfo;

import com.android.tools.r8.RecordTag;
import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticRecord1;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes10.dex */
public final class QuiltModMetadata {
    private final QuiltLoader quilt_loader;
    private final int schema_version;

    private static final class QuiltLoader extends RecordTag {
        private final String id;
        private final Metadata metadata;
        private final String version;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof QuiltLoader)) {
                return false;
            }
            QuiltLoader quiltLoader = (QuiltLoader) obj;
            return Objects.equals(this.id, quiltLoader.id) && Objects.equals(this.version, quiltLoader.version) && Objects.equals(this.metadata, quiltLoader.metadata);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.id, this.version, this.metadata};
        }

        private static final class Metadata extends RecordTag {
            private final JsonObject contact;
            private final JsonObject contributors;
            private final String description;
            private final String icon;
            private final String name;

            private /* synthetic */ boolean $record$equals(Object obj) {
                if (!(obj instanceof Metadata)) {
                    return false;
                }
                Metadata metadata = (Metadata) obj;
                return Objects.equals(this.name, metadata.name) && Objects.equals(this.description, metadata.description) && Objects.equals(this.contributors, metadata.contributors) && Objects.equals(this.icon, metadata.icon) && Objects.equals(this.contact, metadata.contact);
            }

            private /* synthetic */ Object[] $record$getFieldsAsObjects() {
                return new Object[]{this.name, this.description, this.contributors, this.icon, this.contact};
            }

            private Metadata(String name, String description, JsonObject contributors, String icon, JsonObject contact) {
                this.name = name;
                this.description = description;
                this.contributors = contributors;
                this.icon = icon;
                this.contact = contact;
            }

            public JsonObject contact() {
                return this.contact;
            }

            public JsonObject contributors() {
                return this.contributors;
            }

            public String description() {
                return this.description;
            }

            public final boolean equals(Object o) {
                return $record$equals(o);
            }

            public final int hashCode() {
                return ForgeNewModMetadata$$ExternalSyntheticRecord2.m(this.name, this.description, this.contributors, this.icon, this.contact);
            }

            public String icon() {
                return this.icon;
            }

            public String name() {
                return this.name;
            }

            public final String toString() {
                return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Metadata.class, "name;description;contributors;icon;contact");
            }
        }

        private QuiltLoader(String id, String version, Metadata metadata) {
            this.id = id;
            this.version = version;
            this.metadata = metadata;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return QuiltModMetadata$QuiltLoader$$ExternalSyntheticRecord0.m(this.id, this.version, this.metadata);
        }

        public String id() {
            return this.id;
        }

        public Metadata metadata() {
            return this.metadata;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), QuiltLoader.class, "id;version;metadata");
        }

        public String version() {
            return this.version;
        }
    }

    public QuiltModMetadata(int schemaVersion, QuiltLoader quiltLoader) {
        this.schema_version = schemaVersion;
        this.quilt_loader = quiltLoader;
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs) throws JsonParseException, IOException {
        Path path = fs.getPath("quilt.mod.json", new String[0]);
        if (Files.notExists(path, new LinkOption[0])) {
            throw new IOException("File " + modFile + " is not a Quilt mod.");
        }
        InputStream is = Files.newInputStream(path, new OpenOption[0]);
        try {
            QuiltModMetadata root = (QuiltModMetadata) JsonUtils.fromNonNullJsonFully(is, QuiltModMetadata.class);
            if (is != null) {
                is.close();
            }
            if (root.schema_version != 1) {
                throw new IOException("File " + modFile + " is not a supported Quilt mod.");
            }
            return new LocalModFile(modManager, modManager.getLocalMod(root.quilt_loader.id, ModLoaderType.QUILT), modFile, root.quilt_loader.metadata.name, new LocalModFile.Description(root.quilt_loader.metadata.description), (String) root.quilt_loader.metadata.contributors.entrySet().stream().map(new Function() { // from class: com.brixcore.mod.modinfo.QuiltModMetadata$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Map.Entry entry = (Map.Entry) obj;
                    return String.format("%s (%s)", entry.getKey(), ((JsonElement) entry.getValue()).getAsJsonPrimitive().getAsString());
                }
            }).collect(Collectors.joining(", ")), root.quilt_loader.version, "", (String) Optional.ofNullable(root.quilt_loader.metadata.contact.get("homepage")).map(new Function() { // from class: com.brixcore.mod.modinfo.QuiltModMetadata$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((JsonElement) obj).getAsJsonPrimitive().getAsString();
                }
            }).orElse(""), root.quilt_loader.metadata.icon);
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
