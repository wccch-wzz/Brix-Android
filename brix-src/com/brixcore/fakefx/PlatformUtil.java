package com.brixcore.fakefx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.lang3.SystemProperties;

/* JADX INFO: loaded from: classes9.dex */
public class PlatformUtil {
    private static final boolean ANDROID;
    private static final boolean IOS;
    private static final boolean LINUX;
    private static final boolean MAC;
    private static final boolean SOLARIS;
    private static final boolean STATIC_BUILD;
    private static final boolean WINDOWS;
    private static final boolean WINDOWS_7_OR_LATER;
    private static final boolean WINDOWS_VISTA_OR_LATER;
    private static final boolean doEGLCompositing;
    private static final boolean embedded;
    private static final String embeddedType;
    private static String javafxPlatform;
    private static final boolean useEGL;
    private static final String os = System.getProperty(SystemProperties.OS_NAME);
    private static final String version = System.getProperty(SystemProperties.OS_VERSION);

    static {
        String str1 = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda1
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return System.getProperty("javafx.platform");
            }
        });
        javafxPlatform = str1;
        loadProperties();
        boolean bool1 = ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda2
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return Boolean.valueOf(Boolean.getBoolean("com.sun.javafx.isEmbedded"));
            }
        })).booleanValue();
        embedded = bool1;
        String str2 = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda3
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return System.getProperty("glass.platform", "").toLowerCase(Locale.ROOT);
            }
        });
        embeddedType = str2;
        boolean bool2 = ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda4
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return Boolean.valueOf(Boolean.getBoolean("use.egl"));
            }
        })).booleanValue();
        useEGL = bool2;
        boolean z = false;
        if (useEGL) {
            boolean bool3 = ((Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda5
                @Override // java.security.PrivilegedAction
                public final Object run() {
                    return Boolean.valueOf(Boolean.getBoolean("doNativeComposite"));
                }
            })).booleanValue();
            doEGLCompositing = bool3;
        } else {
            doEGLCompositing = false;
        }
        ANDROID = "android".equals(javafxPlatform) || "Dalvik".equals(System.getProperty(SystemProperties.JAVA_VM_NAME));
        WINDOWS = os.startsWith("Windows");
        WINDOWS_VISTA_OR_LATER = WINDOWS && versionNumberGreaterThanOrEqualTo(6.0f);
        WINDOWS_7_OR_LATER = WINDOWS && versionNumberGreaterThanOrEqualTo(6.1f);
        MAC = os.startsWith("Mac");
        if (os.startsWith("Linux") && !ANDROID) {
            z = true;
        }
        LINUX = z;
        SOLARIS = os.startsWith("SunOS");
        IOS = os.startsWith("iOS");
        STATIC_BUILD = "Substrate VM".equals(System.getProperty(SystemProperties.JAVA_VM_NAME));
    }

    private static boolean versionNumberGreaterThanOrEqualTo(float value) {
        try {
            return Float.parseFloat(version) >= value;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isWindows() {
        return WINDOWS;
    }

    public static boolean isWinVistaOrLater() {
        return WINDOWS_VISTA_OR_LATER;
    }

    public static boolean isWin7OrLater() {
        return WINDOWS_7_OR_LATER;
    }

    public static boolean isMac() {
        return MAC;
    }

    public static boolean isLinux() {
        return LINUX;
    }

    public static boolean useEGL() {
        return useEGL;
    }

    public static boolean useEGLWindowComposition() {
        return doEGLCompositing;
    }

    public static boolean useGLES2() {
        String useGles2 = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda6
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return System.getProperty("use.gles2");
            }
        });
        if ("true".equals(useGles2)) {
            return true;
        }
        return false;
    }

    public static boolean isSolaris() {
        return SOLARIS;
    }

    public static boolean isUnix() {
        return LINUX || SOLARIS;
    }

    public static boolean isEmbedded() {
        return embedded;
    }

    public static String getEmbeddedType() {
        return embeddedType;
    }

    public static boolean isIOS() {
        return IOS;
    }

    public static boolean isStaticBuild() {
        return STATIC_BUILD;
    }

    private static void loadPropertiesFromFile(File file) {
        Properties p = new Properties();
        try {
            InputStream in = new FileInputStream(file);
            p.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (javafxPlatform == null) {
            javafxPlatform = p.getProperty("javafx.platform");
        }
        String prefix = javafxPlatform + ".";
        int prefixLength = prefix.length();
        boolean foundPlatform = false;
        for (Object o : p.keySet()) {
            String key = (String) o;
            if (key.startsWith(prefix)) {
                foundPlatform = true;
                String systemKey = key.substring(prefixLength);
                if (System.getProperty(systemKey) == null) {
                    String value = p.getProperty(key);
                    System.setProperty(systemKey, value);
                }
            }
        }
        if (!foundPlatform) {
            System.err.println("Warning: No settings found for javafx.platform='" + javafxPlatform + "'");
        }
    }

    private static File getRTDir() {
        try {
            URL url = PlatformUtil.class.getResource("PlatformUtil.class");
            if (url == null) {
                return null;
            }
            String classUrlString = url.toString();
            if (classUrlString.startsWith("jar:file:") && classUrlString.indexOf(33) != -1) {
                String s = classUrlString.substring(4, classUrlString.lastIndexOf(33));
                int lastIndexOfSlash = Math.max(s.lastIndexOf(47), s.lastIndexOf(92));
                return new File(new URL(s.substring(0, lastIndexOfSlash + 1)).getPath());
            }
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private static void loadProperties() {
        String vmname = System.getProperty(SystemProperties.JAVA_VM_NAME);
        String arch = System.getProperty(SystemProperties.OS_ARCH);
        if (javafxPlatform == null && ((arch == null || !arch.equals("arm")) && (vmname == null || vmname.indexOf("Embedded") <= 0))) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction() { // from class: com.brixcore.fakefx.PlatformUtil$$ExternalSyntheticLambda0
            @Override // java.security.PrivilegedAction
            public final Object run() {
                return PlatformUtil.lambda$loadProperties$6();
            }
        });
    }

    static /* synthetic */ Void lambda$loadProperties$6() {
        File rtDir = getRTDir();
        File rtProperties = new File(rtDir, "javafx.platform.properties");
        if (rtProperties.exists()) {
            loadPropertiesFromFile(rtProperties);
            return null;
        }
        String javaHome = System.getProperty("java.home");
        File javaHomeProperties = new File(javaHome, "lib" + File.separator + "javafx.platform.properties");
        if (javaHomeProperties.exists()) {
            loadPropertiesFromFile(javaHomeProperties);
            return null;
        }
        String javafxRuntimePath = System.getProperty("javafx.runtime.path");
        File javafxRuntimePathProperties = new File(javafxRuntimePath, File.separator + "javafx.platform.properties");
        if (!javafxRuntimePathProperties.exists()) {
            return null;
        }
        loadPropertiesFromFile(javafxRuntimePathProperties);
        return null;
    }

    public static boolean isAndroid() {
        return ANDROID;
    }
}
