/**
 * tauri-bridge.js - 跨架构兼容性桥接层
 *
 * Tauri 架构：由 Tauri sidecar 设置 API_BASE
 * Electron 架构：noop（brix:// 协议直接处理，无需侧车地址）
 */
(function () {
  'use strict';

  // Electron 环境：无需操作，API_BASE 已为空字符串，brix:// 协议由主进程处理
  if (typeof window !== 'undefined' && window.electronAPI) {
    // Electron 模式：无 sidecar，不做任何设置
    return;
  }

  // Tauri 模式：sidecar 就绪后更新 API_BASE
  if (typeof window.__TAURI__ !== 'undefined') {
    window.__TAURI__.core.invoke('get_sidecar_base')
      .then(function (base) {
        if (typeof window._updateApiBase === 'function') {
          window._updateApiBase(base);
        }
      })
      .catch(function (err) {
        console.warn('[tauri-bridge] 获取 sidecar 地址失败:', err);
      });
  }
})();
