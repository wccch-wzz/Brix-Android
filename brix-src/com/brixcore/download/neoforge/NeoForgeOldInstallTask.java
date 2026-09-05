package com.brixcore.download.neoforge;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import com.brixcore.ActivityProviderHolder;
import com.brixcore.download.ArtifactMalformedException;
import com.brixcore.download.DefaultDependencyManager;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.download.ProcessService;
import com.brixcore.download.forge.ForgeNewInstallProfile;
import com.brixcore.download.game.GameLibrariesTask;
import com.brixcore.download.game.VersionJsonDownloadTask;
import com.brixcore.game.Artifact;
import com.brixcore.game.DefaultGameRepository;
import com.brixcore.game.DownloadInfo;
import com.brixcore.game.DownloadType;
import com.brixcore.game.Library;
import com.brixcore.game.Version;
import com.brixcore.task.FileDownloadTask;
import com.brixcore.task.Task;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.DigestUtils;
import com.brixcore.util.Logging;
import com.brixcore.util.SocketServer;
import com.brixcore.util.StringUtils;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import com.brixcore.util.io.ChecksumMismatchException;
import com.brixcore.util.io.CompressingUtils;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.platform.CommandBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import org.slf4j.Logger;

/* JADX INFO: loaded from: classes5.dex */
public class NeoForgeOldInstallTask extends Task<Version> {
    private final DefaultDependencyManager dependencyManager;
    private final DefaultGameRepository gameRepository;
    private final Path installer;
    private Version neoForgeVersion;
    private List<ForgeNewInstallProfile.Processor> processors;
    private ForgeNewInstallProfile profile;
    private final String selfVersion;
    private Path tempDir;
    private final Version version;
    private final List<Task<?>> dependents = new ArrayList(1);
    private final List<Task<?>> dependencies = new ArrayList(1);
    private final AtomicInteger processorDoneCount = new AtomicInteger(0);

    private class ProcessorTask extends Task<Void> {
        private final ForgeNewInstallProfile.Processor processor;
        private final Map<String, String> vars;

        public ProcessorTask(ForgeNewInstallProfile.Processor processor, Map<String, String> vars) {
            this.processor = processor;
            this.vars = vars;
            setSignificance(Task.TaskSignificance.MODERATE);
        }

