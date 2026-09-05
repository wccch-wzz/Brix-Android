package com.brix;

import android.app.Activity;
import android.app.Application;
import com.brixcore.ActivityProviderHolder;

/* JADX INFO: loaded from: classes13.dex */
public class BrixApplication extends Application {
    public static Activity getCurrentActivity() {
        return ActivityProviderHolder.getCurrentActivity();
    }
}
