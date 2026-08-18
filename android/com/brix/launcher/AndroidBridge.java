package com.brix.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class AndroidBridge {
    private final Context context;
    private final SharedPreferences prefs;

    @JavascriptInterface
    public void postResult(String str) {
    }

    public AndroidBridge(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("brix_accounts", 0);
    }

    @JavascriptInterface
    public String launchGame(String str, String str2, String str3) {
        try {
            if (!new File(getGameDir(), "versions/" + str).exists()) {
                return error("版本 " + str + " 未安装");
            }
            Toast.makeText(this.context, "正在启动 Minecraft " + str + "...", 0).show();
            return success("游戏启动中...");
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @JavascriptInterface
    public String getGameDir() {
        File externalFilesDir = this.context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = this.context.getFilesDir();
        }
        File file = new File(externalFilesDir, "Minecraft");
        file.mkdirs();
        return file.getAbsolutePath();
    }

    @JavascriptInterface
    public String getVersionDir(String str) {
        File file = new File(getGameDir(), "versions/" + str);
        file.mkdirs();
        return file.getAbsolutePath();
    }

    @JavascriptInterface
    public String checkJava() throws InterruptedException {
        try {
            Runtime.getRuntime().exec("java -version").waitFor();
            return "{\"available\":true,\"version\":\"java_17\"}";
        } catch (Exception e) {
            return "{\"available\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}";
        }
    }

    @JavascriptInterface
    public String findJavaOnSystem() {
        File[] fileArrListFiles;
        try {
            String[] strArr = {"/usr/lib/jvm", "/usr/java", "/opt/java", Environment.getExternalStorageDirectory() + "/Android/data/com.brix.launcher/files/Minecraft/Java"};
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < 4; i++) {
                File file = new File(strArr[i]);
                if (file.exists() && file.isDirectory() && (fileArrListFiles = file.listFiles()) != null) {
                    for (File file2 : fileArrListFiles) {
                        if (file2.isDirectory() && file2.getName().startsWith("jdk")) {
                            arrayList.add(file2.getAbsolutePath());
                        }
                    }
                }
            }
            return new JSONObject().put("javaPaths", arrayList).toString();
        } catch (Exception unused) {
            return "{\"javaPaths\":[]}";
        }
    }

    @JavascriptInterface
    public String getAccount() {
        String string = this.prefs.getString("access_token", null);
        String string2 = this.prefs.getString("username", null);
        return (string == null || string2 == null) ? "{\"loggedIn\":false}" : "{\"loggedIn\":true,\"username\":\"" + escapeJson(string2) + "\",\"accessToken\":\"" + escapeJson(string) + "\"}";
    }

    @JavascriptInterface
    public void saveAccount(String str, String str2) {
        this.prefs.edit().putString("username", str).putString("access_token", str2).putString("account_type", "microsoft").apply();
    }

    @JavascriptInterface
    public void saveOfflineAccount(String str) {
        this.prefs.edit().putString("username", str).putString("account_type", "offline").apply();
    }

    @JavascriptInterface
    public void clearAccount() {
        this.prefs.edit().clear().apply();
    }

    @JavascriptInterface
    public String getAccountType() {
        return this.prefs.getString("account_type", "none");
    }

    @JavascriptInterface
    public String readJsonFile(String str) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                return error("文件不存在: " + str);
            }
            String str2 = new String(Files.readAllBytes(file.toPath()));
            new JSONObject(str2);
            return str2;
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @JavascriptInterface
    public String writeFile(String str, String str2) throws IOException {
        try {
            File file = new File(str);
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            Files.write(file.toPath(), str2.getBytes("UTF-8"), new OpenOption[0]);
            return success("文件已保存");
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @JavascriptInterface
    public String downloadFile(String str, String str2, String str3, String str4) throws IOException {
        try {
            File file = new File(str2);
            if (!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(file, str3);
            URLConnection uRLConnectionOpenConnection = new URL(str).openConnection();
            uRLConnectionOpenConnection.setRequestProperty("User-Agent", "BrixLauncher/1.0");
            uRLConnectionOpenConnection.connect();
            uRLConnectionOpenConnection.getContentLength();
            InputStream inputStream = uRLConnectionOpenConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            byte[] bArr = new byte[8192];
            long j = 0;
            while (true) {
                int i = inputStream.read(bArr);
                if (i != -1) {
                    fileOutputStream.write(bArr, 0, i);
                    j += i;
                } else {
                    fileOutputStream.close();
                    inputStream.close();
                    return "{\"success\":true,\"size\":" + j + ",\"path\":\"" + escapeJson(file2.getAbsolutePath()) + "\"}";
                }
            }
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @JavascriptInterface
    public long getFileSize(String str) {
        try {
            return new File(str).length();
        } catch (Exception unused) {
            return 0L;
        }
    }

    @JavascriptInterface
    public String getFileHash(String str, String str2) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                return "";
            }
            byte[] bArrDigest = MessageDigest.getInstance(str2.toLowerCase()).digest(Files.readAllBytes(file.toPath()));
            StringBuilder sb = new StringBuilder();
            for (byte b : bArrDigest) {
                sb.append(String.format("%02x", Byte.valueOf(b)));
            }
            return sb.toString();
        } catch (Exception unused) {
            return "";
        }
    }

    @JavascriptInterface
    public String listFiles(String str) {
        File[] fileArrListFiles;
        try {
            File file = new File(str);
            if (!file.exists() || !file.isDirectory() || (fileArrListFiles = file.listFiles()) == null) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < fileArrListFiles.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("{\"name\":\"").append(escapeJson(fileArrListFiles[i].getName())).append("\",\"size\":").append(fileArrListFiles[i].length()).append(",\"isDir\":").append(fileArrListFiles[i].isDirectory()).append("}");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception unused) {
        }
        return "[]";
    }

    @JavascriptInterface
    public String listStorageDirectories() {
        try {
            File[] externalFilesDirs = this.context.getExternalFilesDirs(null);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < externalFilesDirs.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("{\"path\":\"").append(escapeJson(externalFilesDirs[i].getAbsolutePath())).append("\"}");
            }
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (externalStorageDirectory != null) {
                sb.append(",{\"path\":\"").append(escapeJson(externalStorageDirectory.getAbsolutePath())).append("\",\"isExternal\":true}");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception unused) {
            return "[]";
        }
    }

    @JavascriptInterface
    public String getDesktopPath() {
        File file = new File(Environment.getExternalStorageDirectory(), "Desktop");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    @JavascriptInterface
    public String getPicturesPath() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (externalStoragePublicDirectory == null) {
            externalStoragePublicDirectory = new File(this.context.getExternalFilesDir(null), "Pictures");
        }
        externalStoragePublicDirectory.mkdirs();
        return externalStoragePublicDirectory.getAbsolutePath();
    }

    @JavascriptInterface
    public String getDownloadsPath() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (externalStoragePublicDirectory == null) {
            externalStoragePublicDirectory = new File(this.context.getExternalFilesDir(null), "Downloads");
        }
        externalStoragePublicDirectory.mkdirs();
        return externalStoragePublicDirectory.getAbsolutePath();
    }

    @JavascriptInterface
    public String getGameDataPath() {
        return getGameDir();
    }

    @JavascriptInterface
    public void showToast(String str) {
        Toast.makeText(this.context, str, 0).show();
    }

    @JavascriptInterface
    public void showToastLong(String str) {
        Toast.makeText(this.context, str, 1).show();
    }

    @JavascriptInterface
    public String copyAssetToExternal(String str, String str2) throws IOException {
        try {
            File file = new File(str2);
            file.getParentFile().mkdirs();
            InputStream inputStreamOpen = this.context.getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[8192];
            while (true) {
                int i = inputStreamOpen.read(bArr);
                if (i != -1) {
                    fileOutputStream.write(bArr, 0, i);
                } else {
                    inputStreamOpen.close();
                    fileOutputStream.close();
                    return success("复制成功");
                }
            }
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    private String success(String str) {
        return "{\"success\":true,\"message\":\"" + escapeJson(str) + "\"}";
    }

    private String error(String str) {
        return "{\"success\":false,\"error\":\"" + escapeJson(str) + "\"}";
    }

    private String escapeJson(String str) {
        return str == null ? "" : str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
