package com.brixcore.download;

import com.brixcore.game.Argument;
import com.brixcore.game.Artifact;
import com.brixcore.game.CompatibilityRule;
import com.brixcore.game.GameRepository;
import com.brixcore.game.Library;
import com.brixcore.game.StringArgument;
import com.brixcore.game.Version;
import com.brixcore.game.VersionLibraryBuilder;
import com.brixcore.task.Task;
import com.brixcore.util.Logging;
import com.brixcore.util.SimpleMultimap;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.versioning.VersionNumber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes14.dex */
public class MaintainTask extends Task<Version> {
    private final GameRepository repository;
    private final Version version;

    public static /* synthetic */ ArrayList $r8$lambda$aw4WkQINtNlXlsGxYEbzatsgkDc() {
        return new ArrayList();
    }

    public static /* synthetic */ HashMap $r8$lambda$zL9zIj_AuRar_VZKE_7we4Hn4rk() {
        return new HashMap();
    }

    public MaintainTask(GameRepository repository, Version version) {
        this.repository = repository;
        this.version = version;
        if (version.getInheritsFrom() != null) {
            throw new IllegalArgumentException("MaintainTask requires independent game version");
        }
    }

    @Override // com.brixcore.task.Task
    public void execute() {
        setResult(maintain(this.repository, this.version));
    }

    public static Version maintain(GameRepository repository, Version version) {
        Version version2;
        if (version.getInheritsFrom() != null) {
            throw new IllegalArgumentException("MaintainTask requires independent game version");
        }
        String mainClass = version.resolve(null).getMainClass();
        if (mainClass != null && mainClass.equals(LibraryAnalyzer.LAUNCH_WRAPPER_MAIN)) {
            version2 = maintainOptiFineLibrary(repository, maintainGameWithLaunchWrapper(repository, unique(version), true), false);
        } else if (mainClass != null && mainClass.equals(LibraryAnalyzer.MOD_LAUNCHER_MAIN)) {
            version2 = maintainOptiFineLibrary(repository, maintainGameWithCpwModLauncher(repository, unique(version)), true);
        } else {
            version2 = (mainClass == null || !mainClass.equals(LibraryAnalyzer.BOOTSTRAP_LAUNCHER_MAIN)) ? maintainOptiFineLibrary(repository, unique(version), false) : maintainGameWithCpwBoostrapLauncher(repository, unique(version));
        }
        List<Library> libraries = version2.getLibraries();
        if (!libraries.isEmpty()) {
            Library library = libraries.get(0);
            if ("org.glavo".equals(library.getGroupId())) {
                if (("log4j-patch".equals(library.getArtifactId()) || "log4j-patch-beta9".equals(library.getArtifactId())) && "1.0".equals(library.getVersion()) && library.getDownload() == null) {
                    return version2.setLibraries(libraries.subList(1, libraries.size()));
                }
                return version2;
            }
            return version2;
        }
        return version2;
    }

    public static Version maintainPreservingPatches(GameRepository repository, Version version) {
        if (!version.isResolvedPreservingPatches()) {
            throw new IllegalArgumentException("MaintainTask requires independent game version");
        }
        Version newVersion = maintain(repository, version.resolve(repository));
        return newVersion.setPatches(version.getPatches()).markAsUnresolved();
    }

    private static Version maintainGameWithLaunchWrapper(GameRepository repository, Version version, boolean reorderTweakClass) {
        LibraryAnalyzer libraryAnalyzer = LibraryAnalyzer.analyze(version, null);
        VersionLibraryBuilder builder = new VersionLibraryBuilder(version);
        String mainClass = null;
        if (libraryAnalyzer.has(LibraryAnalyzer.LibraryType.LITELOADER) && !libraryAnalyzer.hasModLauncher()) {
            builder.replaceTweakClass(LibraryAnalyzer.LITELOADER_TWEAKER, LibraryAnalyzer.LITELOADER_TWEAKER, !reorderTweakClass, reorderTweakClass);
        } else {
            builder.removeTweakClass(LibraryAnalyzer.LITELOADER_TWEAKER);
        }
        if (libraryAnalyzer.has(LibraryAnalyzer.LibraryType.OPTIFINE)) {
            if (!libraryAnalyzer.has(LibraryAnalyzer.LibraryType.LITELOADER) && !libraryAnalyzer.has(LibraryAnalyzer.LibraryType.FORGE)) {
                if (builder.hasTweakClass(LibraryAnalyzer.OPTIFINE_TWEAKERS[1])) {
                    builder.replaceTweakClass(LibraryAnalyzer.OPTIFINE_TWEAKERS[1], LibraryAnalyzer.OPTIFINE_TWEAKERS[0], !reorderTweakClass, reorderTweakClass);
                }
            } else if (libraryAnalyzer.hasModLauncher()) {
                mainClass = LibraryAnalyzer.MOD_LAUNCHER_MAIN;
                for (String optiFineTweaker : LibraryAnalyzer.OPTIFINE_TWEAKERS) {
                    builder.removeTweakClass(optiFineTweaker);
                }
            } else if (builder.hasTweakClass(LibraryAnalyzer.OPTIFINE_TWEAKERS[0])) {
                builder.replaceTweakClass(LibraryAnalyzer.OPTIFINE_TWEAKERS[0], LibraryAnalyzer.OPTIFINE_TWEAKERS[1], !reorderTweakClass, reorderTweakClass);
            }
        } else {
            for (String optiFineTweaker2 : LibraryAnalyzer.OPTIFINE_TWEAKERS) {
                builder.removeTweakClass(optiFineTweaker2);
            }
        }
        boolean hasForge = libraryAnalyzer.has(LibraryAnalyzer.LibraryType.FORGE);
        boolean hasModLauncher = libraryAnalyzer.hasModLauncher();
        for (String forgeTweaker : LibraryAnalyzer.FORGE_TWEAKERS) {
            if (!hasForge) {
                builder.removeTweakClass(forgeTweaker);
            } else if (!hasModLauncher && builder.hasTweakClass(forgeTweaker)) {
                builder.replaceTweakClass(forgeTweaker, forgeTweaker, !reorderTweakClass, reorderTweakClass);
            }
        }
        Version ret = builder.build();
        return mainClass == null ? ret : ret.setMainClass(mainClass);
    }

