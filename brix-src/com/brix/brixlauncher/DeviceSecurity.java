package com.brix.brixlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.os.EnvironmentCompat;
import java.io.File;
import java.lang.reflect.Method;
import java.net.ProxySelector;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes19.dex */
public final class DeviceSecurity {
    private static final String[] EMULATOR_PROPERTIES = {"goldfish", "emulator", "Android/sdk", "sdk_gphone", "virtual", "nox", "bluestacks", "mumu", "LeapDroid"};
    private static final String[] ROOT_PATHS = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/dev/com.koushikdutta.superuser.daemon/", "/etc/su.bin", "/su/bin/su"};
    private static final String[] SUSPICIOUS_PACKAGES = {"com.jrummy.root.browser.free", "org.lsposed.manager", "io.github.huskydg.magisk"};
    private static final String TAG = "DeviceSecurity";

    private DeviceSecurity() {
    }

    public static SecurityResult checkSecurity(Context context) {
        SecurityResult result = new SecurityResult();
        result.debuggerDetected = isDebuggerConnected();
        result.emulatorDetected = isEmulator(context);
        result.rootDetected = checkRootAccess();
        result.suspiciousAppDetected = hasSuspiciousPackages(context);
        result.proxyDetected = isNetworkProxy();
        result.integrityChecked = checkIntegrity();
        result.isDangerous = result.debuggerDetected || result.rootDetected || result.suspiciousAppDetected;
        Log.i(TAG, "安全检测结果: debugger=" + result.debuggerDetected + ", emulator=" + result.emulatorDetected + ", root=" + result.rootDetected + ", proxy=" + result.proxyDetected + ", suspicious=" + result.suspiciousAppDetected);
        return result;
    }

    private static boolean isDebuggerConnected() {
        try {
            return Debug.isDebuggerConnected();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isEmulator(Context context) {
        try {
            String hardware = Build.HARDWARE;
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String brand = Build.BRAND;
            String product = Build.PRODUCT;
            String device = Build.DEVICE;
            String allInfo = (hardware + StringUtils.SPACE + manufacturer + StringUtils.SPACE + model + StringUtils.SPACE + brand + StringUtils.SPACE + product + StringUtils.SPACE + device).toLowerCase();
            for (String feature : EMULATOR_PROPERTIES) {
                if (allInfo.contains(feature.toLowerCase())) {
                    return true;
                }
            }
            if ("Google".equalsIgnoreCase(manufacturer) && product != null && product.contains("sdk")) {
                return true;
            }
            try {
                Intent batteryIntent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                if (batteryIntent != null) {
                    int status = batteryIntent.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
                    if (status == 1) {
                        return true;
                    }
                }
            } catch (Exception e) {
            }
            return manufacturer == null || manufacturer.toLowerCase().contains(EnvironmentCompat.MEDIA_UNKNOWN) || manufacturer.toLowerCase().contains("simulator");
        } catch (Exception e2) {
            return false;
        }
    }

    private static boolean checkRootAccess() {
        try {
            for (String path : ROOT_PATHS) {
                if (new File(path).exists()) {
                    return true;
                }
            }
            String pathEnv = System.getenv("PATH");
            if (pathEnv != null) {
                for (String path2 : pathEnv.split(":")) {
                    if (new File(path2 + "/su").exists()) {
                        return true;
                    }
                }
            }
            return new File("/init.magisk.rc").exists() || new File("/dbdata/db/com.topjohnsu.magisk").exists();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasSuspiciousPackages(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            for (String pkg : SUSPICIOUS_PACKAGES) {
                try {
                    pm.getApplicationInfo(pkg, 0);
                    return true;
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    private static boolean isNetworkProxy() {
        int port;
        try {
            ProxySelector.getDefault();
            ProxySelector.getDefault();
            String proxyHost = getSystemProperty("http.proxyHost", "");
            String proxyPort = getSystemProperty("http.proxyPort", "");
            return !(proxyHost.isEmpty() || proxyPort.isEmpty() || ((port = Integer.parseInt(proxyPort)) != 8080 && port != 8888 && port != 1080 && port != 10800)) || hasUserInstalledCerts();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkIntegrity() {
        try {
            Context ctx = BrixLauncher.getAppContext();
            if (ctx == null) {
                return true;
            }
            Signature[] signatures = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 64).signatures;
            if (signatures == null || signatures.length <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasUserInstalledCerts() {
        try {
            File certDir = new File(Environment.getExternalStorageDirectory(), "Android/certs");
            return certDir.exists() && certDir.listFiles() != null && certDir.listFiles().length > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean isSafeToRun(Context context) {
        SecurityResult result = checkSecurity(context);
        return !result.isDangerous;
    }

    public static class SecurityResult {
        public boolean debuggerDetected;
        public boolean emulatorDetected;
        public boolean integrityChecked;
        public boolean isDangerous;
        public boolean proxyDetected;
        public boolean rootDetected;
        public boolean suspiciousAppDetected;

        public String toString() {
            return "SecurityResult{debugger=" + this.debuggerDetected + ", emulator=" + this.emulatorDetected + ", root=" + this.rootDetected + ", proxy=" + this.proxyDetected + ", dangerous=" + this.isDangerous + "}";
        }
    }
}
