# IntelliJ Diff Comment Helper

专为 IntelliJ 平台设计的轻量插件，解决 **独立差异视图（Standalone Diff Mode）** 中无法使用快捷键注释的问题。

## 🚀 核心特性

当通过命令行（如 `pycharm64.exe diff file1 file2`）启动轻量级对比时，原生注释功能通常不可用。本插件对此进行了精准修复：

- **🎯 场景隔离**：仅在独立差异视图中生效。在项目内部打开 Diff 时自动禁用，避免与原生功能冲突。
- **⚡ 零依赖启动**：无需加载完整项目工程，完美适配命令行快速对比场景。
- **💬 即时注释**：恢复 `Ctrl/Cmd + /` 快捷键及右键菜单的“行注释”功能，支持在对比时直接标记代码。

## 🛠 安装方式

### 1. Marketplace 安装（推荐）
在 IDE 的 `Settings` > `Plugins` > `Marketplace` 中搜索 **`Diff Comment Helper`** 并安装。

### 2. 手动安装
1. 前往 [GitHub Releases](https://github.com/iibob/intellij-diff-comment/releases) 下载最新 `.zip` 包。
2. 在 IDE 中通过 `Settings` > `Plugins` > ⚙️ > `Install Plugin from Disk...` 加载该文件。

## 📖 使用指南

1. **启动**：通过命令行运行 `ide diff <file1> <file2>` 打开独立对比窗口。
2. **操作**：选中代码行，按下 `Ctrl + /` (Win/Linux) 或 `Cmd + /` (Mac) 即可切换注释。
3. **注意**：在正常项目环境中，请直接使用 IDE 原生的注释功能；插件会自动禁用以避免冲突。
