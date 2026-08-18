/**
 * mobile.js - Brix Launcher 移动端适配脚本
 * 下载源：国内 BMCLAPI / 国外 Modrinth
 * 支持手机和平板，流畅动画
 */
(function() {
  'use strict';

  var mobileState = {
    currentPage: 'home',
    isMobile: false,
    initialized: false,
    javaDownloadSessionId: null,
    javaPollTimer: null,
    installSessionId: null,
    installPollTimer: null,
    canvas: null,
    ctx: null,
    animFrame: null
  };

  function isMobileDevice() {
    return window.innerWidth <= 1024 || /Android|iPhone|iPad|iPod|Mobile/i.test(navigator.userAgent);
  }

  function $(s) { return document.querySelector(s); }
  function $$(s) { return document.querySelectorAll(s); }

  // ========== 绘制 MG 风格壁纸 ==========
  function drawMinecraftWallpaper(theme) {
    var canvas = document.getElementById('wallpaper-canvas');
    if (!canvas) return;
    var ctx = canvas.getContext('2d');
    var w = canvas.width = window.innerWidth;
    var h = canvas.height = window.innerHeight;

    var colors;
    if (theme === 'nether') {
      colors = ['#1a0a0a','#3d1010','#6b1a1a','#8b2020','#a03030'];
    } else if (theme === 'end') {
      colors = ['#0a0a1a','#1a1a3d','#2a2a6b','#3d3d8b','#4d4da0'];
    } else if (theme === 'wild') {
      colors = ['#1a1a0a','#3d3d10','#6b6b1a','#8b8b20','#a0a030'];
    } else {
      colors = ['#0a1a0a','#103d10','#1a6b1a','#208b20','#30a030'];
    }

    // 天空渐变
    var grad = ctx.createLinearGradient(0, 0, 0, h);
    grad.addColorStop(0, colors[0]);
    grad.addColorStop(0.5, colors[1]);
    grad.addColorStop(1, colors[2]);
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, w, h);

    // 像素风格地形
    var pixelSize = 16;
    for (var y = h * 0.5; y < h; y += pixelSize) {
      var groundColor = colors[Math.floor((y - h*0.5) / h * colors.length)];
      ctx.fillStyle = groundColor;
      ctx.fillRect(0, y, w, pixelSize);
    }

    // 简单像素山
    for (var i = 0; i < 8; i++) {
      var sx = (w / 8) * i + Math.random() * 60;
      var sh = 40 + Math.random() * 80;
      ctx.fillStyle = colors[3];
      ctx.beginPath();
      ctx.moveTo(sx, h * 0.5);
      ctx.lineTo(sx + 30, h * 0.5 - sh);
      ctx.lineTo(sx + 60, h * 0.5);
      ctx.fill();
    }

    // 星星
    for (var j = 0; j < 50; j++) {
      var sx = Math.random() * w;
      var sy = Math.random() * h * 0.4;
      ctx.fillStyle = 'rgba(255,255,255,' + (0.3 + Math.random() * 0.7) + ')';
      ctx.fillRect(sx, sy, 2, 2);
    }
  }

  function startMgAnimation(theme) {
    var lastTime = 0;
    function animate(time) {
      var dt = time - lastTime;
      lastTime = time;
      // 简单缓慢移动效果
      drawMinecraftWallpaper(theme);
      mobileState.animFrame = requestAnimationFrame(animate);
    }
    if (mobileState.animFrame) cancelAnimationFrame(mobileState.animFrame);
    mobileState.animFrame = requestAnimationFrame(animate);
  }

  function stopMgAnimation() {
    if (mobileState.animFrame) {
      cancelAnimationFrame(mobileState.animFrame);
      mobileState.animFrame = null;
    }
  }  // ========== 初始化 ==========
  function initMobile() {
    if (!isMobileDevice()) return;
    if (mobileState.initialized) return;
    mobileState.isMobile = true;
    mobileState.initialized = true;

    var mobileCss = document.createElement('link');
    mobileCss.rel = 'stylesheet';
    mobileCss.href = 'css/mobile.css';
    document.head.appendChild(mobileCss);

    var mobileApp = document.createElement('div');
    mobileApp.id = 'mobile-app';
    mobileApp.innerHTML = buildMobileHTML();
    document.body.appendChild(mobileApp);

    hidePCElements();
    bindMobileEvents();

    setTimeout(function() {
      updateMobileUI();
      initMgWallpaper();
    }, 100);
  }

  function initMgWallpaper() {
    var bg = document.getElementById('wallpaper-bg');
    if (!bg) return;
    try {
      var canvas = document.getElementById('wallpaper-canvas');
      if (canvas) {
        canvas.style.display = 'block';
        canvas.style.zIndex = '0';
        drawMinecraftWallpaper('overworld');
        if (window.state && window.state.settings && window.state.settings.wallpaper === 'mgRender') {
          startMgAnimation('overworld');
        }
      }
    } catch(e) {
      console.warn('[Mobile] MG wallpaper init failed:', e);
      if (bg) bg.style.background = 'var(--bg-primary,#0f172a)';
    }
  }

  function hidePCElements() {
    var selectors = ['.sidebar', '.title-bar', '.launch-bar', '#wallpaper-bg',
      '#wallpaper-video-container', '#wallpaper-canvas', '#wallpaper-canvas-gl', '#wallpaper-overlay'];
    selectors.forEach(function(sel) {
      var els = document.querySelectorAll(sel);
      for (var i = 0; i < els.length; i++) {
        if (sel !== '#wallpaper-bg' && sel !== '#wallpaper-canvas') {
          els[i].style.display = 'none';
          els[i].classList.add('pc-hidden');
        }
      }
    });
  }

  function bindMobileEvents() {
    var resizeTimer;
    window.addEventListener('resize', function() {
      clearTimeout(resizeTimer);
      resizeTimer = setTimeout(function() {
        if (window.innerWidth > 1024) {
          $$('.pc-hidden').forEach(function(el) { el.style.display = ''; el.classList.remove('pc-hidden'); });
          var ma = document.getElementById('mobile-app');
          if (ma) ma.remove();
          mobileState.isMobile = false;
        }
      }, 250);
    });
    document.addEventListener('keydown', function(e) {
      if (e.key === 'Escape') {
        $$('.mobile-modal.active').forEach(function(m) { m.classList.remove('active'); });
      }
    });
    // 阻止多点触控缩放
    document.addEventListener('touchstart', function(e) {
      if (e.touches.length > 1) {
        e.preventDefault();
      }
    }, { passive: false });
    document.addEventListener('touchmove', function(e) {
      // 允许内容区域和输入框正常处理
      if (e.target.closest('.mobile-content') ||
          e.target.closest('input') ||
          e.target.closest('textarea') ||
          e.target.closest('select') ||
          e.target.closest('button') ||
          e.target.closest('.mobile-modal')) return;
      // 只阻止多点触控
      if (e.touches.length > 1) {
        e.preventDefault();
      }
    }, { passive: false });
    // 添加触摸反馈效果
    document.addEventListener('touchstart', function(e) {
      var target = e.target;
      if (target.closest('.mobile-btn') || target.closest('.mobile-list-item') ||
          target.closest('.mobile-bento-card') || target.closest('.nav-item')) {
        target.classList.add('touch-active');
        setTimeout(function() { target.classList.remove('touch-active'); }, 150);
      }
    }, { passive: true });
  }
  // ========== 构建移动端 HTML ==========
  function buildMobileHTML() {
    var isT = window.isTablet ? ' tablet' : '';
    return [
      '<div class="mobile-header" id="mobileHeader">',
        '<div class="logo"><img src="img/logo.png" alt="B" onerror="this.style.display=\\'none\\'"><span>Brix</span></div>',
        '<div class="user-badge" id="mobileUserBadge" style="display:none">',
          '<div class="user-avatar" id="mobileUserAvatar">P</div>',
          '<span id="mobileUserName">Player</span>',
        '</div>',
      '</div>',

      '<div class="mobile-content" id="mobileContent">',

        // 首页
        '<div class="mobile-page active" id="mobile-page-home">',
          '<div class="mobile-launch-card">',
            '<div class="game-icon">⛏️</div>',
            '<h1>Minecraft</h1>',
            '<p class="version-info" id="mobileCurrentVersion">选择一个版本开始游戏</p>',
            '<button class="mobile-launch-btn" onclick="window.mobileLaunchGame()"><svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>启动游戏</button>',
          '</div>',

          '<div class="mobile-section-title"><span>快捷功能</span></div>',
          '<div class="mobile-bento-grid">',
            '<div class="mobile-bento-card" onclick="window.mobileNavigate(\\'versions\\')"><div class="card-icon orange">📦</div><h3>版本管理</h3><p>游戏版本</p></div>',
            '<div class="mobile-bento-card" onclick="window.mobileNavigate(\\'mods\\')"><div class="card-icon blue">🔧</div><h3>模组管理</h3><p>安装模组</p></div>',
            '<div class="mobile-bento-card" onclick="window.mobileNavigate(\\'resources\\')"><div class="card-icon green">🔍</div><h3>资源中心</h3><p>模组/光影</p></div>',
            '<div class="mobile-bento-card" onclick="window.mobileNavigate(\\'java\\')"><div class="card-icon purple">☕</div><h3>Java 管理</h3><p>运行环境</p></div>',
            '<div class="mobile-bento-card wide" onclick="window.mobileNavigate(\\'settings\\')"><div class="card-icon red">⚙️</div><h3>设置</h3><p>主题、下载源、启动参数等</p></div>',
          '</div>',

          '<div class="mobile-section-title"><span>最近版本</span><span class="see-all" onclick="window.mobileNavigate(\\'versions\\')">查看全部</span></div>',
          '<div class="mobile-list" id="mobileRecentVersions">',
            '<div class="mobile-empty"><div class="empty-icon">📭</div><h3>暂无版本</h3><p>请先添加游戏版本</p></div>',
          '</div>',
        '</div>',

        // 版本页
        '<div class="mobile-page" id="mobile-page-versions">',
          '<div class="mobile-section-title"><span>版本管理</span><button class="mobile-btn mobile-btn-primary" onclick="window.mobileShowInstallModal()">+ 添加</button></div>',
          '<div class="mobile-search"><span class="search-icon">🔍</span><input type="text" placeholder="搜索版本..." id="mobileVersionSearch" oninput="window.mobileFilterVersions()"></div>',
          '<div class="mobile-list" id="mobileVersionList">',
            '<div class="mobile-empty"><div class="empty-icon">📦</div><h3>暂无版本</h3><p>点击「添加」安装游戏</p></div>',
          '</div>',
        '</div>',

        // 模组页
        '<div class="mobile-page" id="mobile-page-mods">',
          '<div class="mobile-section-title"><span>模组管理</span></div>',
          '<div class="mobile-search"><span class="search-icon">🔍</span><input type="text" placeholder="搜索模组..." id="mobileModSearch" oninput="window.mobileFilterMods()"></div>',
          '<div class="mobile-list" id="mobileModList">',
            '<div class="mobile-empty"><div class="empty-icon">🔧</div><h3>暂无模组</h3><p>从资源中心安装</p></div>',
          '</div>',
        '</div>',

        // 资源中心
        '<div class="mobile-page" id="mobile-page-resources">',
          '<div class="mobile-section-title"><span>资源中心</span></div>',
          '<div class="mobile-search"><span class="search-icon">🔍</span><input type="text" placeholder="搜索模组、整合包、光影..." id="mobileResourceSearch" oninput="window.mobileSearchResources()"></div>',
          '<div class="mobile-list" id="mobileResourceList">',
            '<div class="mobile-empty"><div class="empty-icon">🔍</div><h3>搜索资源</h3><p>从 Modrinth 搜索</p></div>',
          '</div>',
        '</div>',

        // Java 管理
        '<div class="mobile-page" id="mobile-page-java">',
          '<div class="mobile-section-title"><span>Java 管理</span></div>',
          '<div class="mobile-list" id="mobileJavaList">',
            '<div class="mobile-list-item"><div class="item-icon">☕</div><div class="item-info"><div class="item-name">系统 Java</div><div class="item-desc" id="mobileJavaInfo">检测中...</div></div><span class="mobile-version-tag" id="mobileJavaStatus">--</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileToggleCustomJava()"><div class="item-icon">🔧</div><div class="item-info"><div class="item-name">自定义 Java</div><div class="item-desc">手动指定路径</div></div><div class="mobile-toggle" id="mobileCustomJavaToggle"></div></div>',
            '<div class="mobile-list-item" id="mobileJavaDownloadItem" style="display:none"><div class="item-icon">📥</div><div class="item-info"><div class="item-name" id="mobileJavaDownloadName">下载 Java</div><div class="item-desc" id="mobileJavaDownloadStatus">准备中...</div></div></div>',
          '</div>',
          '<button class="mobile-btn mobile-btn-primary mobile-btn-full" id="mobileJavaDownloadBtn" onclick="window.mobileDownloadJava()">下载 Java 17</button>',
        '</div>',

        // 下载管理
        '<div class="mobile-page" id="mobile-page-downloads">',
          '<div class="mobile-section-title"><span>下载管理</span></div>',
          '<div class="mobile-list" id="mobileDownloadList">',
            '<div class="mobile-empty"><div class="empty-icon">📥</div><h3>暂无下载</h3><p>开始下载后将显示</p></div>',
          '</div>',
        '</div>',

        // 账户
        '<div class="mobile-page" id="mobile-page-accounts">',
          '<div class="mobile-section-title"><span>账户管理</span></div>',
          '<div class="mobile-list" id="mobileAccountList">',
            '<div class="mobile-empty"><div class="empty-icon">👤</div><h3>未登录</h3><p>添加账户开始游戏</p></div>',
          '</div>',
          '<button class="mobile-btn mobile-btn-primary mobile-btn-full" onclick="window.mobileLoginMicrosoft()">登录 Microsoft 账户</button>',
          '<button class="mobile-btn mobile-btn-secondary mobile-btn-full" style="margin-top:8px" onclick="window.mobileAddOfflineAccount()">添加离线账户</button>',
        '</div>',

        // 设置
        '<div class="mobile-page" id="mobile-page-settings">',
          '<div class="mobile-section-title"><span>设置</span></div>',
          '<div class="mobile-list">',
            '<div class="mobile-list-item" onclick="window.mobileToggleDarkMode()"><div class="item-icon">🌙</div><div class="item-info"><div class="item-name">深色模式</div><div class="item-desc">切换深色/浅色主题</div></div><div class="mobile-toggle" id="mobileDarkModeToggle"></div></div>',
            '<div class="mobile-list-item" onclick="window.mobileNavigate(\\'accounts\\')"><div class="item-icon">👤</div><div class="item-info"><div class="item-name">账户管理</div><div class="item-desc" id="mobileAccountStatus">未登录</div></div><span style="color:var(--text-tertiary)">›</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileShowDownloadSourceModal()"><div class="item-icon">🌐</div><div class="item-info"><div class="item-name">文件下载源</div><div class="item-desc" id="mobileDownloadSourceDesc">国内优先 (BMCLAPI)</div></div><span style="color:var(--text-tertiary)">›</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileShowVersionSourceModal()"><div class="item-icon">📋</div><div class="item-info"><div class="item-name">版本列表源</div><div class="item-desc" id="mobileVersionSourceDesc">智能选择 (BMCLAPI)</div></div><span style="color:var(--text-tertiary)">›</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileShowMgRenderModal()"><div class="item-icon">🎨</div><div class="item-info"><div class="item-name">MG 渲染壁纸</div><div class="item-desc">像素风格动态背景</div></div><div class="mobile-toggle" id="mobileMgRenderToggle"></div></div>',
            '<div class="mobile-list-item" onclick="window.mobileCustomWallpaper()"><div class="item-icon">🖼️</div><div class="item-info"><div class="item-name">自定义壁纸</div><div class="item-desc">从相册选择图片</div></div><span style="color:var(--text-tertiary)">›</span></div>',
            '<div class="mobile-list-item"><div class="item-icon">💬</div><div class="item-info"><div class="item-name">启动参数</div><div class="item-desc">自定义 JVM 参数</div></div><span style="color:var(--text-tertiary)">›</span></div>',
            '<div class="mobile-list-item"><div class="item-icon">📁</div><div class="item-info"><div class="item-name">游戏目录</div><div class="item-desc" id="mobileGameDir">检测中...</div></div><span style="color:var(--text-tertiary)">›</span></div>',
          '</div>',
        '</div>',

        // 启动中
        '<div class="mobile-page" id="mobile-page-launching">',
          '<div class="mobile-launching">',
            '<div class="launch-icon">🎮</div>',
            '<h2>正在启动...</h2>',
            '<p id="mobileLaunchStatus">准备游戏环境</p>',
            '<div class="mobile-progress"><div class="progress-fill" id="mobileLaunchProgress" style="width:0%"></div></div>',
            '<button class="mobile-btn mobile-btn-secondary" style="margin-top:20px" onclick="window.mobileCancelLaunch()">取消</button>',
          '</div>',
        '</div>',
      '</div>',

      // 底部导航
      '<nav class="mobile-bottom-nav" id="mobileBottomNav">',
        '<button class="nav-item active" data-page="home" onclick="window.mobileNavigate(\\'home\\')"><svg viewBox="0 0 24 24"><path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/></svg><span>首页</span></button>',
        '<button class="nav-item" data-page="versions" onclick="window.mobileNavigate(\\'versions\\')"><svg viewBox="0 0 24 24"><path d="M20 6h-8l-2-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2z"/></svg><span>版本</span></button>',
        '<button class="nav-item" data-page="mods" onclick="window.mobileNavigate(\\'mods\\')"><svg viewBox="0 0 24 24"><path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z"/></svg><span>模组</span></button>',
        '<button class="nav-item" data-page="resources" onclick="window.mobileNavigate(\\'resources\\')"><svg viewBox="0 0 24 24"><path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg><span>资源</span></button>',
        '<button class="nav-item" data-page="settings" onclick="window.mobileNavigate(\\'settings\\')"><svg viewBox="0 0 24 24"><path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/></svg><span>设置</span></button>',
      '</nav>',

      // 安装版本模态框
      '<div class="mobile-modal" id="mobileInstallModal">',
        '<div class="mobile-modal-overlay" onclick="window.mobileCloseModal(\\'mobileInstallModal\\')"></div>',
        '<div class="mobile-modal-content">',
          '<div class="mobile-modal-header"><h2>添加版本</h2><button class="mobile-modal-close" onclick="window.mobileCloseModal(\\'mobileInstallModal\\')">✕</button></div>',
          '<div class="mobile-input-group"><label>版本 ID</label><input class="mobile-input-field" type="text" placeholder="例如: 1.20.1" id="mobileVersionId"></div>',
          '<div class="mobile-input-group"><label>版本类型</label><select class="mobile-input-field" id="mobileVersionType"><option value="release">Release 正式版</option><option value="snapshot">Snapshot 快照</option><option value="optimized">Release 优化版</option><option value="fabric">Fabric</option><option value="forge">Forge</option></select></div>',
          '<button class="mobile-btn mobile-btn-primary mobile-btn-full" onclick="window.mobileInstallVersion()">安装版本</button>',
        '</div>',
      '</div>',

      // 下载源模态框
      '<div class="mobile-modal" id="mobileDownloadSourceModal">',
        '<div class="mobile-modal-overlay" onclick="window.mobileCloseModal(\\'mobileDownloadSourceModal\\')"></div>',
        '<div class="mobile-modal-content">',
          '<div class="mobile-modal-header"><h2>文件下载源</h2><button class="mobile-modal-close" onclick="window.mobileCloseModal(\\'mobileDownloadSourceModal\\')">✕</button></div>',
          '<div class="mobile-list">',
            '<div class="mobile-list-item" onclick="window.mobileSetDownloadSource(\\'china-first\\')"><div class="item-icon">🇨🇳</div><div class="item-info"><div class="item-name">国内优先 (BMCLAPI)</div><div class="item-desc">使用 BMCLAPI 镜像，速度最快</div></div><span class="mobile-version-tag" id="ds-china-first" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetDownloadSource(\\'auto\\')"><div class="item-icon">⚡</div><div class="item-info"><div class="item-name">智能混合</div><div class="item-desc">优先 BMCLAPI，慢时切换官方</div></div><span class="mobile-version-tag" id="ds-auto" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetDownloadSource(\\'official-first\\')"><div class="item-icon">🌍</div><div class="item-info"><div class="item-name">官方优先</div><div class="item-desc">优先 Mojang 官方源</div></div><span class="mobile-version-tag" id="ds-official-first" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetDownloadSource(\\'mojang\\')"><div class="item-icon">📦</div><div class="item-info"><div class="item-name">直连 Mojang</div><div class="item-desc">直连官方服务器</div></div><span class="mobile-version-tag" id="ds-mojang" style="display:none">✓</span></div>',
          '</div>',
        '</div>',
      '</div>',

      // 版本列表源模态框
      '<div class="mobile-modal" id="mobileVersionSourceModal">',
        '<div class="mobile-modal-overlay" onclick="window.mobileCloseModal(\\'mobileVersionSourceModal\\')"></div>',
        '<div class="mobile-modal-content">',
          '<div class="mobile-modal-header"><h2>版本列表源</h2><button class="mobile-modal-close" onclick="window.mobileCloseModal(\\'mobileVersionSourceModal\\')">✕</button></div>',
          '<div class="mobile-list">',
            '<div class="mobile-list-item" onclick="window.mobileSetVersionSource(\\'auto\\')"><div class="item-icon">⚡</div><div class="item-info"><div class="item-name">智能选择</div><div class="item-desc">优先 BMCLAPI，慢时换官方</div></div><span class="mobile-version-tag" id="vs-auto" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetVersionSource(\\'bmclapi\\')"><div class="item-icon">🇨🇳</div><div class="item-info"><div class="item-name">BMCLAPI 镜像</div><div class="item-desc">bangbang93 国内镜像源</div></div><span class="mobile-version-tag" id="vs-bmclapi" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetVersionSource(\\'mojang\\')"><div class="item-icon">🌍</div><div class="item-info"><div class="item-name">Mojang 官方</div><div class="item-desc">直连 Mojang 官方服务器</div></div><span class="mobile-version-tag" id="vs-mojang" style="display:none">✓</span></div>',
          '</div>',
        '</div>',
      '</div>',

      // MG 渲染模态框
      '<div class="mobile-modal" id="mobileMgRenderModal">',
        '<div class="mobile-modal-overlay" onclick="window.mobileCloseModal(\\'mobileMgRenderModal\\')"></div>',
        '<div class="mobile-modal-content">',
          '<div class="mobile-modal-header"><h2>MG 渲染壁纸</h2><button class="mobile-modal-close" onclick="window.mobileCloseModal(\\'mobileMgRenderModal\\')">✕</button></div>',
          '<div class="mobile-list">',
            '<div class="mobile-list-item" onclick="window.mobileSetMgTheme(\\'overworld\\')"><div class="item-icon">🌿</div><div class="item-info"><div class="item-name">主世界</div></div><span class="mobile-version-tag" id="mg-overworld" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetMgTheme(\\'nether\\')"><div class="item-icon">🔥</div><div class="item-info"><div class="item-name">下界</div></div><span class="mobile-version-tag" id="mg-nether" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetMgTheme(\\'end\\')"><div class="item-icon">🟣</div><div class="item-info"><div class="item-name">终界</div></div><span class="mobile-version-tag" id="mg-end" style="display:none">✓</span></div>',
            '<div class="mobile-list-item" onclick="window.mobileSetMgTheme(\\'wild\\')"><div class="item-icon">🌾</div><div class="item-info"><div class="item-name">荒野</div></div><span class="mobile-version-tag" id="mg-wild" style="display:none">✓</span></div>',
          '</div>',
          '<p style="font-size:12px;color:var(--text-tertiary);margin-top:12px;text-align:center;">MG 渲染使用 Canvas 绘制像素风格动态背景，不依赖网络图片</p>',
        '</div>',
      '</div>',

      // Toast
      '<div class="mobile-toast" id="mobileToast"></div>',
    ].join('');
  }  // ========== 更新 UI ==========
  function updateMobileUI() {
    setTimeout(function() {
      var cv = window.state && window.state.currentVersion;
      var ve = document.getElementById('mobileCurrentVersion');
      if (ve) ve.textContent = cv ? '当前版本: ' + cv.id : '选择一个版本开始游戏';

      var accounts = window.state && window.state.accounts || [];
      var ub = document.getElementById('mobileUserBadge');
      var un = document.getElementById('mobileUserName');
      var ua = document.getElementById('mobileUserAvatar');
      if (accounts.length > 0) {
        ub.style.display = 'flex';
        un.textContent = accounts[0].name;
        ua.textContent = accounts[0].name[0].toUpperCase();
      } else {
        ub.style.display = 'none';
      }

      var dm = document.getElementById('mobileDarkModeToggle');
      if (dm) dm.classList.toggle('active', window.state && window.state.settings && window.state.settings.theme === 'dark');

      var ds = window.state && window.state.settings ? (window.state.settings.downloadSource || 'china-first') : 'china-first';
      var dsd = document.getElementById('mobileDownloadSourceDesc');
      if (dsd) dsd.textContent = ds === 'china-first' ? '国内优先 (BMCLAPI)' : ds === 'auto' ? '智能混合' : ds === 'official-first' ? '官方优先' : '直连 Mojang';

      var vs = window.state && window.state.settings ? (window.state.settings.versionSource || 'auto') : 'auto';
      var vsd = document.getElementById('mobileVersionSourceDesc');
      if (vsd) vsd.textContent = vs === 'bmclapi' ? 'BMCLAPI 镜像' : vs === 'mojang' ? 'Mojang 官方' : '智能选择';

      var mgRender = window.state && window.state.settings && window.state.settings.mgRender;
      var mgt = document.getElementById('mobileMgRenderToggle');
      if (mgt) mgt.classList.toggle('active', !!mgRender);

      var mgTheme = window.state && window.state.settings && window.state.settings.mgTheme || 'overworld';
      ['overworld','nether','end','wild'].forEach(function(t) {
        var el = document.getElementById('mg-' + t);
        if (el) el.style.display = t === mgTheme ? 'inline-flex' : 'none';
      });

      var gde = document.getElementById('mobileGameDir');
      if (gde) gde.textContent = '点击查看';

      window.mobileUpdateVersionList();
      window.mobileUpdateCurrentVersion();
      window.mobileCheckJava();
    }, 200);
  }

  // ========== 导航 ==========
  window.mobileNavigate = function(page) {
    if (!mobileState.isMobile) return;
    if (mobileState.currentPage === page) return;
    if (mobileState.javaPollTimer) { clearInterval(mobileState.javaPollTimer); mobileState.javaPollTimer = null; }
    if (mobileState.installPollTimer) { clearInterval(mobileState.installPollTimer); mobileState.installPollTimer = null; }

    $$('.mobile-page').forEach(function(p) { p.classList.remove('active'); });
    var tp = document.getElementById('mobile-page-' + page);
    if (tp) tp.classList.add('active');
    else { var h = document.getElementById('mobile-page-home'); if (h) h.classList.add('active'); }

    $$('.nav-item').forEach(function(n) {
      n.classList.toggle('active', n.getAttribute('data-page') === page);
    });

    var c = document.getElementById('mobileContent');
    if (c) c.scrollTop = 0;
    mobileState.currentPage = page;

    if (page === 'java') window.mobileCheckJava();
    if (page === 'resources') {
      var se = document.getElementById('mobileResourceSearch');
      if (se) se.value = '';
      var rl = document.getElementById('mobileResourceList');
      if (rl) rl.innerHTML = '<div class="mobile-empty"><div class="empty-icon">🔍</div><h3>搜索资源</h3><p>从 Modrinth 搜索</p></div>';
    }
  };

  // ========== 启动游戏 ==========
  var launchInterval = null;
  window.mobileLaunchGame = function() {
    var cv = window.state && window.state.currentVersion;
    if (!cv) { window.mobileToast('请先选择版本'); window.mobileNavigate('versions'); return; }
    window.mobileNavigate('launching');
    window.mobileSimulateLaunch();
  };
  window.mobileSimulateLaunch = function() {
    if (launchInterval) { clearInterval(launchInterval); launchInterval = null; }
    var steps = [{t:'准备游戏环境...',p:10},{t:'加载模组...',p:30},{t:'连接账户...',p:50},{t:'启动 Java...',p:70},{t:'加载资源...',p:90},{t:'进入世界...',p:100}];
    var i = 0;
    launchInterval = setInterval(function() {
      if (i < steps.length) {
        var se = document.getElementById('mobileLaunchStatus');
        var pe = document.getElementById('mobileLaunchProgress');
        if (se) se.textContent = steps[i].t;
        if (pe) pe.style.width = steps[i].p + '%';
        i++;
      } else {
        if (launchInterval) { clearInterval(launchInterval); launchInterval = null; }
        setTimeout(function() { window.mobileToast('游戏启动中...'); window.mobileNavigate('home'); }, 500);
      }
    }, 800);
  };
  window.mobileCancelLaunch = function() {
    if (launchInterval) { clearInterval(launchInterval); launchInterval = null; }
    window.mobileNavigate('home');
    window.mobileToast('已取消');
  };

  // ========== 模态框 ==========
  window.mobileShowInstallModal = function() { var m = document.getElementById('mobileInstallModal'); if (m) m.classList.add('active'); };
  window.mobileCloseModal = function(id) { var m = document.getElementById(id); if (m) m.classList.remove('active'); };

  // ========== 版本安装 ==========
  window.mobileInstallVersion = async function() {
    var id = (document.getElementById('mobileVersionId') || {}).value.trim();
    if (!id) { window.mobileToast('请输入版本 ID'); return; }
    var ds = (window.state && window.state.settings && window.state.settings.downloadSource) || 'china-first';
    window.mobileToast('正在安装 ' + id + '...');
    window.mobileCloseModal('mobileInstallModal');
    if (document.getElementById('mobileVersionId')) document.getElementById('mobileVersionId').value = '';
    try {
      var result = await API.installVersion('', id, null, ds);
      if (result && result.sessionId) {
        mobileState.installSessionId = result.sessionId;
        window.mobileToast('正在下载');
        window.mobilePollInstallProgress(result.sessionId);
      } else { throw new Error(result && result.error ? result.error : '安装失败'); }
    } catch(e) { window.mobileToast('安装失败: ' + e.message); }
  };

  window.mobilePollInstallProgress = function(sid) {
    if (mobileState.installPollTimer) clearInterval(mobileState.installPollTimer);
    mobileState.installPollTimer = setInterval(async function() {
      try {
        var s = await API.getInstallProgress(sid);
        if (s && s.status) {
          var pe = document.getElementById('mobileLaunchProgress');
          var se = document.getElementById('mobileLaunchStatus');
          if (pe) pe.style.width = (s.progress || 0) + '%';
          if (se) se.textContent = s.status || '下载中...';
          if (s.status === 'completed' || s.status === 'done') {
            clearInterval(mobileState.installPollTimer); mobileState.installPollTimer = null;
            mobileState.installSessionId = null;
            window.mobileToast('安装完成');
            window.mobileUpdateVersionList(); window.mobileUpdateCurrentVersion();
            window.mobileNavigate('home');
          } else if (s.status === 'failed' || s.error) {
            clearInterval(mobileState.installPollTimer); mobileState.installPollTimer = null;
            mobileState.installSessionId = null;
            window.mobileToast('安装失败: ' + (s.error || '未知错误'));
            window.mobileNavigate('versions');
          }
        }
      } catch(e) { clearInterval(mobileState.installPollTimer); mobileState.installPollTimer = null; }
    }, 2000);
  };

  // ========== 版本列表 ==========
  window.mobileUpdateVersionList = function() {
    var c = document.getElementById('mobileVersionList');
    var rc = document.getElementById('mobileRecentVersions');
    var versions = window.state ? (window.state.versions || []) : [];
    var empty = '<div class="mobile-empty"><div class="empty-icon">📦</div><h3>暂无版本</h3><p>点击「添加」安装</p></div>';
    if (!versions.length) { if (c) c.innerHTML = empty; if (rc) rc.innerHTML = empty; return; }
    var sorted = versions.slice().sort(function(a,b) { return (b.lastPlayed||0) - (a.lastPlayed||0); });
    if (c) c.innerHTML = sorted.map(function(v) { return window.mobileCreateVersionItem(v); }).join('');
    if (rc) rc.innerHTML = sorted.slice(0,3).map(function(v) { return window.mobileCreateVersionItem(v); }).join('') || empty;
  };
  window.mobileCreateVersionItem = function(v) {
    var isActive = window.state && window.state.currentVersion && window.state.currentVersion.id === v.id;
    return '<div class="mobile-list-item" onclick="window.mobileSelectVersion(\\''+v.id+'\\')"><div class="item-icon">'+(isActive?'✅':'📦')+'</div><div class="item-info"><div class="item-name">'+v.id+'</div><div class="item-desc">'+(v.type||'')+(v.lastPlayed?' · 最近使用':'')+'</div></div>'+(isActive?'<span class="mobile-version-tag release">当前</span>':'')+'</div>';
  };
  window.mobileSelectVersion = function(id) {
    var versions = window.state ? (window.state.versions || []) : [];
    var v = versions.find(function(x) { return x.id === id; });
    if (v) {
      if (!window.state) window.state = {};
      window.state.currentVersion = v; v.lastPlayed = Date.now();
      window.mobileUpdateVersionList(); window.mobileUpdateCurrentVersion();
      window.mobileToast('已选择 ' + id);
    }
  };
  window.mobileUpdateCurrentVersion = function() {
    var e = document.getElementById('mobileCurrentVersion');
    if (e && window.state && window.state.currentVersion) e.textContent = '当前版本: ' + window.state.currentVersion.id;
  };
  window.mobileFilterVersions = function() {
    var q = (document.getElementById('mobileVersionSearch')||{}).value.toLowerCase() || '';
    var versions = window.state ? (window.state.versions || []) : [];
    var f = versions.filter(function(v) { return v.id.toLowerCase().includes(q); });
    var c = document.getElementById('mobileVersionList');
    if (c) c.innerHTML = f.length ? f.map(function(v){return window.mobileCreateVersionItem(v);}).join('') : '<div class="mobile-empty"><h3>未找到</h3></div>';
  };
  window.mobileFilterMods = function() {
    var q = (document.getElementById('mobileModSearch')||{}).value.toLowerCase() || '';
    var mods = window.state ? (window.state.mods || []) : [];
    var f = mods.filter(function(m) { return (m.name||'').toLowerCase().includes(q); });
    var c = document.getElementById('mobileModList');
    if (c) c.innerHTML = f.length ? f.map(function(m){return '<div class="mobile-list-item"><div class="item-icon">🔧</div><div class="item-info"><div class="item-name">'+m.name+'</div><div class="item-desc">'+(m.version||'')+'</div></div></div>';}).join('') : '<div class="mobile-empty"><h3>未找到</h3></div>';
  };  // ========== Java 管理 ==========
  window.mobileCheckJava = function() {
    var e = document.getElementById('mobileJavaInfo');
    var s = document.getElementById('mobileJavaStatus');
    var custom = (window.state && window.state.settings && window.state.settings.javaPath) || null;
    if (custom) {
      if (e) e.textContent = '自定义: ' + custom.split(/[\\/]/).pop();
      if (s) { s.textContent = '自定义'; s.className = 'mobile-version-tag release'; }
    } else if (window.Android && window.Android.isJavaAvailable) {
      window.Android.isJavaAvailable(function(r) {
        if (e) e.textContent = '已检测到 Java';
        if (s) { s.textContent = '可用'; s.className = 'mobile-version-tag release'; }
      });
    } else {
      if (e) e.textContent = '未检测到系统 Java';
      if (s) { s.textContent = '需要安装'; s.className = 'mobile-version-tag warning'; }
    }
  };
  window.mobileToggleCustomJava = function() {
    var c = prompt('请输入 Java 可执行文件路径（例如: C:\\Java\\jdk-17\\bin\\java.exe）');
    if (!c) return;
    if (!window.state) window.state = {};
    if (!window.state.settings) window.state.settings = {};
    window.state.settings.javaPath = c.trim();
    window.mobileCheckJava();
    window.mobileToast('自定义 Java 路径已保存');
  };
  window.mobileDownloadJava = function() {
    if (!window.state) window.state = {};
    if (!window.state.settings) window.state.settings = {};
    window.state.settings.javaVersion = 17;
    var di = document.getElementById('mobileJavaDownloadItem');
    var dn = document.getElementById('mobileJavaDownloadName');
    var ds = document.getElementById('mobileJavaDownloadStatus');
    var db = document.getElementById('mobileJavaDownloadBtn');
    if (di) di.style.display = 'flex';
    if (db) { db.textContent = '下载 Java 17'; db.disabled = true; }
    if (dn) dn.textContent = '下载 Java 17';
    if (ds) ds.textContent = '准备中...';
    window.mobileToast('正在下载 Java 17...');
    API.downloadJava(17).then(function(result) {
      if (result && result.sessionId) {
        mobileState.javaDownloadSessionId = result.sessionId;
        window.mobilePollJavaDownload(result.sessionId);
      } else {
        if (ds) ds.textContent = '启动失败: ' + (result && result.error ? result.error : '未知错误');
        if (db) { db.textContent = '重试'; db.disabled = false; }
      }
    }).catch(function(e) {
      if (ds) ds.textContent = '错误: ' + e.message;
      if (db) { db.textContent = '重试'; db.disabled = false; }
    });
  };
  window.mobilePollJavaDownload = function(sid) {
    if (mobileState.javaPollTimer) clearInterval(mobileState.javaPollTimer);
    mobileState.javaPollTimer = setInterval(function() {
      API.getJavaDownloadStatus(sid).then(function(s) {
        if (!s || !s.status) return;
        var dn = document.getElementById('mobileJavaDownloadName');
        var ds = document.getElementById('mobileJavaDownloadStatus');
        var db = document.getElementById('mobileJavaDownloadBtn');
        if (s.status === 'downloading') {
          var kb = Math.round(s.downloaded / 1024 / 1024 * 10) / 10;
          var tb = Math.round(s.total / 1024 / 1024 * 10) / 10;
          if (ds) ds.textContent = kb + 'MB / ' + tb + 'MB';
          if (db) db.textContent = '下载中 ' + kb + 'MB/' + tb + 'MB';
        } else if (s.status === 'completed') {
          clearInterval(mobileState.javaPollTimer); mobileState.javaPollTimer = null;
          mobileState.javaDownloadSessionId = null;
          if (ds) ds.textContent = '安装完成';
          if (dn) dn.textContent = 'Java 17 已安装';
          if (db) { db.textContent = '已安装'; db.disabled = true; }
          window.mobileCheckJava();
          if (window.Android && window.Android.setJavaPath && s.installPath) {
            try { window.Android.setJavaPath(s.installPath); } catch(e) {}
          }
        } else if (s.status === 'failed') {
          clearInterval(mobileState.javaPollTimer); mobileState.javaPollTimer = null;
          if (ds) ds.textContent = '安装失败: ' + (s.error || '未知错误');
          if (db) { db.textContent = '重试'; db.disabled = false; }
        }
      }).catch(function() {});
    }, 1500);
  };

  // ========== 微软登录 ==========
  var msState = {};
  window.mobileLoginMicrosoft = function() {
    var isAndroid = typeof window.Android !== 'undefined';
    if (isAndroid) {
      window.Android.showToast && window.Android.showToast('正在打开登录页面...');
      setTimeout(function() {
        var url = 'https://login.live.com/oauth20_authorize.srf?client_id=0000000040121627&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=openid profile offline_access&state=' + Math.random().toString(36).substring(2);
        if (window.Android && window.Android.openUrl) {
          window.Android.openUrl(url);
        } else {
          window.open(url, '_blank');
        }
      }, 500);
    } else {
      if (window.Android && window.Android.msLogin) {
        window.Android.msLogin();
      } else {
        window.open('https://login.live.com/oauth20_authorize.srf?client_id=0000000040121627&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=openid profile offline_access', '_blank');
      }
    }
    window.mobileToast('正在打开登录页面...');
  };

  // ========== 离线账户 ==========
  window.mobileAddOfflineAccount = function() {
    var name = prompt('请输入离线账户名称：', 'Player');
    if (!name) return;
    if (!window.state) window.state = {};
    if (!window.state.accounts) window.state.accounts = [];
    var acc = { id: 'offline-' + Date.now(), name: name, type: 'offline' };
    window.state.accounts.push(acc);
    window.state.currentAccount = acc;
    window.mobileToast('离线账户已添加: ' + name);
    window.mobileUpdateUI();
    try { window.showNotification && window.showNotification('账户添加成功'); } catch(e) {}
  };

  // ========== 资源搜索 ==========
  window.mobileSearchResources = async function() {
    var q = (document.getElementById('mobileResourceSearch') || {}).value.trim();
    if (!q) return;
    var c = document.getElementById('mobileResourceList');
    if (c) c.innerHTML = '<div class="mobile-loading"><div class="mobile-spinner"></div><p>搜索中...</p></div>';
    window.mobileToast('搜索: ' + q);
    try {
      var result = await API.searchMods(q);
      if (c) {
        if (result && result.length) {
          c.innerHTML = result.map(function(r) {
            var icon = r.iconUrl || '🔧';
            return '<div class="mobile-list-item" onclick="window.mobileInstallResource(\\''+r.id+'\\',\\''+(r.name||'').replace(/\\'/g,"\\'")+'\\',\\''+icon+'\\')">'+
              '<div class="item-icon">'+icon+'</div>'+
              '<div class="item-info"><div class="item-name">'+(r.name||r.id)+'</div><div class="item-desc">'+(r.author||'')+' · '+((r.downloads||0).toLocaleString()||'0')+' 下载</div></div>'+
              '<button class="mobile-btn mobile-btn-primary mobile-btn-sm" onclick="event.stopPropagation();window.mobileInstallResource(\\''+r.id+'\\',\\''+(r.name||'').replace(/\\'/g,"\\'")+'\\')">安装</button>'+
            '</div>';
          }).join('');
        } else {
          c.innerHTML = '<div class="mobile-empty"><div class="empty-icon">🔍</div><h3>未找到结果</h3><p>尝试其他关键词</p></div>';
        }
      }
    } catch(e) {
      if (c) c.innerHTML = '<div class="mobile-empty"><h3>搜索失败</h3><p>' + e.message + '</p></div>';
    }
  };
  window.mobileInstallResource = function(id, name, icon) {
    var ds = (window.state && window.state.settings && window.state.settings.downloadSource) || 'china-first';
    window.mobileToast('安装: ' + name + ' (' + ds + ')');
    window.mobileNavigate('downloads');
  };

  // ========== 下载管理 ==========
  window.mobileUpdateDownloadList = function() {
    var c = document.getElementById('mobileDownloadList');
    if (!c) return;
    var dl = window.state ? (window.state.downloads || []) : [];
    if (!dl.length) { c.innerHTML = '<div class="mobile-empty"><div class="empty-icon">📥</div><h3>暂无下载</h3><p>开始下载后将显示</p></div>'; return; }
    c.innerHTML = dl.slice().reverse().map(function(d) {
      var statusClass = d.status === 'completed' ? 'release' : d.status === 'failed' ? 'error' : 'warning';
      return '<div class="mobile-list-item"><div class="item-icon">📥</div><div class="item-info"><div class="item-name">'+d.name+'</div><div class="item-desc">'+(d.status||'')+'</div></div><span class="mobile-version-tag '+statusClass+'">'+((d.size||'')+(d.status==='downloading'?'...':''))+'</span></div>';
    }).join('');
  };

  // ========== 设置操作 ==========
  window.mobileToggleDarkMode = function() {
    if (!window.state) window.state = {};
    if (!window.state.settings) window.state.settings = {};
    window.state.settings.theme = window.state.settings.theme === 'dark' ? 'light' : 'dark';
    var dm = document.getElementById('mobileDarkModeToggle');
    if (dm) dm.classList.toggle('active', window.state.settings.theme === 'dark');
    window.mobileToast('主题: ' + window.state.settings.theme);
  };

  window.mobileShowDownloadSourceModal = function() { document.getElementById('mobileDownloadSourceModal') && document.getElementById('mobileDownloadSourceModal').classList.add('active'); };
  window.mobileShowVersionSourceModal = function() { document.getElementById('mobileVersionSourceModal') && document.getElementById('mobileVersionSourceModal').classList.add('active'); };
  window.mobileShowMgRenderModal = function() { document.getElementById('mobileMgRenderModal') && document.getElementById('mobileMgRenderModal').classList.add('active'); };

  window.mobileSetDownloadSource = function(source) {
    if (!window.state) window.state = {};
    if (!window.state.settings) window.state.settings = {};
    window.state.settings.downloadSource = source;
    var d = { 'china-first':'国内优先 (BMCLAPI)', 'auto':'智能混合', 'official-first':'官方优先', 'mojang':'直连 Mojang' };
    var e = document.getElementById('mobileDownloadSourceDesc');
    if (e) e.textContent = d[source] || source;
    ['china-first','auto','official-first','mojang'].forEach(function(s) {
      var el = document.getElementById('ds-' + s);
      if (el) el.style.display = s === source ? 'inline-flex' : 'none';
    });
    window.mobileCloseModal('mobileDownloadSourceModal');
    window.mobileToast('下载源: ' + source);
  };
  window.mobileSetVersionSource = function(source) {
    if (!window.state) window.state = {};
    if (!window.state.settings) window.state.settings = {};
    window.state.settings.versionSource = source;
    var d = { 'auto':'智能选择', 'bmclapi':'BMCLAPI 镜像', 'mojang':'Mojang 官方' };
    var e = document.getElementById('mobileVersionSourceDesc');
    if (e) e.textContent = d[source] || source;
    ['auto','bmclapi','mojang'].forEach(function(s) {
      var el = document.getElementById('vs-' + s);
      if (el) el.style.display = s === source ? 'inline-flex' : 'none';
    });
    window.mobileCloseModal('mobileVersionSourceModal');
    window.mobileToast('版本源: ' + source);
  };
  window.mobileSetMgTheme = function(theme) {
    if (!window.state) window.state = {};
    if (!window.state.settings) window.state.settings = {};
    window.state.settings.mgTheme = theme;
    window.state.settings.wallpaper = 'mgRender';
    ['overworld','nether','end','wild'].forEach(function(t) {
      var el = document.getElementById('mg-' + t);
      if (el) el.style.display = t === theme ? 'inline-flex' : 'none';
    });
    stopMgAnimation();
    drawMinecraftWallpaper(theme);
    startMgAnimation(theme);
    window.mobileCloseModal('mobileMgRenderModal');
    window.mobileToast('MG 渲染: ' + theme);
  };  // ========== 自定义壁纸 ==========
  window.mobileCustomWallpaper = function() {
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.style.display = 'none';
    document.body.appendChild(input);
    input.click();
    input.addEventListener('change', function(e) {
      var file = e.target.files[0];
      if (!file) return;
      if (!window.state) window.state = {};
      if (!window.state.settings) window.state.settings = {};
      var reader = new FileReader();
      reader.onload = function(ev) {
        window.state.settings.wallpaper = ev.target.result;
        window.mobileToast('壁纸已设置');
        window.mobileUpdateWallpaper();
      };
      reader.readAsDataURL(file);
      document.body.removeChild(input);
    });
  };
  window.mobileUpdateWallpaper = function() {
    var bg = document.getElementById('wallpaper-bg');
    var cs = document.getElementById('wallpaper-canvas');
    if (!bg) return;
    var wp = window.state && window.state.settings && window.state.settings.wallpaper;
    if (wp) {
      if (bg) { bg.style.display = 'block'; bg.style.backgroundImage = 'url(' + wp + ')'; bg.style.backgroundSize = 'cover'; bg.style.backgroundPosition = 'center'; }
      if (cs) cs.style.display = 'none';
    } else {
      if (bg) { bg.style.display = 'none'; bg.style.backgroundImage = ''; }
      if (cs) cs.style.display = 'block';
      drawMinecraftWallpaper('overworld');
    }
  };

  // ========== Toast ==========
  window.mobileToast = function(msg) {
    var t = document.getElementById('mobileToast');
    if (!t) return;
    t.textContent = msg;
    t.style.opacity = '1';
    t.style.transform = 'translateY(0)';
    setTimeout(function() {
      t.style.opacity = '0';
      t.style.transform = 'translateY(20px)';
    }, 2000);
  };

  // ========== API 增强 ==========
  if (typeof API !== 'undefined') {
    (function(orig) {
      window.API = Object.assign({}, orig);
      window.API.installVersion = function(type, id, mcVersion, source) {
        var ds = source || (window.state && window.state.settings && window.state.settings.downloadSource) || 'china-first';
        var baseUrls = {
          'bmclapi': 'https://bmclapi2.bangbang93.com',
          'china-first': 'https://bmclapi2.bangbang93.com',
          'auto': 'https://bmclapi2.bangbang93.com',
          'official-first': 'https://launchermeta.mojang.com',
          'mojang': 'https://launchermeta.mojang.com'
        };
        var baseUrl = baseUrls[ds] || 'https://bmclapi2.bangbang93.com';
        if (type === 'release' || type === 'snapshot') {
          return fetch(baseUrl + '/versions/' + id + '/version.json')
            .then(function(r) { return r.json(); })
            .then(function(vj) {
              var lib = vj.downloads || {};
              var client = lib.client || {};
              var session = { status: 'starting', progress: 0, download: null };
              if (client.url) {
                var clientPath = client.downloads && client.downloads.sha1 ? client.downloads.sha1.path : client.url;
                session.download = { url: baseUrl + '/packages/' + client.hash + '/' + clientPath };
                session.progress = 5;
                session.status = 'downloading';
              }
              return { sessionId: Date.now().toString(), ...session };
            })
            .catch(function(e) {
              console.error('[API] installVersion error:', e);
              return { status: 'failed', error: e.message };
            });
        } else if (type === 'fabric') {
          return fetch('https://meta fabricmc.net/setup/metadata.json')
            .then(function(r) { return r.json(); })
            .then(function(meta) {
              var v = meta.versions.find(function(v) { return v.id.includes(id); });
              if (!v) throw new Error('Fabric 版本未找到');
              return { sessionId: Date.now().toString(), version: v.id, status: 'starting', progress: 0 };
            })
            .catch(function(e) { return { status: 'failed', error: e.message }; });
        } else if (type === 'forge') {
          return fetch('https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.json')
            .then(function(r) { return r.json(); })
            .then(function(meta) {
              var v = meta && meta && meta.versioning && meta.versioning.latest;
              return { sessionId: Date.now().toString(), version: v || id, status: 'starting', progress: 0 };
            })
            .catch(function(e) { return { status: 'failed', error: e.message }; });
        }
      };
    })(window.API);
  }

  // ========== 全局事件监听 ==========
  var resizeTimer;
  window.addEventListener('resize', function() {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(function() {
      if (!mobileState.isMobile) {
        var ma = document.getElementById('mobile-app');
        if (ma) ma.remove();
        $$('.pc-hidden').forEach(function(el) { el.style.display = ''; el.classList.remove('pc-hidden'); });
      } else {
        mobileState.isMobile = true;
        window.mobileUpdateUI();
      }
    }, 300);
  });

  document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
      $$('.mobile-modal.active').forEach(function(m) { m.classList.remove('active'); });
    }
  });

  document.addEventListener('touchstart', function(e) {
    if (e.touches.length > 1) {
      e.preventDefault();
    }
  }, { passive: false });

  document.addEventListener('touchmove', function(e) {
    if (e.target.closest('.mobile-content') || e.target.closest('input') || e.target.closest('textarea') || e.target.closest('select') || e.target.closest('button')) return;
    if (e.touches.length > 1) {
      e.preventDefault();
    }
  }, { passive: false });

  // 检测平板
  window.isTablet = window.innerWidth >= 600 && window.innerWidth <= 1024 && /Android/i.test(navigator.userAgent);
  document.documentElement.setAttribute('data-device', window.isTablet ? 'tablet' : 'mobile');

  // 初始化
  if (typeof API !== 'undefined') {
    initMobile();
  } else {
    var waitApi = setInterval(function() {
      if (typeof API !== 'undefined') {
        clearInterval(waitApi);
        initMobile();
      }
    }, 200);
  }
})();