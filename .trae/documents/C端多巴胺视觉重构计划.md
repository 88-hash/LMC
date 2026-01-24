# 🎨 C端多巴胺视觉重构计划 (Dopamine Visual Overhaul Plan)

**状态确认**: 已成功加载 `.claude/skills/ui_beautifie` 技能指令。
我们将结合该技能的核心理念（**柔和配色、圆角卡片、平滑动画、移动端响应式**）与您指定的“多巴胺”配色方案，执行以下重构计划。

*注：为了保持项目轻量并符合您修改 `style.css` 的具体指令，我们将使用原生 CSS/Sass 实现技能描述中的视觉效果，而不额外引入 Tailwind CSS。*

## 🏗️ 阶段一：重构 C 端布局架构 (Layout Refactor)

### 1. 全局样式注入 (`src/style.css`)
- **定义多巴胺变量**:
  - 背景色: `--bg-color: #f7f1e3` (奶茶色)
  - 主色调: `--primary-color: #ff6b6b` (西瓜红)
  - 次色调: `--secondary-color: #4ecdc4` (薄荷绿)
  - 卡片圆角: `--card-radius: 16px`
- **基础样式重置**: 将 `body` 背景色设为 `--bg-color`，优化字体设置。

### 2. 新建智能导航组件 (`src/components/NavBar.vue`)
- **功能**:
  - **左侧**: 返回按钮 (非 Tab 页显示)。
  - **中间**: 页面标题 (读取路由 `meta.title`)。
  - **样式**: 固定顶部，磨砂玻璃效果 (`backdrop-filter: blur`)，深灰加粗字体。

### 3. 重构主布局 (`src/views/Layout.vue`)
- **移除**: 现有的 PC 端顶部 Header (Logo/搜索/右侧操作区)。
- **新增**:
  - 顶部: 引入 `<NavBar />`。
  - 底部: 新增 **TabBar 导航栏** (首页/购物车/订单/我的)，固定底部，选中态使用主色调填充。
  - 中间: `<router-view>` 包裹 `slide-fade` 过渡动画。
- **适配**: 调整 `main` 容器的 `padding-top` 和 `padding-bottom` 以防止内容被遮挡。

## 🎡 阶段二：首页视觉大升级 (Home Page Upgrade)

### 1. 首页改造 (`src/views/Home.vue`)
- **轮播图**: 在搜索栏下方引入 `el-carousel`。
  - **数据**: Mock 3 张色彩鲜艳的零食海报数据 (SVG/占位图)。
  - **样式**: 圆角卡片风格，高度 150px-200px。
- **商品列表糖果化**:
  - **卡片**: 纯白背景 + 深阴影 (`box-shadow`) + 16px 圆角。
  - **布局**: 图片占上半部，信息在下半部。
  - **交互**: 价格使用高亮气泡 (黄底红字)，加购按钮改为**悬浮圆形“+”号** (颜色 `#ff6b6b`)。

## 🎨 阶段三：全站页面“多巴胺”覆盖 (Global Polish)

### 1. 列表页美化 (`Cart.vue`, `Order.vue`)
- **Bento Grid 风格**:
  - 将表格/列表项改为独立的圆角卡片。
  - 增加卡片间距，透出背景的奶茶色。
  - 移除传统的分割线，用阴影和间距区分内容。

### 2. 个人中心重绘 (`Profile.vue`)
- **头部**: 使用 CSS `linear-gradient` 创建波浪形或圆弧形背景。
- **头像**: 增加白色粗边框 (`border: 4px solid white`) 和阴影。
- **菜单**: 将列表式菜单改为悬浮卡片组，每个入口前添加彩色 Icon。

## ⚠️ 风险与对策
- **Element Plus 样式覆盖**: 可能需要使用 `:deep()` 深度选择器来覆盖组件库默认样式。
- **移动端适配**: 确保点击区域 (Touch Targets) 大于 44px。
- **导航逻辑**: 需确保路由 meta 信息中包含正确的 title。
