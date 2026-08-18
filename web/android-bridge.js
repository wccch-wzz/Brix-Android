/**
 * android-bridge.js - Android WebView 桥接层
 * 为移动端提供原生功能接口
 */
(function() {
  'use strict';

  if (typeof window._androidBridgeInited === 'undefined') {
    window._androidBridgeInited = true;
    window.Android = window.Android || {};

    // ===== 全局环境检测 =====
    window.Android.isAndroid = true;
    window.Android.isTablet = window.isTablet || false;
    window.Android.androidVersion = window.androidVersion || 0;
    window.Android.deviceType = window.deviceType || 'phone';

    // ===== 存储权限相关 =====
    window.Android.checkPermissions = function() {
      if (window.Android && Android.checkPermissions) {
        try { return JSON.parse(Android.checkPermissions()); } catch(e) { return {}; }
      }
      return { READ_EXTERNAL_STORAGE: true, WRITE_EXTERNAL_STORAGE: true };
    };

    window.Android.requestPermissions = function() {
      // 自动请求，调用方无需处理
      if (window.Android && Android.openSettings) {
        // 先检查，无权限则提示用户
        var perms = window.Android.checkPermissions();
        var hasAll = true;
        for (var k in perms) {
          if (!perms[k]) { hasAll = false; break; }
        }
        if (!hasAll) {
          window.Android.openSettings();
        }
      }
    };

    // ===== 文件操作 =====
    window.Android.readJsonFile = function(path) {
      if (window.Android && Android.readJsonFile) {
        try { return JSON.parse(Android.readJsonFile(path)); } catch(e) { return null; }
      }
      return null;
    };

    window.Android.writeFile = function(path, content) {
      if (window.Android && Android.writeFile) {
        Android.writeFile(path, JSON.stringify(content, null, 2));
      }
    };

    // ===== 目录获取 =====
    window.Android.getGameDir = function() {
      if (window.Android && Android.getGameDir) {
        return Android.getGameDir();
      }
      return null;
    };

    window.Android.getVersionDir = function(versionId) {
      if (window.Android && Android.getVersionDir) {
        return Android.getVersionDir(versionId);
      }
      return null;
    };

    window.Android.getExternalStoragePath = function() {
      if (window.Android && Android.getStoragePath) {
        return Android.getStoragePath();
      }
      return null;
    };

    window.Android.listStorageDirs = function() {
      if (window.Android && Android.listStorageDirectories) {
        try { return JSON.parse(Android.listStorageDirectories()); } catch(e) { return []; }
      }
      return [];
    };

    window.Android.getDesktopPath = function() {
      if (window.Android && Android.getDesktopPath) {
        return Android.getDesktopPath();
      }
      return null;
    };

    window.Android.getPicturesPath = function() {
      if (window.Android && Android.getPicturesPath) {
        return Android.getPicturesPath();
      }
      return null;
    };

    window.Android.getDownloadsPath = function() {
      if (window.Android && Android.getDownloadsPath) {
        return Android.getDownloadsPath();
      }
      return null;
    };

    window.Android.listFiles = function(path) {
      if (window.Android && Android.listFiles) {
        try { return JSON.parse(Android.listFiles(path)); } catch(e) { return []; }
      }
      return [];
    };

    // ===== 浏览器和文件选择 =====
    window.Android.openInBrowser = function(url) {
      if (window.Android && Android.openInBrowser) {
        Android.openInBrowser(url);
      } else {
        window.open(url, '_blank');
      }
    };

    window.Android.openFileChooser = function(acceptType) {
      if (window.Android && Android.openFileChooser) {
        Android.openFileChooser(acceptType);
      }
    };

    window.Android.openSettings = function() {
      if (window.Android && Android.openSettings) {
        Android.openSettings();
      }
    };

    // ===== 账户管理 =====
    window.Android.getAccount = function() {
      if (window.Android && Android.getAccount) {
        try { return JSON.parse(Android.getAccount()); } catch(e) { return { loggedIn: false }; }
      }
      return { loggedIn: false };
    };

    window.Android.saveAccount = function(username, accessToken) {
      if (window.Android && Android.saveAccount) {
        Android.saveAccount(username, accessToken);
      }
    };

    window.Android.saveOfflineAccount = function(username) {
      if (window.Android && Android.saveOfflineAccount) {
        Android.saveOfflineAccount(username);
      }
    };

    window.Android.getAccountType = function() {
      if (window.Android && Android.getAccountType) {
        return Android.getAccountType();
      }
      return 'none';
    };

    window.Android.clearAccount = function() {
      if (window.Android && Android.clearAccount) {
        Android.clearAccount();
      }
    };

    // ===== Java 检测 =====
    window.Android.checkJava = function() {
      if (window.Android && Android.checkJava) {
        try { return JSON.parse(Android.checkJava()); } catch(e) { return { available: false }; }
      }
      return { available: false };
    };

    window.Android.findJavaOnSystem = function() {
      if (window.Android && Android.findJavaOnSystem) {
        try { return JSON.parse(Android.findJavaOnSystem()); } catch(e) { return { javaPaths: [] }; }
      }
      return { javaPaths: [] };
    };

    // ===== 游戏启动 =====
    window.Android.launchGame = function(versionId, javaPath, args) {
      if (window.Android && Android.launchGame) {
        return Android.launchGame(versionId, javaPath || '', args || '');
      }
      return '{"success":false,"error":"Android bridge not available"}';
    };

    // ===== Toast =====
    window.Android.showToast = function(message) {
      if (window.Android && Android.showToast) {
        Android.showToast(message);
      }
    };

    window.Android.showToastLong = function(message) {
      if (window.Android && Android.showToastLong) {
        Android.showToastLong(message);
      }
    };

    // ===== 下载 =====
    window.Android.downloadFile = function(url, destPath, filename) {
      if (window.Android && Android.downloadFile) {
        return Android.downloadFile(url, destPath, filename, 'mobile');
      }
      return '{"success":false}';
    };

    // ===== 设备信息 =====
    window.Android.getDeviceInfo = function() {
      if (window.Android && Android.getDeviceInfo) {
        try { return JSON.parse(Android.getDeviceInfo()); } catch(e) { return {}; }
      }
      return {};
    };

    // ===== 回调事件 =====
    window.Android._onFileSelected = function(path, uri) {
      if (window._onFileSelectedHandler) {
        window._onFileSelectedHandler(path, uri);
      }
    };

    window.Android._onOAuthRedirect = function(url) {
      if (window._onOAuthRedirectHandler) {
        window._onOAuthRedirectHandler(url);
      }
    };

    // ===== 原生事件监听（由 MainActivity 注入） =====
    document.addEventListener('file-selected', function(e) {
      if (window._onFileSelectedHandler) {
        window._onFileSelectedHandler(e.detail.path, e.detail.uri);
      }
    });

    document.addEventListener('oauth-callback', function(e) {
      if (window._onOAuthRedirectHandler) {
        window._onOAuthRedirectHandler(e.detail.url);
      }
    });
  }
})();
