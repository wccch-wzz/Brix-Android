package com.brixcore.download;

import com.brixcore.download.cleanroom.CleanroomInstallTask;
import com.brixcore.download.forge.ForgeInstallTask;
import com.brixcore.download.game.GameAssetDownloadTask;
import com.brixcore.download.game.GameDownloadTask;
import com.brixcore.download.game.GameLibrariesTask;
import com.brixcore.download.neoforge.NeoForgeInstallTask;
import com.brixcore.download.optifine.OptiFineInstallTask;
import com.brixcore.game.Artifact;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.Library;
import com.brixcore.game.Version;
import com.brixcore.task.Task;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.function.ExceptionalSupplier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes14.dex */
public class DefaultDependencyManager extends AbstractDependencyManager {
    private final DefaultCacheRepository cacheRepository;
    private final DownloadProvider downloadProvider;
    private final DefaultGameRepository repository;

    public static class UnsupportedLibraryInstallerException extends Exception {
    }

    public DefaultDependencyManager(DefaultGameRepository repository, DownloadProvider downloadProvider, DefaultCacheRepository cacheRepository) {
        this.repository = repository;
        this.downloadProvider = downloadProvider;
        this.cacheRepository = cacheRepository;
    }

    @Override // com.brixcore.download.DependencyManager
    public DefaultGameRepository getGameRepository() {
        return this.repository;
    }

    @Override // com.brixcore.download.AbstractDependencyManager
    public DownloadProvider getDownloadProvider() {
        return this.downloadProvider;
    }

    @Override // com.brixcore.download.AbstractDependencyManager, com.brixcore.download.DependencyManager
    public DefaultCacheRepository getCacheRepository() {
        return this.cacheRepository;
    }

    @Override // com.brixcore.download.DependencyManager
    public GameBuilder gameBuilder() {
        return new DefaultGameBuilder(this);
    }

