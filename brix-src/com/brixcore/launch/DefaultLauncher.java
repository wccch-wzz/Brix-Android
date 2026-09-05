package com.brixcore.launch;

import android.content.Context;
import android.os.Build;
import com.brixcore.BrixConfig;
import com.brixcore.auth.AuthInfo;
import com.brixcore.bridge.BrixBridge;
import com.brixcore.data.Renderer;
import com.brixcore.download.LibraryAnalyzer;
import com.brixcore.game.Argument;
import com.brixcore.game.Arguments;
import com.brixcore.game.GameRepository;
import com.brixcore.game.JavaVersion;
import com.brixcore.game.LaunchOptions;
import com.brixcore.game.Version;
import com.brixcore.plugins.NativeLibPlugin;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.Pair;
import com.brixcore.util.StringUtils;
import com.brixcore.util.gson.UUIDTypeAdapter;
import com.brixcore.util.io.FileUtils;
import com.brixcore.util.io.IOUtils;
import com.brixcore.util.io.NetworkUtils;
import com.brixcore.util.platform.CommandBuilder;
import com.brixcore.util.platform.OperatingSystem;
import com.brixcore.util.versioning.GameVersionNumber;
import com.brixcore.util.versioning.VersionNumber;
import com.brixcore.utils.Architecture;
import com.brixcore.utils.BrixPath;
import com.mio.JavaManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SystemProperties;
import org.jackhuang.hmcl.util.ServerAddress;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultLauncher extends Launcher {
    private final Map<String, Supplier<Boolean>> forbiddens;
    private String jnaVersion;
    private String lwjglVersion;
    private boolean useLwjglX;

    public static /* synthetic */ ArrayList $r8$lambda$aw4WkQINtNlXlsGxYEbzatsgkDc() {
        return new ArrayList();
    }

    public DefaultLauncher(Context context, GameRepository repository, Version version, AuthInfo authInfo, LaunchOptions options) {
        super(context, repository, version, authInfo, options);
        this.lwjglVersion = "3.3.3";
        this.useLwjglX = false;
        this.forbiddens = Lang.mapOf(Pair.pair("-Xincgc", new Supplier() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda7
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.lambda$new$6();
            }
        }));
    }

    private CommandBuilder generateCommandLine() throws IOException {
        File libJna;
        String inherits;
        String str;
        final CommandBuilder res = new CommandBuilder();
        getCacioJavaArgs(res, this.version, this.options);
        res.addAllWithoutParsing((Collection) this.options.getJavaArguments().stream().filter(new Predicate() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return DefaultLauncher.lambda$generateCommandLine$0((String) obj);
            }
        }).collect(Collectors.toList()));
        if (this.options.getMaxMemory() != null && this.options.getMaxMemory().intValue() > 0) {
            res.addDefault("-Xmx", this.options.getMaxMemory() + "m");
        }
        if (this.options.getMinMemory() != null && this.options.getMinMemory().intValue() > 0 && (this.options.getMaxMemory() == null || this.options.getMinMemory().intValue() <= this.options.getMaxMemory().intValue())) {
            res.addDefault("-Xms", this.options.getMinMemory() + "m");
        }
        Charset encoding = OperatingSystem.NATIVE_CHARSET;
        String fileEncoding = res.addDefault("-Dfile.encoding=", encoding.name());
        if (fileEncoding != null && !"-Dfile.encoding=COMPAT".equals(fileEncoding)) {
            try {
                encoding = Charset.forName(fileEncoding.substring("-Dfile.encoding=".length()));
            } catch (Throwable ex) {
                Logging.LOG.log(Level.WARNING, "Bad file encoding", ex);
            }
        }
        if (this.options.getJava().getVersion() < 19) {
            res.addDefault("-Dsun.stdout.encoding=", encoding.name());
            res.addDefault("-Dsun.stderr.encoding=", encoding.name());
        } else {
            res.addDefault("-Dstdout.encoding=", encoding.name());
            res.addDefault("-Dstderr.encoding=", encoding.name());
        }
        res.addDefault("-Djava.rmi.server.useCodebaseOnly=", "true");
        res.addDefault("-Dcom.sun.jndi.rmi.object.trustURLCodebase=", "false");
        res.addDefault("-Dcom.sun.jndi.cosnaming.object.trustURLCodebase=", "false");
        String formatMsgNoLookups = res.addDefault("-Dlog4j2.formatMsgNoLookups=", "true");
        if (isUsingLog4j() && (this.options.isDebugLog() || !"-Dlog4j2.formatMsgNoLookups=false".equals(formatMsgNoLookups))) {
            res.addDefault("-Dlog4j.configurationFile=", getLog4jConfigurationFile().getAbsolutePath());
        }
        appendJvmArgs(res);
        res.addDefault("-Dminecraft.client.jar=", this.repository.getVersionJar(this.version).toString());
        if (Architecture.is32BitsDevice()) {
            res.addDefault("-Xss", "1m");
        }
        res.addDefault("-XX:ActiveProcessorCount=", String.valueOf(Runtime.getRuntime().availableProcessors()));
        res.addDefault("-Dfml.ignoreInvalidMinecraftCertificates=", "true");
        res.addDefault("-Dfml.ignorePatchDiscrepancies=", "true");
        JavaVersion javaVersion = this.options.getJava().getIsAuto() ? JavaManager.getSuitableJavaVersion(this.version) : this.options.getJava();
        res.addDefault("-Dext.net.resolvPath=", BrixPath.JAVA_PATH + "/resolv.conf");
        res.addDefault("-Djava.io.tmpdir=", BrixPath.CACHE_DIR);
        res.addDefault("-Dos.name=", "Linux");
        res.addDefault("-Dos.version=Android-", Build.VERSION.RELEASE);
        res.addDefault("-Dorg.lwjgl.opengl.libname=", "${gl_lib_name}");
        res.addDefault("-Dorg.lwjgl.openal.libname=", this.context.getApplicationInfo().nativeLibraryDir + "/libopenal.so");
        res.addDefault("-Dorg.lwjgl.freetype.libname=", BrixPath.LWJGL_DIR + "/" + this.lwjglVersion + "/natives/" + Architecture.archAsStringAndroid(Architecture.getDeviceArchitecture()) + "/libfreetype.so");
        res.addDefault("-Dorg.lwjgl.system.allocator=", "system");
        res.addDefault("-Dfml.earlyprogresswindow=", "false");
        res.addDefault("-Dglfwstub.initEgl=", "false");
        res.addDefault("-Dloader.disable_forked_guis=", "true");
        res.addDefault("-Duser.home=", this.options.getGameDir().getAbsolutePath());
        res.addDefault("-Duser.language=", System.getProperty(SystemProperties.USER_LANGUAGE));
        res.addDefault("-Duser.country=", Locale.getDefault().getCountry());
        res.addDefault("-Duser.timezone=", TimeZone.getDefault().getID());
        res.addDefault("-Dorg.lwjgl.vulkan.libname=", "libvulkan.so");
        res.addDefault("-Dorg.lwjgl.spvc.libname=", "spirv-cross-c-shared");
        res.addDefault("-Dsodium.checks.issue2561=", "false");
        res.addDefault("-Djdk.lang.Process.launchMechanism=", "FORK");
        res.addDefault("-Dcpu.name=", Architecture.getSocName());
        NativeLibPlugin.getJVMEnv().forEach(new BiConsumer() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda9
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                res.addDefault(((String) obj) + NetworkUtils.NAME_VALUE_SEPARATOR, (String) obj2);
            }
        });
        File libJna2 = new File(BrixPath.RUNTIME_DIR, "jna");
        if (this.jnaVersion != null && !this.jnaVersion.isEmpty()) {
            libJna = new File(libJna2, this.jnaVersion);
        } else {
            libJna = libJna2;
        }
        res.addDefault("-Djna.boot.library.path=", libJna.exists() ? libJna.getAbsolutePath() : this.context.getApplicationInfo().nativeLibraryDir);
        if (this.repository.getGameVersion(this.version).isPresent() && this.repository.getGameVersion(this.version).get().equals("1.7.2")) {
            res.addDefault("-Dsort.patch=", "true");
        }
        res.add("-javaagent:" + BrixPath.LIB_PATCHER_PATH);
        Set<String> classpath = this.repository.getClasspath(this.version);
        addLWJGLClassPath(classpath);
        classpath.add(BrixPath.MIO_LAUNCH_WRAPPER);
        File jar = this.repository.getVersionJar(this.version);
        if ((!jar.exists() || !jar.isFile()) && (inherits = this.version.getInheritsFrom()) != null && !inherits.isEmpty()) {
            jar = this.repository.getVersionJar(inherits);
        }
        classpath.add(jar.getAbsolutePath());
        Path gameAssets = this.repository.getActualAssetDirectory(this.version.getId(), this.version.getAssetIndex().getId());
        final Map<String, String> configuration = getConfigurations();
        configuration.put("${classpath}", String.join(File.pathSeparator, classpath));
        configuration.put("${game_assets}", gameAssets.toAbsolutePath().toString());
        configuration.put("${assets_root}", gameAssets.toAbsolutePath().toString());
        configuration.put("${natives_directory}", "${natives_directory}");
        List<String> jvmArgs = Arguments.parseArguments((List) this.version.getArguments().map(new Function() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Arguments) obj).getJvm();
            }
        }).orElseGet(new Supplier() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda11
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.getDefaultJVMArguments();
            }
        }), configuration);
        res.addAll((Collection) jvmArgs.stream().map(new Function() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda12
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DefaultLauncher.lambda$generateCommandLine$2((String) obj);
            }
        }).collect(Collectors.toList()));
        Arguments argumentsFromAuthInfo = this.authInfo.getLaunchArguments(this.options);
        if (argumentsFromAuthInfo != null && argumentsFromAuthInfo.getJvm() != null && !argumentsFromAuthInfo.getJvm().isEmpty()) {
            res.addAll(Arguments.parseArguments(argumentsFromAuthInfo.getJvm(), configuration));
        }
        if (javaVersion.getVersion() != 8) {
            res.add("--add-exports");
            String pkg = this.version.getMainClass().substring(0, this.version.getMainClass().lastIndexOf("."));
            res.add(pkg + "/" + pkg + "=ALL-UNNAMED");
        }
        res.add("mio.Wrapper");
        res.add(this.version.getMainClass());
        res.addAll(Arguments.parseStringArguments((List) this.version.getMinecraftArguments().map(new Function() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return StringUtils.tokenize((String) obj);
            }
        }).orElseGet(new Supplier() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return DefaultLauncher.$r8$lambda$aw4WkQINtNlXlsGxYEbzatsgkDc();
            }
        }), configuration));
        final Map<String, Boolean> features = getFeatures();
        this.version.getArguments().map(new Function() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Arguments) obj).getGame();
            }
        }).ifPresent(new Consumer() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                res.addAll(Arguments.parseArguments((List) obj, configuration, features));
            }
        });
        if (this.version.getMinecraftArguments().isPresent()) {
            res.addAll(Arguments.parseArguments(getDefaultGameArguments(), configuration, features));
        }
        if (argumentsFromAuthInfo != null && argumentsFromAuthInfo.getGame() != null && !argumentsFromAuthInfo.getGame().isEmpty()) {
            res.addAll(Arguments.parseArguments(argumentsFromAuthInfo.getGame(), configuration, features));
        }
        String address = this.options.getServerIp();
        if (StringUtils.isNotBlank(address)) {
            try {
                ServerAddress parsed = ServerAddress.parse(address);
                if (GameVersionNumber.compare(this.repository.getGameVersion(this.version).orElse("0.0"), "1.20") < 0) {
                    try {
                        res.add("--server");
                        res.add(parsed.getHost());
                        res.add("--port");
                        String[] strArr = new String[1];
                        strArr[0] = parsed.getPort() >= 0 ? String.valueOf(parsed.getPort()) : "25565";
                        res.add(strArr);
                    } catch (IllegalArgumentException e) {
                        e = e;
                        Logging.LOG.warning("Invalid server address: " + address + org.apache.commons.lang3.StringUtils.LF + e);
                    }
                } else {
                    res.add("--quickPlayMultiplayer");
                    String[] strArr2 = new String[1];
                    if (parsed.getPort() < 0) {
                        try {
                            str = address + ":25565";
                        } catch (IllegalArgumentException e2) {
                            e = e2;
                            Logging.LOG.warning("Invalid server address: " + address + org.apache.commons.lang3.StringUtils.LF + e);
                        }
                    } else {
                        str = address;
                    }
                    strArr2[0] = str;
                    res.add(strArr2);
                }
            } catch (IllegalArgumentException e3) {
                e = e3;
            }
        }
        res.addAllWithoutParsing(Arguments.parseStringArguments(this.options.getGameArguments(), configuration));
        res.removeIf(new Predicate() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return this.f$0.lambda$generateCommandLine$4((String) obj);
            }
        });
        return res;
    }

    static /* synthetic */ boolean lambda$generateCommandLine$0(String arg) {
        return !arg.equals("noXmx");
    }

    static /* synthetic */ String lambda$generateCommandLine$2(String arg) {
        if (!arg.contains("-Dio.netty.native.workdir") && !arg.contains("-Djna.tmpdir") && !arg.contains("-Dorg.lwjgl.system.SharedLibraryExtractPath")) {
            return arg;
        }
        String result = arg.replace("${natives_directory}", BrixPath.CACHE_DIR);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$generateCommandLine$4(String it) {
        return getForbiddens().containsKey(it) && getForbiddens().get(it).get().booleanValue();
    }

    private void addLWJGLClassPath(Set<String> classpath) {
        Set<String> temp = new LinkedHashSet<>();
        File dir = new File(BrixPath.LWJGL_DIR, this.lwjglVersion);
        temp.add(dir.getAbsolutePath() + "/lwjgl.jar");
        if (this.useLwjglX) {
            temp.add(dir.getAbsolutePath() + "/lwjgl-lwjglx.jar");
        }
        File[] files = dir.listFiles();
        if (files != null) {
            Set<String> list = (Set) Arrays.stream(files).filter(new Predicate() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DefaultLauncher.lambda$addLWJGLClassPath$5((File) obj);
                }
            }).map(new Function() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((File) obj).getAbsolutePath();
                }
            }).collect(Collectors.toSet());
            temp.addAll(list);
        } else {
            Logging.LOG.warning("LWJGL directory(" + dir + ") not found!");
        }
        temp.addAll(classpath);
        classpath.clear();
        classpath.addAll(temp);
    }

    static /* synthetic */ boolean lambda$addLWJGLClassPath$5(File file) {
        return (!file.getName().endsWith(".jar") || file.getName().equals("lwjgl.jar") || file.getName().equals("lwjgl-lwjglx.jar")) ? false : true;
    }

    public static void getCacioJavaArgs(CommandBuilder res, Version version, LaunchOptions options) {
        JavaVersion javaVersion;
        if (options.getJava().getIsAuto()) {
            javaVersion = JavaManager.getSuitableJavaVersion(version);
        } else {
            javaVersion = options.getJava();
        }
        boolean isJava8 = javaVersion.getVersion() == 8;
        res.addDefault("-Djava.awt.headless=", "false");
        res.addDefault("-Dcacio.managed.screensize=", options.getWidth() + "x" + options.getHeight());
        res.addDefault("-Dcacio.font.fontmanager=", "sun.awt.X11FontManager");
        res.addDefault("-Dcacio.font.fontscaler=", "sun.font.FreetypeFontScaler");
        res.addDefault("-Dswing.defaultlaf=", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
        if (isJava8) {
            res.addDefault("-Dawt.toolkit=", "net.java.openjdk.cacio.ctc.CTCToolkit");
            res.addDefault("-Djava.awt.graphicsenv=", "net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
        } else {
            res.addDefault("-Dawt.toolkit=", "com.github.caciocavallosilano.cacio.ctc.CTCToolkit");
            res.addDefault("-Djava.awt.graphicsenv=", "com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment");
            res.addDefault("-javaagent:", BrixPath.CACIOCAVALLO_17_DIR + "/cacio-agent.jar");
            res.add("--add-exports=java.desktop/java.awt=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/java.awt.dnd.peer=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt.event=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt.datatransfer=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.font=ALL-UNNAMED");
            res.add("--add-exports=java.base/sun.security.action=ALL-UNNAMED");
            res.add("--add-opens=java.base/java.util=ALL-UNNAMED");
            res.add("--add-opens=java.desktop/java.awt=ALL-UNNAMED");
            res.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");
            res.add("--add-opens=java.desktop/sun.java2d=ALL-UNNAMED");
            res.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
            res.add("--add-opens=java.base/java.net=ALL-UNNAMED");
        }
        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/").append(isJava8 ? "p" : "a");
        File cacioDir = new File(isJava8 ? BrixPath.CACIOCAVALLO_8_DIR : BrixPath.CACIOCAVALLO_17_DIR);
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : (File[]) Objects.requireNonNull(cacioDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        res.add(cacioClasspath.toString());
    }

    public Map<String, Boolean> getFeatures() {
        return Collections.singletonMap("has_custom_resolution", Boolean.valueOf((this.options.getHeight() == null || this.options.getHeight().intValue() == 0 || this.options.getWidth() == null || this.options.getWidth().intValue() == 0) ? false : true));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$new$6() {
        return Boolean.valueOf(this.options.getJava().getVersion() >= 9);
    }

    protected Map<String, Supplier<Boolean>> getForbiddens() {
        return this.forbiddens;
    }

    protected List<Argument> getDefaultJVMArguments() {
        return Arguments.DEFAULT_JVM_ARGUMENTS;
    }

    protected List<Argument> getDefaultGameArguments() {
        return Arguments.DEFAULT_GAME_ARGUMENTS;
    }

    protected void appendJvmArgs(CommandBuilder result) {
    }

    private boolean isUsingLog4j() {
        return GameVersionNumber.compare(this.repository.getGameVersion(this.version).orElse("1.7"), "1.7") >= 0;
    }

    public File getLog4jConfigurationFile() {
        return new File(this.repository.getVersionRoot(this.version.getId()), "log4j2.xml");
    }

    public void extractLog4jConfigurationFile() throws IOException {
        InputStream source;
        File targetFile = getLog4jConfigurationFile();
        if (GameVersionNumber.asGameVersion(this.repository.getGameVersion(this.version)).compareTo("1.12") < 0) {
            if (this.options.isDebugLog()) {
                source = DefaultLauncher.class.getResourceAsStream("/assets/game/log4j2-1.7-debug.xml");
            } else {
                source = DefaultLauncher.class.getResourceAsStream("/assets/game/log4j2-1.7.xml");
            }
        } else if (this.options.isDebugLog()) {
            source = DefaultLauncher.class.getResourceAsStream("/assets/game/log4j2-1.12-debug.xml");
        } else {
            source = DefaultLauncher.class.getResourceAsStream("/assets/game/log4j2-1.12.xml");
        }
        InputStream input = source;
        try {
            OutputStream output = new FileOutputStream(targetFile);
            try {
                IOUtils.copyTo(input, output);
                output.close();
                if (input != null) {
                    input.close();
                }
            } catch (Throwable th) {
                try {
                    output.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Throwable th3) {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    protected Map<String, String> getConfigurations() {
        String uuid = this.options.getUuid().replace("-", "");
        boolean customUuid = uuid.length() == 32;
        Pair[] pairArr = new Pair[19];
        pairArr[0] = Pair.pair("${auth_player_name}", this.authInfo.getUsername());
        pairArr[1] = Pair.pair("${auth_session}", this.authInfo.getAccessToken());
        pairArr[2] = Pair.pair("${auth_access_token}", this.authInfo.getAccessToken());
        pairArr[3] = Pair.pair("${auth_uuid}", customUuid ? this.options.getUuid() : UUIDTypeAdapter.fromUUID(this.authInfo.getUUID()));
        pairArr[4] = Pair.pair("${version_name}", (String) Optional.ofNullable(this.options.getVersionName()).orElse(this.version.getId()));
        pairArr[5] = Pair.pair("${profile_name}", (String) Optional.ofNullable(this.options.getProfileName()).orElse("Minecraft"));
        pairArr[6] = Pair.pair("${version_type}", (String) Optional.ofNullable(this.options.getVersionType()).orElse(this.version.getType().getId()));
        pairArr[7] = Pair.pair("${game_directory}", this.repository.getRunDirectory(this.version.getId()).getAbsolutePath());
        pairArr[8] = Pair.pair("${user_type}", AuthInfo.USER_TYPE_MSA);
        pairArr[9] = Pair.pair("${assets_index_name}", this.version.getAssetIndex().getId());
        pairArr[10] = Pair.pair("${user_properties}", this.authInfo.getUserProperties());
        pairArr[11] = Pair.pair("${resolution_width}", this.options.getWidth().toString());
        pairArr[12] = Pair.pair("${resolution_height}", this.options.getHeight().toString());
        pairArr[13] = Pair.pair("${library_directory}", this.repository.getLibrariesDirectory(this.version).getAbsolutePath());
        pairArr[14] = Pair.pair("${classpath_separator}", File.pathSeparator);
        pairArr[15] = Pair.pair("${primary_jar}", this.repository.getVersionJar(this.version).getAbsolutePath());
        pairArr[16] = Pair.pair("${language}", Locale.getDefault().toString());
        pairArr[17] = Pair.pair("${file_separator}", File.pathSeparator);
        pairArr[18] = Pair.pair("${primary_jar_name}", FileUtils.getName(this.repository.getVersionJar(this.version).toPath()));
        return Lang.mapOf(pairArr);
    }

    @Override // com.brixcore.launch.Launcher
    public BrixBridge launch() throws InterruptedException, IOException {
        CommandBuilder command = generateCommandLine();
        List<String> rawCommandLine = command.asList();
        if (rawCommandLine.stream().anyMatch(new Predicate() { // from class: com.brixcore.launch.DefaultLauncher$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return StringUtils.isBlank((String) obj);
            }
        })) {
            throw new IllegalStateException("Illegal command line " + rawCommandLine);
        }
        if (isUsingLog4j()) {
            extractLog4jConfigurationFile();
        }
        String[] finalArgs = (String[]) rawCommandLine.toArray(new String[0]);
        LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(this.version, this.repository.getGameVersion(this.version).orElse(null));
        Renderer renderer = this.options.getRenderer();
        BrixConfig config = new BrixConfig(this.context, BrixPath.LOG_DIR, this.options.getJava().getJavaPath(this.version), this.repository.getRunDirectory(this.version.getId()).getAbsolutePath(), renderer, finalArgs);
        config.setUseVKDriverSystem(this.options.isVKDriverSystem());
        config.setPojavBigCore(this.options.isPojavBigCore());
        config.setInstalledModLoaders(new BrixConfig.InstalledModLoaders(analyzer.has(LibraryAnalyzer.LibraryType.FORGE), analyzer.has(LibraryAnalyzer.LibraryType.CLEANROOM), analyzer.has(LibraryAnalyzer.LibraryType.NEO_FORGE), analyzer.has(LibraryAnalyzer.LibraryType.OPTIFINE), analyzer.has(LibraryAnalyzer.LibraryType.LITELOADER), analyzer.has(LibraryAnalyzer.LibraryType.FABRIC), analyzer.has(LibraryAnalyzer.LibraryType.QUILT)));
        config.setLwjglVersion(this.lwjglVersion);
        return launchGame(config);
    }

    protected BrixBridge launchGame(BrixConfig config) {
        return null;
    }

    public void setJnaVersion(String jnaVersion) {
        this.jnaVersion = jnaVersion;
    }

    public void setLwjglVersion(String lwjglVersion) {
        try {
            VersionNumber v1 = VersionNumber.asVersion(lwjglVersion);
            if (v1.compareTo("3.0") < 0) {
                this.useLwjglX = true;
            }
            if (v1.compareTo("3.4.1") >= 0) {
                this.lwjglVersion = "3.4.1";
                return;
            }
        } catch (Throwable th) {
        }
        this.lwjglVersion = "3.3.3";
    }
}
