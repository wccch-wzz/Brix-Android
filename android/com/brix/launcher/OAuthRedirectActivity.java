package com.brix.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class OAuthRedirectActivity extends AppCompatActivity {
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            if (MainActivity.webView != null) {
                final String str = "window.dispatchEvent(new CustomEvent('oauth-callback', {detail: {url: '" + data.toString() + "'}}));";
                MainActivity.webView.post(new Runnable() { // from class: com.brix.launcher.OAuthRedirectActivity$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.webView.evaluateJavascript(str, null);
                    }
                });
            }
        }
        finish();
    }
}
