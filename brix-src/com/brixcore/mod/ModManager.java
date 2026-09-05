package com.brixcore.mod;

import android.app.Activity;
import android.net.Uri;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.GameRepository;
import com.brixcore.mod.modinfo.FabricModMetadata;
import com.brixcore.mod.modinfo.ForgeNewModMetadata;
import com.brixcore.mod.modinfo.ForgeOldModMetadata;
import com.brixcore.mod.modinfo.LiteModMetadata;
import com.brixcore.mod.modinfo.PackMcMeta;
import com.brixcore.mod.modinfo.QuiltModMetadata;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.versioning.VersionNumber;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class ModManager {
    public static final String DISABLED_EXTENSION = ".disabled";
    public static final String OLD_EXTENSION = ".old";
    private static final Map<String, List<Pair<ModMetadataReader, ModLoaderType>>> READERS;
    private LibraryAnalyzer analyzer;
    private final String id;
    private final GameRepository repository;
    private final TreeSet<LocalModFile> localModFiles = new TreeSet<>();
    private final HashMap<Pair<String, ModLoaderType>, LocalMod> localMods = new HashMap<>();
    private final List<Path> brokenFiles = new ArrayList();
    private boolean loaded = false;

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    interface ModMetadataReader {
        LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) throws JsonParseException, IOException;
    }

    static {
        HashMap<String, List<Pair<ModMetadataReader, ModLoaderType>>> map = new HashMap<>();
        List<Pair<ModMetadataReader, ModLoaderType>> zipReaders = ModManager$$ExternalSyntheticBackport0.m(new Object[]{Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda7
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return ForgeNewModMetadata.fromForgeFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.FORGE), Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda8
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return ForgeNewModMetadata.fromNeoForgeFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.NEO_FORGED), Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda9
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return ForgeOldModMetadata.fromFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.FORGE), Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda10
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return FabricModMetadata.fromFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.FABRIC), Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda11
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return QuiltModMetadata.fromFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.QUILT), Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda4
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return PackMcMeta.fromFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.PACK)});
        map.put(ArchiveStreamFactory.ZIP, zipReaders);
        map.put("jar", zipReaders);
        map.put("litemod", ModManager$$ExternalSyntheticBackport0.m(new Object[]{Pair.pair(new ModMetadataReader() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda5
            @Override // com.brixcore.mod.ModManager.ModMetadataReader
            public final LocalModFile fromFile(ModManager modManager, Path path, FileSystem fileSystem) {
                return LiteModMetadata.fromFile(modManager, path, fileSystem);
            }
        }, ModLoaderType.LITE_LOADER)}));
        READERS = map;
    }

    public ModManager(GameRepository repository, String id) {
        this.repository = repository;
        this.id = id;
    }

    public GameRepository getRepository() {
        return this.repository;
    }

    public String getInstanceId() {
        return this.id;
    }

    public Path getModsDirectory() {
        return this.repository.getModsDirectory(this.id);
    }

    public LibraryAnalyzer getLibraryAnalyzer() {
        return this.analyzer;
    }

    public LocalMod getLocalMod(String modId, ModLoaderType modLoaderType) {
        return this.localMods.computeIfAbsent(Pair.pair(modId, modLoaderType), new Function() { // from class: com.brixcore.mod.ModManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ModManager.lambda$getLocalMod$0((Pair) obj);
            }
        });
    }

    static /* synthetic */ LocalMod lambda$getLocalMod$0(Pair x) {
        return new LocalMod((String) x.getKey(), (ModLoaderType) x.getValue());
    }

    public boolean hasMod(String modId, ModLoaderType modLoaderType) {
        return this.localMods.containsKey(Pair.pair(modId, modLoaderType));
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0143 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private LocalModFile addModInfo(Path file) throws IOException {
        LocalModFile modInfo;
        LocalModFile modInfo2;
        String fileName = StringUtils.removeSuffix(FileUtils.getName(file), ".disabled", ".old");
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        List<Pair<ModMetadataReader, ModLoaderType>> readersMap = READERS.get(extension);
        LocalModFile localModFile = null;
        if (readersMap == null) {
            return null;
        }
        Set<ModLoaderType> modLoaderTypes = this.analyzer.getModLoaders();
        ArrayList<ModMetadataReader> supportedReaders = new ArrayList<>();
        ArrayList<ModMetadataReader> unsupportedReaders = new ArrayList<>();
        for (Pair<ModMetadataReader, ModLoaderType> reader : readersMap) {
            if (modLoaderTypes.contains(reader.getValue())) {
                supportedReaders.add(reader.getKey());
            } else {
                unsupportedReaders.add(reader.getKey());
            }
        }
        LocalModFile modInfo3 = null;
        List<Exception> exceptions = new ArrayList<>();
        try {
            FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(file);
            try {
                for (ModMetadataReader reader2 : supportedReaders) {
                    try {
                        try {
                            modInfo3 = reader2.fromFile(this, file, fs);
                            break;
                        } catch (Exception e) {
                            exceptions.add(e);
                        }
                    } catch (Throwable th) {
                        th = th;
                        modInfo = modInfo3;
                        Throwable th2 = th;
                        if (fs == null) {
                            throw th2;
                        }
                        try {
                            fs.close();
                            throw th2;
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                            throw th2;
                        }
                    }
                }
                if (modInfo3 == null) {
                    Iterator<ModMetadataReader> it = unsupportedReaders.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            ModMetadataReader reader3 = it.next();
                            try {
                                modInfo2 = reader3.fromFile(this, file, fs);
                                break;
                            } catch (Exception e2) {
                            }
                        } else {
                            modInfo2 = modInfo3;
                            break;
                        }
                    }
                } else {
                    modInfo2 = modInfo3;
                    break;
                }
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (Throwable th4) {
                        e = th4;
                        localModFile = null;
                        Logging.LOG.warning("Failed to open mod file " + file + e);
                        this.brokenFiles.add(file);
                        return localModFile;
                    }
                }
                if (modInfo2 == null) {
                    Exception exception = new Exception("Failed to read mod metadata");
                    for (Exception e3 : exceptions) {
                        exception.addSuppressed(e3);
                    }
                    Logging.LOG.warning("Failed to read mod metadata:\n" + exception);
                    String fileNameWithoutExtension = FileUtils.getNameWithoutExtension(file);
                    modInfo2 = new LocalModFile(this, getLocalMod(fileNameWithoutExtension, ModLoaderType.UNKNOWN), file, fileNameWithoutExtension, new LocalModFile.Description("litemod".equals(extension) ? "LiteLoader Mod" : ""));
                }
                if (modInfo2.isOld()) {
                    return 0;
                }
                this.localModFiles.add(modInfo2);
                return modInfo2;
            } catch (Throwable th5) {
                th = th5;
                modInfo = null;
            }
        } catch (Throwable th6) {
            e = th6;
            localModFile = null;
        }
    }

    public void refreshMods() throws IOException {
        refreshMods(null);
    }

    public void refreshMods(Consumer<LocalModFile> onScanned) throws IOException {
        this.localModFiles.clear();
        this.localMods.clear();
        this.brokenFiles.clear();
        this.analyzer = LibraryAnalyzer.analyze(getRepository().getResolvedPreservingPatchesVersion(this.id), null);
        if (Files.isDirectory(getModsDirectory(), new LinkOption[0])) {
            DirectoryStream<Path> modsDirectoryStream = Files.newDirectoryStream(getModsDirectory());
            try {
                for (Path subitem : modsDirectoryStream) {
                    if (Files.isDirectory(subitem, new LinkOption[0]) && VersionNumber.isIntVersionNumber(FileUtils.getName(subitem))) {
                        DirectoryStream<Path> subitemDirectoryStream = Files.newDirectoryStream(subitem);
                        try {
                            for (Path subsubitem : subitemDirectoryStream) {
                                LocalModFile mod = addModInfo(subsubitem);
                                if (mod != null && onScanned != null) {
                                    onScanned.accept(mod);
                                }
                            }
                            if (subitemDirectoryStream != null) {
                                subitemDirectoryStream.close();
                            }
                        } catch (Throwable th) {
                            if (subitemDirectoryStream != null) {
                                try {
                                    subitemDirectoryStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } else {
                        LocalModFile mod2 = addModInfo(subitem);
                        if (mod2 != null && onScanned != null) {
                            onScanned.accept(mod2);
                        }
                    }
                }
                if (modsDirectoryStream != null) {
                    modsDirectoryStream.close();
                }
            } catch (Throwable th3) {
                if (modsDirectoryStream != null) {
                    try {
                        modsDirectoryStream.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        }
        this.loaded = true;
    }

    public List<LocalModFile> getMods() throws IOException {
        if (!this.loaded) {
            refreshMods();
        }
        return ModManager$$ExternalSyntheticBackport1.m(this.localModFiles);
    }

    public List<Path> getBrokenFiles() {
        return ModManager$$ExternalSyntheticBackport1.m(this.brokenFiles);
    }

    public void addMod(Path file) throws IOException {
        if (!isFileNameMod(file)) {
            throw new IllegalArgumentException("File " + file + " is not a valid mod file.");
        }
        if (!this.loaded) {
            refreshMods();
        }
        Path modsDirectory = getModsDirectory();
        Files.createDirectories(modsDirectory, new FileAttribute[0]);
        Path newFile = modsDirectory.resolve(file.getFileName());
        FileUtils.copyFile(file, newFile);
        addModInfo(newFile);
    }

    public void addMod(Activity activity, Uri uri, String name) throws IOException {
        if (!isFileNameMod(uri)) {
            throw new IllegalArgumentException("File " + uri + " is not a valid mod file.");
        }
        if (!this.loaded) {
            refreshMods();
        }
        Path modsDirectory = getModsDirectory();
        Files.createDirectories(modsDirectory, new FileAttribute[0]);
        Path newFile = modsDirectory.resolve(name);
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Failed to open content stream");
        }
        FileOutputStream outputStream = new FileOutputStream(newFile.toFile());
        try {
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();
            addModInfo(newFile);
        } catch (Throwable th) {
            try {
                outputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public void removeMods(LocalModFile... localModFiles) throws IOException {
        for (LocalModFile localModFile : localModFiles) {
            Files.deleteIfExists(localModFile.getFile());
        }
    }

    public void rollback(LocalModFile from, LocalModFile to) throws IOException {
        if (!this.loaded) {
            throw new IllegalStateException("ModManager Not loaded");
        }
        if (!this.localModFiles.contains(from)) {
            throw new IllegalStateException("Rolling back an unknown mod " + from.getFileName());
        }
        if (from.isOld()) {
            throw new IllegalArgumentException("Rolling back an old mod " + from.getFileName());
        }
        if (!to.isOld()) {
            throw new IllegalArgumentException("Rolling back to an old path " + to.getFileName());
        }
        if (from.getFileName().equals(to.getFileName())) {
            return;
        }
        LocalMod mod = (LocalMod) Objects.requireNonNull(from.getMod());
        if (mod != to.getMod()) {
            throw new IllegalArgumentException("Rolling back mod " + from.getFileName() + " to a different mod " + to.getFileName());
        }
        if (!mod.getFiles().contains(from) || !mod.getOldFiles().contains(to)) {
            throw new IllegalStateException("LocalMod state corrupt");
        }
        boolean active = from.isActive();
        from.setActive(true);
        from.setOld(true);
        to.setOld(false);
        to.setActive(active);
    }

    private Path backupMod(Path file) throws IOException {
        Path newPath = file.resolveSibling(StringUtils.addSuffix(StringUtils.removeSuffix(FileUtils.getName(file), ".disabled"), ".old"));
        if (Files.exists(file, new LinkOption[0])) {
            Files.move(file, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return newPath;
    }

    private Path restoreMod(Path file) throws IOException {
        Path newPath = file.resolveSibling(StringUtils.removeSuffix(FileUtils.getName(file), ".old"));
        if (Files.exists(file, new LinkOption[0])) {
            Files.move(file, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return newPath;
    }

    public Path setOld(LocalModFile modFile, boolean old) throws IOException {
        if (old) {
            Path newPath = backupMod(modFile.getFile());
            this.localModFiles.remove(modFile);
            return newPath;
        }
        Path newPath2 = modFile.getFile();
        Path newPath3 = restoreMod(newPath2);
        this.localModFiles.add(modFile);
        return newPath3;
    }

    public Path disableMod(Path file) throws IOException {
        if (isOld(file)) {
            return file;
        }
        String fileName = FileUtils.getName(file);
        if (fileName.endsWith(".disabled")) {
            return file;
        }
        Path disabled = file.resolveSibling(fileName + ".disabled");
        if (Files.exists(file, new LinkOption[0])) {
            Files.move(file, disabled, StandardCopyOption.REPLACE_EXISTING);
        }
        return disabled;
    }

    public Path enableMod(Path file) throws IOException {
        if (isOld(file)) {
            return file;
        }
        Path enabled = file.resolveSibling(StringUtils.removeSuffix(FileUtils.getName(file), ".disabled"));
        if (Files.exists(file, new LinkOption[0])) {
            Files.move(file, enabled, StandardCopyOption.REPLACE_EXISTING);
        }
        return enabled;
    }

    public static String getModName(Path file) {
        return StringUtils.removeSuffix(FileUtils.getName(file), ".disabled", ".old");
    }

    public boolean isOld(Path file) {
        return FileUtils.getName(file).endsWith(".old");
    }

    public boolean isDisabled(Path file) {
        return FileUtils.getName(file).endsWith(".disabled");
    }

    public static boolean isFileNameMod(Path file) {
        String name = getModName(file);
        return name.endsWith(".zip") || name.endsWith(".jar") || name.endsWith(".litemod");
    }

    public static boolean isFileNameMod(Uri uri) {
        return isFileNameMod(new File(uri.toString()).toPath());
    }

    public static boolean isFileMod(Path modFile) {
        try {
            FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(modFile);
            try {
                if (!Files.exists(fs.getPath("mcmod.info", new String[0]), new LinkOption[0]) && !Files.exists(fs.getPath("META-INF/mods.toml", new String[0]), new LinkOption[0])) {
                    if (Files.exists(fs.getPath("fabric.mod.json", new String[0]), new LinkOption[0])) {
                        if (fs != null) {
                            fs.close();
                        }
                        return true;
                    }
                    if (Files.exists(fs.getPath("quilt.mod.json", new String[0]), new LinkOption[0])) {
                        if (fs != null) {
                            fs.close();
                        }
                        return true;
                    }
                    if (Files.exists(fs.getPath("litemod.json", new String[0]), new LinkOption[0])) {
                        if (fs != null) {
                            fs.close();
                        }
                        return true;
                    }
                    if (Files.exists(fs.getPath("pack.mcmeta", new String[0]), new LinkOption[0])) {
                        if (fs != null) {
                            fs.close();
                        }
                        return true;
                    }
                    if (fs != null) {
                        fs.close();
                    }
                    return false;
                    return false;
                }
                if (fs != null) {
                    fs.close();
                }
                return true;
            } catch (Throwable th) {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasSimpleMod(String fileName) {
        return Files.exists(getModsDirectory().resolve(StringUtils.removeSuffix(fileName, ".disabled")), new LinkOption[0]) || Files.exists(getModsDirectory().resolve(StringUtils.addSuffix(fileName, ".disabled")), new LinkOption[0]);
    }

    public Path getSimpleModPath(String fileName) {
        return getModsDirectory().resolve(fileName);
    }
}
