**问题定位**

* Vite 报错“Element is missing end tag”，指向 Home.vue 的 <style scoped> 段。

* 检查发现 .goods-list 的样式块未闭合，紧接着写了 .goods-card，导致 SFC 解析失败。

* 同时 Skeleton 的 .skeleton-line.long/.price 规则丢失，需要补齐。

**修复方案（仅前端样式）**

* 在 Home.vue 的 <style scoped> 中：

  * 为 .goods-list 样式块补上右花括号 }

  * 还原 Skeleton 规则：.skeleton .skeleton-line.long 与 .skeleton .skeleton-line.price，并补齐末尾闭合。

* 不修改任何模板结构或脚本逻辑。

**验证**

* 重新编译并打开首页，确保不再出现 SFC 解析错误，页面正常渲染；商品列表与骨架屏样式生效。

