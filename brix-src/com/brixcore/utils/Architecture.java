package com.brixcore.utils;

import android.os.Build;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: loaded from: classes11.dex */
public class Architecture {
    public static final int ARCH_ARM = 2;
    public static final int ARCH_ARM64 = 1;
    public static final int ARCH_X86 = 4;
    public static final int ARCH_X86_64 = 8;
    public static final int UNSUPPORTED_ARCH = -1;

    public static boolean is64BitsDevice() {
        return Build.SUPPORTED_64_BIT_ABIS.length != 0;
    }

    public static boolean is32BitsDevice() {
        return !is64BitsDevice();
    }

    public static int getDeviceArchitecture() {
        if (isx86Device()) {
            return is64BitsDevice() ? 8 : 4;
        }
        return is64BitsDevice() ? 1 : 2;
    }

    public static boolean isx86Device() {
        String[] ABI = is64BitsDevice() ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
        int comparedArch = is64BitsDevice() ? 8 : 4;
        for (String str : ABI) {
            if (archAsInt(str) == comparedArch) {
                return true;
            }
        }
        return false;
    }

    public static boolean isArmDevice() {
        return !isx86Device();
    }

    public static int archAsInt(String arch) {
        String arch2 = arch.toLowerCase().trim().replace(StringUtils.SPACE, "");
        if (arch2.contains("arm64") || arch2.equals("aarch64")) {
            return 1;
        }
        if (arch2.contains("arm") || arch2.equals("aarch32")) {
            return 2;
        }
        if (arch2.contains("x86_64") || arch2.contains("amd64")) {
            return 8;
        }
        if (arch2.contains("x86")) {
            return 4;
        }
        if (arch2.startsWith("i") && arch2.endsWith("86")) {
            return 4;
        }
        return -1;
    }

    public static String archAsString(int arch) {
        if (arch == 1) {
            return "arm64";
        }
        if (arch == 2) {
            return "arm";
        }
        if (arch == 8) {
            return "x86_64";
        }
        return arch == 4 ? "x86" : "UNSUPPORTED_ARCH";
    }

    public static String archAsStringAndroid(int arch) {
        if (arch == 1) {
            return "arm64-v8a";
        }
        if (arch == 2) {
            return "armeabi-v7a";
        }
        if (arch == 8) {
            return "x86_64";
        }
        return arch == 4 ? "x86" : "UNSUPPORTED_ARCH";
    }

    public static String getSocName() {
        String name = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.soc.model");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            name = reader.readLine();
            reader.close();
        } catch (Exception e) {
        }
        return (name == null || name.trim().isEmpty()) ? Build.HARDWARE : name;
    }
}
