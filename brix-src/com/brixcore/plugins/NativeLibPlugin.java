package com.brixcore.plugins;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes16.dex */
public class NativeLibPlugin {

    public interface NativePlugin {
        String getAppName();

        String getMaxMCVer();

        String getMinMCVer();
    }

    public static List<NativePlugin> getPluginList() {
        return Collections.emptyList();
    }

    public static String getPaths(String split) {
        return "";
    }

    public static Map<String, String> getJVMEnv() {
        return Collections.emptyMap();
    }
}
