package com.brixcore.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BrixPath.kt */
/* JADX INFO: loaded from: classes11.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001b\n\u0002\u0010\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u0005H\u0007J\u0012\u0010%\u001a\u00020#2\b\u0010&\u001a\u0004\u0018\u00010\u0007H\u0002R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001b\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001d\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001f\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010 \u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010!\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006'"}, d2 = {"Lcom/brixcore/utils/BrixPath;", "", "<init>", "()V", "CONTEXT", "Landroid/content/Context;", "NATIVE_LIB_DIR", "", "LOG_DIR", "CACHE_DIR", "RUNTIME_DIR", "MOD_RUNTIME_DIR", "JAVA_8_PATH", "JAVA_17_PATH", "JAVA_21_PATH", "JAVA_25_PATH", "JAVA_PATH", "JNA_PATH", "LWJGL_DIR", "CACIOCAVALLO_8_DIR", "CACIOCAVALLO_17_DIR", "FILES_DIR", "PLUGIN_DIR", "BACKGROUND_DIR", "CONTROLLER_DIR", "SHARE_DIR", "PRIVATE_COMMON_DIR", "SHARED_COMMON_DIR", "AUTHLIB_INJECTOR_PATH", "LIB_PATCHER_PATH", "MIO_LAUNCH_WRAPPER", "LT_BACKGROUND_PATH", "DK_BACKGROUND_PATH", "LIVE_BACKGROUND_PATH", "loadPaths", "", "context", "initDir", "path", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class BrixPath {
    public static String AUTHLIB_INJECTOR_PATH;
    public static String BACKGROUND_DIR;
    public static String CACHE_DIR;
    public static String CACIOCAVALLO_17_DIR;
    public static String CACIOCAVALLO_8_DIR;
    public static Context CONTEXT;
    public static String CONTROLLER_DIR;
    public static String DK_BACKGROUND_PATH;
    public static String FILES_DIR;
    public static String JAVA_17_PATH;
    public static String JAVA_21_PATH;
    public static String JAVA_25_PATH;
    public static String JAVA_8_PATH;
    public static String JAVA_PATH;
    public static String JNA_PATH;
    public static String LIB_PATCHER_PATH;
    public static String LIVE_BACKGROUND_PATH;
    public static String LOG_DIR;
    public static String LT_BACKGROUND_PATH;
    public static String LWJGL_DIR;
    public static String MIO_LAUNCH_WRAPPER;
    public static String MOD_RUNTIME_DIR;
    public static String NATIVE_LIB_DIR;
    public static String PLUGIN_DIR;
    public static String PRIVATE_COMMON_DIR;
    public static String RUNTIME_DIR;
    public static String SHARE_DIR;
    public static final BrixPath INSTANCE = new BrixPath();
    public static final String SHARED_COMMON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brix/.minecraft";

    private BrixPath() {
    }

    @JvmStatic
    public static final void loadPaths(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        CONTEXT = context;
        NATIVE_LIB_DIR = context.getApplicationInfo().nativeLibraryDir;
        LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brix/log";
        CACHE_DIR = context.getCacheDir().toString() + "/BrixLauncher";
        RUNTIME_DIR = context.getDir("runtime", 0).getAbsolutePath();
        JAVA_PATH = RUNTIME_DIR + "/java";
        JAVA_8_PATH = RUNTIME_DIR + "/java/jre8";
        JAVA_17_PATH = RUNTIME_DIR + "/java/jre17";
        JAVA_21_PATH = RUNTIME_DIR + "/java/jre21";
        JAVA_25_PATH = RUNTIME_DIR + "/java/jre25";
        JNA_PATH = RUNTIME_DIR + "/jna";
        LWJGL_DIR = RUNTIME_DIR + "/lwjgl";
        CACIOCAVALLO_8_DIR = RUNTIME_DIR + "/caciocavallo";
        CACIOCAVALLO_17_DIR = RUNTIME_DIR + "/caciocavallo17";
        MOD_RUNTIME_DIR = context.getDir("runtime_mod", 0).getAbsolutePath();
        FILES_DIR = context.getFilesDir().getAbsolutePath();
        PLUGIN_DIR = FILES_DIR + "/plugins";
        BACKGROUND_DIR = FILES_DIR + "/background";
        CONTROLLER_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brix/control";
        SHARE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brix/share";
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/files");
        }
        PRIVATE_COMMON_DIR = new File(externalFilesDir, ".minecraft").getAbsolutePath();
        AUTHLIB_INJECTOR_PATH = PLUGIN_DIR + "/authlib-injector.jar";
        LIB_PATCHER_PATH = PLUGIN_DIR + "/MioLibPatcher.jar";
        MIO_LAUNCH_WRAPPER = PLUGIN_DIR + "/MioLaunchWrapper.jar";
        LT_BACKGROUND_PATH = BACKGROUND_DIR + "/lt.png";
        DK_BACKGROUND_PATH = BACKGROUND_DIR + "/dk.png";
        LIVE_BACKGROUND_PATH = BACKGROUND_DIR + "/live.mp4";
        INSTANCE.initDir(LOG_DIR);
        INSTANCE.initDir(CACHE_DIR);
        INSTANCE.initDir(RUNTIME_DIR);
        INSTANCE.initDir(MOD_RUNTIME_DIR);
        INSTANCE.initDir(JAVA_8_PATH);
        INSTANCE.initDir(JAVA_25_PATH);
        INSTANCE.initDir(JAVA_17_PATH);
        INSTANCE.initDir(JAVA_21_PATH);
        INSTANCE.initDir(LWJGL_DIR);
        INSTANCE.initDir(CACIOCAVALLO_8_DIR);
        INSTANCE.initDir(CACIOCAVALLO_17_DIR);
        INSTANCE.initDir(FILES_DIR);
        INSTANCE.initDir(PLUGIN_DIR);
        INSTANCE.initDir(BACKGROUND_DIR);
        INSTANCE.initDir(CONTROLLER_DIR);
        INSTANCE.initDir(SHARE_DIR);
        INSTANCE.initDir(PRIVATE_COMMON_DIR);
        INSTANCE.initDir(SHARED_COMMON_DIR);
    }

    private final void initDir(String path) {
        if (path != null) {
            new File(path).mkdirs();
        }
    }
}
