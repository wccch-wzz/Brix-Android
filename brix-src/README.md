# brix-src — 反编译恢复的源码树

本目录由 `brix-launcher.apk` 静态反编译(jadx 1.5.6)恢复而来,供参考与版本对照。

- `com/brix/brixlauncher/` — 应用自研层:授权/卡密、好友、私聊、界面(13 个类)
- `com/brixcore/` — 启动器内核(基于开源项目 HMCL 改造,原名 org.jackhuang.hmcl)
  - 内含 `fakefx/`(JavaFX 兼容层)、`bridge/`、`auth/`、`download/`、`game/`、`launch/`、`mod/`、`task/`、`util/` 等
- `com/bytedance/` 等 — 第三方库(ByteHook 等)

> 说明:
> 1. 反编译产物仅供学习/对比,不可直接作为可编译工程(缺少资源编译产物与 Gradle 工程骨架)。
> 2. `com/brixcore` 源自 GPLv3 开源项目 HMCL(HMCL-dev/HMCL),本目录随本仓库以相应许可证义务公开。
> 3. `AndroidManifest.xml` 为 APK 原样解码版本(versionCode 1 / versionName 1.0,minSdk 26,targetSdk 34)。

对应 APK:仓库 Release `beta3` 资产 `brix-launcher.apk`。