        @Override // com.brixcore.task.Task
        public void execute() throws Exception {
            Map<String, String> outputs = new HashMap<>();
            boolean miss = false;
            for (Map.Entry<String, String> entry : this.processor.getOutputs().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String key2 = NeoForgeOldInstallTask.this.parseLiteral(key, this.vars);
                String value2 = NeoForgeOldInstallTask.this.parseLiteral(value, this.vars);
                if (key2 == null || value2 == null) {
                    throw new ArtifactMalformedException("Invalid forge installation configuration");
                }
                outputs.put(key2, value2);
                Path artifact = Paths.get(key2, new String[0]);
                if (Files.exists(artifact, new LinkOption[0])) {
                    InputStream stream = Files.newInputStream(artifact, new OpenOption[0]);
                    try {
                        String code = DigestUtils.digestToString(CacheRepository.SHA1, stream);
                        if (stream != null) {
                            stream.close();
                        }
                        if (!Objects.equals(code, value2)) {
                            Files.delete(artifact);
                            Logging.LOG.info("Found existing file is not valid: " + artifact);
                            miss = true;
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
                    miss = true;
                }
            }
            if (!this.processor.getOutputs().isEmpty() && !miss) {
                return;
            }
            Path jar = NeoForgeOldInstallTask.this.gameRepository.getArtifactFile(NeoForgeOldInstallTask.this.version, this.processor.getJar());
            if (!Files.isRegularFile(jar, new LinkOption[0])) {
                throw new FileNotFoundException("Game processor file not found, should be downloaded in preprocess");
            }
            JarFile jarFile = new JarFile(jar.toFile());
            try {
                String mainClass = jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
                jarFile.close();
                if (StringUtils.isBlank(mainClass)) {
                    throw new Exception("Game processor jar does not have main class " + jar);
                }
                List<String> command = new ArrayList<>();
                command.add("-cp");
                List<String> classpath = new ArrayList<>(this.processor.getClasspath().size() + 1);
                Iterator<Artifact> it = this.processor.getClasspath().iterator();
                while (it.hasNext()) {
                    Path file = NeoForgeOldInstallTask.this.gameRepository.getArtifactFile(NeoForgeOldInstallTask.this.version, it.next());
                    if (!Files.isRegularFile(file, new LinkOption[0])) {
                        throw new Exception("Game processor dependency missing");
                    }
                    classpath.add(file.toString());
                }
                classpath.add(jar.toString());
                command.add(String.join(File.pathSeparator, classpath));
                command.add(mainClass);
                List<String> args = new ArrayList<>(this.processor.getArgs().size());
                for (String arg : this.processor.getArgs()) {
                    String parsed = NeoForgeOldInstallTask.this.parseLiteral(arg, this.vars);
                    if (parsed == null) {
                        throw new ArtifactMalformedException("Invalid forge installation configuration");
                    }
                    args.add(parsed);
                }
                command.addAll(args);
                NeoForgeOldInstallTask.this.runJVMProcess(this.processor, command, 8);
                for (Map.Entry<String, String> entry2 : outputs.entrySet()) {
                    Path artifact2 = Paths.get(entry2.getKey(), new String[0]);
                    if (!Files.isRegularFile(artifact2, new LinkOption[0])) {
                        throw new FileNotFoundException("File missing: " + artifact2);
                    }
                    InputStream stream2 = Files.newInputStream(artifact2, new OpenOption[0]);
                    try {
                        String code2 = DigestUtils.digestToString(CacheRepository.SHA1, stream2);
                        if (stream2 != null) {
                            stream2.close();
                        }
                        if (!Objects.equals(code2, entry2.getValue())) {
                            Files.delete(artifact2);
                            throw new ChecksumMismatchException(CacheRepository.SHA1, entry2.getValue(), code2);
                        }
                    } catch (Throwable th3) {
                        if (stream2 != null) {
                            try {
                                stream2.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                        }
                        throw th3;
                    }
                }
            } catch (Throwable th5) {
                try {
                    jarFile.close();
                } catch (Throwable th6) {
                    th5.addSuppressed(th6);
                }
                throw th5;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runJVMProcess(ForgeNewInstallProfile.Processor processor, List<String> command, int java) throws Exception {
        Logging.LOG.info("Executing external processor " + processor.getJar().toString() + ", command line: " + new CommandBuilder().addAll(command));
        Activity context = ActivityProviderHolder.getCurrentActivity();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        boolean listen = true;
        while (listen) {
            if (activityManager.getRunningAppProcesses().size() == 1) {
                listen = false;
            }
        }
        final CountDownLatch latch = new CountDownLatch(1);
        SocketServer server = new SocketServer("127.0.0.1", ProcessService.PROCESS_SERVICE_PORT, new SocketServer.Listener() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda0
            @Override // com.brixcore.util.SocketServer.Listener
            public final void onReceive(SocketServer socketServer, String str) {
                NeoForgeOldInstallTask.lambda$runJVMProcess$0(latch, socketServer, str);
            }
        });
        for (int i = 0; i < 5; i++) {
            try {
                Intent service = new Intent(context, (Class<?>) ProcessService.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("command", (String[]) command.toArray(new String[0]));
                bundle.putInt("java", java);
                service.putExtras(bundle);
                context.startForegroundService(service);
                break;
            } catch (Throwable e) {
                activityManager.getRunningAppProcesses().forEach(new Consumer() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        NeoForgeOldInstallTask.lambda$runJVMProcess$1((ActivityManager.RunningAppProcessInfo) obj);
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
                runJVMProcess(processor, command, 17);
            } else if (java == 17) {
                runJVMProcess(processor, command, 11);
            } else {
                if (java == 11) {
                    runJVMProcess(processor, command, 21);
                    return;
                }
                throw new IOException("Game processor exited abnormally with code " + exitCode);
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

    NeoForgeOldInstallTask(DefaultDependencyManager dependencyManager, Version version, String selfVersion, Path installer) {
        this.dependencyManager = dependencyManager;
        this.gameRepository = dependencyManager.getGameRepository();
        this.version = version;
        this.installer = installer;
        this.selfVersion = selfVersion;
        setSignificance(Task.TaskSignificance.MAJOR);
    }

    private static String replaceTokens(Map<String, String> tokens, String value) {
        StringBuilder buf = new StringBuilder();
        int x = 0;
        while (x < value.length()) {
            char c = value.charAt(x);
            if (c == '\\') {
                if (x == value.length() - 1) {
                    throw new IllegalArgumentException("Illegal pattern (Bad escape): " + value);
                }
                x++;
                buf.append(value.charAt(x));
            } else if (c == '{' || c == '\'') {
                StringBuilder key = new StringBuilder();
                int y = x + 1;
                while (y <= value.length()) {
                    if (y == value.length()) {
                        throw new IllegalArgumentException("Illegal pattern (Unclosed " + c + "): " + value);
                    }
                    char d = value.charAt(y);
                    if (d == '\\') {
                        if (y == value.length() - 1) {
                            throw new IllegalArgumentException("Illegal pattern (Bad escape): " + value);
                        }
                        y++;
                        key.append(value.charAt(y));
                    } else {
                        if (c == '{' && d == '}') {
                            x = y;
                            break;
                        }
                        if (c == '\'' && d == '\'') {
                            x = y;
                            break;
                        }
                        key.append(d);
                    }
                    y++;
                }
                if (c == '\'') {
                    buf.append((CharSequence) key);
                } else {
                    if (!tokens.containsKey(key.toString())) {
                        throw new IllegalArgumentException("Illegal pattern: " + value + " Missing Key: " + ((Object) key));
                    }
                    buf.append(tokens.get(key.toString()));
                }
            } else {
                buf.append(c);
            }
            x++;
        }
        return buf.toString();
    }

    private <E extends Exception> String parseLiteral(String literal, Map<String, String> var, ExceptionalFunction<String, String, E> plainConverter) throws Exception {
        if (StringUtils.isSurrounded(literal, "{", "}")) {
            return var.get(StringUtils.removeSurrounding(literal, "{", "}"));
        }
        if (StringUtils.isSurrounded(literal, "'", "'")) {
            return StringUtils.removeSurrounding(literal, "'");
        }
        if (StringUtils.isSurrounded(literal, "[", "]")) {
            return this.gameRepository.getArtifactFile(this.version, Artifact.fromDescriptor(StringUtils.removeSurrounding(literal, "[", "]"))).toString();
        }
        return plainConverter.apply(replaceTokens(var, literal));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String parseLiteral(String literal, Map<String, String> var) {
        return parseLiteral(literal, var, ExceptionalFunction.identity());
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
    public boolean doPreExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void preExecute() throws Exception {
        try {
            FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(this.installer);
            try {
                this.profile = (ForgeNewInstallProfile) JsonUtils.fromNonNullJson(FileUtils.readText(fs.getPath("install_profile.json", new String[0])), ForgeNewInstallProfile.class);
                this.processors = this.profile.getProcessors();
                this.neoForgeVersion = (Version) JsonUtils.fromNonNullJson(FileUtils.readText(fs.getPath(this.profile.getJson(), new String[0])), Version.class);
                for (Library library : this.profile.getLibraries()) {
                    Path file = fs.getPath("maven", new String[0]).resolve(library.getPath());
                    if (Files.exists(file, new LinkOption[0])) {
                        Path dest = this.gameRepository.getLibraryFile(this.version, library).toPath();
                        FileUtils.copyFile(file, dest);
                    }
                }
                if (this.profile.getPath().isPresent()) {
                    Path mainJar = this.profile.getPath().get().getPath(fs.getPath("maven", new String[0]));
                    if (Files.exists(mainJar, new LinkOption[0])) {
                        Path dest2 = this.gameRepository.getArtifactFile(this.version, this.profile.getPath().get());
                        FileUtils.copyFile(mainJar, dest2);
                    }
                }
                if (fs != null) {
                    fs.close();
                }
                this.dependents.add(new GameLibrariesTask(this.dependencyManager, this.version, true, this.profile.getLibraries()));
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
        } catch (ZipException ex) {
            throw new ArtifactMalformedException("Malformed forge installer file", ex);
        }
    }

    private Map<String, String> parseOptions(List<String> args, Map<String, String> vars) {
        Map<String, String> options = new LinkedHashMap<>();
        String optionName = null;
        for (String arg : args) {
            if (arg.startsWith("--")) {
                if (optionName != null) {
                    options.put(optionName, "");
                }
                optionName = arg.substring(2);
            } else if (optionName != null) {
                options.put(optionName, parseLiteral(arg, vars));
                optionName = null;
            }
        }
        if (optionName != null) {
            options.put(optionName, "");
        }
        return options;
    }

    private Task<?> patchDownloadMojangMappingsTask(ForgeNewInstallProfile.Processor processor, Map<String, String> vars) {
        Map<String, String> options = parseOptions(processor.getArgs(), vars);
        if (!"DOWNLOAD_MOJMAPS".equals(options.get("task")) || !"client".equals(options.get("side"))) {
            return null;
        }
        String version = options.get("version");
        final String output = options.get("output");
        if (version == null || output == null) {
            return null;
        }
        Logging.LOG.info("Patching DOWNLOAD_MOJMAPS task");
        return new VersionJsonDownloadTask(version, this.dependencyManager).thenComposeAsync(new ExceptionalFunction() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda5
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return this.f$0.lambda$patchDownloadMojangMappingsTask$2(output, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Task lambda$patchDownloadMojangMappingsTask$2(String output, String json) throws Exception {
        DownloadInfo mappings = ((Version) JsonUtils.fromNonNullJson(json, Version.class)).getDownloads().get(DownloadType.CLIENT_MAPPINGS);
        if (mappings == null) {
            throw new Exception("client_mappings download info not found");
        }
        List<URL> mappingsUrl = this.dependencyManager.getDownloadProvider().injectURLWithCandidates(mappings.getUrl());
        FileDownloadTask mappingsTask = new FileDownloadTask(mappingsUrl, new File(output), FileDownloadTask.IntegrityCheck.of(CacheRepository.SHA1, mappings.getSha1()));
        mappingsTask.setCaching(true);
        mappingsTask.setCacheRepository(this.dependencyManager.getCacheRepository());
        return mappingsTask;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: createProcessorTask, reason: merged with bridge method [inline-methods] */
    public Task<?> lambda$execute$5(ForgeNewInstallProfile.Processor processor, Map<String, String> vars) {
        Task<?> task = patchDownloadMojangMappingsTask(processor, vars);
        if (task == null) {
            task = new ProcessorTask(processor, vars);
        }
        task.onDone().register(new Runnable() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createProcessorTask$3();
            }
        });
        return task;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createProcessorTask$3() {
        updateProgress(this.processorDoneCount.incrementAndGet(), this.processors.size());
    }

    @Override // com.brixcore.task.Task
    public void execute() throws Exception {
        this.tempDir = Files.createTempDirectory("neoforge_installer", new FileAttribute[0]);
        final HashMap map = new HashMap();
        try {
            final FileSystem fileSystemCreateReadOnlyZipFileSystem = CompressingUtils.createReadOnlyZipFileSystem(this.installer);
            try {
                for (Map.Entry<String, String> entry : this.profile.getData().entrySet()) {
                    map.put(entry.getKey(), parseLiteral(entry.getValue(), Collections.emptyMap(), new ExceptionalFunction() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda2
                        @Override // com.brixcore.util.function.ExceptionalFunction
                        public final Object apply(Object obj) {
                            return this.f$0.lambda$execute$4(fileSystemCreateReadOnlyZipFileSystem, (String) obj);
                        }
                    }));
                }
                if (fileSystemCreateReadOnlyZipFileSystem != null) {
                    fileSystemCreateReadOnlyZipFileSystem.close();
                }
                map.put("SIDE", "client");
                map.put("MINECRAFT_JAR", this.gameRepository.getVersionJar(this.version).getAbsolutePath());
                map.put("MINECRAFT_VERSION", this.gameRepository.getVersionJar(this.version).getAbsolutePath());
                map.put(Logger.ROOT_LOGGER_NAME, this.gameRepository.getBaseDirectory().getAbsolutePath());
                map.put("INSTALLER", this.installer.toAbsolutePath().toString());
                map.put("LIBRARY_DIR", this.gameRepository.getLibrariesDirectory(this.version).getAbsolutePath());
                updateProgress(0L, this.processors.size());
                this.dependencies.add((Task<?>) Task.runSequentially((Task[]) this.processors.stream().map(new Function() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda3
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return this.f$0.lambda$execute$5(map, (ForgeNewInstallProfile.Processor) obj);
                    }
                }).toArray(new IntFunction() { // from class: com.brixcore.download.neoforge.NeoForgeOldInstallTask$$ExternalSyntheticLambda4
                    @Override // java.util.function.IntFunction
                    public final Object apply(int i) {
                        return NeoForgeOldInstallTask.lambda$execute$6(i);
                    }
                })).thenComposeAsync(this.dependencyManager.checkLibraryCompletionAsync(this.neoForgeVersion, true)));
                setResult(this.neoForgeVersion.setPriority(30000).setId(LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId()).setVersion(this.selfVersion));
            } catch (Throwable th) {
                if (fileSystemCreateReadOnlyZipFileSystem != null) {
                    try {
                        fileSystemCreateReadOnlyZipFileSystem.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (ZipException e) {
            throw new ArtifactMalformedException("Malformed neoforge installer file", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$execute$4(FileSystem fs, String str) throws IOException {
        Path dest = Files.createTempFile(this.tempDir, null, null, new FileAttribute[0]);
        FileUtils.copyFile(fs.getPath(str, new String[0]), dest);
        return dest.toString();
    }

    static /* synthetic */ Task[] lambda$execute$6(int x$0) {
        return new Task[x$0];
    }

    @Override // com.brixcore.task.Task
    public boolean doPostExecute() {
        return true;
    }

    @Override // com.brixcore.task.Task
    public void postExecute() throws Exception {
        FileUtils.deleteDirectory(this.tempDir.toFile());
    }
}
