package com.brixcore.task;

import androidx.core.app.NotificationCompat;
import com.brixcore.event.EventManager;
import com.brixcore.fakefx.beans.property.ReadOnlyDoubleProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyDoubleWrapper;
import com.brixcore.fakefx.beans.property.ReadOnlyStringProperty;
import com.brixcore.fakefx.beans.property.ReadOnlyStringWrapper;
import com.brixcore.util.InvocationDispatcher;
import com.brixcore.util.Logging;
import com.brixcore.util.ReflectionHelper;
import com.brixcore.util.function.ExceptionalConsumer;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.function.ExceptionalRunnable;
import com.brixcore.util.function.ExceptionalSupplier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes7.dex */
public abstract class Task<T> {
    private Supplier<Boolean> cancelled;
    private Exception exception;
    private final ReadOnlyStringWrapper message;
    private final InvocationDispatcher<String> messageUpdate;
    private Runnable notifyPropertiesChanged;
    private final InvocationDispatcher<Double> progressUpdate;
    Map<String, Object> properties;
    private T result;
    private Consumer<T> resultConsumer;
    private final EventManager<TaskEvent> onDone = new EventManager<>();
    private TaskSignificance significance = TaskSignificance.MAJOR;
    private String stage = null;
    private String inheritedStage = null;
    private TaskState state = TaskState.READY;
    private Executor executor = Schedulers.defaultScheduler();
    private boolean dependentsSucceeded = false;
    private boolean dependenciesSucceeded = false;
    private String name = getClass().getName();
    private long lastTime = Long.MIN_VALUE;
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, NotificationCompat.CATEGORY_PROGRESS, -1.0d);

    public interface FinalizedCallback {
        void execute(Exception exc) throws Exception;
    }

    public interface FinalizedCallbackWithResult<T> {
        void execute(T t, Exception exc) throws Exception;
    }

    public enum TaskState {
        READY,
        RUNNING,
        EXECUTED,
        SUCCEEDED,
        FAILED
    }

    public abstract void execute() throws Exception;

    public Task() {
        Executor executorAndroidUIThread = Schedulers.androidUIThread();
        final ReadOnlyDoubleWrapper readOnlyDoubleWrapper = this.progress;
        Objects.requireNonNull(readOnlyDoubleWrapper);
        this.progressUpdate = InvocationDispatcher.runOn(executorAndroidUIThread, new Consumer() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                readOnlyDoubleWrapper.set(((Double) obj).doubleValue());
            }
        });
        this.message = new ReadOnlyStringWrapper(this, "message", null);
        Executor executorAndroidUIThread2 = Schedulers.androidUIThread();
        final ReadOnlyStringWrapper readOnlyStringWrapper = this.message;
        Objects.requireNonNull(readOnlyStringWrapper);
        this.messageUpdate = InvocationDispatcher.runOn(executorAndroidUIThread2, new Consumer() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                readOnlyStringWrapper.set((String) obj);
            }
        });
    }

    public final TaskSignificance getSignificance() {
        return this.significance;
    }

    public final Task<T> setSignificance(TaskSignificance significance) {
        this.significance = significance;
        return this;
    }

    final void setCancelled(Supplier<Boolean> cancelled) {
        this.cancelled = cancelled;
    }

    protected final boolean isCancelled() {
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
            return true;
        }
        if (this.cancelled != null) {
            return this.cancelled.get().booleanValue();
        }
        return false;
    }

    public String getStage() {
        return this.stage;
    }

    protected final void setStage(String stage) {
        this.stage = stage;
    }

    public String getInheritedStage() {
        return this.inheritedStage;
    }

    void setInheritedStage(String inheritedStage) {
        this.inheritedStage = inheritedStage;
    }

    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new HashMap();
        }
        return this.properties;
    }

    void setNotifyPropertiesChanged(Runnable runnable) {
        this.notifyPropertiesChanged = runnable;
    }

    protected void notifyPropertiesChanged() {
        if (this.notifyPropertiesChanged != null) {
            this.notifyPropertiesChanged.run();
        }
    }

    public final TaskState getState() {
        return this.state;
    }

    final void setState(TaskState state) {
        this.state = state;
    }

    public final Exception getException() {
        return this.exception;
    }

    final void setException(Exception e) {
        this.exception = e;
    }

    public final Executor getExecutor() {
        return this.executor;
    }

    public final Task<T> setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public boolean isDependentsSucceeded() {
        return this.dependentsSucceeded;
    }

    void setDependentsSucceeded() {
        this.dependentsSucceeded = true;
    }

    public boolean isDependenciesSucceeded() {
        return this.dependenciesSucceeded;
    }

    void setDependenciesSucceeded() {
        this.dependenciesSucceeded = true;
    }

    public boolean isRelyingOnDependents() {
        return true;
    }

    public boolean isRelyingOnDependencies() {
        return true;
    }

    public String getName() {
        return this.name;
    }

    public Task<T> setName(String name) {
        this.name = name;
        return this;
    }

    public String toString() {
        if (getClass().getName().equals(getName())) {
            return getName();
        }
        return getClass().getName() + "[" + getName() + "]";
    }

    public T getResult() {
        return this.result;
    }

    protected void setResult(T result) {
        this.result = result;
        if (this.resultConsumer != null) {
            this.resultConsumer.accept(result);
        }
    }

    public Task<T> storeTo(Consumer<T> action) {
        this.resultConsumer = action;
        action.accept(getResult());
        return this;
    }

    public boolean doPreExecute() {
        return false;
    }

    public void preExecute() throws Exception {
    }

    public boolean doPostExecute() {
        return false;
    }

    public void postExecute() throws Exception {
    }

    public Collection<? extends Task<?>> getDependents() {
        return Collections.emptySet();
    }

    public Collection<? extends Task<?>> getDependencies() {
        return Collections.emptySet();
    }

    public EventManager<TaskEvent> onDone() {
        return this.onDone;
    }

    protected long getProgressInterval() {
        return 1000L;
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return this.progress.getReadOnlyProperty();
    }

    protected void updateProgress(long progress, long total) {
        updateProgress((progress * 1.0d) / total);
    }

    protected void updateProgress(double progress) {
        if (progress < 0.0d || progress > 1.0d) {
            throw new IllegalArgumentException("Progress is must between 0 and 1.");
        }
        long now = System.currentTimeMillis();
        if (this.lastTime == Long.MIN_VALUE || now - this.lastTime >= getProgressInterval()) {
            updateProgressImmediately(progress);
            this.lastTime = now;
        }
    }

    protected void updateProgressImmediately(double progress) {
        this.progressUpdate.accept(Double.valueOf(progress));
    }

    public final ReadOnlyStringProperty messageProperty() {
        return this.message.getReadOnlyProperty();
    }

    protected final void updateMessage(String newMessage) {
        this.messageUpdate.accept(newMessage);
    }

    public final T run() throws Exception {
        if (getSignificance().shouldLog()) {
            Logging.LOG.log(Level.FINE, "Executing task: " + getName());
        }
        for (Task<?> task : getDependents()) {
            doSubTask(task);
        }
        execute();
        for (Task<?> task2 : getDependencies()) {
            doSubTask(task2);
        }
        this.onDone.fireEvent(new TaskEvent(this, this, false));
        return getResult();
    }

    private void doSubTask(Task<?> task) throws Exception {
        this.message.bind(task.message);
        this.progress.bind(task.progress);
        task.run();
        this.message.unbind();
        this.progress.unbind();
    }

    public final TaskExecutor executor() {
        return new AsyncTaskExecutor(this);
    }

    public final TaskExecutor executor(boolean start) {
        TaskExecutor executor = new AsyncTaskExecutor(this);
        if (start) {
            executor.start();
        }
        return executor;
    }

    public final TaskExecutor executor(TaskListener taskListener) {
        TaskExecutor executor = new AsyncTaskExecutor(this);
        executor.addTaskListener(taskListener);
        return executor;
    }

    public final void start() {
        executor().start();
    }

    public final boolean test() {
        return executor().test();
    }

    public <U, E extends Exception> Task<U> thenApplyAsync(ExceptionalFunction<T, U, E> fn) {
        return thenApplyAsync(Schedulers.defaultScheduler(), fn);
    }

    public <U, E extends Exception> Task<U> thenApplyAsync(Executor executor, ExceptionalFunction<T, U, E> fn) {
        return thenApplyAsync(getCaller(), executor, fn).setSignificance(TaskSignificance.MODERATE);
    }

    public <U, E extends Exception> Task<U> thenApplyAsync(String name, Executor executor, ExceptionalFunction<T, U, E> fn) {
        return new UniApply(fn).setExecutor(executor).setName(name);
    }

    public <E extends Exception> Task<Void> thenAcceptAsync(ExceptionalConsumer<T, E> action) {
        return thenAcceptAsync(Schedulers.defaultScheduler(), action);
    }

    public <E extends Exception> Task<Void> thenAcceptAsync(Executor executor, ExceptionalConsumer<T, E> action) {
        return thenAcceptAsync(getCaller(), executor, action).setSignificance(TaskSignificance.MODERATE);
    }

    public <E extends Exception> Task<Void> thenAcceptAsync(String name, Executor executor, final ExceptionalConsumer<T, E> action) {
        return thenApplyAsync(name, executor, new ExceptionalFunction() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda7
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return Task.lambda$thenAcceptAsync$0(action, obj);
            }
        });
    }

    static /* synthetic */ Void lambda$thenAcceptAsync$0(ExceptionalConsumer action, Object result) throws Exception {
        action.accept(result);
        return null;
    }

    public <E extends Exception> Task<Void> thenRunAsync(ExceptionalRunnable<E> action) {
        return thenRunAsync(Schedulers.defaultScheduler(), action);
    }

    public <E extends Exception> Task<Void> thenRunAsync(Executor executor, ExceptionalRunnable<E> action) {
        return thenRunAsync(getCaller(), executor, action).setSignificance(TaskSignificance.MODERATE);
    }

    public <E extends Exception> Task<Void> thenRunAsync(String name, Executor executor, final ExceptionalRunnable<E> action) {
        return thenApplyAsync(name, executor, new ExceptionalFunction() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda12
            @Override // com.brixcore.util.function.ExceptionalFunction
            public final Object apply(Object obj) {
                return Task.lambda$thenRunAsync$1(action, obj);
            }
        });
    }

    static /* synthetic */ Void lambda$thenRunAsync$1(ExceptionalRunnable action, Object ignore) throws Exception {
        action.run();
        return null;
    }

    public final <U> Task<U> thenSupplyAsync(final Callable<U> fn) {
        return thenComposeAsync(new ExceptionalSupplier() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda6
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return Task.supplyAsync(fn);
            }
        });
    }

    public final <U> Task<U> thenSupplyAsync(final String name, final Callable<U> fn) {
        return thenComposeAsync(new ExceptionalSupplier() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda5
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return Task.supplyAsync(name, fn);
            }
        });
    }

    static /* synthetic */ Task lambda$thenComposeAsync$4(Task other) throws Exception {
        return other;
    }

    public final <U> Task<U> thenComposeAsync(final Task<U> other) {
        return thenComposeAsync(new ExceptionalSupplier() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda3
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return Task.lambda$thenComposeAsync$4(this.f$0);
            }
        });
    }

    public final <U> Task<U> thenComposeAsync(ExceptionalSupplier<Task<U>, ?> fn) {
        return thenComposeAsync(Schedulers.defaultScheduler(), fn);
    }

    public final <U> Task<U> thenComposeAsync(Executor executor, ExceptionalSupplier<Task<U>, ?> fn) {
        return new UniCompose((Task) this, (ExceptionalSupplier) fn, true).setExecutor(executor);
    }

    public <U, E extends Exception> Task<U> thenComposeAsync(ExceptionalFunction<T, Task<U>, E> fn) {
        return thenComposeAsync(Schedulers.defaultScheduler(), fn);
    }

    public <U, E extends Exception> Task<U> thenComposeAsync(Executor executor, ExceptionalFunction<T, Task<U>, E> fn) {
        return new UniCompose((ExceptionalFunction) fn, true).setExecutor(executor);
    }

    static /* synthetic */ Task lambda$withComposeAsync$5(Task other) throws RuntimeException {
        return other;
    }

    public final <U> Task<U> withComposeAsync(final Task<U> other) {
        return withComposeAsync(new ExceptionalSupplier() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda2
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return Task.lambda$withComposeAsync$5(this.f$0);
            }
        });
    }

    public final <U, E extends Exception> Task<U> withComposeAsync(ExceptionalSupplier<Task<U>, E> fn) {
        return new UniCompose((Task) this, (ExceptionalSupplier) fn, false);
    }

    public <E extends Exception> Task<Void> withRunAsync(ExceptionalRunnable<E> action) {
        return withRunAsync(Schedulers.defaultScheduler(), action);
    }

    public <E extends Exception> Task<Void> withRunAsync(Executor executor, ExceptionalRunnable<E> action) {
        return withRunAsync(getCaller(), executor, action).setSignificance(TaskSignificance.MODERATE);
    }

    public <E extends Exception> Task<Void> withRunAsync(final String name, final Executor executor, final ExceptionalRunnable<E> action) {
        return new UniCompose((Task) this, new ExceptionalSupplier() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda10
            @Override // com.brixcore.util.function.ExceptionalSupplier
            public final Object get() {
                return Task.runAsync(name, executor, action);
            }
        }, false);
    }

    public final Task<Void> whenComplete(FinalizedCallback action) {
        return whenComplete(Schedulers.defaultScheduler(), action);
    }

    public final Task<Void> whenComplete(Executor executor, final FinalizedCallback action) {
        return new Task<Void>() { // from class: com.brixcore.task.Task.1
            {
                setSignificance(TaskSignificance.MODERATE);
            }

            @Override // com.brixcore.task.Task
            public void execute() throws Exception {
                if (isDependentsSucceeded() != (Task.this.getException() == null)) {
                    throw new AssertionError("When whenComplete succeeded, Task.exception must be null.", Task.this.getException());
                }
                action.execute(Task.this.getException());
                if (!isDependentsSucceeded()) {
                    setSignificance(TaskSignificance.MINOR);
                    if (Task.this.getException() == null) {
                        throw new AssertionError("When failed, exception cannot be null");
                    }
                    throw Task.this.getException();
                }
            }

            @Override // com.brixcore.task.Task
            public Collection<Task<?>> getDependents() {
                return Collections.singleton(Task.this);
            }

            @Override // com.brixcore.task.Task
            public boolean isRelyingOnDependents() {
                return false;
            }
        }.setExecutor(executor).setName(getCaller()).setSignificance(TaskSignificance.MODERATE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$whenComplete$7(FinalizedCallbackWithResult action, Exception exception) throws Exception {
        action.execute(getResult(), exception);
    }

    public Task<Void> whenComplete(Executor executor, final FinalizedCallbackWithResult<T> action) {
        return whenComplete(executor, new FinalizedCallback() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda13
            @Override // com.brixcore.task.Task.FinalizedCallback
            public final void execute(Exception exc) throws Exception {
                this.f$0.lambda$whenComplete$7(action, exc);
            }
        });
    }

    public final <E1 extends Exception, E2 extends Exception> Task<Void> whenComplete(Executor executor, final ExceptionalRunnable<E1> success, final ExceptionalConsumer<Exception, E2> failure) {
        return whenComplete(executor, new FinalizedCallback() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda1
            @Override // com.brixcore.task.Task.FinalizedCallback
            public final void execute(Exception exc) throws Exception {
                Task.lambda$whenComplete$8(success, failure, exc);
            }
        });
    }

    static /* synthetic */ void lambda$whenComplete$8(ExceptionalRunnable success, ExceptionalConsumer failure, Exception exception) throws Exception {
        if (exception == null) {
            if (success != null) {
                try {
                    success.run();
                    return;
                } catch (Exception e) {
                    Logging.LOG.log(Level.WARNING, "Failed to execute " + success, (Throwable) e);
                    if (failure != null) {
                        failure.accept(e);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if (failure != null) {
            failure.accept(exception);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$whenComplete$9(ExceptionalConsumer success) throws Exception {
        success.accept(getResult());
    }

    public <E1 extends Exception, E2 extends Exception> Task<Void> whenComplete(Executor executor, final ExceptionalConsumer<T, E1> success, ExceptionalConsumer<Exception, E2> failure) {
        return whenComplete(executor, new ExceptionalRunnable() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda11
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() throws Exception {
                this.f$0.lambda$whenComplete$9(success);
            }
        }, failure);
    }

    public Task<T> withStage(String stage) {
        Task<T>.StageTask task = new StageTask();
        task.setStage(stage);
        return task;
    }

    public Task<T> withStagesHint(List<String> stages) {
        return new StagesHintTask(stages);
    }

    public class StagesHintTask extends Task<T> {
        private final List<String> stages;

        public StagesHintTask(List<String> stages) {
            this.stages = stages;
        }

        @Override // com.brixcore.task.Task
        public Collection<Task<?>> getDependents() {
            return Collections.singleton(Task.this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.brixcore.task.Task
        public void execute() {
            setResult(Task.this.getResult());
        }

        public List<String> getStages() {
            return this.stages;
        }
    }

    public Task<T> withCounter(String countStage) {
        return new CountTask(countStage);
    }

    public static Task<Void> runAsync(ExceptionalRunnable<?> closure) {
        return runAsync(Schedulers.defaultScheduler(), closure);
    }

    public static Task<Void> runAsync(String name, ExceptionalRunnable<?> closure) {
        return runAsync(name, Schedulers.defaultScheduler(), closure);
    }

    public static Task<Void> runAsync(Executor executor, ExceptionalRunnable<?> closure) {
        return runAsync(getCaller(), executor, closure).setSignificance(TaskSignificance.MODERATE);
    }

    public static Task<Void> runAsync(String str, Executor executor, ExceptionalRunnable<?> exceptionalRunnable) {
        return new SimpleTask(exceptionalRunnable.toCallable()).setExecutor(executor).setName(str);
    }

    public static <T> Task<T> composeAsync(ExceptionalSupplier<Task<T>, ?> fn) {
        return composeAsync(getCaller(), fn).setSignificance(TaskSignificance.MODERATE);
    }

    public static <T> Task<T> composeAsync(String name, final ExceptionalSupplier<Task<T>, ?> fn) {
        return new Task<T>() { // from class: com.brixcore.task.Task.2
            Task<T> then;

            @Override // com.brixcore.task.Task
            public void execute() throws Exception {
                this.then = (Task) fn.get();
                if (this.then != null) {
                    this.then.storeTo(new Consumer() { // from class: com.brixcore.task.Task$2$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            setResult(obj);
                        }
                    });
                }
            }

            @Override // com.brixcore.task.Task
            public Collection<Task<?>> getDependencies() {
                return this.then == null ? Collections.emptySet() : Collections.singleton(this.then);
            }
        }.setName(name);
    }

    public static <T> Task<T> composeAsync(Executor executor, ExceptionalSupplier<Task<T>, ?> fn) {
        return composeAsync(fn).setExecutor(executor);
    }

    public static <V> Task<V> supplyAsync(Callable<V> callable) {
        return supplyAsync(getCaller(), callable).setSignificance(TaskSignificance.MODERATE);
    }

    public static <V> Task<V> supplyAsync(Executor executor, Callable<V> callable) {
        return supplyAsync(getCaller(), executor, callable).setSignificance(TaskSignificance.MODERATE);
    }

    public static <V> Task<V> supplyAsync(String name, Callable<V> callable) {
        return supplyAsync(name, Schedulers.defaultScheduler(), callable);
    }

    public static <V> Task<V> supplyAsync(String str, Executor executor, Callable<V> callable) {
        return new SimpleTask(callable).setExecutor(executor).setName(str);
    }

    public static <V> Task<V> completed(V value) {
        return fromCompletableFuture(CompletableFuture.completedFuture(value));
    }

    public static Task<List<Object>> allOf(Task<?>... tasks) {
        return allOf(Arrays.asList(tasks));
    }

    public static Task<List<Object>> allOf(final Collection<Task<?>> tasks) {
        return new Task<List<Object>>() { // from class: com.brixcore.task.Task.3
            {
                setSignificance(TaskSignificance.MINOR);
            }

            @Override // com.brixcore.task.Task
            public void execute() {
                setResult((List) tasks.stream().map(new Function() { // from class: com.brixcore.task.Task$3$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((Task) obj).getResult();
                    }
                }).collect(Collectors.toList()));
            }

            @Override // com.brixcore.task.Task
            public Collection<Task<?>> getDependents() {
                return tasks;
            }
        };
    }

    public static Task<?> runSequentially(Task<?>... tasks) {
        if (tasks.length == 0) {
            return new SimpleTask(new Callable() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return Task.lambda$runSequentially$10();
                }
            });
        }
        Task<?> task = tasks[0];
        for (int i = 1; i < tasks.length; i++) {
            task = task.thenComposeAsync(tasks[i]);
        }
        return task;
    }

    static /* synthetic */ Object lambda$runSequentially$10() throws Exception {
        return null;
    }

    public static <T> Task<T> fromCompletableFuture(final CompletableFuture<T> future) {
        return new CompletableFutureTask<T>() { // from class: com.brixcore.task.Task.4
            @Override // com.brixcore.task.CompletableFutureTask
            public CompletableFuture<T> getFuture(TaskCompletableFuture executor) {
                return future;
            }
        };
    }

    public enum TaskSignificance {
        MAJOR,
        MODERATE,
        MINOR;

        public boolean shouldLog() {
            return this != MINOR;
        }

        public boolean shouldShow() {
            return this == MAJOR;
        }
    }

    private static String getCaller() {
        return ReflectionHelper.getCaller(new Predicate() { // from class: com.brixcore.task.Task$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Task.lambda$getCaller$11((String) obj);
            }
        }).toString();
    }

    static /* synthetic */ boolean lambda$getCaller$11(String packageName) {
        return !"com.brixcore.task".equals(packageName);
    }

    private static final class SimpleTask<T> extends Task<T> {
        private final Callable<T> callable;

        SimpleTask(Callable<T> callable) {
            this.callable = callable;
        }

        @Override // com.brixcore.task.Task
        public void execute() throws Exception {
            setResult(this.callable.call());
        }
    }

    private class UniApply<R> extends Task<R> {
        private final ExceptionalFunction<T, R, ?> callable;

        UniApply(ExceptionalFunction<T, R, ?> callable) {
            this.callable = callable;
        }

        @Override // com.brixcore.task.Task
        public Collection<Task<?>> getDependents() {
            return Collections.singleton(Task.this);
        }

        @Override // com.brixcore.task.Task
        public void execute() throws Exception {
            setResult(this.callable.apply((T) Task.this.getResult()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class UniCompose<U> extends Task<U> {
        private final ExceptionalFunction<T, Task<U>, ?> fn;
        private final boolean relyingOnDependents;
        private Task<U> succ;

        UniCompose(Task task, final ExceptionalSupplier<Task<U>, ?> fn, boolean relyingOnDependents) {
            this(new ExceptionalFunction() { // from class: com.brixcore.task.Task$UniCompose$$ExternalSyntheticLambda1
                @Override // com.brixcore.util.function.ExceptionalFunction
                public final Object apply(Object obj) {
                    return Task.UniCompose.lambda$new$0(fn, obj);
                }
            }, relyingOnDependents);
        }

        static /* synthetic */ Task lambda$new$0(ExceptionalSupplier fn, Object result) throws Exception {
            return (Task) fn.get();
        }

        UniCompose(ExceptionalFunction<T, Task<U>, ?> fn, boolean relyingOnDependents) {
            this.fn = fn;
            this.relyingOnDependents = relyingOnDependents;
            setSignificance(TaskSignificance.MODERATE);
            setName(fn.toString());
        }

        @Override // com.brixcore.task.Task
        public void execute() throws Exception {
            setName(this.fn.toString());
            this.succ = this.fn.apply((T) Task.this.getResult());
            if (this.succ != null) {
                this.succ.storeTo(new Consumer() { // from class: com.brixcore.task.Task$UniCompose$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        this.f$0.setResult(obj);
                    }
                });
            }
        }

        @Override // com.brixcore.task.Task
        public Collection<Task<?>> getDependents() {
            return Collections.singleton(Task.this);
        }

        @Override // com.brixcore.task.Task
        public Collection<Task<?>> getDependencies() {
            return this.succ == null ? Collections.emptySet() : Collections.singleton(this.succ);
        }

        @Override // com.brixcore.task.Task
        public boolean isRelyingOnDependents() {
            return this.relyingOnDependents;
        }
    }

    public class StageTask extends Task<T> {
        public StageTask() {
        }

        @Override // com.brixcore.task.Task
        public Collection<Task<?>> getDependents() {
            return Collections.singleton(Task.this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.brixcore.task.Task
        public void execute() throws Exception {
            setResult(Task.this.getResult());
        }
    }

    public final class CountTask extends Task<T> {
        private final String countStage;

        private CountTask(String countStage) {
            this.countStage = countStage;
            setSignificance(TaskSignificance.MINOR);
        }

        public String getCountStage() {
            return this.countStage;
        }

        @Override // com.brixcore.task.Task
        public Collection<Task<?>> getDependents() {
            return Collections.singleton(Task.this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.brixcore.task.Task
        public void execute() throws Exception {
            setResult(Task.this.getResult());
        }

        @Override // com.brixcore.task.Task
        public boolean doPostExecute() {
            return true;
        }

        @Override // com.brixcore.task.Task
        public void postExecute() throws Exception {
            notifyPropertiesChanged();
        }
    }
}
