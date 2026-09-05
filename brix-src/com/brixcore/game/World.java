package com.brixcore.game;

import com.brixcore.util.Logging;
import com.brixcore.util.function.ExceptionalConsumer;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.Unzipper;
import com.brixcore.util.io.Zipper;
import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: classes2.dex */
public class World {
    private final Path file;
    private String fileName;
    private String gameVersion;
    private long lastPlayed;
    private String worldName;

    public World(Path file) throws IOException {
        this.file = file;
        if (Files.isDirectory(file, new LinkOption[0])) {
            loadFromDirectory();
        } else {
            if (Files.isRegularFile(file, new LinkOption[0])) {
                loadFromZip();
                return;
            }
            throw new IOException("Path " + file + " cannot be recognized as a Minecraft world");
        }
    }

    private void loadFromDirectory() throws IOException {
        this.fileName = FileUtils.getName(this.file);
        Path levelDat = this.file.resolve("level.dat");
        getWorldName(levelDat);
    }

    public Path getFile() {
        return this.file;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public Path getLevelDatFile() {
        return this.file.resolve("level.dat");
    }

    public long getLastPlayed() {
        return this.lastPlayed;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    private void loadFromZipImpl(Path root) throws IOException {
        Path levelDat = root.resolve("level.dat");
        if (!Files.exists(levelDat, new LinkOption[0])) {
            throw new IOException("Not a valid world zip file since level.dat cannot be found.");
        }
        getWorldName(levelDat);
    }

    private void loadFromZip() throws IOException {
        FileSystem fs = CompressingUtils.readonly(this.file).setAutoDetectEncoding(true).build();
        try {
            Path cur = fs.getPath("/level.dat", new String[0]);
            if (Files.isRegularFile(cur, new LinkOption[0])) {
                this.fileName = FileUtils.getName(this.file);
                loadFromZipImpl(fs.getPath("/", new String[0]));
                if (fs != null) {
                    fs.close();
                    return;
                }
                return;
            }
            Stream<Path> stream = Files.list(fs.getPath("/", new String[0]));
            try {
                Path root = stream.filter(new Predicate() { // from class: com.brixcore.game.World$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return Files.isDirectory((Path) obj, new LinkOption[0]);
                    }
                }).findAny().orElseThrow(new Supplier() { // from class: com.brixcore.game.World$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        return World.lambda$loadFromZip$1();
                    }
                });
                this.fileName = FileUtils.getName(root);
                loadFromZipImpl(root);
                if (stream != null) {
                    stream.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            if (fs != null) {
                try {
                    fs.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    static /* synthetic */ IOException lambda$loadFromZip$1() {
        return new IOException("Not a valid world zip file");
    }

    private void getWorldName(Path levelDat) throws IOException {
        CompoundTag nbt = parseLevelDat(levelDat);
        CompoundTag data = (CompoundTag) nbt.get("Data");
        if (data == null) {
            throw new IOException("level.dat missing Data");
        }
        if (data.get("LevelName") instanceof StringTag) {
            this.worldName = ((StringTag) data.get("LevelName")).getValue();
            if (data.get("LastPlayed") instanceof LongTag) {
                this.lastPlayed = ((LongTag) data.get("LastPlayed")).getValue().longValue();
                this.gameVersion = null;
                if (data.get("Version") instanceof CompoundTag) {
                    CompoundTag version = (CompoundTag) data.get("Version");
                    if (version.get("Name") instanceof StringTag) {
                        this.gameVersion = ((StringTag) version.get("Name")).getValue();
                        return;
                    }
                    return;
                }
                return;
            }
            throw new IOException("level.dat missing LastPlayed");
        }
        throw new IOException("level.dat missing LevelName");
    }

    public void rename(String newName) throws IOException {
        if (!Files.isDirectory(this.file, new LinkOption[0])) {
            throw new IOException("Not a valid world directory");
        }
        CompoundTag nbt = readLevelDat();
        CompoundTag data = (CompoundTag) nbt.get("Data");
        data.put(new StringTag("LevelName", newName));
        writeLevelDat(nbt);
        Files.move(this.file, this.file.resolveSibling(newName), new CopyOption[0]);
    }

    public void install(Path savesDir, String name) throws IOException {
        try {
            Path worldDir = savesDir.resolve(name);
            if (!Files.isDirectory(worldDir, new LinkOption[0])) {
                if (!Files.isRegularFile(this.file, new LinkOption[0])) {
                    if (Files.isDirectory(this.file, new LinkOption[0])) {
                        FileUtils.copyDirectory(this.file, worldDir);
                        return;
                    }
                    return;
                }
                FileSystem fs = CompressingUtils.readonly(this.file).setAutoDetectEncoding(true).build();
                try {
                    Path cur = fs.getPath("/level.dat", new String[0]);
                    if (!Files.isRegularFile(cur, new LinkOption[0])) {
                        Stream<Path> stream = Files.list(fs.getPath("/", new String[0]));
                        try {
                            List<Path> subDirs = (List) stream.collect(Collectors.toList());
                            if (subDirs.size() != 1) {
                                throw new IOException("World zip malformed");
                            }
                            String subDirectoryName = FileUtils.getName(subDirs.get(0));
                            new Unzipper(this.file, worldDir).setSubDirectory("/" + subDirectoryName + "/").unzip();
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } else {
                        this.fileName = FileUtils.getName(this.file);
                        new Unzipper(this.file, worldDir).unzip();
                    }
                    if (fs != null) {
                        fs.close();
                    }
                    new World(worldDir).rename(name);
                    return;
                } catch (Throwable th3) {
                    if (fs != null) {
                        try {
                            fs.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            }
            throw new FileAlreadyExistsException("World already exists");
        } catch (InvalidPathException e) {
            throw new IOException(e);
        }
    }

    public void export(Path zip, String worldName) throws IOException {
        if (!Files.isDirectory(this.file, new LinkOption[0])) {
            throw new IOException();
        }
        Zipper zipper = new Zipper(zip);
        try {
            zipper.putDirectory(this.file, worldName);
            zipper.close();
        } catch (Throwable th) {
            try {
                zipper.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public CompoundTag readLevelDat() throws IOException {
        if (!Files.isDirectory(this.file, new LinkOption[0])) {
            throw new IOException("Not a valid world directory");
        }
        return parseLevelDat(getLevelDatFile());
    }

    public void writeLevelDat(final CompoundTag nbt) throws IOException {
        if (!Files.isDirectory(this.file, new LinkOption[0])) {
            throw new IOException("Not a valid world directory");
        }
        FileUtils.saveSafely(getLevelDatFile(), (ExceptionalConsumer<? super OutputStream, IOException>) new ExceptionalConsumer() { // from class: com.brixcore.game.World$$ExternalSyntheticLambda4
            @Override // com.brixcore.util.function.ExceptionalConsumer
            public final void accept(Object obj) throws IOException {
                World.lambda$writeLevelDat$2(nbt, (OutputStream) obj);
            }
        });
    }

    static /* synthetic */ void lambda$writeLevelDat$2(CompoundTag nbt, OutputStream os) throws IOException {
        OutputStream gos = new GZIPOutputStream(os);
        try {
            NBTIO.writeTag(gos, nbt);
            gos.close();
        } catch (Throwable th) {
            try {
                gos.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static CompoundTag parseLevelDat(Path path) throws IOException {
        InputStream is = new GZIPInputStream(Files.newInputStream(path, new OpenOption[0]));
        try {
            Tag nbt = NBTIO.readTag(is);
            if (nbt instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag) nbt;
                is.close();
                return compoundTag;
            }
            throw new IOException("level.dat malformed");
        } catch (Throwable th) {
            try {
                is.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static List<World> getWorlds(Path savesDir) {
        try {
            if (Files.exists(savesDir, new LinkOption[0])) {
                Stream<Path> stream = Files.list(savesDir);
                try {
                    List<World> list = (List) stream.filter(new Predicate() { // from class: com.brixcore.game.World$$ExternalSyntheticLambda2
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            return Files.isDirectory((Path) obj, new LinkOption[0]);
                        }
                    }).flatMap(new Function() { // from class: com.brixcore.game.World$$ExternalSyntheticLambda3
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return World.lambda$getWorlds$4((Path) obj);
                        }
                    }).collect(Collectors.toList());
                    if (stream != null) {
                        stream.close();
                    }
                    return list;
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Failed to read saves", (Throwable) e);
        }
        return Collections.EMPTY_LIST;
    }

    static /* synthetic */ Stream lambda$getWorlds$4(Path world) {
        try {
            return Stream.of(new World(world));
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Failed to read world " + world, (Throwable) e);
            return Stream.empty();
        }
    }
}
