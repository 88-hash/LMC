# 一键启动脚本 JAVA_HOME 缺失修复计划

## 问题根源分析
根据您提供的最新截图，问题已经非常明确：
1.  **LeYi Backend 窗口报错**：`The JAVA_HOME environment variable is not defined correctly`。
2.  **现象解释**：
    *   虽然我在 IDE 内帮您查到了 `mvn` 的路径，但 Maven 运行必须依赖 `JAVA_HOME` 环境变量。
    *   您的 IDE（Trae）内部环境已经自动注入了 Java 路径（`C:\Users\lmc\.jdks\temurin-17`），所以我在 IDE 里直接运行没问题。
    *   但是！当您脱离 IDE，直接在桌面上双击 `.bat` 脚本时，系统全局环境变量中**并没有配置 JAVA_HOME**，导致 Maven 无法找到 Java 运行环境，从而启动失败。

## 修复方案：在脚本中手动注入 JAVA_HOME

既然系统没有配置，那我们就**直接在脚本里告诉 Maven Java 在哪里**。这是一种最稳健的“绿色版”修复方式，不需要您去修改复杂的系统环境变量。

### 1. 获取 Java 路径
刚才我已经通过命令查到了您机器上有效的 Java 路径：
`C:\Users\lmc\.jdks\temurin-17`

### 2. 修改 `start_system.bat`
我将在启动 Maven 之前，临时设置 `JAVA_HOME` 变量。

*   **修改前**：
    ```batch
    cd backend
    start "LeYi Backend" cmd /k "call "C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd" spring-boot:run"
    ```
*   **修改后**：
    ```batch
    cd backend
    start "LeYi Backend" cmd /k "set JAVA_HOME=C:\Users\lmc\.jdks\temurin-17&& call "C:\Users\lmc\Desktop\apache-maven-3.9.12\bin\mvn.cmd" spring-boot:run"
    ```

## 验证预期
修改完成后，脚本会先设置好环境变量，再启动 Maven。
*   **预期结果**：
    *   LeYi Backend 窗口将不再报 `JAVA_HOME` 错误。
    *   Spring Boot 将正常启动。
    *   网页端的 500 错误将消失，数据恢复正常。

---
**请确认**：是否立即执行此修复？