    private static Version maintainGameWithCpwModLauncher(final GameRepository repository, final Version version) {
        LibraryAnalyzer libraryAnalyzer = LibraryAnalyzer.analyze(version, null);
        final VersionLibraryBuilder builder = new VersionLibraryBuilder(version);
        if (!libraryAnalyzer.has(LibraryAnalyzer.LibraryType.FORGE)) {
            return version;
        }
        if (libraryAnalyzer.has(LibraryAnalyzer.LibraryType.OPTIFINE)) {
            final Library hmclTransformerDiscoveryService = new Library(new Artifact("org.jackhuang.hmcl", "transformer-discovery-service", "1.0"));
            Optional<Library> optiFine = version.getLibraries().stream().filter(new Predicate() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((Library) obj).is("optifine", "OptiFine");
                }
            }).findAny();
            final boolean libraryExisting = version.getLibraries().stream().anyMatch(new Predicate() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((Library) obj).is("org.jackhuang.hmcl", "transformer-discovery-service");
                }
            });
            optiFine.ifPresent(new Consumer() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MaintainTask.lambda$maintainGameWithCpwModLauncher$2(builder, libraryExisting, hmclTransformerDiscoveryService, repository, version, (Library) obj);
                }
            });
        }
        return builder.build();
    }

    static /* synthetic */ void lambda$maintainGameWithCpwModLauncher$2(VersionLibraryBuilder builder, boolean libraryExisting, Library hmclTransformerDiscoveryService, GameRepository repository, Version version, Library library) {
        builder.addJvmArgument("-Dhmcl.transformer.candidates=${library_directory}/" + library.getPath());
        if (!libraryExisting) {
            builder.addLibrary(hmclTransformerDiscoveryService);
        }
        Path libraryPath = repository.getLibraryFile(version, hmclTransformerDiscoveryService).toPath();
        try {
            InputStream input = MaintainTask.class.getResourceAsStream("/assets/game/HMCLTransformerDiscoveryService-1.0.jar");
            try {
                Files.createDirectories(libraryPath.getParent(), new FileAttribute[0]);
                Files.copy((InputStream) Objects.requireNonNull(input, "Bundled HMCLTransformerDiscoveryService is missing."), libraryPath, StandardCopyOption.REPLACE_EXISTING);
                if (input != null) {
                    input.close();
                }
            } catch (Throwable th) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException | NullPointerException e) {
            Logging.LOG.log(Level.WARNING, "Unable to unpack HMCLTransformerDiscoveryService", (Throwable) e);
        }
    }

    private static String updateIgnoreList(GameRepository repository, Version version, String ignoreList) {
        String absolutePath;
        String[] ignores = ignoreList.split(",");
        List<String> newIgnoreList = new ArrayList<>();
        newIgnoreList.add("${primary_jar}");
        Path libraryDirectory = repository.getLibrariesDirectory(version).toPath().toAbsolutePath();
        for (String classpathName : repository.getClasspath(version)) {
            Path classpathFile = Paths.get(classpathName, new String[0]).toAbsolutePath();
            final String fileName = classpathFile.getFileName().toString();
            Stream streamOf = Stream.of((Object[]) ignores);
            Objects.requireNonNull(fileName);
            if (streamOf.anyMatch(new Predicate() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return fileName.contains((String) obj);
                }
            })) {
                if (classpathFile.startsWith(libraryDirectory)) {
                    absolutePath = "${library_directory}${file_separator}" + libraryDirectory.relativize(classpathFile).toString().replace(File.separator, "${file_separator}");
                } else {
                    absolutePath = classpathFile.toString();
                }
                newIgnoreList.add(StringUtils.substringBefore(absolutePath, ","));
            }
        }
        return String.join(",", newIgnoreList);
    }

    private static Version maintainGameWithCpwBoostrapLauncher(GameRepository repository, Version version) {
        LibraryAnalyzer libraryAnalyzer = LibraryAnalyzer.analyze(version, null);
        VersionLibraryBuilder builder = new VersionLibraryBuilder(version);
        if (!libraryAnalyzer.has(LibraryAnalyzer.LibraryType.FORGE) && !libraryAnalyzer.has(LibraryAnalyzer.LibraryType.NEO_FORGE)) {
            return version;
        }
        Optional<String> bslVersion = libraryAnalyzer.getVersion(LibraryAnalyzer.LibraryType.BOOTSTRAP_LAUNCHER);
        if (bslVersion.isPresent()) {
            if (VersionNumber.compare(bslVersion.get(), "0.1.17") < 0) {
                List<Argument> jvm = builder.getMutableJvmArguments();
                for (int i = 0; i < jvm.size(); i++) {
                    Argument jvmArg = jvm.get(i);
                    if (jvmArg instanceof StringArgument) {
                        String jvmArgStr = jvmArg.toString();
                        if (jvmArgStr.startsWith("-DignoreList=")) {
                            jvm.set(i, new StringArgument("-DignoreList=" + updateIgnoreList(repository, version, jvmArgStr.substring("-DignoreList=".length()))));
                        }
                    }
                }
            } else {
                List<Argument> jvm2 = builder.getMutableJvmArguments();
                for (int i2 = 0; i2 < jvm2.size(); i2++) {
                    Argument jvmArg2 = jvm2.get(i2);
                    if (jvmArg2 instanceof StringArgument) {
                        String jvmArgStr2 = jvmArg2.toString();
                        if (jvmArgStr2.startsWith("-DignoreList=")) {
                            jvm2.set(i2, new StringArgument(jvmArgStr2 + ",${primary_jar_name}"));
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    private static Version maintainOptiFineLibrary(GameRepository repository, Version version, boolean remove) {
        LibraryAnalyzer libraryAnalyzer = LibraryAnalyzer.analyze(version, null);
        List<Library> libraries = new ArrayList<>(version.getLibraries());
        if (libraryAnalyzer.has(LibraryAnalyzer.LibraryType.OPTIFINE) && ((libraryAnalyzer.has(LibraryAnalyzer.LibraryType.LITELOADER) || libraryAnalyzer.has(LibraryAnalyzer.LibraryType.FORGE)) && repository != null)) {
            for (int i = 0; i < version.getLibraries().size(); i++) {
                Library library = libraries.get(i);
                if (library.is("optifine", "OptiFine")) {
                    Library newLibrary = new Library(new Artifact("optifine", "OptiFine", library.getVersion(), "installer"));
                    if (repository.getLibraryFile(version, newLibrary).exists()) {
                        libraries.set(i, null);
                        if (!remove) {
                            libraries.add(newLibrary);
                        }
                    }
                }
                if (library.is("optifine", "launchwrapper-of")) {
                    libraries.set(i, null);
                }
            }
        }
        return version.setLibraries((List) libraries.stream().filter(new Predicate() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Library) obj);
            }
        }).collect(Collectors.toList()));
    }

    public static Version unique(Version version) {
        List<Library> libraries = new ArrayList<>();
        SimpleMultimap<String, Integer, List<Integer>> multimap = new SimpleMultimap<>(new Supplier() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return MaintainTask.$r8$lambda$zL9zIj_AuRar_VZKE_7we4Hn4rk();
            }
        }, new Supplier() { // from class: com.brixcore.download.MaintainTask$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return MaintainTask.$r8$lambda$aw4WkQINtNlXlsGxYEbzatsgkDc();
            }
        });
        for (Library library : version.getLibraries()) {
            String id = library.getGroupId() + ":" + library.getArtifactId();
            VersionNumber number = VersionNumber.asVersion(library.getVersion());
            String serialized = JsonUtils.GSON.toJson(library);
            if (multimap.containsKey(id)) {
                boolean duplicate = false;
                Iterator it = ((List) multimap.get(id)).iterator();
                while (it.hasNext()) {
                    int otherLibraryIndex = ((Integer) it.next()).intValue();
                    Library otherLibrary = libraries.get(otherLibraryIndex);
                    VersionNumber otherNumber = VersionNumber.asVersion(otherLibrary.getVersion());
                    if (CompatibilityRule.equals(library.getRules(), otherLibrary.getRules())) {
                        boolean flag = true;
                        if (number.compareTo(otherNumber) > 0) {
                            libraries.set(otherLibraryIndex, library);
                        } else if (number.compareTo(otherNumber) == 0) {
                            if (library.equals(otherLibrary)) {
                                String otherSerialized = JsonUtils.GSON.toJson(otherLibrary);
                                if (serialized.length() > otherSerialized.length()) {
                                    libraries.set(otherLibraryIndex, library);
                                }
                            } else {
                                flag = false;
                            }
                        }
                        if (flag) {
                            duplicate = true;
                            break;
                        }
                    }
                }
                if (!duplicate) {
                    multimap.put(id, Integer.valueOf(libraries.size()));
                    libraries.add(library);
                }
            } else {
                multimap.put(id, Integer.valueOf(libraries.size()));
                libraries.add(library);
            }
        }
        return version.setLibraries(libraries);
    }
}
