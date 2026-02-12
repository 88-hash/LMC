问题：Vite 报 Element is missing end tag，指向 Home.vue 的 <style scoped> 段末尾。
原因：文件末尾缺少 </style> 闭合标签，导致 SFC 解析失败。
处理：
- 在 Home.vue 末尾补齐 </style> 闭合标签；不修改模板与脚本逻辑。
验证：保存后刷新首页与切换路由，确认不再出现 500 与动态模块加载失败。