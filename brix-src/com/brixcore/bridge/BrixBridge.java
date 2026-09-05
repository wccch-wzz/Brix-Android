package com.brixcore.bridge;

import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
public class BrixBridge implements Serializable {
    public static final int DEFAULT_HEIGHT = 720;
    public static final int DEFAULT_WIDTH = 1280;
    public static boolean FORCE_RESOLUTION = false;
    public static float FORCE_RESOLUTION_SCALE = -1.0f;
    public static int FORCE_RESOLUTION_WIDTH = 1920;
    public static int FORCE_RESOLUTION_HEIGHT = 1080;
    public static int FORCE_RESOLUTION_START_SIZE = -1;

    public void setLogPath(String path) {
    }

    public void setThread(Thread t) {
    }

    public long dlopen(String path) {
        return 0L;
    }

    public void onExit(int code) {
    }
}
