# Brix

> 💻 **PC 版（Windows）配套仓库：[wswangzai/Brix-PC](https://github.com/wswangzai/Brix-PC)**
> 本仓库为 **Brix 手机版（主项目）**；PC 版与关联项目见上方链接。
pc版https://github.com/zhizhu0002/Brix-PC
作者并非本人，如有侵权提issue删除。
一个基于 WebView 的 Minecraft（我的世界）启动器 / 模组管理器，适用于 Android。

## 简介

Brix 是一个 Android 平台的 Minecraft 启动器，采用 **WebView 壳 + H5 前端** 的混合架构。原生层仅负责 WebView 容器与系统能力桥接，业务逻辑全部由 H5 前端（HTML / CSS / JavaScript）实现。

## 目录结构

```
.
├── AndroidManifest.xml        # 应用清单
├── android/                   # 原生 Java 层（WebView 壳 + 桥接）
│   └── com/brix/launcher/
│       ├── MainActivity.java          # 主 Activity（WebView 容器）
│       ├── AndroidBridge.java         # 原生能力桥接（window.Android）
│       ├── JSBridge.java              # 日志桥接（window.JSBridge）
│       ├── BrixApplication.java       # Application 入口
│       ├── OAuthRedirectActivity.java # OAuth 回调（msauth://、brix://auth）
│       ├── InstanceProvider.java      # 实例提供者
│       ├── BuildConfig.java           # 构建配置
│       └── R.java                     # 资源引用
└── web/                       # H5 前端（业务核心）
    ├── index.html             # 主入口
    ├── file-browser.html      # 文件浏览器
    ├── editor.html            # 编辑器
    ├── activate-tool.html     # 激活工具
    ├── css/                   # 样式（14 个）
    ├── js/                    # 业务逻辑（app/ 下 30+ 模块）
    ├── img/                   # 图片资源
    ├── plugins/               # 插件（modrinth、mod-dev-tools）
    └── fonts/                 # 字体
```

## 技术栈

- **原生层**：Kotlin / Java（Android 7.0+，targetSdk 33）
- **前端**：原生 HTML / CSS / JavaScript + Three.js（3D 皮肤预览）
- **桥接**：`@JavascriptInterface`（`window.Android` / `window.JSBridge`）

## 主要功能

- Minecraft 游戏启动与版本管理
- 模组浏览、安装、整合包导入（Modrinth 平台集成）
- 微软账号 OAuth 登录 + 离线账号
- Java 运行时检测与管理
- 云同步、收藏、公告、个性化、壁纸引擎
- 3D 皮肤预览、局域网联机

## 说明

- 应用入口：`MainActivity` → `loadUrl("file:///android_asset/index.html")`
- 深链协议：`brix://`（主入口）、`brix://auth` 与 `msauth://`（OAuth 回调）