    @Override // com.brixcore.download.DependencyManager
    public Task<?> checkGameCompletionAsync(final Version version, boolean integrityCheck) {
        return Task.allOf((Task<?>[]) new Task[]{Task.composeAsync(new ExceptionalSupplier() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda13
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$checkGameCompletionAsync$0(version);
            }
        }).thenComposeAsync(checkPatchCompletionAsync(version, integrityCheck)), new GameAssetDownloadTask(this, version, false, integrityCheck), new GameLibrariesTask(this, version, integrityCheck)});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$checkGameCompletionAsync$0(Version version) throws Exception {
        File versionJar = this.repository.getVersionJar(version);
        if (!versionJar.exists() || versionJar.length() == 0) {
            return new GameDownloadTask(this, null, version);
        }
        return null;
    }

    @Override // com.brixcore.download.DependencyManager
    public Task<?> checkLibraryCompletionAsync(Version version, boolean integrityCheck) {
        return new GameLibrariesTask(this, version, integrityCheck, version.getLibraries());
    }

    @Override // com.brixcore.download.DependencyManager
    public Task<?> checkPatchCompletionAsync(final Version version, final boolean integrityCheck) {
        return Task.composeAsync(new ExceptionalSupplier() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda7
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$checkPatchCompletionAsync$5(version, integrityCheck);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$checkPatchCompletionAsync$5(final Version version, final boolean integrityCheck) throws Exception {
        int i;
        Version resolved;
        LibraryAnalyzer analyzer;
        LibraryAnalyzer.LibraryType[] libraryTypeArr;
        int i2 = 0;
        List<Task<?>> tasks = new ArrayList<>(0);
        String gameVersion = this.repository.getGameVersion(version).orElse(null);
        if (gameVersion == null) {
            return null;
        }
        Version original = this.repository.getVersion(version.getId());
        final Version resolved2 = original.resolvePreservingPatches(this.repository);
        LibraryAnalyzer analyzer2 = LibraryAnalyzer.analyze(resolved2, gameVersion);
        LibraryAnalyzer.LibraryType[] libraryTypeArrValues = LibraryAnalyzer.LibraryType.values();
        int length = libraryTypeArrValues.length;
        while (i2 < length) {
            LibraryAnalyzer.LibraryType type = libraryTypeArrValues[i2];
            if (!analyzer2.has(type)) {
                i = i2;
                resolved = resolved2;
                analyzer = analyzer2;
                libraryTypeArr = libraryTypeArrValues;
            } else if (type != LibraryAnalyzer.LibraryType.OPTIFINE) {
                i = i2;
                resolved = resolved2;
                analyzer = analyzer2;
                libraryTypeArr = libraryTypeArrValues;
            } else {
                String optifinePatchVersion = (String) analyzer2.getVersion(type).map(new Function() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda10
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return DefaultDependencyManager.lambda$checkPatchCompletionAsync$1((String) obj);
                    }
                }).orElseGet(new Supplier() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda11
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        return DefaultDependencyManager.lambda$checkPatchCompletionAsync$3(resolved2);
                    }
                });
                boolean needsReInstallation = version.getLibraries().stream().anyMatch(new Predicate() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda12
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return this.f$0.lambda$checkPatchCompletionAsync$4(version, integrityCheck, (Library) obj);
                    }
                });
                if (!needsReInstallation) {
                    i = i2;
                    resolved = resolved2;
                    analyzer = analyzer2;
                    libraryTypeArr = libraryTypeArrValues;
                } else {
                    i = i2;
                    resolved = resolved2;
                    analyzer = analyzer2;
                    libraryTypeArr = libraryTypeArrValues;
                    Library installer = new Library(new Artifact("optifine", "OptiFine", gameVersion + "_" + optifinePatchVersion, "installer"));
                    if (!GameLibrariesTask.shouldDownloadLibrary(this.repository, version, installer, integrityCheck)) {
                        tasks.add(OptiFineInstallTask.install(this, original, this.repository.getLibraryFile(version, installer).toPath()));
                    } else {
                        tasks.add(installLibraryAsync(gameVersion, original, "optifine", optifinePatchVersion));
                    }
                }
            }
            i2 = i + 1;
            resolved2 = resolved;
            analyzer2 = analyzer;
            libraryTypeArrValues = libraryTypeArr;
        }
        return Task.allOf(tasks);
    }

    static /* synthetic */ String lambda$checkPatchCompletionAsync$1(String optifineVersion) {
        Matcher matcher = Pattern.compile("^([0-9.]+)_(?<optifine>HD_.+)$").matcher(optifineVersion);
        return matcher.find() ? matcher.group("optifine") : optifineVersion;
    }

    static /* synthetic */ String lambda$checkPatchCompletionAsync$3(Version resolved) {
        return (String) resolved.getPatches().stream().filter(new Predicate() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return "optifine".equals(((Version) obj).getId());
            }
        }).findAny().map(new Function() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Version) obj).getVersion();
            }
        }).orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$checkPatchCompletionAsync$4(Version version, boolean integrityCheck, Library library) {
        return !library.hasDownloadURL() && "optifine".equals(library.getGroupId()) && GameLibrariesTask.shouldDownloadLibrary(this.repository, version, library, integrityCheck);
    }

    @Override // com.brixcore.download.DependencyManager
    public Task<Version> installLibraryAsync(final String gameVersion, final Version baseVersion, final String libraryId, final String libraryVersion) {
        if (baseVersion.isResolved()) {
            throw new IllegalArgumentException("Version should not be resolved");
        }
        final VersionList<?> versionList = getVersionList(libraryId);
        return Task.fromCompletableFuture(versionList.loadAsync(gameVersion)).thenComposeAsync(new ExceptionalSupplier() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda1
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$installLibraryAsync$7(baseVersion, versionList, gameVersion, libraryVersion, libraryId);
            }
        }).withStage(String.format("Brix.install.%s:%s", libraryId, libraryVersion));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$installLibraryAsync$7(Version baseVersion, VersionList versionList, String gameVersion, final String libraryVersion, final String libraryId) throws Exception {
        return installLibraryAsync(baseVersion, (RemoteVersion) versionList.getVersion(gameVersion, libraryVersion).orElseThrow(new Supplier() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return DefaultDependencyManager.lambda$installLibraryAsync$6(libraryId, libraryVersion);
            }
        }));
    }

    static /* synthetic */ IOException lambda$installLibraryAsync$6(String libraryId, String libraryVersion) {
        return new IOException("Remote library " + libraryId + " has no version " + libraryVersion);
    }

    @Override // com.brixcore.download.DependencyManager
    public Task<Version> installLibraryAsync(Version baseVersion, final RemoteVersion libraryVersion) {
        if (baseVersion.isResolved()) {
            throw new IllegalArgumentException("Version should not be resolved");
        }
        final AtomicReference<Version> removedLibraryVersion = new AtomicReference<>();
        return removeLibraryAsync(baseVersion.resolvePreservingPatches(this.repository), libraryVersion.getLibraryId()).thenComposeAsync(new ExceptionalFunction() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda8
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$installLibraryAsync$8(removedLibraryVersion, libraryVersion, (Version) obj);
            }
        }).thenApplyAsync(new ExceptionalFunction() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda9
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return DefaultDependencyManager.lambda$installLibraryAsync$9(removedLibraryVersion, (Version) obj);
            }
        }).withStage(String.format("Brix.install.%s:%s", libraryVersion.getLibraryId(), libraryVersion.getSelfVersion()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$installLibraryAsync$8(AtomicReference removedLibraryVersion, RemoteVersion libraryVersion, Version version) throws RuntimeException {
        removedLibraryVersion.set(version);
        return libraryVersion.getInstallTask(this, version);
    }

    static /* synthetic */ Version lambda$installLibraryAsync$9(AtomicReference removedLibraryVersion, Version patch) throws RuntimeException {
        return patch == null ? (Version) removedLibraryVersion.get() : ((Version) removedLibraryVersion.get()).addPatch(patch);
    }

    public Task<Version> installLibraryAsync(final Version oldVersion, final Path installer) {
        if (oldVersion.isResolved()) {
            throw new IllegalArgumentException("Version should not be resolved");
        }
        Task taskComposeAsync = Task.composeAsync(new ExceptionalSupplier() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda3
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return this.f$0.lambda$installLibraryAsync$10(oldVersion, installer);
            }
        });
        Objects.requireNonNull(oldVersion);
        return taskComposeAsync.thenApplyAsync(new ExceptionalFunction() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda4
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return oldVersion.addPatch((Version) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$installLibraryAsync$10(Version oldVersion, Path installer) throws Exception {
        try {
            return CleanroomInstallTask.install(this, oldVersion, installer);
        } catch (IOException e) {
            try {
                return NeoForgeInstallTask.install(this, oldVersion, installer);
            } catch (IOException e2) {
                try {
                    return ForgeInstallTask.install(this, oldVersion, installer);
                } catch (IOException e3) {
                    try {
                        return OptiFineInstallTask.install(this, oldVersion, installer);
                    } catch (IOException e4) {
                        throw new UnsupportedLibraryInstallerException();
                    }
                }
            }
        }
    }

    public Task<Version> removeLibraryAsync(Version version, final String libraryId) {
        if (version.isResolved()) {
            throw new IllegalArgumentException("removeLibraryWithoutSavingAsync requires non-resolved version");
        }
        final Version independentVersion = version.resolvePreservingPatches(this.repository);
        final String gameVersion = this.repository.getGameVersion(independentVersion).orElse(null);
        return Task.supplyAsync(new Callable() { // from class: com.brixcore.download.DefaultDependencyManager$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return LibraryAnalyzer.analyze(independentVersion, gameVersion).removeLibrary(libraryId).build();
            }
        });
    }
}
