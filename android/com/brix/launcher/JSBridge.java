package com.brix.launcher;

import android.util.Log;
import android.webkit.JavascriptInterface;

/* compiled from: AndroidBridge.java */
/* loaded from: classes.dex */
class JSBridge {
    JSBridge() {
    }

    @JavascriptInterface
    public void log(String str) {
        Log.d("JSBridge", str);
    }

    @JavascriptInterface
    public void error(String str) {
        Log.e("JSBridge", str);
    }
}
