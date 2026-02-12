# 一键启动脚本修复计划

## 问题诊断
您遇到的问题（客户端数据缺失、管理端登录 500 错误）的根本原因在于：**一键启动脚本 (`start_system.bat`) 无法正确启动后端服务**。

1.  **现象分析**：
    *   **客户端**：显示首页但无数据。这是因为首页是公开路由（不需要登录），但由于后端没起来，API 请求（如获取商品列表）失败（返回 500 或网络错误），导致页面空白。
    *   **管理端**：登录接口返回 500。这实际上是前端代理服务器（Vite Proxy）在尝试连接后端（`localhost:8080`）失败时抛出的错误。
    *   **根本原因**：脚本中使用了 `mvn spring-boot:run` 命令，但您的系统全局环境变量中并没有配置 Maven。此前我在 IDE 中是使用您桌面上的绝对路径 `C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd` 来启动的。

## 修复方案

### 1. 修正 `start_system.bat`
我将修改启动脚本，把 `mvn` 替换为您本地确切存在的绝对路径。

*   **修改前**：
    ```batch
    start "LeYi Backend" cmd /c "mvn spring-boot:run"
    ```
*   **修改后**：
    ```batch
    start "LeYi Backend" cmd /c ""C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd" spring-boot:run"
    ```

### 2. 增强脚本健壮性
为了防止未来路径变化导致的问题，并在启动失败时能看到报错，我将优化脚本：
*   添加 `pause` 命令到后端启动窗口，这样如果启动失败，窗口不会立即消失，您能看到具体的错误信息。

### 3. 验证计划
修改完成后，请您再次双击桌面的 `LeYi_Start` 图标。
*   **预期结果**：
    *   会看到一个标题为 "LeYi Backend" 的黑框窗口，里面有大量日志滚动，最终显示 "Started LeyiSnackApplication"。
    *   客户端首页将正常显示商品数据。
    *   管理端登录将成功跳转。

---
**请确认**：是否同意我修改 `start_system.bat` 文件？
