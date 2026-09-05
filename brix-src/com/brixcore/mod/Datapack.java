package com.brixcore.mod;

import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.property.SimpleBooleanProperty;
import com.brixcore.fakefx.collections.FXCollections;
import com.brixcore.fakefx.collections.ObservableList;
import com.brixcore.mod.modinfo.PackMcMeta;
import com.brixcore.util.Logging;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.Unzipper;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes2.dex */
public class Datapack {
    private static final String DISABLED_EXT = "disabled";
    private final ObservableList<Pack> info = FXCollections.observableArrayList();
    private boolean isMultiple;
    private final Path path;

    public Datapack(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public ObservableList<Pack> getInfo() {
        return this.info;
    }

    public void installTo(Path worldPath) throws IOException {
        Path datapacks = worldPath.resolve("datapacks");
        Set<String> packs = new HashSet<>();
        for (Pack pack : this.info) {
            packs.add(pack.getId());
        }
        if (Files.isDirectory(datapacks, new LinkOption[0])) {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(datapacks);
            try {
                for (Path datapack : directoryStream) {
                    if (Files.isDirectory(datapack, new LinkOption[0]) && packs.contains(FileUtils.getName(datapack))) {
                        FileUtils.deleteDirectory(datapack.toFile());
                    } else if (Files.isRegularFile(datapack, new LinkOption[0]) && packs.contains(FileUtils.getNameWithoutExtension(datapack))) {
                        Files.delete(datapack);
                    }
                }
                if (directoryStream != null) {
                    directoryStream.close();
                }
            } catch (Throwable th) {
                if (directoryStream != null) {
                    try {
                        directoryStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (this.isMultiple) {
            new Unzipper(this.path, worldPath).setReplaceExistentFile(true).setFilter(new Unzipper.FileFilter() { // from class: com.brixcore.mod.Datapack.1
                @Override // com.brixcore.util.io.Unzipper.FileFilter
                public boolean accept(Path destPath, boolean isDirectory, Path zipEntry, String entryPath) {
                    return !entryPath.equals("resources.zip");
                }
            }).unzip();
            FileSystem dest = CompressingUtils.createWritableZipFileSystem(worldPath.resolve("resources.zip"));
            try {
                FileSystem zip = CompressingUtils.createReadOnlyZipFileSystem(this.path);
                try {
                    Path resourcesZip = zip.getPath("resources.zip", new String[0]);
                    if (Files.isRegularFile(resourcesZip, new LinkOption[0])) {
                        Path temp = Files.createTempFile("Brix", ".zip", new FileAttribute[0]);
                        Files.copy(resourcesZip, temp, StandardCopyOption.REPLACE_EXISTING);
                        FileSystem resources = CompressingUtils.createReadOnlyZipFileSystem(temp);
                        try {
                            FileUtils.copyDirectory(resources.getPath("/", new String[0]), dest.getPath("/", new String[0]));
                            if (resources != null) {
                                resources.close();
                            }
                        } catch (Throwable th3) {
                            if (resources != null) {
                                try {
                                    resources.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            }
                            throw th3;
                        }
                    }
                    Path packMcMeta = dest.getPath("pack.mcmeta", new String[0]);
                    Files.write(packMcMeta, Arrays.asList("{", "\t\"pack\": {", "\t\t\"pack_format\": 4,", "\t\t\"description\": \"Modified by Brix.\"", "\t}", "}"), StandardOpenOption.CREATE);
                    Path packPng = dest.getPath("pack.png", new String[0]);
                    if (Files.isRegularFile(packPng, new LinkOption[0])) {
                        Files.delete(packPng);
                    }
                    if (zip != null) {
                        zip.close();
                    }
                    if (dest != null) {
                        dest.close();
                        return;
                    }
                    return;
                } catch (Throwable th5) {
                    if (zip != null) {
                        try {
                            zip.close();
                        } catch (Throwable th6) {
                            th5.addSuppressed(th6);
                        }
                    }
                    throw th5;
                }
            } catch (Throwable th7) {
                if (dest != null) {
                    try {
                        dest.close();
                    } catch (Throwable th8) {
                        th7.addSuppressed(th8);
                    }
                }
                throw th7;
            }
        }
        FileUtils.copyFile(this.path.toFile(), datapacks.resolve(FileUtils.getName(this.path)).toFile());
    }

    public void deletePack(final Pack pack) throws IOException {
        Path subPath = pack.file;
        if (Files.isDirectory(subPath, new LinkOption[0])) {
            FileUtils.deleteDirectory(subPath.toFile());
        } else if (Files.isRegularFile(subPath, new LinkOption[0])) {
            Files.delete(subPath);
        }
        this.info.removeIf(new Predicate() { // from class: com.brixcore.mod.Datapack$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Datapack.Pack) obj).getId().equals(pack.getId());
            }
        });
    }

    public void loadFromZip() throws IOException {
        FileSystem fs = CompressingUtils.readonly(this.path).setAutoDetectEncoding(true).build();
        try {
            Path datapacks = fs.getPath("/datapacks/", new String[0]);
            Path mcmeta = fs.getPath("pack.mcmeta", new String[0]);
            if (Files.exists(datapacks, new LinkOption[0])) {
                this.isMultiple = true;
                loadFromDir(datapacks);
            } else if (Files.exists(mcmeta, new LinkOption[0])) {
                this.isMultiple = false;
                try {
                    PackMcMeta pack = (PackMcMeta) JsonUtils.fromNonNullJson(FileUtils.readText(mcmeta), PackMcMeta.class);
                    this.info.add(new Pack(this.path, FileUtils.getNameWithoutExtension(this.path), pack.pack().description(), this));
                } catch (Exception e) {
                    Logging.LOG.log(Level.WARNING, "Failed to read datapack " + this.path, (Throwable) e);
                }
            } else {
                throw new IOException("Malformed datapack zip");
            }
            if (fs != null) {
                fs.close();
            }
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
    }

    public void loadFromDir() {
        try {
            loadFromDir(this.path);
        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Failed to read datapacks " + this.path, (Throwable) e);
        }
    }

    private void loadFromDir(Path dir) throws IOException {
        PackMcMeta pack;
        int i;
        List<Pack> info = new ArrayList<>();
        int i2 = 0;
        if (Files.isDirectory(dir, new LinkOption[0])) {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
            try {
                for (Path subDir : directoryStream) {
                    if (Files.isDirectory(subDir, new LinkOption[i2])) {
                        Path mcmeta = subDir.resolve("pack.mcmeta");
                        Path mcmetaDisabled = subDir.resolve("pack.mcmeta.disabled");
                        if (Files.exists(mcmeta, new LinkOption[i2]) || Files.exists(mcmetaDisabled, new LinkOption[i2])) {
                            boolean enabled = Files.exists(mcmeta, new LinkOption[i2]);
                            if (enabled) {
                                try {
                                    pack = (PackMcMeta) JsonUtils.fromNonNullJson(FileUtils.readText(mcmeta), PackMcMeta.class);
                                } catch (JsonParseException | IOException e) {
                                    Logging.LOG.log(Level.WARNING, "Failed to read datapack " + subDir, (Throwable) e);
                                }
                            } else {
                                pack = (PackMcMeta) JsonUtils.fromNonNullJson(FileUtils.readText(mcmetaDisabled), PackMcMeta.class);
                            }
                            info.add(new Pack(enabled ? mcmeta : mcmetaDisabled, FileUtils.getName(subDir), pack.pack().description(), this));
                            i = 0;
                            i2 = i;
                        }
                    } else {
                        if (Files.isRegularFile(subDir, new LinkOption[0])) {
                            try {
                                FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(subDir);
                                try {
                                    Path mcmeta2 = fs.getPath("pack.mcmeta", new String[0]);
                                    try {
                                        if (Files.exists(mcmeta2, new LinkOption[0])) {
                                            String name = FileUtils.getName(subDir);
                                            if (name.endsWith(".disabled")) {
                                                i = 0;
                                                try {
                                                    name = name.substring(0, name.length() - ".disabled".length());
                                                } catch (Throwable th) {
                                                    th = th;
                                                    Throwable th2 = th;
                                                    if (fs != null) {
                                                        try {
                                                            fs.close();
                                                        } catch (Throwable th3) {
                                                            th2.addSuppressed(th3);
                                                        }
                                                    }
                                                    throw th2;
                                                }
                                            } else {
                                                i = 0;
                                            }
                                            if (name.endsWith(".zip")) {
                                                String name2 = StringUtils.substringBeforeLast(name, ".zip");
                                                PackMcMeta pack2 = (PackMcMeta) JsonUtils.fromNonNullJson(FileUtils.readText(mcmeta2), PackMcMeta.class);
                                                info.add(new Pack(subDir, name2, pack2.pack().description(), this));
                                                if (fs != null) {
                                                    fs.close();
                                                }
                                            } else {
                                                if (fs != null) {
                                                    try {
                                                        fs.close();
                                                    } catch (JsonParseException e2) {
                                                        e = e2;
                                                        Logging.LOG.log(Level.WARNING, "Failed to read datapack " + subDir, (Throwable) e);
                                                        i2 = i;
                                                    } catch (IOException e3) {
                                                        e = e3;
                                                        Logging.LOG.log(Level.WARNING, "Failed to read datapack " + subDir, (Throwable) e);
                                                        i2 = i;
                                                    }
                                                }
                                                i2 = i;
                                            }
                                        } else {
                                            if (fs != null) {
                                                fs.close();
                                            }
                                            i2 = 0;
                                        }
                                    } catch (Throwable th4) {
                                        th = th4;
                                        i = 0;
                                    }
                                } catch (Throwable th5) {
                                    th = th5;
                                    i = 0;
                                }
                            } catch (JsonParseException | IOException e4) {
                                e = e4;
                                i = 0;
                            }
                        } else {
                            i = 0;
                        }
                        i2 = i;
                    }
                }
                if (directoryStream != null) {
                    directoryStream.close();
                }
            } catch (Throwable th6) {
                if (directoryStream == null) {
                    throw th6;
                }
                try {
                    directoryStream.close();
                    throw th6;
                } catch (Throwable th7) {
                    th6.addSuppressed(th7);
                    throw th6;
                }
            }
        }
        this.info.setAll(info);
    }

    public static class Pack {
        private final BooleanProperty active;
        private final Datapack datapack;
        private final LocalModFile.Description description;
        private Path file;
        private final String id;

        public Pack(Path file, String id, LocalModFile.Description description, Datapack datapack) {
            this.file = file;
            this.id = id;
            this.description = description;
            this.datapack = datapack;
            this.active = new SimpleBooleanProperty(this, "active", !Datapack.DISABLED_EXT.equals(FileUtils.getExtension(file))) { // from class: com.brixcore.mod.Datapack.Pack.1
                @Override // com.brixcore.fakefx.beans.property.BooleanPropertyBase
                protected void invalidated() {
                    Path newF;
                    Path f = Pack.this.file.toAbsolutePath();
                    if (Datapack.DISABLED_EXT.equals(FileUtils.getExtension(f))) {
                        newF = f.getParent().resolve(FileUtils.getNameWithoutExtension(f));
                    } else {
                        Path newF2 = f.getParent();
                        newF = newF2.resolve(FileUtils.getName(f) + "." + Datapack.DISABLED_EXT);
                    }
                    try {
                        Files.move(f, newF, new CopyOption[0]);
                        Pack.this.file = newF;
                    } catch (IOException e) {
                        Logging.LOG.warning("Unable to rename file " + f + " to " + newF);
                    }
                }
            };
        }

        public String getId() {
            return this.id;
        }

        public LocalModFile.Description getDescription() {
            return this.description;
        }

        public Datapack getDatapack() {
            return this.datapack;
        }

        public BooleanProperty activeProperty() {
            return this.active;
        }

        public boolean isActive() {
            return this.active.get();
        }

        public void setActive(boolean active) {
            this.active.set(active);
        }
    }
}
