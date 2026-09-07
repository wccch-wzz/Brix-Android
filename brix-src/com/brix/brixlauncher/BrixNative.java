package com.brix.brixlauncher;

/* JADX INFO: loaded from: classes19.dex */
public final class BrixNative {
    public static final String HEX_AUTH_API_URL = "5d2af3c1619022a8d1259811ef16f559555097b86f8f3cf6d570c555a214f45154439da56c9c78a8ca6f";
    public static final String HEX_AUTH_PAGE_TMPL = "572af3c1619022a8d1259811ef16f559555097b86f8f3cf6d570c555a214f451544b86b561ca3eaccf73885de200f91c";
    public static final String HEX_BASE_URL = "642af3c1619022a8d1259811ef16f559555097b86f8f3cf6d570c555a205ec48545cc2";

    private static native boolean checkDebugger();

    private static native boolean checkEmulator();

    private static native int checkEnvironmentIntegrity(Object obj);

    private static native boolean checkRoot();

    private static native boolean checkSuspiciousApps();

    private static native String getAppSignatureHash(Object obj);

    private static native String nativeDecryptBatch(String str);

    private static native String nativeDecryptString(String str);

    static {
        System.loadLibrary("brixnative");
    }

    private BrixNative() {
    }

    public static String getApiBaseUrl() {
        return nativeDecryptString(HEX_BASE_URL);
    }

    public static String getAuthApiUrl() {
        return nativeDecryptString(HEX_AUTH_API_URL);
    }

    public static String getAuthPageTemplate() {
        return nativeDecryptString(HEX_AUTH_PAGE_TMPL);
    }

    public static String getAuthPageUrl(String code) {
        String tmpl = getAuthPageTemplate();
        return tmpl + code;
    }

    public static boolean hasSuspiciousPackages() {
        return false;
    }

    public static boolean isEnvironmentSafe(Object context) {
        return checkEnvironmentIntegrity(context) == 0;
    }

    public static int getEnvironmentScore(Object context) {
        return checkEnvironmentIntegrity(context);
    }

    public static boolean isDebuggerAttached() {
        return checkDebugger();
    }

    public static boolean isRooted() {
        return checkRoot();
    }

    public static boolean isEmulator() {
        return checkEmulator();
    }

    public static boolean isDeviceSecure(Object context) {
        return (checkRoot() || checkEmulator() || checkSuspiciousApps() || checkEnvironmentIntegrity(context) >= 8) ? false : true;
    }
}
