# GitHub Codespaces 终端位置指南

## 🖥️ 如何找到Codespaces终端

### 步骤1: 打开Codespace

1. **访问您的GitHub仓库**:
   ```
   https://github.com/dopod2009/calendar-app
   ```

2. **创建或打开Codespace**:
   - 点击绿色的 **"Code"** 按钮（页面右上角）
   - 选择 **"Codespaces"** 标签
   - 点击 **"Create codespace on main"**（或点击已存在的codespace）

3. **等待Codespace启动**:
   - 会显示加载动画
   - 约30秒-1分钟后自动打开

---

### 步骤2: 找到终端窗口

Codespace打开后，您会看到一个**在线VS Code编辑器**界面：

#### 终端位置图解

```
┌─────────────────────────────────────────────────────────┐
│  File  Edit  View  Go  Run  Terminal  Help              │ ← 顶部菜单栏
├─────────────────────────────────────────────────────────┤
│  文件浏览器  │           代码编辑区                      │
│             │                                            │
│  📁 app     │                                            │
│  📁 backend │                                            │
│  📁 core    │                                            │
│  📁 feature │                                            │
│  ...        │                                            │
│             │                                            │
├─────────────┴────────────────────────────────────────────┤
│  终端 (Terminal)                                          │ ← 底部终端区域
│  @codespace-xxx ➜ /workspaces/calendar-app $             │
│                                                          │
│  █                                                       │
└──────────────────────────────────────────────────────────┘
```

---

### 步骤3: 如果没有看到终端

#### 方法A: 使用菜单打开终端

1. 点击顶部菜单栏的 **"Terminal"**
2. 点击 **"New Terminal"**

**或使用快捷键**:
- Windows/Linux: `Ctrl + Shift + `` ` (反引号)
- Mac: `Cmd + Shift + `` ` (反引号)

---

#### 方法B: 使用命令面板

1. 按 `F1` 或 `Ctrl+Shift+P` (Mac: `Cmd+Shift+P`)
2. 输入: `Terminal: Create New Terminal`
3. 按 Enter

---

### 步骤4: 终端打开后的样子

终端打开后，您会看到类似这样的界面：

```
@codespace-a1b2c3d ➜ /workspaces/calendar-app $
```

这就是**命令提示符**，您可以在后面输入命令。

---

## 📝 在终端中执行命令

### 复制粘贴命令

1. **复制命令**（从我提供的文档中）
2. **粘贴到终端**:
   - Windows/Linux: 右键 → 粘贴 或 `Ctrl+V`
   - Mac: `Cmd+V`
3. **按Enter键执行**

---

## 🎯 创建签名密钥的完整操作

### 1. 打开终端后，粘贴并执行：

```bash
keytool -genkeypair -v \
  -alias calendar-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore keystore/calendar-release.jks \
  -storepass calendar2026alpha \
  -keypass calendar2026alpha \
  -dname "CN=Calendar App, OU=Development, O=Calendar Team, L=Beijing, ST=Beijing, C=CN"
```

### 2. 成功后，继续执行：

```bash
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

### 3. 复制输出的Base64字符串

---

## 🖼️ 图文说明

### Codespace界面截图说明

```
顶部菜单栏位置:
┌─────────────────────────────────────────┐
│ File Edit View Go Run Terminal Help     │ ← 点击这里的 "Terminal"
└─────────────────────────────────────────┘

终端面板位置:
┌─────────────────────────────────────────┐
│                                         │
│         代码编辑区                      │
│                                         │
├─────────────────────────────────────────┤
│ 终端面板 (可拖动调整大小)                │
│ $ [在这里粘贴命令]                       │
│                                         │
└─────────────────────────────────────────┘
```

---

## 💡 常见问题

### Q1: 终端面板被隐藏了怎么办？

**解决方案**:
1. 点击顶部菜单 **"View"** → **"Terminal"**
2. 或使用快捷键: `Ctrl+Shift+`` ` (反引号)

---

### Q2: 终端字体太小看不清

**解决方案**:
1. 按 `Ctrl + =` (Mac: `Cmd + =`) 放大字体
2. 或点击终端右上角的设置图标调整

---

### Q3: 终端提示符是 `$` 不是 `>`

**说明**: 
- `$` 表示普通用户提示符（Unix/Linux/macOS系统）
- `>` 是Windows命令提示符
- 都可以正常使用

---

### Q4: 如何关闭终端面板

**操作**:
- 点击终端面板右上角的 **"X"** 按钮
- 或拖动终端面板到底部隐藏

---

### Q5: 如何重新打开之前关闭的终端

**操作**:
1. 点击 **"Terminal"** 菜单
2. 选择 **"New Terminal"**
3. 之前的终端会在列表中显示，可以切换

---

## 🎬 操作视频演示

如果您还是找不到终端，可以：

1. **查看官方文档**: https://docs.github.com/en/codespaces/developing-in-codespaces/using-the-command-palette-in-codespaces

2. **或告诉我**，我会用更详细的图文步骤帮您！

---

## ✅ 快速检查清单

```
□ 已访问 https://github.com/dopod2009/calendar-app
□ 已点击 Code → Codespaces → Create codespace
□ 已等待Codespace启动完成
□ 已看到VS Code编辑器界面
□ 已找到底部终端面板（或通过菜单打开）
□ 已看到命令提示符 (如: @codespace-xxx ➜ /workspaces/calendar-app $)
□ 准备好粘贴命令
```

---

**现在请按照步骤打开Codespace终端，然后告诉我是否找到了！**
