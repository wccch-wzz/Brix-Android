package com.brix.launcher;

import android.app.Application;
import android.content.Context;

/* loaded from: classes.dex */
public class BrixApplication extends Application {

    public static class InstanceProvider {
        public static Context context;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        InstanceProvider.context = this;
    }
}
