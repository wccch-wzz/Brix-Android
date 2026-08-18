package com.brix.launcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    private static final int FILE_PICKER_REQUEST = 1002;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST = 1003;
    public static AndroidBridge androidBridge;
    public static WebView webView;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private boolean isTablet = false;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @JavascriptInterface
    public String loginMicrosoft() {
        return "{\"action\":\"open_browser\"}";
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.isTablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        enableImmersiveMode();
        getWindow().addFlags(128);
        WebView webView2 = new WebView(this);
        webView = webView2;
        setContentView(webView2);
        androidBridge = new AndroidBridge(this);
        setupWebView();
        requestPermissionsIfNeeded();
        setupActivityResultLaunchers();
        loadApp();
    }

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(5894);
        getWindow().setStatusBarColor(0);
        getWindow().setNavigationBarColor(0);
        getWindow().addFlags(Integer.MIN_VALUE);
    }

    private void requestPermissionsIfNeeded() {
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_IMAGES") != 0) {
                arrayList.add("android.permission.READ_MEDIA_IMAGES");
            }
            if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VIDEO") != 0) {
                arrayList.add("android.permission.READ_MEDIA_VIDEO");
            }
        }
        if (Build.VERSION.SDK_INT <= 32) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
            }
            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
        }
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, "android.permission.MANAGE_EXTERNAL_STORAGE") != 0) {
            arrayList.add("android.permission.MANAGE_EXTERNAL_STORAGE");
        }
        if (arrayList.isEmpty()) {
            return;
        }
        this.permissionLauncher.launch((String[]) arrayList.toArray(new String[0]));
    }

    private void setupActivityResultLaunchers() {
        this.filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.brix.launcher.MainActivity$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m44xbb9c5c64((ActivityResult) obj);
            }
        });
        this.permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.brix.launcher.MainActivity$$ExternalSyntheticLambda2
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m45x43cc9c43((Map) obj);
            }
        });
    }

    /* renamed from: lambda$setupActivityResultLaunchers$0$com-brix-launcher-MainActivity, reason: not valid java name */
    /* synthetic */ void m44xbb9c5c64(ActivityResult activityResult) {
        Uri data;
        String realPathFromURI;
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null || (data = activityResult.getData().getData()) == null || (realPathFromURI = getRealPathFromURI(data)) == null || androidBridge == null) {
            return;
        }
        onFileSelected(realPathFromURI, data.toString());
    }

    /* renamed from: lambda$setupActivityResultLaunchers$1$com-brix-launcher-MainActivity, reason: not valid java name */
    /* synthetic */ void m45x43cc9c43(Map map) {
        boolean z;
        Iterator it = map.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                z = true;
                break;
            } else if (!((Boolean) it.next()).booleanValue()) {
                z = false;
                break;
            }
        }
        if (z) {
            Toast.makeText(this, "权限已授予", 0).show();
        }
    }

    private String getRealPathFromURI(Uri uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        }
        try {
            InputStream inputStreamOpenInputStream = getContentResolver().openInputStream(uri);
            if (inputStreamOpenInputStream == null) {
                return null;
            }
            File file = new File(getCacheDir(), "temp_" + System.currentTimeMillis());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[8192];
            while (true) {
                int i = inputStreamOpenInputStream.read(bArr);
                if (i != -1) {
                    fileOutputStream.write(bArr, 0, i);
                } else {
                    inputStreamOpenInputStream.close();
                    fileOutputStream.close();
                    return file.getAbsolutePath();
                }
            }
        } catch (Exception unused) {
            return null;
        }
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(-1);
        settings.setMixedContentMode(0);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setTextZoom(100);
        webView.addJavascriptInterface(androidBridge, "Android");
        webView.addJavascriptInterface(new JSBridge(), "JSBridge");
        webView.setWebViewClient(new AnonymousClass1());
    }

    /* renamed from: com.brix.launcher.MainActivity$1, reason: invalid class name */
    class AnonymousClass1 extends WebViewClient {
        AnonymousClass1() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(final WebView webView, WebResourceRequest webResourceRequest) {
            String string = webResourceRequest.getUrl().toString();
            Uri uri = Uri.parse(string);
            if ("brix".equals(uri.getScheme()) && "auth".equals(uri.getHost())) {
                final String str = "window.dispatchEvent(new CustomEvent('oauth-callback', {detail: {url: '" + string + "'}}));";
                webView.post(new Runnable() { // from class: com.brix.launcher.MainActivity$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        webView.evaluateJavascript(str, null);
                    }
                });
                return true;
            }
            if ("msauth".equals(uri.getScheme())) {
                final String str2 = "window.dispatchEvent(new CustomEvent('oauth-callback', {detail: {url: '" + string + "'}}));";
                webView.post(new Runnable() { // from class: com.brix.launcher.MainActivity$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        webView.evaluateJavascript(str2, null);
                    }
                });
                return true;
            }
            if (!string.startsWith("http://") && !string.startsWith("https://")) {
                return false;
            }
            try {
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                intent.addFlags(268435456);
                MainActivity.this.startActivity(intent);
            } catch (Exception unused) {
                Toast.makeText(MainActivity.this, "无法打开链接", 0).show();
            }
            return true;
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            webView.evaluateJavascript("(function() {window.isAndroid = true;window.isTablet = " + MainActivity.this.isTablet + ";window.deviceType = '" + (MainActivity.this.isTablet ? "tablet" : "phone") + "';window.androidVersion = " + Build.VERSION.SDK_INT + ";})()", null);
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            super.onReceivedError(webView, webResourceRequest, webResourceError);
            if (!webResourceRequest.isForMainFrame() || webResourceRequest.getUrl().toString().startsWith("file://")) {
                return;
            }
            MainActivity.this.showErrorPage();
        }
    }

    private void loadApp() {
        webView.loadUrl("file:///android_asset/index.html");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorPage() {
        webView.loadDataWithBaseURL(null, "<!DOCTYPE html><html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>body{background:#0f172a;color:#f1f5f9;font-family:system-ui;display:flex;align-items:center;justify-content:center;height:100vh;margin:0;text-align:center;padding:20px;}h1{font-size:24px;margin-bottom:12px;color:#f97316;}p{color:#94a3b8;font-size:14px;line-height:1.6;}button{margin-top:24px;padding:12px 32px;background:#f97316;color:white;border:none;border-radius:12px;font-size:16px;font-weight:600;cursor:pointer;}</style></head><body><div><h1>加载失败</h1><p>无法加载应用资源，请检查文件完整性后重启。</p><button onclick='location.reload()'>重试</button></div></body></html>", "text/html", "UTF-8", null);
    }

    @JavascriptInterface
    public void openInBrowser(String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
            intent.addFlags(268435456);
            startActivity(intent);
        } catch (Exception unused) {
            Toast.makeText(this, "无法打开浏览器", 0).show();
        }
    }

    @JavascriptInterface
    public void openFileChooser(String str) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        if (str == null || str.isEmpty()) {
            str = "*/*";
        }
        intent.setType(str);
        intent.addCategory("android.intent.category.OPENABLE");
        if (Build.VERSION.SDK_INT >= 26) {
            intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"image/*", "video/*", "application/java-archive", "application/zip", "application/x-zip-compressed"});
        }
        try {
            this.filePickerLauncher.launch(intent);
        } catch (Exception unused) {
            Toast.makeText(this, "无法打开文件选择器", 0).show();
        }
    }

    @JavascriptInterface
    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(268435456);
        startActivity(intent);
    }

    @JavascriptInterface
    public String checkPermissions() {
        HashMap map = new HashMap();
        if (Build.VERSION.SDK_INT >= 33) {
            map.put("READ_MEDIA_IMAGES", Boolean.valueOf(ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_IMAGES") == 0));
            map.put("READ_MEDIA_VIDEO", Boolean.valueOf(ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VIDEO") == 0));
        } else {
            map.put("READ_EXTERNAL_STORAGE", Boolean.valueOf(ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0));
            map.put("WRITE_EXTERNAL_STORAGE", Boolean.valueOf(ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0));
        }
        if (Build.VERSION.SDK_INT >= 33) {
            map.put("MANAGE_EXTERNAL_STORAGE", Boolean.valueOf(Environment.isExternalStorageManager()));
        }
        try {
            return new JSONObject(map).toString();
        } catch (Exception unused) {
            return "{}";
        }
    }

    @JavascriptInterface
    public String getStoragePath() {
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = getFilesDir();
        }
        return externalFilesDir.getAbsolutePath();
    }

    @JavascriptInterface
    public String getAndroidVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    @JavascriptInterface
    public String getDeviceInfo() throws JSONException {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("model", Build.MODEL);
            jSONObject.put("brand", Build.BRAND);
            jSONObject.put("manufacturer", Build.MANUFACTURER);
            jSONObject.put("androidVersion", Build.VERSION.RELEASE);
            jSONObject.put("sdkInt", Build.VERSION.SDK_INT);
            jSONObject.put("isTablet", this.isTablet);
            jSONObject.put("screenWidth", getResources().getDisplayMetrics().widthPixels);
            jSONObject.put("screenHeight", getResources().getDisplayMetrics().heightPixels);
            return jSONObject.toString();
        } catch (Exception unused) {
            return "{}";
        }
    }

    public void onOAuthRedirect(String str) {
        if (webView != null) {
            final String str2 = "window.dispatchEvent(new CustomEvent('oauth-callback', {detail: {url: '" + str.replace("'", "\\'") + "'}}));";
            webView.post(new Runnable() { // from class: com.brix.launcher.MainActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.webView.evaluateJavascript(str2, null);
                }
            });
        }
    }

    public void onFileSelected(String str, String str2) {
        if (webView != null) {
            final String str3 = "window.dispatchEvent(new CustomEvent('file-selected', {detail: {path: '" + str.replace("'", "\\'") + "', uri: '" + str2.replace("'", "\\'") + "'}}));";
            webView.post(new Runnable() { // from class: com.brix.launcher.MainActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.webView.evaluateJavascript(str3, null);
                }
            });
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        WebView webView2 = webView;
        if (webView2 != null) {
            webView2.resumeTimers();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        WebView webView2 = webView;
        if (webView2 != null) {
            webView2.pauseTimers();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        WebView webView2 = webView;
        if (webView2 != null) {
            webView2.destroy();
            webView = null;
        }
    }
}
