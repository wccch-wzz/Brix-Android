package com.brixcore.download;

import com.brixcore.game.Argument;
import com.brixcore.game.Arguments;
import com.brixcore.game.Library;
import com.brixcore.game.StringArgument;
import com.brixcore.game.Version;
import com.brixcore.game.VersionProvider;
import com.brixcore.mod.ModLoaderType;
import com.brixcore.util.Lang;
import com.brixcore.util.Pair;
import com.brixcore.util.versioning.VersionNumber;
import com.brixcore.util.versioning.VersionRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes14.dex */
public final class LibraryAnalyzer implements Iterable<LibraryMark> {
    public static final String LITELOADER_TWEAKER = "com.mumfrey.liteloader.launch.LiteLoaderTweaker";
    private final Map<String, Pair<Library, String>> libraries;
    private Version version;
    public static final String VANILLA_MAIN = "net.minecraft.client.main.Main";
    public static final String LAUNCH_WRAPPER_MAIN = "net.minecraft.launchwrapper.Launch";
    public static final String MOD_LAUNCHER_MAIN = "cpw.mods.modlauncher.Launcher";
    public static final String BOOTSTRAP_LAUNCHER_MAIN = "cpw.mods.bootstraplauncher.BootstrapLauncher";
    public static final String FORGE_BOOTSTRAP_MAIN = "net.minecraftforge.bootstrap.ForgeBootstrap";
    public static final Set<String> FORGE_OPTIFINE_MAIN = new HashSet(Lang.immutableListOf(VANILLA_MAIN, LAUNCH_WRAPPER_MAIN, MOD_LAUNCHER_MAIN, BOOTSTRAP_LAUNCHER_MAIN, FORGE_BOOTSTRAP_MAIN));
    public static final VersionRange<VersionNumber> FORGE_OPTIFINE_BROKEN_RANGE = VersionNumber.between("48.0.0", "49.0.50");
    public static final String[] FORGE_TWEAKERS = {"net.minecraftforge.legacy._1_5_2.LibraryFixerTweaker", "cpw.mods.fml.common.launcher.FMLTweaker", "net.minecraftforge.fml.common.launcher.FMLTweaker"};
    public static final String[] OPTIFINE_TWEAKERS = {"optifine.OptiFineTweaker", "optifine.OptiFineForgeTweaker"};

    private LibraryAnalyzer(Version version, Map<String, Pair<Library, String>> libraries) {
        this.version = version;
        this.libraries = libraries;
    }

    public Optional<String> getVersion(LibraryType type) {
        return getVersion(type.getPatchId());
    }

