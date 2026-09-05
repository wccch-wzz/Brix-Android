package com.brixcore.task;

import java.util.function.Supplier;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes7.dex */
public final /* synthetic */ class AsyncTaskExecutor$$ExternalSyntheticLambda30 implements Supplier {
    public final /* synthetic */ AsyncTaskExecutor f$0;

    @Override // java.util.function.Supplier
    public final Object get() {
        return Boolean.valueOf(this.f$0.isCancelled());
    }
}
