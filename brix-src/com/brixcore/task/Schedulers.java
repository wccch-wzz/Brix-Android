package com.brixcore.task;

import android.os.Handler;
import android.os.Looper;
import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes7.dex */
public final class Schedulers {
    private static volatile ExecutorService IO_EXECUTOR;

    private Schedulers() {
    }

    public static ExecutorService io() {
        if (IO_EXECUTOR == null) {
            synchronized (Schedulers.class) {
                if (IO_EXECUTOR == null) {
                    IO_EXECUTOR = Lang.threadPool("IO", true, 4, 10L, TimeUnit.SECONDS);
                }
            }
        }
        return IO_EXECUTOR;
    }

    public static Executor androidUIThread() {
        final Handler handler = new Handler(Looper.getMainLooper());
        Objects.requireNonNull(handler);
        return new Executor() { // from class: com.brixcore.task.Schedulers$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                handler.post(runnable);
            }
        };
    }

    public static Executor defaultScheduler() {
        return ForkJoinPool.commonPool();
    }

    public static synchronized void shutdown() {
        Logging.LOG.info("Shutting down executor services.");
        if (IO_EXECUTOR != null) {
            IO_EXECUTOR.shutdownNow();
        }
    }
}
