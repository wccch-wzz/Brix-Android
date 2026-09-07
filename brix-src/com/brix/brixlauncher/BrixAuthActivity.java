package com.brix.brixlauncher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.brixcore.utils.BrixPath;

/* JADX INFO: loaded from: classes19.dex */
public class BrixAuthActivity extends AppCompatActivity {
    private static final String KEY_AUTH_CODE = "pending_auth_code";
    private static final String PREFS_NAME = "auth_state";
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private static final String TAG = "BrixAuthActivity";
    private AuthManager authManager;
    private Button btnOpenBrowser;
    private String currentAuthCode;
    private volatile boolean pollingActive;
    private Thread pollingThread;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (BrixPath.CONTEXT == null) {
                BrixPath.loadPaths(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "初始化失败");
        }
        setContentView(R.layout.activity_auth);
        this.authManager = new AuthManager(this);
        this.currentAuthCode = getSharedPreferences(PREFS_NAME, 0).getString(KEY_AUTH_CODE, null);
        initViews();
        if (this.authManager.isLoggedIn()) {
            Log.d(TAG, "已登录，返回主界面");
            finishWithSuccess();
        } else {
            checkLocationPermission();
        }
    }

    private void initViews() {
        this.btnOpenBrowser = (Button) findViewById(R.id.btn_open_browser);
        this.btnOpenBrowser.setOnClickListener(new View.OnClickListener() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$initViews$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initViews$0(View v) {
        startAuthFlow();
    }

    private void startAuthFlow() {
        if (this.btnOpenBrowser == null) {
            return;
        }
        this.btnOpenBrowser.setEnabled(false);
        this.btnOpenBrowser.setText("正在创建授权码...");
        new Thread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startAuthFlow$4();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startAuthFlow$4() {
        try {
            this.currentAuthCode = this.authManager.createAuthCode();
            getSharedPreferences(PREFS_NAME, 0).edit().putString(KEY_AUTH_CODE, this.currentAuthCode).apply();
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startAuthFlow$1();
                }
            });
        } catch (AuthManager.AuthException e) {
            Log.e(TAG, "授权流程异常");
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startAuthFlow$2();
                }
            });
        } catch (Exception e2) {
            Log.e(TAG, "未知异常");
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startAuthFlow$3();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startAuthFlow$2() {
        if (this.btnOpenBrowser != null) {
            this.btnOpenBrowser.setEnabled(true);
            this.btnOpenBrowser.setText("打开浏览器授权");
        }
        Toast.makeText(this, "授权服务暂时不可用，请重试", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startAuthFlow$3() {
        if (this.btnOpenBrowser != null) {
            this.btnOpenBrowser.setEnabled(true);
            this.btnOpenBrowser.setText("打开浏览器授权");
        }
        Toast.makeText(this, "授权失败，请重试", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: openAuthUrl, reason: merged with bridge method [inline-methods] */
    public void lambda$startAuthFlow$1() {
        String url = this.authManager.getAuthUrl();
        if (url != null) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        } else {
            Toast.makeText(this, "授权页面地址无效", 0).show();
        }
    }

    private void finishWithSuccess() {
        this.pollingActive = false;
        if (this.pollingThread != null) {
            this.pollingThread.interrupt();
            this.pollingThread = null;
        }
        getSharedPreferences(PREFS_NAME, 0).edit().remove(KEY_AUTH_CODE).apply();
        Intent intent = new Intent(this, (Class<?>) BrixLauncher.class);
        intent.setFlags(268468224);
        startActivity(intent);
        finish();
    }

    private void startPolling() {
        if (this.pollingActive) {
            return;
        }
        this.pollingActive = true;
        this.pollingThread = new Thread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startPolling$8();
            }
        }, "auth-poll");
        this.pollingThread.setDaemon(true);
        this.pollingThread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startPolling$8() {
        try {
            this.authManager.pollUntilAuthorized();
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startPolling$5();
                }
            });
        } catch (AuthManager.AuthException e) {
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startPolling$6();
                }
            });
        } catch (Exception e2) {
            runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$startPolling$7();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startPolling$5() {
        if (this.pollingActive) {
            finishWithSuccess();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startPolling$6() {
        Toast.makeText(this, "授权超时，请重新点击按钮", 0).show();
        if (this.btnOpenBrowser != null) {
            this.btnOpenBrowser.setEnabled(true);
            this.btnOpenBrowser.setText("打开浏览器授权");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startPolling$7() {
        Toast.makeText(this, "授权检查失败，请重试", 0).show();
        if (this.btnOpenBrowser != null) {
            this.btnOpenBrowser.setEnabled(true);
            this.btnOpenBrowser.setText("打开浏览器授权");
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.currentAuthCode != null && !this.authManager.isLoggedIn()) {
            startPolling();
        }
        checkAuthResult();
    }

    private void checkAuthResult() {
        try {
            if (!this.authManager.isLoggedIn()) {
                final String error = getSharedPreferences(PREFS_NAME, 0).getString("auth_error", null);
                if (error != null && !error.isEmpty()) {
                    runOnUiThread(new Runnable() { // from class: com.brix.brixlauncher.BrixAuthActivity$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$checkAuthResult$9(error);
                        }
                    });
                    getSharedPreferences(PREFS_NAME, 0).edit().remove("auth_error").apply();
                    return;
                }
                return;
            }
            Log.d(TAG, "已登录，进入主界面");
            finishWithSuccess();
        } catch (Exception e) {
            Log.e(TAG, "检查授权结果异常");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkAuthResult$9(String error) {
        if (this.btnOpenBrowser != null) {
            this.btnOpenBrowser.setEnabled(true);
            this.btnOpenBrowser.setText("打开浏览器授权");
        }
        Toast.makeText(this, error, 1).show();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1001);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                Toast.makeText(this, "定位权限已授予", 0).show();
            } else {
                Toast.makeText(this, "定位权限已拒绝，IP可能不准确", 1).show();
            }
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
    }
}
