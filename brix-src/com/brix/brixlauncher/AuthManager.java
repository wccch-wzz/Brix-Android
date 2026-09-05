package com.brix.brixlauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.brixcore.util.io.HttpRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes18.dex */
public class AuthManager {
    private static final String AUTH_API_URL = "https://brix.zdyfkj.work/php/index.php";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_USER_INFO = "user_info";
    private static final int POLL_INTERVAL_MS = 2000;
    private static final int POLL_TIMEOUT_MS = 120000;
    private static final String PREFS_NAME = "brix_auth";
    private static final String PRODUCT_NAME = "BrixLauncher";
    private static final long REMEMBER_DAYS = 30;
    private static final long REMEMBER_MS = 2592000000L;
    private static final String TAG = "AuthManager";
    private volatile String accessToken;
    private final Context context;
    private volatile String currentCode;
    private final SharedPreferences prefs;
    private volatile JSONObject userInfo;

    public AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, 0);
        loadSavedSession();
    }

    public boolean isLoggedIn() {
        if (this.accessToken == null || this.accessToken.isEmpty()) {
            return false;
        }
        if (!isRememberMe()) {
            return true;
        }
        long loginTime = this.prefs.getLong(KEY_LOGIN_TIME, 0L);
        return System.currentTimeMillis() - loginTime < REMEMBER_MS;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public JSONObject getUserInfo() {
        return this.userInfo;
    }

    public boolean isRememberMe() {
        return this.prefs.getBoolean(KEY_REMEMBER_ME, false);
    }

    public void setRememberMe(boolean enabled) {
        this.prefs.edit().putBoolean(KEY_REMEMBER_ME, enabled).apply();
    }

    private void loadSavedSession() {
        String token = this.prefs.getString(KEY_ACCESS_TOKEN, null);
        if (token == null || token.isEmpty()) {
            return;
        }
        this.accessToken = token;
        String userInfoJson = this.prefs.getString(KEY_USER_INFO, null);
        if (userInfoJson != null && !userInfoJson.isEmpty()) {
            try {
                this.userInfo = new JSONObject(userInfoJson);
            } catch (Exception e) {
                Log.w(TAG, "解析用户信息失败: " + e.getMessage());
                clearSession();
                return;
            }
        }
        Log.d(TAG, "已加载缓存会话, uid=" + this.accessToken);
    }

    private void saveSession(String token, JSONObject info) {
        this.accessToken = token;
        this.userInfo = info;
        this.prefs.edit().putString(KEY_ACCESS_TOKEN, token).putString(KEY_USER_INFO, info != null ? info.toString() : "").putLong(KEY_LOGIN_TIME, System.currentTimeMillis()).apply();
        Log.d(TAG, "会话已保存, uid=" + token);
    }

    public void clearSession() {
        this.accessToken = null;
        this.userInfo = null;
        this.prefs.edit().clear().apply();
        Log.d(TAG, "会话已清除");
    }

    public String createAuthCode() throws AuthException {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("product", PRODUCT_NAME);
            Log.d(TAG, "auth_create URL: https://brix.zdyfkj.work/php/index.php?action=auth_create");
            Log.d(TAG, "auth_create 参数: product=BrixLauncher");
            String response = HttpRequest.POST("https://brix.zdyfkj.work/php/index.php?action=auth_create").form(params).retry(3).getString();
            Log.d(TAG, "auth_create 响应: " + response);
            JSONObject json = new JSONObject(response);
            int code = json.optInt("code");
            String msg = json.optString(NotificationCompat.CATEGORY_MESSAGE, "");
            if (code != 200) {
                throw new AuthException(msg.isEmpty() ? "创建授权码失败" : msg);
            }
            this.currentCode = json.optString("auth_code");
            Log.d(TAG, "auth_create 成功, code=" + this.currentCode);
            return this.currentCode;
        } catch (IOException e) {
            throw new AuthException("网络错误: " + e.getMessage(), e);
        } catch (Exception e2) {
            throw new AuthException("创建授权码异常: " + e2.getMessage(), e2);
        }
    }

    public void pollUntilAuthorized() throws AuthException {
        long j;
        long startMs = System.currentTimeMillis();
        while (System.currentTimeMillis() - startMs < 120000) {
            try {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("code", this.currentCode);
                    String response = HttpRequest.POST("https://brix.zdyfkj.work/php/index.php?action=auth_poll").form(params).retry(3).getString();
                    Log.d(TAG, "poll 响应: " + response);
                    JSONObject json = new JSONObject(response);
                    int code = json.optInt("code");
                    int status = json.optInt(NotificationCompat.CATEGORY_STATUS);
                    j = 2000;
                    try {
                        String msg = json.optString(NotificationCompat.CATEGORY_MESSAGE, "");
                        if (code == 200 && status == 3) {
                            this.userInfo = json.getJSONObject("user");
                            this.accessToken = this.userInfo.optString("uid");
                            this.currentCode = null;
                            saveSession(this.accessToken, this.userInfo);
                            Log.d(TAG, "auth_poll 成功, username=" + this.userInfo.optString("username"));
                            return;
                        }
                        if (status == 1) {
                            Thread.sleep(2000L);
                        } else {
                            if (code == 200) {
                                throw new AuthException(msg.isEmpty() ? "授权失败" : msg);
                            }
                            if (!msg.isEmpty()) {
                                throw new AuthException(msg);
                            }
                        }
                    } catch (Exception e) {
                        e = e;
                        Log.w(TAG, "轮询网络错误: " + e.getMessage());
                        try {
                            Thread.sleep(j);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new AuthException("轮询被中断", ie);
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                    j = 2000;
                }
            } catch (AuthException e3) {
                throw e3;
            } catch (InterruptedException e4) {
                Thread.currentThread().interrupt();
                throw new AuthException("轮询被中断", e4);
            }
        }
        throw new AuthException("授权超时，请重新点击按钮");
    }

    public String getAuthUrl() {
        if (this.currentCode == null) {
            return null;
        }
        return "https://brix.zdyfkj.work/php/auth.html?code=" + this.currentCode;
    }

    public void logout() {
        clearSession();
        Log.d(TAG, "用户已退出登录");
    }

    public String getUsername() {
        return this.userInfo != null ? this.userInfo.optString("username", "未知用户") : "未知用户";
    }

    public String getUserAvatar() {
        if (this.userInfo == null) {
            return null;
        }
        String avatar = this.userInfo.optString("avatar", null);
        if (avatar == null || avatar.isEmpty()) {
            avatar = this.userInfo.optString("head", null);
        }
        if (avatar == null || avatar.isEmpty()) {
            return this.userInfo.optString("avatar_url", null);
        }
        return avatar;
    }

    public String getUserId() {
        return this.userInfo != null ? this.userInfo.optString("uid", "") : "";
    }

    private String getLocalIpAddress() {
        NetworkInfo ni;
        NetworkInterface niObj;
        int ipInt;
        try {
            WifiManager wm = (WifiManager) this.context.getApplicationContext().getSystemService("wifi");
            if (wm != null && (ipInt = wm.getConnectionInfo().getIpAddress()) != 0) {
                String ip = (ipInt & 255) + "." + ((ipInt >> 8) & 255) + "." + ((ipInt >> 16) & 255) + "." + ((ipInt >> 24) & 255);
                Log.d(TAG, "WiFi IP=" + ip);
                return ip;
            }
        } catch (Exception e) {
            Log.d(TAG, "WiFi IP失败: " + e.getMessage());
        }
        try {
            ConnectivityManager cm = (ConnectivityManager) this.context.getApplicationContext().getSystemService("connectivity");
            if (cm != null && (ni = cm.getActiveNetworkInfo()) != null && ni.isConnected() && (niObj = NetworkInterface.getByName(ni.getTypeName())) != null) {
                Enumeration<InetAddress> en = niObj.getInetAddresses();
                while (en.hasMoreElements()) {
                    InetAddress addr = en.nextElement();
                    String s = addr.getHostAddress();
                    if (s != null && !s.equals("127.0.0.1") && !s.contains(":")) {
                        Log.d(TAG, "NetInterface IP=" + s);
                        return s;
                    }
                }
            }
        } catch (Exception e2) {
            Log.d(TAG, "NetInterface IP失败: " + e2.getMessage());
        }
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr2 = addrs.nextElement();
                    String s2 = addr2.getHostAddress();
                    if (s2 != null && !s2.equals("127.0.0.1") && !s2.contains(":") && !s2.startsWith("169.254.")) {
                        Log.d(TAG, "allIF IP=" + s2);
                        return s2;
                    }
                }
            }
        } catch (Exception e3) {
            Log.d(TAG, "遍历网络接口失败: " + e3.getMessage());
        }
        Log.w(TAG, "所有IP获取失败，回退到127.0.0.1");
        return "127.0.0.1";
    }

    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }

        public AuthException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
