package com.brixcore.mod;

import com.brixcore.fakefx.beans.property.BooleanProperty;
import com.brixcore.fakefx.beans.property.SimpleBooleanProperty;
import com.brixcore.util.Logging;
import com.brixcore.util.io.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes2.dex */
public final class LocalModFile implements Comparable<LocalModFile> {
    private final BooleanProperty activeProperty;
    private final String authors;
    private final Description description;
    private Path file;
    private final String fileName;
    private final String gameVersion;
    private final String logoPath;
    private final LocalMod mod;
    private final ModManager modManager;
    private final String name;
    private RemoteMod.Version remoteVersion;
    private final String url;
    private final String version;

    public LocalModFile(ModManager modManager, LocalMod mod, Path file, String name, Description description) {
        this(modManager, mod, file, name, description, "", "", "", "", "");
    }

    public LocalModFile(final ModManager modManager, LocalMod mod, Path file, String name, Description description, String authors, String version, String gameVersion, String url, String logoPath) {
        this.modManager = modManager;
        this.mod = mod;
        this.file = file;
        this.name = name;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.gameVersion = gameVersion;
        this.url = url;
        this.logoPath = logoPath;
        this.activeProperty = new SimpleBooleanProperty(this, "active", !modManager.isDisabled(file)) { // from class: com.brixcore.mod.LocalModFile.1
            @Override // com.brixcore.fakefx.beans.property.BooleanPropertyBase
            protected void invalidated() {
                if (LocalModFile.this.isOld()) {
                    return;
                }
                Path path = LocalModFile.this.file.toAbsolutePath();
                try {
                    if (get()) {
                        LocalModFile.this.file = modManager.enableMod(path);
                    } else {
                        LocalModFile.this.file = modManager.disableMod(path);
                    }
                } catch (IOException e) {
                    Logging.LOG.log(Level.SEVERE, "Unable to invert state of mod file " + path, (Throwable) e);
                }
            }
        };
        this.fileName = FileUtils.getNameWithoutExtension(ModManager.getModName(file));
        if (isOld()) {
            mod.getOldFiles().add(this);
        } else {
            mod.getFiles().add(this);
        }
    }

    public ModManager getModManager() {
        return this.modManager;
    }

    public LocalMod getMod() {
        return this.mod;
    }

    public Path getFile() {
        return this.file;
    }

    public ModLoaderType getModLoaderType() {
        return this.mod.getModLoaderType();
    }

    public String getId() {
        return this.mod.getId();
    }

    public String getName() {
        return this.name;
    }

    public Description getDescription() {
        return this.description;
    }

    public String getAuthors() {
        return this.authors;
    }

    public String getVersion() {
        return this.version;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getUrl() {
        return this.url;
    }

    public String getLogoPath() {
        return this.logoPath;
    }

    public BooleanProperty activeProperty() {
        return this.activeProperty;
    }

    public boolean isActive() {
        return this.activeProperty.get();
    }

    public void setActive(boolean active) {
        this.activeProperty.set(active);
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isOld() {
        return this.modManager.isOld(this.file);
    }

    public void setOld(boolean old) throws IOException {
        this.file = this.modManager.setOld(this, old);
        if (old) {
            this.mod.getFiles().remove(this);
            this.mod.getOldFiles().add(this);
        } else {
            this.mod.getOldFiles().remove(this);
            this.mod.getFiles().add(this);
        }
    }

    public void disable() throws IOException {
        this.file = this.modManager.disableMod(this.file);
    }

    public ModUpdate checkUpdates(final String gameVersion, RemoteModRepository repository) throws IOException {
        Optional<RemoteMod.Version> currentVersion = Optional.empty();
        try {
            currentVersion = repository.getRemoteVersionByLocalFile(this, this.file);
        } catch (Throwable e) {
            System.gc();
            Logging.LOG.log(Level.SEVERE, e.toString());
        }
        if (LocalModFile$$ExternalSyntheticBackport0.m(currentVersion)) {
            return null;
        }
        final Optional<RemoteMod.Version> finalCurrentVersion = currentVersion;
        List<RemoteMod.Version> remoteVersions = (List) repository.getRemoteVersionsById(currentVersion.get().getModid()).filter(new Predicate() { // from class: com.brixcore.mod.LocalModFile$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((RemoteMod.Version) obj).getGameVersions().contains(gameVersion);
            }
        }).filter(new Predicate() { // from class: com.brixcore.mod.LocalModFile$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$checkUpdates$1((RemoteMod.Version) obj);
            }
        }).filter(new Predicate() { // from class: com.brixcore.mod.LocalModFile$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return LocalModFile.lambda$checkUpdates$2(finalCurrentVersion, (RemoteMod.Version) obj);
            }
        }).sorted(Comparator.comparing(new Function() { // from class: com.brixcore.mod.LocalModFile$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((RemoteMod.Version) obj).getDatePublished();
            }
        }).reversed()).collect(Collectors.toList());
        if (remoteVersions.isEmpty()) {
            return null;
        }
        return new ModUpdate(this, currentVersion.get(), remoteVersions);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$checkUpdates$1(RemoteMod.Version version) {
        return version.getLoaders().contains(getModLoaderType());
    }

    static /* synthetic */ boolean lambda$checkUpdates$2(Optional finalCurrentVersion, RemoteMod.Version version) {
        return version.getDatePublished().compareTo(((RemoteMod.Version) finalCurrentVersion.get()).getDatePublished()) > 0;
    }

    @Override // java.lang.Comparable
    public int compareTo(LocalModFile o) {
        return getFileName().compareToIgnoreCase(o.getFileName());
    }

    public boolean equals(Object obj) {
        return (obj instanceof LocalModFile) && Objects.equals(getFileName(), ((LocalModFile) obj).getFileName());
    }

    public int hashCode() {
        return Objects.hash(getFileName());
    }

    public RemoteMod.Version getRemoteVersion() {
        return this.remoteVersion;
    }

    public void setRemoteVersion(RemoteMod.Version remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public static class ModUpdate {
        private final List<RemoteMod.Version> candidates;
        private final RemoteMod.Version currentVersion;
        private final LocalModFile localModFile;

        public ModUpdate(LocalModFile localModFile, RemoteMod.Version currentVersion, List<RemoteMod.Version> candidates) {
            this.localModFile = localModFile;
            this.currentVersion = currentVersion;
            this.candidates = candidates;
        }

        public LocalModFile getLocalMod() {
            return this.localModFile;
        }

        public RemoteMod.Version getCurrentVersion() {
            return this.currentVersion;
        }

        public List<RemoteMod.Version> getCandidates() {
            return this.candidates;
        }
    }

    public static class Description {
        private final List<Part> parts;

        public Description(String text) {
            this.parts = new ArrayList();
            this.parts.add(new Part(text, "black"));
        }

        public Description(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return this.parts;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Part part : this.parts) {
                builder.append(part.text);
            }
            return builder.toString();
        }

        public static class Part {
            private final String color;
            private final String text;

            public Part(String text) {
                this(text, "");
            }

            public Part(String text, String color) {
                this.text = (String) Objects.requireNonNull(text);
                this.color = (String) Objects.requireNonNull(color);
            }

            public String getText() {
                return this.text;
            }

            public String getColor() {
                return this.color;
            }
        }
    }
}