    public Optional<String> getVersion(String type) {
        return Optional.ofNullable(this.libraries.get(type)).map(new Function() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (String) ((Pair) obj).getValue();
            }
        });
    }

    public Optional<Library> getLibrary(LibraryType type) {
        return Optional.ofNullable(this.libraries.get(type.getPatchId())).map(new Function() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (Library) ((Pair) obj).getKey();
            }
        });
    }

    public LibraryMark.LibraryStatus getLibraryStatus(String type) {
        return this.version.hasPatch(type) ? LibraryMark.LibraryStatus.CLEAR : LibraryMark.LibraryStatus.JUST_EXISTED;
    }

    @Override // java.lang.Iterable
    public Iterator<LibraryMark> iterator() {
        return new Iterator<LibraryMark>() { // from class: com.brixcore.download.LibraryAnalyzer.1
            Iterator<Map.Entry<String, Pair<Library, String>>> impl;

            {
                this.impl = LibraryAnalyzer.this.libraries.entrySet().iterator();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.impl.hasNext();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public LibraryMark next() {
                Map.Entry<String, Pair<Library, String>> entry = this.impl.next();
                return new LibraryMark(entry.getKey(), entry.getValue().getValue(), LibraryAnalyzer.this.getLibraryStatus(entry.getKey()));
            }
        };
    }

    public boolean has(LibraryType type) {
        return has(type.getPatchId());
    }

    public boolean has(String type) {
        return this.libraries.containsKey(type);
    }

    public boolean hasModLoader() {
        return this.libraries.keySet().stream().map(new Function() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return LibraryAnalyzer.LibraryType.fromPatchId((String) obj);
            }
        }).filter(new Predicate() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((LibraryAnalyzer.LibraryType) obj);
            }
        }).anyMatch(new LibraryAnalyzer$$ExternalSyntheticLambda8());
    }

    public boolean hasModLauncher() {
        return MOD_LAUNCHER_MAIN.equals(this.version.getMainClass()) || this.version.getPatches().stream().anyMatch(new Predicate() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return LibraryAnalyzer.MOD_LAUNCHER_MAIN.equals(((Version) obj).getMainClass());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: removingMatchedLibrary, reason: merged with bridge method [inline-methods] */
    public Version lambda$removeLibrary$2(Version version, String libraryId) {
        LibraryType type = LibraryType.fromPatchId(libraryId);
        if (type == null) {
            return version;
        }
        List<Library> libraries = new ArrayList<>();
        List<Library> rawLibraries = version.getLibraries();
        for (Library library : rawLibraries) {
            if (!type.matchLibrary(library, rawLibraries)) {
                libraries.add(library);
            }
        }
        return version.setLibraries(libraries);
    }

    public LibraryAnalyzer removeLibrary(final String libraryId) {
        if (!has(libraryId)) {
            return this;
        }
        this.version = lambda$removeLibrary$2(this.version, libraryId).setPatches((List) this.version.getPatches().stream().filter(new Predicate() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return LibraryAnalyzer.lambda$removeLibrary$1(libraryId, (Version) obj);
            }
        }).map(new Function() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$removeLibrary$2(libraryId, (Version) obj);
            }
        }).collect(Collectors.toList()));
        return this;
    }

    static /* synthetic */ boolean lambda$removeLibrary$1(String libraryId, Version patch) {
        return !libraryId.equals(patch.getId());
    }

    public Version build() {
        return this.version;
    }

    public static LibraryAnalyzer analyze(Version version, String gameVersion) {
        if (version.getInheritsFrom() != null) {
            throw new IllegalArgumentException("LibraryAnalyzer can only analyze independent game version");
        }
        Map<String, Pair<Library, String>> libraries = new HashMap<>();
        if (gameVersion != null) {
            libraries.put(LibraryType.MINECRAFT.getPatchId(), Pair.pair(null, gameVersion));
        }
        List<Library> rawLibraries = version.resolve(null).getLibraries();
        for (Library library : rawLibraries) {
            for (LibraryType type : LibraryType.values()) {
                if (type.matchLibrary(library, rawLibraries)) {
                    libraries.put(type.getPatchId(), Pair.pair(library, type.patchVersion(version, library.getVersion())));
                    break;
                }
            }
        }
        for (Version patch : version.getPatches()) {
            if (!patch.isHidden()) {
                libraries.put(patch.getId(), Pair.pair(null, patch.getVersion()));
            }
        }
        return new LibraryAnalyzer(version, libraries);
    }

    public static boolean isModded(VersionProvider provider, Version version) {
        Version resolvedVersion = version.resolve(provider);
        String mainClass = resolvedVersion.getMainClass();
        return mainClass != null && (LAUNCH_WRAPPER_MAIN.equals(mainClass) || mainClass.startsWith("net.minecraftforge") || mainClass.startsWith("net.neoforged") || mainClass.startsWith("top.outlands") || mainClass.startsWith("net.fabricmc") || mainClass.startsWith("org.quiltmc") || mainClass.startsWith("cpw.mods"));
    }

    public Set<ModLoaderType> getModLoaders() {
        return (Set) Arrays.stream(LibraryType.values()).filter(new LibraryAnalyzer$$ExternalSyntheticLambda8()).filter(new Predicate() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.has((LibraryAnalyzer.LibraryType) obj);
            }
        }).map(new Function() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((LibraryAnalyzer.LibraryType) obj).getModLoaderType();
            }
        }).filter(new Predicate() { // from class: com.brixcore.download.LibraryAnalyzer$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((ModLoaderType) obj);
            }
        }).collect(Collectors.toSet());
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    public static class LibraryType {
        private final Pattern artifact;
        private final Pattern group;
        private final boolean modLoader;
        private final ModLoaderType modLoaderType;
        private final String patchId;
        public static final LibraryType MINECRAFT = new LibraryType("MINECRAFT", 0, true, "game", Pattern.compile("^$"), Pattern.compile("^$"), null);
        public static final LibraryType FABRIC = new LibraryType("FABRIC", 1, true, "fabric", Pattern.compile("net\\.fabricmc"), Pattern.compile("fabric-loader"), ModLoaderType.FABRIC);
        public static final LibraryType FABRIC_API = new LibraryType("FABRIC_API", 2, true, "fabric-api", Pattern.compile("net\\.fabricmc"), Pattern.compile("fabric-api"), null);
        public static final LibraryType FORGE = new AnonymousClass1("FORGE", 3, true, DefaultCacheRepository.LibraryIndex.TYPE_FORGE, Pattern.compile("net\\.minecraftforge"), Pattern.compile("(forge|fmlloader)"), ModLoaderType.FORGE);
        public static final LibraryType CLEANROOM = new LibraryType("CLEANROOM", 4, true, "cleanroom", Pattern.compile("com\\.cleanroommc"), Pattern.compile("cleanroom"), ModLoaderType.CLEANROOM);
        public static final LibraryType NEO_FORGE = new AnonymousClass2("NEO_FORGE", 5, true, "neoforge", Pattern.compile("net\\.neoforged\\.fancymodloader"), Pattern.compile("(core|loader)"), ModLoaderType.NEO_FORGED);
        public static final LibraryType LITELOADER = new LibraryType("LITELOADER", 6, true, "liteloader", Pattern.compile("com\\.mumfrey"), Pattern.compile("liteloader"), ModLoaderType.LITE_LOADER);
        public static final LibraryType OPTIFINE = new LibraryType("OPTIFINE", 7, false, "optifine", Pattern.compile("(net\\.)?optifine"), Pattern.compile("^(?!.*launchwrapper).*$"), null);
        public static final LibraryType QUILT = new LibraryType("QUILT", 8, true, "quilt", Pattern.compile("org\\.quiltmc"), Pattern.compile("quilt-loader"), ModLoaderType.QUILT);
        public static final LibraryType QUILT_API = new LibraryType("QUILT_API", 9, true, "quilt-api", Pattern.compile("org\\.quiltmc"), Pattern.compile("quilt-api"), null);
        public static final LibraryType BOOTSTRAP_LAUNCHER = new LibraryType("BOOTSTRAP_LAUNCHER", 10, false, "", Pattern.compile("cpw\\.mods"), Pattern.compile("bootstraplauncher"), null);
        private static final /* synthetic */ LibraryType[] $VALUES = $values();

        private static /* synthetic */ LibraryType[] $values() {
            return new LibraryType[]{MINECRAFT, FABRIC, FABRIC_API, FORGE, CLEANROOM, NEO_FORGE, LITELOADER, OPTIFINE, QUILT, QUILT_API, BOOTSTRAP_LAUNCHER};
        }

        public static LibraryType valueOf(String name) {
            return (LibraryType) Enum.valueOf(LibraryType.class, name);
        }

        public static LibraryType[] values() {
            return (LibraryType[]) $VALUES.clone();
        }

        /* JADX INFO: renamed from: com.brixcore.download.LibraryAnalyzer$LibraryType$1, reason: invalid class name */
        final enum AnonymousClass1 extends LibraryType {
            private final Pattern FORGE_VERSION_MATCHER;

            private AnonymousClass1(String str, int i, boolean modLoader, String patchId, Pattern group, Pattern artifact, ModLoaderType modLoaderType) {
                super(str, i, modLoader, patchId, group, artifact, modLoaderType);
                this.FORGE_VERSION_MATCHER = Pattern.compile("^([0-9.]+)-(?<forge>[0-9.]+)(-([0-9.]+))?$");
            }

            @Override // com.brixcore.download.LibraryAnalyzer.LibraryType
            protected String patchVersion(Version gameVersion, String libraryVersion) {
                Matcher matcher = this.FORGE_VERSION_MATCHER.matcher(libraryVersion);
                if (matcher.find()) {
                    return matcher.group(DefaultCacheRepository.LibraryIndex.TYPE_FORGE);
                }
                return super.patchVersion(gameVersion, libraryVersion);
            }

            @Override // com.brixcore.download.LibraryAnalyzer.LibraryType
            protected boolean matchLibrary(Library library, List<Library> libraries) {
                for (Library l : libraries) {
                    if (NEO_FORGE.matchLibrary(l, libraries)) {
                        return false;
                    }
                }
                return super.matchLibrary(library, libraries);
            }
        }

        /* JADX INFO: renamed from: com.brixcore.download.LibraryAnalyzer$LibraryType$2, reason: invalid class name */
        final enum AnonymousClass2 extends LibraryType {
            private final Pattern NEO_FORGE_VERSION_MATCHER;

            private AnonymousClass2(String str, int i, boolean modLoader, String patchId, Pattern group, Pattern artifact, ModLoaderType modLoaderType) {
                super(str, i, modLoader, patchId, group, artifact, modLoaderType);
                this.NEO_FORGE_VERSION_MATCHER = Pattern.compile("^([0-9.]+)-(?<forge>[0-9.]+)(-([0-9.]+))?$");
            }

            @Override // com.brixcore.download.LibraryAnalyzer.LibraryType
            protected String patchVersion(Version gameVersion, String libraryVersion) {
                Matcher matcher = this.NEO_FORGE_VERSION_MATCHER.matcher(libraryVersion);
                if (matcher.find()) {
                    return matcher.group(DefaultCacheRepository.LibraryIndex.TYPE_FORGE);
                }
                String res = scanVersion(gameVersion);
                if (res != null) {
                    return res;
                }
                for (Version patch : gameVersion.getPatches()) {
                    String res2 = scanVersion(patch);
                    if (res2 != null) {
                        return res2;
                    }
                }
                return super.patchVersion(gameVersion, libraryVersion);
            }

            private String scanVersion(Version version) {
                List<Argument> gameArguments;
                Optional<Arguments> optArgument = version.getArguments();
                if (!optArgument.isPresent() || (gameArguments = optArgument.get().getGame()) == null) {
                    return null;
                }
                for (int i = 0; i < gameArguments.size() - 1; i++) {
                    Argument argument = gameArguments.get(i);
                    if ((argument instanceof StringArgument) && "--fml.neoForgeVersion".equals(((StringArgument) argument).getArgument())) {
                        Argument next = gameArguments.get(i + 1);
                        if (next instanceof StringArgument) {
                            return ((StringArgument) next).getArgument();
                        }
                        return null;
                    }
                }
                return null;
            }
        }

        private LibraryType(String str, int i, boolean modLoader, String patchId, Pattern group, Pattern artifact, ModLoaderType modLoaderType) {
            super(str, i);
            this.modLoader = modLoader;
            this.patchId = patchId;
            this.group = group;
            this.artifact = artifact;
            this.modLoaderType = modLoaderType;
        }

        public boolean isModLoader() {
            return this.modLoader;
        }

        public String getPatchId() {
            return this.patchId;
        }

        public ModLoaderType getModLoaderType() {
            return this.modLoaderType;
        }

        public static LibraryType fromPatchId(String patchId) {
            for (LibraryType type : values()) {
                if (type.getPatchId().equals(patchId)) {
                    return type;
                }
            }
            return null;
        }

        protected boolean matchLibrary(Library library, List<Library> libraries) {
            return this.group.matcher(library.getGroupId()).matches() && this.artifact.matcher(library.getArtifactId()).matches();
        }

        protected String patchVersion(Version gameVersion, String libraryVersion) {
            return libraryVersion;
        }
    }

    public static final class LibraryMark {
        private final String libraryId;
        private final String libraryVersion;
        private final LibraryStatus status;

        public enum LibraryStatus {
            CLEAR,
            UNSURE,
            JUST_EXISTED
        }

        private LibraryMark(String libraryId, String libraryVersion, LibraryStatus status) {
            this.libraryId = libraryId;
            this.libraryVersion = libraryVersion;
            this.status = status;
        }

        public String getLibraryId() {
            return this.libraryId;
        }

        public String getLibraryVersion() {
            return this.libraryVersion;
        }

        public LibraryStatus getStatus() {
            return this.status;
        }
    }
}
