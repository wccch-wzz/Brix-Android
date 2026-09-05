package com.brixcore.mod.modinfo;

import com.android.tools.r8.RecordTag;
import com.brixcore.mod.LocalMod;
import com.brixcore.mod.LocalModFile;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.mod.ModManager;
import com.brixcore.mod.curse.CurseAddon$$ExternalSyntheticRecord1;
import com.brixcore.util.Logging;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.gson.Validation;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.JsonAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.tomlj.JsonOptions;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

/* JADX INFO: loaded from: classes10.dex */
public final class ForgeNewModMetadata extends RecordTag {
    private final String license;
    private final String loaderVersion;
    private final String logoFile;
    private final String modLoader;
    private final List<Mod> mods;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ForgeNewModMetadata)) {
            return false;
        }
        ForgeNewModMetadata forgeNewModMetadata = (ForgeNewModMetadata) obj;
        return Objects.equals(this.modLoader, forgeNewModMetadata.modLoader) && Objects.equals(this.loaderVersion, forgeNewModMetadata.loaderVersion) && Objects.equals(this.logoFile, forgeNewModMetadata.logoFile) && Objects.equals(this.license, forgeNewModMetadata.license) && Objects.equals(this.mods, forgeNewModMetadata.mods);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.modLoader, this.loaderVersion, this.logoFile, this.license, this.mods};
    }

    public ForgeNewModMetadata(String modLoader, String loaderVersion, String logoFile, String license, List<Mod> mods) {
        this.modLoader = modLoader;
        this.loaderVersion = loaderVersion;
        this.logoFile = logoFile;
        this.license = license;
        this.mods = mods;
    }

    public final boolean equals(Object o) {
        return $record$equals(o);
    }

    public final int hashCode() {
        return ForgeNewModMetadata$$ExternalSyntheticRecord2.m(this.modLoader, this.loaderVersion, this.logoFile, this.license, this.mods);
    }

    public String license() {
        return this.license;
    }

    public String loaderVersion() {
        return this.loaderVersion;
    }

    public String logoFile() {
        return this.logoFile;
    }

    public String modLoader() {
        return this.modLoader;
    }

    public List<Mod> mods() {
        return this.mods;
    }

    public final String toString() {
        return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ForgeNewModMetadata.class, "modLoader;loaderVersion;logoFile;license;mods");
    }

    public static final class Mod extends RecordTag {

        @JsonAdapter(AuthorDeserializer.class)
        private final String authors;
        private final String description;
        private final String displayName;
        private final String displayURL;
        private final String modId;
        private final String side;
        private final String version;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Mod)) {
                return false;
            }
            Mod mod = (Mod) obj;
            return Objects.equals(this.modId, mod.modId) && Objects.equals(this.version, mod.version) && Objects.equals(this.displayName, mod.displayName) && Objects.equals(this.side, mod.side) && Objects.equals(this.displayURL, mod.displayURL) && Objects.equals(this.authors, mod.authors) && Objects.equals(this.description, mod.description);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.modId, this.version, this.displayName, this.side, this.displayURL, this.authors, this.description};
        }

        public Mod(String modId, String version, String displayName, String side, String displayURL, String authors, String description) {
            this.modId = modId;
            this.version = version;
            this.displayName = displayName;
            this.side = side;
            this.displayURL = displayURL;
            this.authors = authors;
            this.description = description;
        }

        public String authors() {
            return this.authors;
        }

        public String description() {
            return this.description;
        }

        public String displayName() {
            return this.displayName;
        }

        public String displayURL() {
            return this.displayURL;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return ForgeNewModMetadata$Mod$$ExternalSyntheticRecord0.m(this.modId, this.version, this.displayName, this.side, this.displayURL, this.authors, this.description);
        }

        public String modId() {
            return this.modId;
        }

        public String side() {
            return this.side;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), Mod.class, "modId;version;displayName;side;displayURL;authors;description");
        }

        public String version() {
            return this.version;
        }

        public Mod() {
            this("", "", "", "", "", "", "");
        }

        static final class AuthorDeserializer implements JsonDeserializer<String> {
            AuthorDeserializer() {
            }

            @Override // com.google.gson.JsonDeserializer
            public String deserialize(JsonElement authors, Type type, JsonDeserializationContext context) throws JsonParseException {
                if (authors == null || authors.isJsonNull()) {
                    return null;
                }
                if (authors instanceof JsonPrimitive) {
                    JsonPrimitive primitive = (JsonPrimitive) authors;
                    return primitive.getAsString();
                }
                if (authors instanceof JsonArray) {
                    JsonArray array = (JsonArray) authors;
                    StringJoiner joiner = new StringJoiner(", ");
                    for (int i = 0; i < array.size(); i++) {
                        JsonElement jsonElement = array.get(i);
                        if (!(jsonElement instanceof JsonPrimitive)) {
                            return authors.toString();
                        }
                        JsonPrimitive element = (JsonPrimitive) jsonElement;
                        joiner.add(element.getAsString());
                    }
                    return joiner.toString();
                }
                return authors.toString();
            }
        }
    }

    public static LocalModFile fromForgeFile(ModManager modManager, Path modFile, FileSystem fs) throws IOException {
        return fromFile(modManager, modFile, fs, ModLoaderType.FORGE);
    }

    public static LocalModFile fromNeoForgeFile(ModManager modManager, Path modFile, FileSystem fs) throws IOException {
        return fromFile(modManager, modFile, fs, ModLoaderType.NEO_FORGED);
    }

    private static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs, ModLoaderType modLoaderType) throws IOException {
        if (modLoaderType != ModLoaderType.FORGE && modLoaderType != ModLoaderType.NEO_FORGED) {
            throw new IOException("Invalid mod loader: " + modLoaderType);
        }
        if (modLoaderType == ModLoaderType.NEO_FORGED) {
            try {
                return fromFile0("META-INF/neoforge.mods.toml", modLoaderType, modManager, modFile, fs);
            } catch (Exception e) {
            }
        }
        try {
            return fromFile0("META-INF/mods.toml", modLoaderType, modManager, modFile, fs);
        } catch (Exception e2) {
            try {
                return fromEmbeddedMod(modManager, modFile, fs, modLoaderType);
            } catch (Exception e3) {
                throw new IOException("File " + modFile + " is not a Forge 1.13+ or NeoForge mod.");
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:33:0x00dd  */
    private static LocalModFile fromFile0(String tomlPath, ModLoaderType modLoaderType, ModManager modManager, Path modFile, FileSystem fs) throws JsonParseException, IOException {
        String jarVersion;
        String strVersion;
        Path modToml = fs.getPath(tomlPath, new String[0]);
        if (Files.notExists(modToml, new LinkOption[0])) {
            throw new IOException("File " + modFile + " is not a Forge 1.13+ or NeoForge mod.");
        }
        TomlParseResult tomlParseResult = Toml.parse(FileUtils.readText(modToml));
        if (tomlParseResult.hasErrors()) {
            final IOException ioException = new IOException("Mod " + modFile + StringUtils.SPACE + tomlPath + " is malformed..");
            List<TomlParseError> listErrors = tomlParseResult.errors();
            Objects.requireNonNull(ioException);
            listErrors.forEach(new Consumer() { // from class: com.brixcore.mod.modinfo.ForgeNewModMetadata$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ioException.addSuppressed((TomlParseError) obj);
                }
            });
            throw ioException;
        }
        ForgeNewModMetadata metadata = (ForgeNewModMetadata) JsonUtils.GSON.fromJson(tomlParseResult.toJson(new JsonOptions[0]), ForgeNewModMetadata.class);
        if (metadata == null || metadata.mods().isEmpty()) {
            throw new IOException("Mod " + modFile + StringUtils.SPACE + tomlPath + " is malformed..");
        }
        Mod mod = metadata.mods().get(0);
        Path manifestMF = fs.getPath("META-INF/MANIFEST.MF", new String[0]);
        if (!Files.exists(manifestMF, new LinkOption[0])) {
            jarVersion = "";
        } else {
            try {
                InputStream is = Files.newInputStream(manifestMF, new OpenOption[0]);
                try {
                    Manifest manifest = new Manifest(is);
                    String jarVersion2 = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                    if (is != null) {
                        is.close();
                    }
                    jarVersion = jarVersion2;
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
                    String jarVersion3 = mod.modId();
                    ModLoaderType type = analyzeLoader(tomlParseResult, jarVersion3, modLoaderType);
                    LocalMod localMod = modManager.getLocalMod(mod.modId(), type);
                    String strDisplayName = mod.displayName();
                    LocalModFile.Description description = new LocalModFile.Description(mod.description());
                    String strAuthors = mod.authors();
                    strVersion = mod.version();
                    if (jarVersion != null) {
                        strVersion = strVersion.replace("${file.jarVersion}", jarVersion);
                    }
                    return new LocalModFile(modManager, localMod, modFile, strDisplayName, description, strAuthors, strVersion, "", mod.displayURL(), metadata.logoFile());
                }
            } catch (IOException e) {
                Logging.LOG.warning("Failed to parse MANIFEST.MF in file " + modFile);
                jarVersion = "";
            }
        }
        String jarVersion4 = mod.modId();
        ModLoaderType type2 = analyzeLoader(tomlParseResult, jarVersion4, modLoaderType);
        LocalMod localMod2 = modManager.getLocalMod(mod.modId(), type2);
        String strDisplayName2 = mod.displayName();
        LocalModFile.Description description2 = new LocalModFile.Description(mod.description());
        String strAuthors2 = mod.authors();
        strVersion = mod.version();
        if (jarVersion != null) {
            strVersion = strVersion.replace("${file.jarVersion}", jarVersion);
        }
        return new LocalModFile(modManager, localMod2, modFile, strDisplayName2, description2, strAuthors2, strVersion, "", mod.displayURL(), metadata.logoFile());
    }

    private static LocalModFile fromEmbeddedMod(ModManager modManager, Path modFile, FileSystem fs, ModLoaderType modLoaderType) throws Throwable {
        Path manifestFile = fs.getPath("META-INF/MANIFEST.MF", new String[0]);
        if (Files.notExists(manifestFile, new LinkOption[0])) {
            throw new IOException("Missing MANIFEST.MF in file " + modFile);
        }
        InputStream input = Files.newInputStream(manifestFile, new OpenOption[0]);
        try {
            Manifest manifest = new Manifest(input);
            if (input != null) {
                input.close();
            }
            List<Path> embeddedModFiles = Collections.EMPTY_LIST;
            String embeddedDependenciesMod = manifest.getMainAttributes().getValue("Embedded-Dependencies-Mod");
            if (embeddedDependenciesMod != null) {
                Path embeddedModFile = fs.getPath(embeddedDependenciesMod, new String[0]);
                if (Files.notExists(embeddedModFile, new LinkOption[0])) {
                    Logging.LOG.warning("Missing embedded-dependencies-mod: " + embeddedDependenciesMod);
                    throw new IOException();
                }
                embeddedModFiles = ForgeNewModMetadata$$ExternalSyntheticBackport0.m(new Object[]{embeddedModFile});
            } else {
                Path jarInJarMetadata = fs.getPath("META-INF/jarjar/metadata.json", new String[0]);
                if (Files.exists(jarInJarMetadata, new LinkOption[0])) {
                    InputStream is = Files.newInputStream(jarInJarMetadata, new OpenOption[0]);
                    try {
                        JarInJarMetadata metadata = (JarInJarMetadata) JsonUtils.fromJsonFully(is, JarInJarMetadata.class);
                        if (is != null) {
                            is.close();
                        }
                        if (metadata == null) {
                            throw new IOException("Invalid metadata file: " + jarInJarMetadata);
                        }
                        metadata.validate();
                        embeddedModFiles = new ArrayList<>();
                        for (EmbeddedJarMetadata jar : metadata.jars) {
                            Path path = fs.getPath(jar.path, new String[0]);
                            if (Files.exists(path, new LinkOption[0])) {
                                embeddedModFiles.add(path);
                            } else {
                                Logging.LOG.warning("Missing embedded-dependencies-mod: " + jar.path);
                            }
                        }
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
            if (embeddedModFiles.isEmpty()) {
                throw new IOException("Missing embedded mods");
            }
            Path tempFile = Files.createTempFile("hmcl-", ".zip", new FileAttribute[0]);
            try {
                Iterator<Path> it = embeddedModFiles.iterator();
                while (it.hasNext()) {
                    Files.copy(it.next(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                    try {
                        FileSystem embeddedFs = CompressingUtils.createReadOnlyZipFileSystem(tempFile);
                        try {
                            try {
                                LocalModFile localModFileFromFile = fromFile(modManager, modFile, embeddedFs, modLoaderType);
                                if (embeddedFs != null) {
                                    embeddedFs.close();
                                }
                                Files.deleteIfExists(tempFile);
                                return localModFileFromFile;
                            } catch (Throwable th3) {
                                if (embeddedFs == null) {
                                    throw th3;
                                }
                                try {
                                    embeddedFs.close();
                                    throw th3;
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                    throw th3;
                                }
                            }
                        } catch (Exception e) {
                        } catch (Throwable th5) {
                            th = th5;
                            Files.deleteIfExists(tempFile);
                            throw th;
                        }
                    } catch (Exception e2) {
                    }
                }
                Files.deleteIfExists(tempFile);
                throw new IOException();
            } catch (Throwable th6) {
                th = th6;
            }
        } catch (Throwable th7) {
            if (input == null) {
                throw th7;
            }
            try {
                input.close();
                throw th7;
            } catch (Throwable th8) {
                th7.addSuppressed(th8);
                throw th7;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:39:0x00c3  */
    /* JADX WARN: Code duplicated, block: B:45:0x00d0 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:46:0x00d2  */
    /* JADX WARN: Code duplicated, block: B:48:0x00ff  */
    private static ModLoaderType analyzeLoader(TomlParseResult toml, String modID, ModLoaderType loader) {
        List<Map<String, Object>> dependencies = null;
        try {
            TomlArray tomlArray = toml.getArray("dependencies." + modID);
            if (tomlArray != null) {
                dependencies = (List) tomlArray.toList().stream().map(new Function() { // from class: com.brixcore.mod.modinfo.ForgeNewModMetadata$$ExternalSyntheticLambda4
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((TomlTable) obj).toMap();
                    }
                }).collect(Collectors.toList());
            }
        } catch (ClassCastException e) {
        }
        if (dependencies == null) {
            try {
                TomlArray tomlArray2 = toml.getArray("dependencies");
                if (tomlArray2 != null) {
                    dependencies = (List) tomlArray2.toList().stream().map(new Function() { // from class: com.brixcore.mod.modinfo.ForgeNewModMetadata$$ExternalSyntheticLambda5
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return ((TomlTable) obj).toMap();
                        }
                    }).collect(Collectors.toList());
                }
            } catch (ClassCastException e2) {
                try {
                    TomlTable table = toml.getTable("dependencies");
                    if (table == null) {
                        return loader;
                    }
                    TomlArray tomlArray3 = table.getArray(modID);
                    if (tomlArray3 != null) {
                        dependencies = (List) tomlArray3.toList().stream().map(new Function() { // from class: com.brixcore.mod.modinfo.ForgeNewModMetadata$$ExternalSyntheticLambda6
                            @Override // java.util.function.Function
                            public final Object apply(Object obj) {
                                return ((TomlTable) obj).toMap();
                            }
                        }).collect(Collectors.toList());
                    }
                } catch (Throwable th) {
                }
            }
            if (dependencies == null) {
                return loader;
            }
        }
        ModLoaderType result = null;
        for (Map<String, Object> dependency : dependencies) {
            switch ((String) dependency.get("modId")) {
                case "forge":
                    result = ModLoaderType.FORGE;
                    break;
                case "neoforge":
                    result = ModLoaderType.NEO_FORGED;
                    break;
                default:
                    break;
            }
            if (result != null) {
                if (result != loader) {
                    Logging.LOG.warning("Loader mismatch for mod " + modID + ", found " + result + ", expecting " + loader);
                }
                return result;
            }
            Logging.LOG.warning("Cannot determine the mod loader for mod " + modID + ", expected " + loader);
            return loader;
        }
        if (result != null) {
            if (result != loader) {
                Logging.LOG.warning("Loader mismatch for mod " + modID + ", found " + result + ", expecting " + loader);
            }
            return result;
        }
        Logging.LOG.warning("Cannot determine the mod loader for mod " + modID + ", expected " + loader);
        return loader;
    }

    private static final class JarInJarMetadata extends RecordTag implements Validation {
        private final List<EmbeddedJarMetadata> jars;

        private /* synthetic */ boolean $record$equals(Object obj) {
            return (obj instanceof JarInJarMetadata) && Objects.equals(this.jars, ((JarInJarMetadata) obj).jars);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.jars};
        }

        private JarInJarMetadata(List<EmbeddedJarMetadata> jars) {
            this.jars = jars;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return Objects.hashCode(this.jars);
        }

        public List<EmbeddedJarMetadata> jars() {
            return this.jars;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), JarInJarMetadata.class, "jars");
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException {
            Validation.requireNonNull(this.jars, "jars");
            for (EmbeddedJarMetadata jar : this.jars) {
                jar.validate();
            }
        }
    }

    private static final class EmbeddedJarMetadata extends RecordTag implements Validation {
        private final boolean isObfuscated;
        private final String path;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof EmbeddedJarMetadata)) {
                return false;
            }
            EmbeddedJarMetadata embeddedJarMetadata = (EmbeddedJarMetadata) obj;
            return this.isObfuscated == embeddedJarMetadata.isObfuscated && Objects.equals(this.path, embeddedJarMetadata.path);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.path, Boolean.valueOf(this.isObfuscated)};
        }

        private EmbeddedJarMetadata(String path, boolean isObfuscated) {
            this.path = path;
            this.isObfuscated = isObfuscated;
        }

        public final boolean equals(Object o) {
            return $record$equals(o);
        }

        public final int hashCode() {
            return ForgeNewModMetadata$EmbeddedJarMetadata$$ExternalSyntheticRecord0.m(this.isObfuscated, this.path);
        }

        public boolean isObfuscated() {
            return this.isObfuscated;
        }

        public String path() {
            return this.path;
        }

        public final String toString() {
            return CurseAddon$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), EmbeddedJarMetadata.class, "path;isObfuscated");
        }

        @Override // com.brixcore.util.gson.Validation
        public void validate() throws JsonParseException {
            Validation.requireNonNull(this.path, "path");
        }
    }
}
