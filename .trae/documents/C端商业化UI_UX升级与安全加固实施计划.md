# 目标
- 将移动端体验从“网页感”提升到“类原生App”级别，并完善安全细节与演示抗压能力。
- 不改你的数据库结构，只优化前后端代码与静态资源。

# 维度一：C端视觉商业化重构（优先实现）
## 首页双栏独立滚动（Sticky Sidebar）
- 文件： [Home.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/Home.vue)
- 实施要点：
  - 根容器设定 height: 100vh; 页面整体 overflow: hidden（已具备基础结构，再加强兼容）
  - 左侧 .category-sidebar 设置 overflow-y: auto；右侧 .product-area 设置 overflow-y: auto；二者互不影响
  - 修复可能的遮挡：确保顶部品牌栏采用 flex 固定高度，不随内容滚动

## 多巴胺卡片设计（商品卡）
- 文件： [Home.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/Home.vue)
- 调整：
  - 卡片大圆角 16px + 更重阴影（0 6px 18px rgba(0,0,0,0.08)）
  - 价格数字加大加粗；颜色使用 #ff6b6b
  - 加购按钮做悬浮圆形（右下角绝对定位），高饱和渐变背景；点击缩放+发光动画

## 空状态插画（Cart/Order）
- 文件： [Cart.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/Cart.vue) [Order.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/Order.vue)
- 逻辑：
  - 当列表为空时使用 el-empty + 自定义插画 URL（稳定图源）与文案：“肚子饿饿，快去喂食~”

## 骨架屏（Skeleton）
- 文件： [Home.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/Home.vue)
- 实施：
  - 增加 loading 状态；在 fetchGoods 前后切换
  - loading 时渲染 6 个骨架卡片（图片占位+两行文本）

# 维度二：交互体验升级
## 全局 Loading 反馈（不引入新依赖）
- 文件： [request.js](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/utils/request.js)
- 方案：
  - 使用 Element-Plus ElLoading 在请求拦截器内全局显示/隐藏（并支持并发计数，避免闪烁）
  - 错误统一提示；超时/网络异常统一文案

## 按钮点击触感
- 文件：全局样式或每页 scoped 样式
- 规则：
  - 按钮与可点击元素加 :active { transform: scale(0.95); } + 过渡，提升“按下去”的反馈

## 简易加购动效（可选增强）
- 文件： [Home.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/Home.vue)
- 方案：
  - 克隆商品图→贝塞尔曲线飞向底部 TabBar 购物车徽标→结束后徽标轻微抖动；若实现周期有限，保留缩放+光晕动效作为第一版

# 维度三：演示防呆与安全细节
## 404 页面
- 新建： [NotFound.vue](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/views/NotFound.vue)
- 路由： [router/index.js](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/frontend/src/router/index.js)
  - 添加 `/:pathMatch(.*)*` 指向 NotFound.vue；配可爱文案与回到首页按钮

## 全局数据与图片稳定性
- 脚本： init.sql（不改表，仅补充更稳定的图片 URL 与示例订单）
- 要求：
  - 商品图片换用稳定图床（如 imgur/阿里云OSS/自有静态资源）
  - 预置 3~5 条历史订单（含 0/1/-1 状态），用于导师演示

## 安全强化（不改表，只加控制）
- 文件： [OrderController.java](file:///c:/Users/lmc/Desktop/LeYi_Snack_System/backend/src/main/java/com/leyi/snack/controller/OrderController.java)
- 规则：
  - getByNo：若 `order.userId != currentUserId`，直接返回错误；避免 ID 枚举越权
  - detail：若传入 id 对应订单的 userId 与当前用户不符，拒绝

# 验收路径
1. 首页加载→骨架屏→商品列表→加购动效→购物车→订单（空状态文案与插画）
2. 支付→C端订单显示核销码→B端核销中心显示待核销→核销→B端订单管理显示已完成
3. 输入错误地址→显示 404 页面

# 实施顺序
1) 首页双栏滚动与卡片美化（Step 19）
2) 全局 Loading 与按钮触感、404 页面（Step 18）
3) 空状态插画与骨架屏完善
4) 安全加固与数据美化

请确认执行；确认后我将逐项落地代码与样式，并进行演示级自测。