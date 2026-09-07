package com.brixcore.util.platform;

import android.app.ActivityManager;
import android.content.Context;
import com.brixcore.utils.Architecture;

/* JADX INFO: loaded from: classes7.dex */
public class MemoryUtils {
    public static int getTotalDeviceMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);
        return (int) (memInfo.totalMem / 1048576);
    }

    public static int getUsedDeviceMemory(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) ((memInfo.totalMem - memInfo.availMem) / 1048576);
    }

    public static int getFreeDeviceMemory(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService("activity");
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) (memInfo.availMem / 1048576);
    }

    public static int findBestRAMAllocation(Context context) {
        int totalDeviceMemory = getTotalDeviceMemory(context);
        if (totalDeviceMemory <= 1024) {
            return 512;
        }
        if (totalDeviceMemory <= 6144) {
            return Architecture.is32BitsDevice() ? 768 : 1024;
        }
        if (totalDeviceMemory <= 12288) {
            return Architecture.is32BitsDevice() ? 768 : 2048;
        }
        return Architecture.is32BitsDevice() ? 768 : 4096;
    }
}
