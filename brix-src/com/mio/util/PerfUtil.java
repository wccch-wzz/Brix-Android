package com.mio.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.apache.commons.lang3.StringUtils;

/* JADX INFO: compiled from: PerfUtil.kt */
/* JADX INFO: loaded from: classes10.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u0000 \u000e2\u00020\u0001:\u0002\u000e\u000fB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0016R\u0012\u0010\u0004\u001a\u00060\u0005R\u00020\u0000X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/mio/util/PerfUtil;", "Landroid/util/Printer;", "<init>", "()V", "sampler", "Lcom/mio/util/PerfUtil$StackSampler;", "isStarted", "", "startTime", "", "println", "", "x", "", "Companion", "StackSampler", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class PerfUtil implements Printer {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private boolean isStarted;
    private final StackSampler sampler = new StackSampler(300);
    private long startTime;

    /* JADX INFO: compiled from: PerfUtil.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007J\b\u0010\u0006\u001a\u00020\u0005H\u0007¨\u0006\u0007"}, d2 = {"Lcom/mio/util/PerfUtil$Companion;", "", "<init>", "()V", "install", "", "printStackTrace", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final void install() {
            Looper.getMainLooper().setMessageLogging(new PerfUtil());
        }

        @JvmStatic
        public final void printStackTrace() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String str = "";
            Intrinsics.checkNotNull(stackTrace);
            for (StackTraceElement element : stackTrace) {
                str = str + element;
            }
            Log.e("PerfUtil-printStackTrace", str);
        }
    }

    @JvmStatic
    public static final void install() {
        INSTANCE.install();
    }

    @JvmStatic
    public static final void printStackTrace() {
        INSTANCE.printStackTrace();
    }

    @Override // android.util.Printer
    public void println(String x) {
        if (!this.isStarted) {
            this.isStarted = true;
            this.startTime = System.currentTimeMillis();
            this.sampler.startDump();
        } else {
            this.isStarted = false;
            long endTime = System.currentTimeMillis();
            if (endTime - this.startTime > 300) {
                Log.e("Brix PerfUtil", "block time = " + (endTime - this.startTime));
            }
            this.sampler.stopDump();
        }
    }

    /* JADX INFO: compiled from: PerfUtil.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lcom/mio/util/PerfUtil$StackSampler;", "", "interval", "", "<init>", "(Lcom/mio/util/PerfUtil;J)V", "getInterval", "()J", "handler", "Landroid/os/Handler;", "runnable", "Ljava/lang/Runnable;", "startDump", "", "stopDump", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public final class StackSampler {
        private final Handler handler;
        private final long interval;
        private final Runnable runnable = new Runnable() { // from class: com.mio.util.PerfUtil$StackSampler$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PerfUtil.StackSampler.runnable$lambda$0();
            }
        };

        public StackSampler(long interval) {
            this.interval = interval;
            HandlerThread handlerThread = new HandlerThread("");
            handlerThread.start();
            this.handler = new Handler(handlerThread.getLooper());
        }

        public final long getInterval() {
            return this.interval;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void runnable$lambda$0() {
            StringBuilder sb = new StringBuilder();
            Object[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            Intrinsics.checkNotNullExpressionValue(stackTrace, "getStackTrace(...)");
            Object[] $this$forEach$iv = stackTrace;
            for (Object element$iv : $this$forEach$iv) {
                StackTraceElement it = (StackTraceElement) element$iv;
                sb.append(it.toString());
                sb.append(StringUtils.LF);
            }
            Log.e("Brix PerfUtil", sb.toString());
        }

        public final void startDump() {
            this.handler.postDelayed(this.runnable, this.interval);
        }

        public final void stopDump() {
            this.handler.removeCallbacks(this.runnable);
        }
    }
}
