# 🚀 最终冲刺：技能驱动优化计划

遵照您的指令，我将作为核心系统，激活以下增强模块来执行任务。

## 🎨 模块一：激活技能 [UI_Beautifie]

> **Status**: Ready to Initialize
> **Objective**: 提升视觉交互质感，打造多巴胺糖果风格体验。

### 1. 构建 404 缺省空间
- **生成文件**: `src/views/NotFound.vue`
- **视觉参数**:
    - 背景色: `#f7f1e3` (奶茶色)
    - 核心元素: 🍪 Emoji 动态浮动
    - 交互: "返回首页" 按钮 (主色调圆角, 悬停反馈)
- **路由注入**: 自动识别并捕获所有未定义路径 (`/:pathMatch(.*)*`)。

### 2. 全局样式重构 (`src/style.css`)
- **滚动条引擎**: 注入 Webkit 样式，应用 `8px` 圆角与奶茶色系配色。
- **触感反馈**: 为所有 `.el-button` 注入 `:active` 缩放物理效果 (`scale(0.96)`).
- **样式清洗**: 移除默认的深色背景配置，统一为明亮多巴胺风格。

### 3. 动态标题引擎
- **挂载点**: `router/index.js` -> `beforeEach`
- **逻辑**: 实时监听页面路由变化，格式化输出 `页面名 | 乐逸零食`。

---

## ⚡ 模块二：激活技能 [Performance_Tuner]

> **Status**: Ready to Initialize
> **Objective**: 优化关键路径响应速度与资源加载策略。

### 1. 交互响应优化 (`src/utils/request.js`)
- **加载反馈**: 集成 `nprogress` 进度条。
    - Request Start: 进度条启动。
    - Response End: 进度条完成。
- **智能鉴权路由**:
    - **痛点修复**: 解决 `request.js` 仅读取前台 `token` 的缺陷。
    - **策略**: 动态识别请求上下文。若访问 `/admin` 资源，自动切换读取 `adminToken`；否则读取 `token`。

### 2. 资源懒加载扫描
- **目标**: `src/router/index.js`
- **动作**: 确保非核心首屏路由（除 Login/Home 外）全部采用 `import()` 动态导入，降低 Bundle 体积。

---

## 🧹 模块三：交付协议 (Final Protocol)

1.  **代码净化**: 全局扫描并剔除 `console.log` 调试残留。
2.  **冒烟测试**: 验证 订单 -> 核销 -> 评价 全链路。
3.  **资产归档**: 提醒导出 SQL 数据快照。

**Ready to execute skills.**
