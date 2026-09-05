package com.bytedance.android.bytehook;

import androidx.core.os.EnvironmentCompat;

/* JADX INFO: loaded from: classes.dex */
public class ByteHook {
    private static final int ERRNO_INIT_EXCEPTION = 101;
    private static final int ERRNO_LOAD_LIBRARY_EXCEPTION = 100;
    private static final int ERRNO_OK = 0;
    private static final int ERRNO_UNINIT = 1;
    private static final boolean defaultDebug = false;
    private static final boolean defaultRecordable = false;
    private static final String libName = "bytehook";
    private static final int recordItemAll = 255;
    private static final int recordItemCallerLibName = 2;
    private static final int recordItemErrno = 64;
    private static final int recordItemLibName = 8;
    private static final int recordItemNewAddr = 32;
    private static final int recordItemOp = 4;
    private static final int recordItemStub = 128;
    private static final int recordItemSymName = 16;
    private static final int recordItemTimestamp = 1;
    private static boolean inited = false;
    private static int initStatus = 1;
    private static long initCostMs = -1;
    private static final ILibLoader defaultLibLoader = null;
    private static final int defaultMode = Mode.AUTOMATIC.getValue();

    public enum RecordItem {
        TIMESTAMP,
        CALLER_LIB_NAME,
        OP,
        LIB_NAME,
        SYM_NAME,
        NEW_ADDR,
        ERRNO,
        STUB
    }

    private static native int nativeAddIgnore(String str);

    private static native String nativeGetArch();

    private static native boolean nativeGetDebug();

    private static native int nativeGetMode();

    private static native boolean nativeGetRecordable();

    private static native String nativeGetRecords(int i);

    private static native String nativeGetVersion();

    private static native int nativeInit(int i, boolean z);

    private static native void nativeSetDebug(boolean z);

    private static native void nativeSetRecordable(boolean z);

    public static String getVersion() {
        return nativeGetVersion();
    }

    public static int init() {
        return init(null);
    }

    public static synchronized int init(Config config) {
        if (inited) {
            return initStatus;
        }
        inited = true;
        long start = System.currentTimeMillis();
        if (config == null) {
            config = new ConfigBuilder().build();
        }
        try {
            if (config.getLibLoader() == null) {
                System.loadLibrary(libName);
            } else {
                config.getLibLoader().loadLibrary(libName);
            }
            try {
                initStatus = nativeInit(config.getMode(), config.getDebug());
            } catch (Throwable th) {
                initStatus = ERRNO_INIT_EXCEPTION;
            }
            if (config.getRecordable()) {
                try {
                    nativeSetRecordable(config.getRecordable());
                } catch (Throwable th2) {
                    initStatus = ERRNO_INIT_EXCEPTION;
                }
            }
            initCostMs = System.currentTimeMillis() - start;
            return initStatus;
        } catch (Throwable th3) {
            initStatus = 100;
            initCostMs = System.currentTimeMillis() - start;
            return initStatus;
        }
    }

    public static int addIgnore(String callerPathName) {
        if (initStatus == 0) {
            return nativeAddIgnore(callerPathName);
        }
        return initStatus;
    }

    public static int getInitErrno() {
        return initStatus;
    }

    public static long getInitCostMs() {
        return initCostMs;
    }

    public static Mode getMode() {
        if (initStatus == 0) {
            return Mode.AUTOMATIC.getValue() == nativeGetMode() ? Mode.AUTOMATIC : Mode.MANUAL;
        }
        return Mode.AUTOMATIC;
    }

    public static boolean getDebug() {
        if (initStatus == 0) {
            return nativeGetDebug();
        }
        return false;
    }

    public static void setDebug(boolean debug) {
        if (initStatus == 0) {
            nativeSetDebug(debug);
        }
    }

    public static boolean getRecordable() {
        if (initStatus == 0) {
            return nativeGetRecordable();
        }
        return false;
    }

    public static void setRecordable(boolean recordable) {
        if (initStatus == 0) {
            nativeSetRecordable(recordable);
        }
    }

    public static String getRecords(RecordItem... recordItems) {
        if (initStatus == 0) {
            int itemFlags = 0;
            for (RecordItem recordItem : recordItems) {
                switch (recordItem) {
                    case TIMESTAMP:
                        itemFlags |= 1;
                        break;
                    case CALLER_LIB_NAME:
                        itemFlags |= 2;
                        break;
                    case OP:
                        itemFlags |= 4;
                        break;
                    case LIB_NAME:
                        itemFlags |= 8;
                        break;
                    case SYM_NAME:
                        itemFlags |= 16;
                        break;
                    case NEW_ADDR:
                        itemFlags |= 32;
                        break;
                    case ERRNO:
                        itemFlags |= 64;
                        break;
                    case STUB:
                        itemFlags |= 128;
                        break;
                }
            }
            if (itemFlags == 0) {
                itemFlags = 255;
            }
            return nativeGetRecords(itemFlags);
        }
        return null;
    }

    public static String getArch() {
        if (initStatus == 0) {
            return nativeGetArch();
        }
        return EnvironmentCompat.MEDIA_UNKNOWN;
    }

    public static class Config {
        private boolean debug;
        private ILibLoader libLoader;
        private int mode;
        private boolean recordable;

        public void setLibLoader(ILibLoader libLoader) {
            this.libLoader = libLoader;
        }

        public ILibLoader getLibLoader() {
            return this.libLoader;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return this.mode;
        }

        public void setDebug(boolean debug) {
            this.debug = debug;
        }

        public boolean getDebug() {
            return this.debug;
        }

        public void setRecordable(boolean recordable) {
            this.recordable = recordable;
        }

        public boolean getRecordable() {
            return this.recordable;
        }
    }

    public static class ConfigBuilder {
        private ILibLoader libLoader = ByteHook.defaultLibLoader;
        private int mode = ByteHook.defaultMode;
        private boolean debug = false;
        private boolean recordable = false;

        public ConfigBuilder setLibLoader(ILibLoader libLoader) {
            this.libLoader = libLoader;
            return this;
        }

        public ConfigBuilder setMode(Mode mode) {
            this.mode = mode.getValue();
            return this;
        }

        public ConfigBuilder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public ConfigBuilder setRecordable(boolean recordable) {
            this.recordable = recordable;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setLibLoader(this.libLoader);
            config.setMode(this.mode);
            config.setDebug(this.debug);
            config.setRecordable(this.recordable);
            return config;
        }
    }

    public enum Mode {
        AUTOMATIC(0),
        MANUAL(1);

        private final int value;

        Mode(int value) {
            this.value = value;
        }

        int getValue() {
            return this.value;
        }
    }
}
