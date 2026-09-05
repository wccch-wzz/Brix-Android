package com.brixcore.download.optifine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import com.brixcore.ActivityProviderHolder;
import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.ProcessService;
import com.brixcore.download.UnsupportedInstallationException;
import com.brixcore.download.VersionMismatchException;
import com.brixcore.game.Arguments;
import com.brixcore.game.Artifact;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.LibrariesDownloadInfo;
import com.brixcore.game.Library;
import com.brixcore.game.LibraryDownloadInfo;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.Lang;
import com.brixcore.util.SocketServer;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.platform.CommandBuilder;
import com.brixcore.util.versioning.VersionNumber;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import org.jenkinsci.constant_pool_scanner.ConstantPool;
import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner;
import org.jenkinsci.constant_pool_scanner.ConstantType;
import org.jenkinsci.constant_pool_scanner.Utf8Constant;

/* JADX INFO: loaded from: classes11.dex */
public final class OptiFineInstallTask extends Task<Version> {
    private final List<Task<?>> dependencies;
    private final DefaultDependencyManager dependencyManager;
    private final List<Task<?>> dependents;
    private Path dest;
    private final DefaultGameRepository gameRepository;
    private final Path installer;
    private final Library optiFineInstallerLibrary;
    private final Library optiFineLibrary;
    private final OptiFineRemoteVersion remote;
    private final Version version;

    public OptiFineInstallTask(DefaultDependencyManager dependencyManager, Version version, OptiFineRemoteVersion remoteVersion) {
        this(dependencyManager, version, remoteVersion, null);
    }

    public OptiFineInstallTask(DefaultDependencyManager dependencyManager, Version version, OptiFineRemoteVersion remoteVersion, Path installer) {
        this.dependents = new ArrayList(0);
        this.dependencies = new ArrayList(1);
        this.dependencyManager = dependencyManager;
        this.gameRepository = dependencyManager.getGameRepository();
        this.version = version;
        this.remote = remoteVersion;
        this.installer = installer;
        String mavenVersion = this.remote.getGameVersion() + "_" + this.remote.getSelfVersion();
        this.optiFineLibrary = new Library(new Artifact("optifine", "OptiFine", mavenVersion));
        this.optiFineInstallerLibrary = new Library(new Artifact("optifine", "OptiFine", mavenVersion, "installer"), null, new LibrariesDownloadInfo(new LibraryDownloadInfo("optifine/OptiFine/" + mavenVersion + "/OptiFine-" + mavenVersion + "-installer.jar", this.remote.getUrls().get(0))));
    }

    @Override // com.brixcore.task.Task
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        this.dest = Files.createTempFile("optifine-installer", ".jar", new FileAttribute[0]);
        if (this.installer == null) {
            FileDownloadTask task = new FileDownloadTask(this.dependencyManager.getDownloadProvider().injectURLsWithCandidates(this.remote.getUrls()), this.dest.toFile(), (FileDownloadTask.IntegrityCheck) null);
            task.setCacheRepository(this.dependencyManager.getCacheRepository());
            task.setCaching(true);
            this.dependents.add(task);
            return;
        }
        FileUtils.copyFile(this.installer, this.dest);
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependents() {
        return this.dependents;
    }

    @Override // com.brixcore.task.Task
    public Collection<Task<?>> getDependencies() {
        return this.dependencies;
    }

