# 🍬 移动端“商业级”首页重构计划 (Mobile Shop Overhaul)

**目标确认**: 放弃桌面端适配思维，完全转向 **App Shell** 模式。
我们将抛弃现有的 `el-container` 和传统滚动，采用 **Flexbox 全屏分层** 布局，实现左侧导航与右侧商品区的独立丝滑滚动。

## 🏗️ 任务一：重构页面结构 (Layout Rewrite)

### 1. 顶层容器重构 (`src/views/Home.vue` <template>)
- **根容器**: `.mobile-shop-container` (Flex Column, 100vh)。
- **头部区域**: `.shop-header` (固定高度，渐变背景)。
  - 包含品牌标题、副标题。
  - 包含悬浮搜索框。
- **主体区域**: `.shop-body` (Flex 1, 溢出隐藏)。
  - **左侧**: `.category-sidebar` (独立滚动)。
  - **右侧**: `.product-area` (独立滚动)。

### 2. 内容填充
- **Banner**: 移动到 `.product-area` 顶部。
- **商品列表**: 保持现有的数据绑定，但外层结构适配新布局。

## 🎨 任务二：商业级 CSS 样式重写 (Style Overhaul)

### 1. 全局与容器
- **背景**: `#f5f6fa` (拒绝大白屏)。
- **布局**: 确保 `shop-body` 占满剩余空间且 `overflow: hidden`，防止整体页面滚动。

### 2. 顶部品牌栏 (.shop-header)
- **背景**: `linear-gradient(135deg, #ff9f43 0%, #ff6b6b 100%)`。
- **造型**: 底部大圆角 (24px)，营造“糖果盖”效果。
- **内容**: 白色大标题 + 英文副标题。
- **搜索框**: 圆角纯白背景，无边框，悬浮感。

### 3. 左侧导航 (.category-sidebar)
- **宽度**: `100px`。
- **背景**: `#f7f8fa`。
- **选中态**: 纯白背景 + 伪元素左侧指示条 (`#ff6b6b`)。
- **滚动条**: 隐藏 (`::-webkit-scrollbar { display: none }`)。

### 4. 右侧商品区 (.product-area)
- **背景**: `#ffffff` (纯白，与左侧选中项连通)。
- **布局**: Padding `12px`。
- **滚动条**: 隐藏。

### 5. 商品卡片 (.goods-card)
- **样式**: 纯白背景 + 圆角 `16px` + 轻微阴影。
- **布局**: Flex (Bento 风格)，左图右文。
- **加购按钮**: 保持之前的优化 (圆形悬浮按钮)。

## 🔧 任务三：细节打磨 (Polish)

### 1. 轮播图美化
- 高度 `160px`，圆角 `16px`，增加投影。

### 2. 搜索框优化
- 移除 Element Plus 默认边框，使用 `border: none` 或 `box-shadow` 代替。

---
*请批准执行，我将为您打造一个媲美原生 App 的移动端网页体验！*