package com.brixcore.task;

import com.brixcore.event.Event;
import com.brixcore.event.EventBus;
import com.brixcore.util.CacheRepository;
import com.brixcore.util.Lang;
import com.brixcore.util.ToStringBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes7.dex */
public abstract class FetchTask<T> extends Task<T> {
    public static int DEFAULT_CONCURRENCY;
    private static volatile ThreadPoolExecutor DOWNLOAD_EXECUTOR;
    private static int downloadExecutorConcurrency;
    protected boolean caching;
    protected CacheRepository repository = CacheRepository.getInstance();
    protected final int retry;
    protected final List<URL> urls;
    private static final Timer timer = new Timer("DownloadSpeedRecorder", true);
    private static final AtomicInteger downloadSpeed = new AtomicInteger(0);
    public static final EventBus speedEvent = new EventBus();

    protected enum EnumCheckETag {
        CHECK_E_TAG,
        NOT_CHECK_E_TAG,
        CACHED
    }

    protected abstract Context getContext(URLConnection uRLConnection, boolean z) throws IOException;

    protected abstract EnumCheckETag shouldCheckETag();

    protected abstract void useCachedResult(Path path) throws IOException;

    public FetchTask(List<URL> urls, int retry) {
        Objects.requireNonNull(urls);
        this.urls = (List) urls.stream().filter(new Predicate() { // from class: com.brixcore.task.FetchTask$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((URL) obj);
            }
        }).collect(Collectors.toList());
        this.retry = retry;
        if (this.urls.isEmpty()) {
            throw new IllegalArgumentException("At least one URL is required");
        }
        setExecutor(download());
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    public void setCacheRepository(CacheRepository repository) {
        this.repository = repository;
    }

    protected void beforeDownload(URL url) throws IOException {
    }

    /*  JADX ERROR: Type inference failed
        jadx.core.utils.exceptions.JadxOverflowException: Type inference error: updates count limit reached with updateSeq = 8501. Try increasing type updates limit count.
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:79)
        */
    @Override // com.brixcore.task.Task
    public void execute() throws java.lang.Exception {
        /*
            Method dump skipped, instruction units count: 850
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.brixcore.task.FetchTask.execute():void");
    }

    /* JADX INFO: renamed from: com.brixcore.task.FetchTask$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$brixcore$task$FetchTask$EnumCheckETag = new int[EnumCheckETag.values().length];

        static {
            try {
                $SwitchMap$com$brixcore$task$FetchTask$EnumCheckETag[EnumCheckETag.CHECK_E_TAG.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$brixcore$task$FetchTask$EnumCheckETag[EnumCheckETag.NOT_CHECK_E_TAG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    static {
        timer.schedule(new TimerTask() { // from class: com.brixcore.task.FetchTask.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                FetchTask.speedEvent.channel(SpeedEvent.class).fireEvent(new SpeedEvent(FetchTask.speedEvent, FetchTask.downloadSpeed.getAndSet(0)));
            }
        }, 0L, 1000L);
        DEFAULT_CONCURRENCY = Math.min(Runtime.getRuntime().availableProcessors() * 4, 64);
        downloadExecutorConcurrency = DEFAULT_CONCURRENCY;
    }

    private static void updateDownloadSpeed(int speed) {
        downloadSpeed.addAndGet(speed);
    }

    public static class SpeedEvent extends Event {
        private final int speed;

        public SpeedEvent(Object source, int speed) {
            super(source);
            this.speed = speed;
        }

        public int getSpeed() {
            return this.speed;
        }

        @Override // com.brixcore.event.Event
        public String toString() {
            return new ToStringBuilder(this).append("speed", Integer.valueOf(this.speed)).toString();
        }
    }

    protected static abstract class Context implements Closeable {
        private boolean success;

        public abstract void write(byte[] bArr, int i, int i2) throws IOException;

        protected Context() {
        }

        public final void withResult(boolean success) {
            this.success = success;
        }

        protected boolean isSuccess() {
            return this.success;
        }
    }

    protected static final class DownloadState {
        private final int currentPosition;
        private final int endPosition;
        private final boolean finished;
        private final int startPosition;

        public DownloadState(int startPosition, int endPosition, int currentPosition) {
            if (currentPosition < startPosition || currentPosition > endPosition) {
                throw new IllegalArgumentException("Illegal download state: start " + startPosition + ", end " + endPosition + ", cur " + currentPosition);
            }
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.currentPosition = currentPosition;
            this.finished = currentPosition == endPosition;
        }

        public int getStartPosition() {
            return this.startPosition;
        }

        public int getEndPosition() {
            return this.endPosition;
        }

        public int getCurrentPosition() {
            return this.currentPosition;
        }

        public boolean isFinished() {
            return this.finished;
        }
    }

    protected static final class DownloadMission {
        protected DownloadMission() {
        }
    }

    protected static ExecutorService download() {
        if (DOWNLOAD_EXECUTOR == null) {
            synchronized (Schedulers.class) {
                if (DOWNLOAD_EXECUTOR == null) {
                    DOWNLOAD_EXECUTOR = Lang.threadPool("Download", true, downloadExecutorConcurrency, 10L, TimeUnit.SECONDS);
                }
            }
        }
        return DOWNLOAD_EXECUTOR;
    }

    public static void setDownloadExecutorConcurrency(int concurrency) {
        int concurrency2 = Math.max(concurrency, 1);
        synchronized (Schedulers.class) {
            downloadExecutorConcurrency = concurrency2;
            ThreadPoolExecutor downloadExecutor = DOWNLOAD_EXECUTOR;
            if (downloadExecutor != null) {
                if (downloadExecutor.getMaximumPoolSize() <= concurrency2) {
                    downloadExecutor.setMaximumPoolSize(concurrency2);
                    downloadExecutor.setCorePoolSize(concurrency2);
                } else {
                    downloadExecutor.setCorePoolSize(concurrency2);
                    downloadExecutor.setMaximumPoolSize(concurrency2);
                }
            }
        }
    }

    public static int getDownloadExecutorConcurrency() {
        int i;
        synchronized (Schedulers.class) {
            i = downloadExecutorConcurrency;
        }
        return i;
    }
}