    @Override // com.brixcore.task.Task
    public boolean isRelyingOnDependencies() {
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x0183 A[Catch: all -> 0x0205, TryCatch #2 {all -> 0x0205, blocks: (B:29:0x0172, B:31:0x0183, B:33:0x0197, B:36:0x01a4, B:37:0x01aa), top: B:76:0x0172 }] */
    /* JADX WARN: Code duplicated, block: B:39:0x01ad  */
    /* JADX WARN: Code duplicated, block: B:41:0x01b2  */
    /* JADX WARN: Code duplicated, block: B:83:0x021a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:87:? A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        Throwable th;
        char c;
        boolean hasLaunchWrapper;
        Path buildofText;
        String originalMainClass = this.version.resolve(this.dependencyManager.getGameRepository()).getMainClass();
        if (!LibraryAnalyzer.FORGE_OPTIFINE_MAIN.contains(originalMainClass)) {
            throw new UnsupportedInstallationException(1);
        }
        List<Library> libraries = new ArrayList<>(4);
        libraries.add(this.optiFineLibrary);
        Path optiFineInstallerLibraryPath = this.gameRepository.getLibraryFile(this.version, this.optiFineInstallerLibrary).toPath();
        FileUtils.copyFile(this.dest, optiFineInstallerLibraryPath);
        FileSystem fs2 = CompressingUtils.createWritableZipFileSystem(optiFineInstallerLibraryPath);
        try {
            Files.deleteIfExists(fs2.getPath("/META-INF/mods.toml", new String[0]));
            if (fs2 != null) {
                fs2.close();
            }
            boolean hasLaunchWrapper2 = false;
            FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(this.dest);
            try {
                Path optiFineLibraryPath = this.gameRepository.getLibraryFile(this.version, this.optiFineLibrary).toPath();
                if (Files.exists(fs.getPath("optifine/Patcher.class", new String[0]), new LinkOption[0])) {
                    String[] command = {"-cp", this.dest.toString(), "optifine.Patcher", this.gameRepository.getVersionJar(this.version).getAbsolutePath(), this.dest.toString(), optiFineLibraryPath.toString()};
                    runJVMProcess(command, 8);
                } else {
                    FileUtils.copyFile(this.dest, optiFineLibraryPath);
                }
                FileSystem fs3 = CompressingUtils.createWritableZipFileSystem(optiFineLibraryPath);
                try {
                    Files.deleteIfExists(fs3.getPath("/META-INF/mods.toml", new String[0]));
                    if (fs3 != null) {
                        fs3.close();
                    }
                    Path launchWrapper2 = fs.getPath("launchwrapper-2.0.jar", new String[0]);
                    if (Files.exists(launchWrapper2, new LinkOption[0])) {
                        Library launchWrapper = new Library(new Artifact("optifine", "launchwrapper", "2.0"));
                        File launchWrapperFile = this.gameRepository.getLibraryFile(this.version, launchWrapper);
                        FileUtils.makeDirectory(launchWrapperFile.getAbsoluteFile().getParentFile());
                        FileUtils.copyFile(launchWrapper2, launchWrapperFile.toPath());
                        hasLaunchWrapper2 = true;
                        libraries.add(launchWrapper);
                    }
                    Path launchWrapperVersionText = fs.getPath("launchwrapper-of.txt", new String[0]);
                    try {
                        if (!Files.exists(launchWrapperVersionText, new LinkOption[0])) {
                            c = 1;
                        } else {
                            String launchWrapperVersion = FileUtils.readText(launchWrapperVersionText).trim();
                            c = 1;
                            Path launchWrapperJar = fs.getPath("launchwrapper-of-" + launchWrapperVersion + ".jar", new String[0]);
                            Library launchWrapper3 = new Library(new Artifact("optifine", "launchwrapper-of", launchWrapperVersion));
                            if (Files.exists(launchWrapperJar, new LinkOption[0])) {
                                File launchWrapperFile2 = this.gameRepository.getLibraryFile(this.version, launchWrapper3);
                                FileUtils.makeDirectory(launchWrapperFile2.getAbsoluteFile().getParentFile());
                                FileUtils.copyFile(launchWrapperJar, launchWrapperFile2.toPath());
                                libraries.add(launchWrapper3);
                                hasLaunchWrapper = true;
                            }
                            buildofText = fs.getPath("buildof.txt", new String[0]);
                            if (Files.exists(buildofText, new LinkOption[0])) {
                                String buildof = FileUtils.readText(buildofText).trim();
                                VersionNumber buildofVer = VersionNumber.asVersion(buildof);
                                if (LibraryAnalyzer.BOOTSTRAP_LAUNCHER_MAIN.equals(originalMainClass) && buildofVer.compareTo(VersionNumber.asVersion("20210924-190833")) < 0) {
                                    throw new UnsupportedInstallationException(2);
                                }
                            }
                            if (fs != null) {
                                fs.close();
                            }
                            if (!hasLaunchWrapper) {
                                libraries.add(new Library(new Artifact("net.minecraft", "launchwrapper", "1.12")));
                            }
                            String patchId = LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId();
                            String selfVersion = this.remote.getSelfVersion();
                            Arguments arguments = new Arguments();
                            String[] strArr = new String[2];
                            strArr[0] = "--tweakClass";
                            strArr[c] = "optifine.OptiFineTweaker";
                            setResult(new Version(patchId, selfVersion, 10000, arguments.addGameArguments(strArr), LibraryAnalyzer.LAUNCH_WRAPPER_MAIN, libraries));
                            this.dependencies.add(this.dependencyManager.checkLibraryCompletionAsync(getResult(), c));
                            return;
                        }
                        buildofText = fs.getPath("buildof.txt", new String[0]);
                        if (Files.exists(buildofText, new LinkOption[0])) {
                            String buildof2 = FileUtils.readText(buildofText).trim();
                            VersionNumber buildofVer2 = VersionNumber.asVersion(buildof2);
                            if (LibraryAnalyzer.BOOTSTRAP_LAUNCHER_MAIN.equals(originalMainClass)) {
                                throw new UnsupportedInstallationException(2);
                            }
                        }
                        if (fs != null) {
                            fs.close();
                        }
                        if (!hasLaunchWrapper) {
                            libraries.add(new Library(new Artifact("net.minecraft", "launchwrapper", "1.12")));
                        }
                        String patchId2 = LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId();
                        String selfVersion2 = this.remote.getSelfVersion();
                        Arguments arguments2 = new Arguments();
                        String[] strArr2 = new String[2];
                        strArr2[0] = "--tweakClass";
                        strArr2[c] = "optifine.OptiFineTweaker";
                        setResult(new Version(patchId2, selfVersion2, 10000, arguments2.addGameArguments(strArr2), LibraryAnalyzer.LAUNCH_WRAPPER_MAIN, libraries));
                        this.dependencies.add(this.dependencyManager.checkLibraryCompletionAsync(getResult(), c));
                        return;
                    } catch (Throwable th2) {
                        th = th2;
                        if (fs != null) {
                            throw th;
                        }
                        try {
                            fs.close();
                            throw th;
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                            throw th;
                        }
                    }
                    hasLaunchWrapper = hasLaunchWrapper2;
                } catch (Throwable th4) {
                    if (fs3 == null) {
                        throw th4;
                    }
                    try {
                        fs3.close();
                        throw th4;
                    } catch (Throwable th5) {
                        th4.addSuppressed(th5);
                        throw th4;
                    }
                }
            } catch (Throwable th6) {
                th = th6;
                if (fs != null) {
                    throw th;
                }
                fs.close();
                throw th;
            }
        } catch (Throwable th7) {
            if (fs2 == null) {
                throw th7;
            }
            try {
                fs2.close();
                throw th7;
            } catch (Throwable th8) {
                th7.addSuppressed(th8);
                throw th7;
            }
        }
    }

    private void runJVMProcess(String[] command, int java) throws Exception {
        Activity context = ActivityProviderHolder.getCurrentActivity();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        boolean listen = true;
        while (listen) {
            if (activityManager.getRunningAppProcesses().size() == 1) {
                listen = false;
            }
        }
        final CountDownLatch latch = new CountDownLatch(1);
        SocketServer server = new SocketServer("127.0.0.1", ProcessService.PROCESS_SERVICE_PORT, new SocketServer.Listener() { // from class: com.brixcore.download.optifine.OptiFineInstallTask$$ExternalSyntheticLambda1
            @Override // com.brixcore.util.SocketServer.Listener
            public final void onReceive(SocketServer socketServer, String str) {
                OptiFineInstallTask.lambda$runJVMProcess$0(latch, socketServer, str);
            }
        });
        for (int i = 0; i < 5; i++) {
            try {
                Intent service = new Intent(context, (Class<?>) ProcessService.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("command", command);
                bundle.putInt("java", java);
                service.putExtras(bundle);
                context.startForegroundService(service);
                break;
            } catch (Throwable e) {
                activityManager.getRunningAppProcesses().forEach(new Consumer() { // from class: com.brixcore.download.optifine.OptiFineInstallTask$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        OptiFineInstallTask.lambda$runJVMProcess$1((ActivityManager.RunningAppProcessInfo) obj);
                    }
                });
                if (i == 4) {
                    throw e;
                }
            }
        }
        server.start();
        latch.await();
        int exitCode = Integer.parseInt((String) server.getResult());
        if (exitCode != 0) {
            if (java == 8) {
                runJVMProcess(command, 17);
            } else if (java == 17) {
                runJVMProcess(command, 11);
            } else {
                if (java == 11) {
                    runJVMProcess(command, 21);
                    return;
                }
                throw new IOException("OptiFine patcher failed, command: " + new CommandBuilder().addAll(Arrays.asList(command)));
            }
        }
    }

    static /* synthetic */ void lambda$runJVMProcess$0(CountDownLatch latch, SocketServer server1, String msg) {
        server1.setResult(msg);
        server1.stop();
        latch.countDown();
    }

    static /* synthetic */ void lambda$runJVMProcess$1(ActivityManager.RunningAppProcessInfo info) {
        if (info.pid != Process.myPid()) {
            Process.killProcess(info.pid);
        }
    }

    public static Task<Version> install(DefaultDependencyManager dependencyManager, Version version, Path installer) throws VersionMismatchException, IOException {
        Optional<String> gameVersion = dependencyManager.getGameRepository().getGameVersion(version);
        if (!gameVersion.isPresent()) {
            throw new IOException();
        }
        FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(installer);
        try {
            Path configClass = fs.getPath("Config.class", new String[0]);
            if (!Files.exists(configClass, new LinkOption[0])) {
                configClass = fs.getPath("net/optifine/Config.class", new String[0]);
            }
            if (!Files.exists(configClass, new LinkOption[0])) {
                configClass = fs.getPath("notch/net/optifine/Config.class", new String[0]);
            }
            if (!Files.exists(configClass, new LinkOption[0])) {
                throw new IOException("Unrecognized installer");
            }
            ConstantPool pool = ConstantPoolScanner.parse(Files.readAllBytes(configClass), ConstantType.UTF8);
            final List<String> constants = new ArrayList<>();
            pool.list(Utf8Constant.class).forEach(new Consumer() { // from class: com.brixcore.download.optifine.OptiFineInstallTask$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    constants.add(((Utf8Constant) obj).get());
                }
            });
            String mcVersion = (String) Lang.getOrDefault(constants, constants.indexOf("MC_VERSION") + 1, null);
            String ofEdition = (String) Lang.getOrDefault(constants, constants.indexOf("OF_EDITION") + 1, null);
            String ofRelease = (String) Lang.getOrDefault(constants, constants.indexOf("OF_RELEASE") + 1, null);
            if (mcVersion == null || ofEdition == null || ofRelease == null) {
                throw new IOException("Unrecognized OptiFine installer");
            }
            if (!mcVersion.equals(gameVersion.get())) {
                throw new VersionMismatchException(mcVersion, gameVersion.get());
            }
            OptiFineInstallTask optiFineInstallTask = new OptiFineInstallTask(dependencyManager, version, new OptiFineRemoteVersion(mcVersion, ofEdition + "_" + ofRelease, Collections.singletonList(""), false), installer);
            if (fs != null) {
                fs.close();
            }
            return optiFineInstallTask;
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
}
