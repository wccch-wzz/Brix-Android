package com.brixcore;

import android.app.Activity;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;

/* JADX INFO: compiled from: ActivityProviderHolder.kt */
/* JADX INFO: loaded from: classes14.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0007R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\f"}, d2 = {"Lcom/brixcore/ActivityProviderHolder;", "", "<init>", "()V", "provider", "Lcom/brixcore/ActivityProvider;", "getProvider", "()Lcom/brixcore/ActivityProvider;", "setProvider", "(Lcom/brixcore/ActivityProvider;)V", "getCurrentActivity", "Landroid/app/Activity;", "BrixCore_debug"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ActivityProviderHolder {
    public static final ActivityProviderHolder INSTANCE = new ActivityProviderHolder();
    private static volatile ActivityProvider provider;

    private ActivityProviderHolder() {
    }

    public final ActivityProvider getProvider() {
        return provider;
    }

    public final void setProvider(ActivityProvider activityProvider) {
        provider = activityProvider;
    }

    @JvmStatic
    public static final Activity getCurrentActivity() {
        ActivityProvider activityProvider = provider;
        if (activityProvider != null) {
            return activityProvider.getCurrentActivity();
        }
        return null;
    }
}
