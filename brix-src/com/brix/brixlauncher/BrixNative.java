package com.brix.brixlauncher;

import android.util.Log;

/* JADX INFO: loaded from: classes19.dex */
public final class BrixNative {
    public static final String HEX_AUTH_API_URL = "5d2af3c1619022a8d1259811ef16f559555097b86f8f3cf6d570c555a214f45154439da56c9c78a8ca6f";
    public static final String HEX_AUTH_PAGE_TMPL = "572af3c1619022a8d1259811ef16f559555097b86f8f3cf6d570c555a214f451544b86b561ca3eaccf73885de200f91c";
    public static final String HEX_BASE_URL = "642af3c1619022a8d1259811ef16f559555097b86f8f3cf6d570c555a205ec48545cc2";
    private static final String TAG = "BrixNative";
    private static volatile boolean nativeLoaded;

    private static native boolean checkDebugger();

    private static native boolean checkEmulator();

    private static native int checkEnvironmentIntegrity(Object obj);

    private static native boolean checkRoot();

    private static native boolean checkSuspiciousApps();

    private static native String getAppSignatureHash(Object obj);

    private static native String nativeDecryptBatch(String str);

    private static native String nativeDecryptString(String str);

    static {
        nativeLoaded = false;
        try {
            System.loadLibrary("brixnative");
            nativeLoaded = true;
            Log.d(TAG, "libbrixnative 加载成功");
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "libbrixnative 未找到，使用降级模式: " + e.getMessage());
            nativeLoaded = false;
        }
    }

    private BrixNative() {
    }

    public static String getApiBaseUrl() {
        if (nativeLoaded) {
            return nativeDecryptString(HEX_BASE_URL);
        }
        return null;
    }

    public static String getAuthApiUrl() {
        if (nativeLoaded) {
            return nativeDecryptString(HEX_AUTH_API_URL);
        }
        return null;
    }

    public static String getAuthPageTemplate() {
        if (nativeLoaded) {
            return nativeDecryptString(HEX_AUTH_PAGE_TMPL);
        }
        return null;
    }

    public static String getAuthPageUrl(String code) {
        String tmpl = getAuthPageTemplate();
        if (tmpl == null) {
            return null;
        }
        return tmpl + code;
    }

    public static boolean hasSuspiciousPackages() {
        return !nativeLoaded ? false : false;
    }

    public static boolean isEnvironmentSafe(Object context) {
        return !nativeLoaded || checkEnvironmentIntegrity(context) == 0;
    }

    public static int getEnvironmentScore(Object context) {
        if (nativeLoaded) {
            return checkEnvironmentIntegrity(context);
        }
        return 0;
    }

    public static boolean isDebuggerAttached() {
        if (nativeLoaded) {
            return checkDebugger();
        }
        return false;
    }

    public static boolean isRooted() {
        if (nativeLoaded) {
            return checkRoot();
        }
        return false;
    }

    public static boolean isEmulator() {
        if (nativeLoaded) {
            return checkEmulator();
        }
        return false;
    }

    public static boolean isDeviceSecure(Object context) {
        if (nativeLoaded) {
            return (checkRoot() || checkEmulator() || checkSuspiciousApps() || checkEnvironmentIntegrity(context) >= 8) ? false : true;
        }
        return true;
    }
}
