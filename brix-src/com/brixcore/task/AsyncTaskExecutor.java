package com.brixcore.task;

import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.function.ExceptionalRunnable;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes7.dex */
public final class AsyncTaskExecutor extends TaskExecutor {
    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
    private CompletableFuture<Boolean> future;

    public AsyncTaskExecutor(Task<?> task) {
        super(task);
    }

    @Override // com.brixcore.task.TaskExecutor
    public TaskExecutor start() {
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda25
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onStart();
            }
        });
        this.future = executeTasks(null, Collections.singleton(this.firstTask)).thenApplyAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda26
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$start$1((Exception) obj);
            }
        }).exceptionally((Function<Throwable, ? extends U>) new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda27
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return AsyncTaskExecutor.lambda$start$2((Throwable) obj);
            }
        });
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$start$1(Exception exception) {
        final boolean success = exception == null;
        if (!success) {
            Logging.LOG.log(Level.WARNING, "An exception occurred in task execution", (Throwable) exception);
            Throwable resolvedException = CompletableFutureTask.resolveException(exception);
            if ((resolvedException instanceof RuntimeException) && !(resolvedException instanceof CancellationException) && !(resolvedException instanceof JsonParseException) && !(resolvedException instanceof RejectedExecutionException) && uncaughtExceptionHandler != null) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), resolvedException);
            }
        }
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda34
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$start$0(success, (TaskListener) obj);
            }
        });
        return Boolean.valueOf(success);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$start$0(boolean success, TaskListener it) {
        it.onStop(success, this);
    }

    static /* synthetic */ Boolean lambda$start$2(Throwable e) {
        Lang.handleUncaughtException(CompletableFutureTask.resolveException(e));
        return false;
    }

    @Override // com.brixcore.task.TaskExecutor
    public boolean test() {
        start();
        try {
            return this.future.get().booleanValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (CancellationException e2) {
            Logging.LOG.log(Level.INFO, "Task " + this.firstTask + " has been cancelled.", (Throwable) e2);
            return false;
        } catch (ExecutionException e3) {
            return false;
        }
    }

    @Override // com.brixcore.task.TaskExecutor
    public synchronized void cancel() {
        if (this.future == null) {
            throw new IllegalStateException("Cannot cancel a not started TaskExecutor");
        }
        this.cancelled.set(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CompletableFuture<?> executeTasksExceptionally(final Task<?> parentTask, final Collection<? extends Task<?>> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(null).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda36
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeTasksExceptionally$6(tasks, parentTask, obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeTasksExceptionally$6(Collection tasks, final Task parentTask, Object unused) {
        this.totTask.addAndGet(tasks.size());
        if (isCancelled()) {
            Iterator it = tasks.iterator();
            while (it.hasNext()) {
                Task<?> task = (Task) it.next();
                task.setException(new CancellationException());
            }
            return CompletableFuture.runAsync(new Runnable() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.checkCancellation();
                }
            });
        }
        return CompletableFuture.allOf((CompletableFuture[]) tasks.stream().map(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeTasksExceptionally$4(parentTask, (Task) obj);
            }
        }).toArray(new IntFunction() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda22
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return AsyncTaskExecutor.lambda$executeTasksExceptionally$5(i);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$executeTasksExceptionally$4(final Task parentTask, final Task task) {
        return CompletableFuture.completedFuture(null).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda24
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeTasksExceptionally$3(parentTask, task, obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeTasksExceptionally$3(Task parentTask, Task task, Object unused2) {
        return executeTask(parentTask, task);
    }

    static /* synthetic */ CompletableFuture[] lambda$executeTasksExceptionally$5(int x$0) {
        return new CompletableFuture[x$0];
    }

    private CompletableFuture<Exception> executeTasks(Task<?> parentTask, Collection<? extends Task<?>> tasks) {
        return executeTasksExceptionally(parentTask, tasks).thenApplyAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda32
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return AsyncTaskExecutor.lambda$executeTasks$7(obj);
            }
        }).exceptionally((Function<Throwable, ? extends U>) new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda33
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return AsyncTaskExecutor.lambda$executeTasks$8((Throwable) obj);
            }
        });
    }

    static /* synthetic */ Exception lambda$executeTasks$7(Object unused) {
        return null;
    }

    static /* synthetic */ Exception lambda$executeTasks$8(Throwable throwable) {
        Throwable resolved = CompletableFutureTask.resolveException(throwable);
        if (resolved instanceof Exception) {
            return (Exception) resolved;
        }
        throw new CompletionException(throwable);
    }

    private <T> CompletableFuture<T> executeCompletableFutureTask(final Task<?> parentTask, final CompletableFutureTask<T> task) {
        return CompletableFuture.completedFuture(null).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda20
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeCompletableFutureTask$10(task, parentTask, obj);
            }
        }).thenApplyAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda21
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeCompletableFutureTask$12(task, obj);
            }
        }).exceptionally((Function) new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda23
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeCompletableFutureTask$15(task, (Throwable) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeCompletableFutureTask$10(final CompletableFutureTask task, Task parentTask, Object unused) {
        checkCancellation();
        task.setCancelled(new AsyncTaskExecutor$$ExternalSyntheticLambda30(this));
        task.setState(Task.TaskState.READY);
        if (parentTask != null && task.getStage() == null) {
            task.setStage(parentTask.getStage());
        }
        if (task.getSignificance().shouldLog()) {
            Logging.LOG.log(Level.FINE, "Executing task: " + task.getName());
        }
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda31
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onReady(task);
            }
        });
        return task.getFuture(new TaskCompletableFuture() { // from class: com.brixcore.task.AsyncTaskExecutor.1
            @Override // com.brixcore.task.TaskCompletableFuture
            public <T2> CompletableFuture<T2> one(Task<T2> subtask) {
                return AsyncTaskExecutor.this.executeTask(task, subtask);
            }

            @Override // com.brixcore.task.TaskCompletableFuture
            public CompletableFuture<?> all(Collection<Task<?>> tasks) {
                return AsyncTaskExecutor.this.executeTasksExceptionally(task, tasks);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$executeCompletableFutureTask$12(final CompletableFutureTask task, Object result) {
        checkCancellation();
        if (task.getSignificance().shouldLog()) {
            Logging.LOG.log(Level.FINER, "Task finished: " + task.getName());
        }
        task.setResult(result);
        task.onDone().fireEvent(new TaskEvent(this, task, false));
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda29
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onFinished(task);
            }
        });
        task.setState(Task.TaskState.SUCCEEDED);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$executeCompletableFutureTask$15(final CompletableFutureTask task, Throwable throwable) {
        Throwable resolved = CompletableFutureTask.resolveException(throwable);
        if (resolved instanceof Exception) {
            final Exception e = (Exception) resolved;
            if ((e instanceof InterruptedException) || (e instanceof CancellationException)) {
                task.setException(null);
                if (task.getSignificance().shouldLog()) {
                    Logging.LOG.log(Level.FINE, "Task aborted: " + task.getName());
                }
                task.onDone().fireEvent(new TaskEvent(this, task, true));
                this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda10
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((TaskListener) obj).onFailed(task, e);
                    }
                });
            } else {
                task.setException(e);
                this.exception = e;
                if (task.getSignificance().shouldLog()) {
                    Logging.LOG.log(Level.FINE, "Task failed: " + task.getName(), (Throwable) e);
                }
                task.onDone().fireEvent(new TaskEvent(this, task, true));
                this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda12
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((TaskListener) obj).onFailed(task, e);
                    }
                });
            }
            task.setState(Task.TaskState.FAILED);
        }
        throw new CompletionException(resolved);
    }

    private <T> CompletableFuture<T> executeNormalTask(final Task<?> parentTask, final Task<T> task) {
        return CompletableFuture.completedFuture(null).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeNormalTask$19(task, parentTask, obj);
            }
        }).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda14
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeNormalTask$20(task, (Void) obj);
            }
        }).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda15
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeNormalTask$24(task, (Exception) obj);
            }
        }).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda16
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeNormalTask$25(task, (Void) obj);
            }
        }).thenComposeAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda17
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return AsyncTaskExecutor.lambda$executeNormalTask$27(task, (Exception) obj);
            }
        }).thenApplyAsync(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda18
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeNormalTask$29(task, (Exception) obj);
            }
        }).exceptionally((Function) new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda19
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$executeNormalTask$31(task, (Throwable) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeNormalTask$19(final Task task, Task parentTask, Object unused) {
        checkCancellation();
        task.setCancelled(new AsyncTaskExecutor$$ExternalSyntheticLambda30(this));
        task.setState(Task.TaskState.READY);
        if (task.getStage() != null) {
            task.setInheritedStage(task.getStage());
        } else if (parentTask != null) {
            task.setInheritedStage(parentTask.getInheritedStage());
        }
        task.setNotifyPropertiesChanged(new Runnable() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$executeNormalTask$17(task);
            }
        });
        if (task.getSignificance().shouldLog()) {
            Logging.LOG.log(Level.FINE, "Executing task: " + task.getName());
        }
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onReady(task);
            }
        });
        if (task.doPreExecute()) {
            Objects.requireNonNull(task);
            return CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda3
                @Override // com.brixcore.util.function.ExceptionalRunnable
                public final void run() throws Exception {
                    task.preExecute();
                }
            }), task.getExecutor());
        }
        return CompletableFuture.completedFuture(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$executeNormalTask$17(final Task task) {
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda28
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onPropertiesUpdate(task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeNormalTask$20(Task task, Void unused) {
        return executeTasks(task, task.getDependents());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeNormalTask$24(final Task task, Exception dependentsException) {
        boolean isDependentsSucceeded = dependentsException == null;
        if (isDependentsSucceeded) {
            task.setDependentsSucceeded();
        } else {
            task.setException(dependentsException);
            if (task.isRelyingOnDependents()) {
                Lang.rethrow(dependentsException);
            }
        }
        return CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda8
            @Override // com.brixcore.util.function.ExceptionalRunnable
            public final void run() throws Exception {
                this.f$0.lambda$executeNormalTask$22(task);
            }
        }), task.getExecutor()).whenComplete(new BiConsumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda9
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AsyncTaskExecutor.lambda$executeNormalTask$23(task, (Void) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$executeNormalTask$22(final Task task) throws Exception {
        task.setState(Task.TaskState.RUNNING);
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda35
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onRunning(task);
            }
        });
        task.execute();
    }

    static /* synthetic */ void lambda$executeNormalTask$23(Task task, Void unused, Throwable throwable) {
        task.setState(Task.TaskState.EXECUTED);
        Lang.rethrow(throwable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletionStage lambda$executeNormalTask$25(Task task, Void unused) {
        return executeTasks(task, task.getDependencies());
    }

    static /* synthetic */ CompletionStage lambda$executeNormalTask$27(final Task task, final Exception dependenciesException) {
        boolean isDependenciesSucceeded = dependenciesException == null;
        if (isDependenciesSucceeded) {
            task.setDependenciesSucceeded();
        }
        if (task.doPostExecute()) {
            Objects.requireNonNull(task);
            return CompletableFuture.runAsync(Lang.wrap((ExceptionalRunnable<?>) new ExceptionalRunnable() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda6
                @Override // com.brixcore.util.function.ExceptionalRunnable
                public final void run() throws Exception {
                    task.postExecute();
                }
            }), task.getExecutor()).thenApply(new Function() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda7
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return AsyncTaskExecutor.lambda$executeNormalTask$26(dependenciesException, (Void) obj);
                }
            });
        }
        return CompletableFuture.completedFuture(dependenciesException);
    }

    static /* synthetic */ Exception lambda$executeNormalTask$26(Exception dependenciesException, Void unused) {
        return dependenciesException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$executeNormalTask$29(final Task task, Exception dependenciesException) {
        boolean isDependenciesSucceeded = dependenciesException == null;
        if (!isDependenciesSucceeded) {
            Logging.LOG.severe("Subtasks failed for " + task.getName());
            task.setException(dependenciesException);
            if (task.isRelyingOnDependencies()) {
                Lang.rethrow(dependenciesException);
            }
        }
        checkCancellation();
        if (task.getSignificance().shouldLog()) {
            Logging.LOG.log(Level.FINER, "Task finished: " + task.getName());
        }
        task.onDone().fireEvent(new TaskEvent(this, task, false));
        this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((TaskListener) obj).onFinished(task);
            }
        });
        task.setState(Task.TaskState.SUCCEEDED);
        return task.getResult();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$executeNormalTask$31(final Task task, Throwable throwable) {
        Throwable resolved = CompletableFutureTask.resolveException(throwable);
        if (resolved instanceof Exception) {
            final Exception e = convertInterruptedException((Exception) resolved);
            task.setException(e);
            this.exception = e;
            if (e instanceof CancellationException) {
                if (task.getSignificance().shouldLog()) {
                    Logging.LOG.log(Level.FINE, "Task aborted: " + task.getName());
                }
            } else if (task.getSignificance().shouldLog()) {
                Logging.LOG.log(Level.FINE, "Task failed: " + task.getName(), (Throwable) e);
            }
            task.onDone().fireEvent(new TaskEvent(this, task, true));
            this.taskListeners.forEach(new Consumer() { // from class: com.brixcore.task.AsyncTaskExecutor$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((TaskListener) obj).onFailed(task, e);
                }
            });
            task.setState(Task.TaskState.FAILED);
        }
        throw new CompletionException(resolved);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> CompletableFuture<T> executeTask(Task<?> parentTask, Task<T> task) {
        if (task instanceof CompletableFutureTask) {
            return executeCompletableFutureTask(parentTask, (CompletableFutureTask) task);
        }
        return executeNormalTask(parentTask, task);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkCancellation() {
        if (isCancelled()) {
            throw new CancellationException("Cancelled by user");
        }
    }

    private static Exception convertInterruptedException(Exception e) {
        if (e instanceof InterruptedException) {
            return new CancellationException(e.getMessage());
        }
        return e;
    }

    public static void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler2) {
        uncaughtExceptionHandler = uncaughtExceptionHandler2;
    }
}
