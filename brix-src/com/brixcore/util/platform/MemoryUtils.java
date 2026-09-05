package com.brixcore.util.platform;

import android.app.ActivityManager;
import android.content.Context;
import com.brixcore.utils.Architecture;
import com.google.android.material.internal.ViewUtils;
import org.apache.commons.io.FileUtils;

/* JADX INFO: loaded from: classes7.dex */
public class MemoryUtils {
    public static int getTotalDeviceMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);
        return (int) (memInfo.totalMem / FileUtils.ONE_MB);
    }

    public static int getUsedDeviceMemory(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) ((memInfo.totalMem - memInfo.availMem) / FileUtils.ONE_MB);
    }

    public static int getFreeDeviceMemory(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) (memInfo.availMem / FileUtils.ONE_MB);
    }

    public static int findBestRAMAllocation(Context context) {
        int totalDeviceMemory = getTotalDeviceMemory(context);
        if (totalDeviceMemory <= 1024) {
            return 512;
        }
        if (totalDeviceMemory <= 6144) {
            if (Architecture.is32BitsDevice()) {
                return ViewUtils.EDGE_TO_EDGE_FLAGS;
            }
            return 1024;
        }
        if (totalDeviceMemory <= 12288) {
            if (Architecture.is32BitsDevice()) {
                return ViewUtils.EDGE_TO_EDGE_FLAGS;
            }
            return 2048;
        }
        if (Architecture.is32BitsDevice()) {
            return ViewUtils.EDGE_TO_EDGE_FLAGS;
        }
        return 4096;
    }
}
