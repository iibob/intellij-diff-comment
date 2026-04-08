# IntelliJ Diff Comment Helper

专为 IntelliJ 平台设计的轻量插件，解决 **独立差异视图（Standalone Diff Mode）** 中无法使用快捷键注释及缩进的问题。

<br>

## 🚀 核心特性

当通过命令行（如 `pycharm64.exe diff file1 file2`）启动轻量级对比时，原生注释和缩进功能通常不可用。本插件对此进行了精准修复：

- **🎯 场景隔离**：仅在独立差异视图中生效。在项目内部打开 Diff 时自动禁用，避免与原生功能冲突。
- **⚡ 零依赖启动**：无需加载完整项目工程，完美适配命令行快速对比场景。
- **💬 即时注释**：支持单行或多行选中的代码注释与取消注释（快捷键或右键菜单）。
- **⌨️ 智能缩进**：支持单行或多行选中的代码增加与减少缩进（快捷键或右键菜单）。

<br>

## 🛠 安装方式

### 1. Marketplace 安装（推荐）
在 IDE 的 `Settings` > `Plugins` > `Marketplace` 中搜索 **`Diff Comment Helper`** 并安装。
> [插件地址（官方）](https://plugins.jetbrains.com/plugin/30874-diff-comment-helper)

### 2. 手动安装
1. 前往 [GitHub Releases](https://github.com/iibob/intellij-diff-comment/releases) 下载最新 `.zip` 包。
2. 在 IDE 中通过 `Settings` > `Plugins` > ⚙️ > `Install Plugin from Disk...` 加载该文件。

<br>

## 📖 使用指南

| 功能 | 快捷键 (Win/Linux) | 快捷键 (Mac) | 说明 |
| :--- | :--- | :--- | :--- |
| **切换注释** | `Ctrl + /` | `Cmd + /` | 支持单行或多行选中注释 |
| **增加缩进** | `Ctrl + ]` | `Cmd + ]` | 支持多行批量缩进 |
| **取消缩进** | `Ctrl + [` | `Cmd + [` | 快速回退代码层级 |

1. **启动**：通过命令行运行 `ide diff <file1> <file2>` 打开独立对比窗口。
2. **操作**：选中代码行后使用上述快捷键，或通过右键菜单执行操作。
3. **注意**：在正常项目环境中，请直接使用 IDE 原生的功能；插件会自动检测并禁用以避免干扰。
