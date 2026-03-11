# 终端操作指引

## ⚠️ 重要说明

`gh auth login` 命令需要**交互式操作**（需要您手动选择选项和在浏览器中授权），无法通过我直接执行。

---

## 🖥️ 请在您的电脑终端执行

### 打开终端

**macOS方式**：
- 按 `Command + 空格`
- 输入 `Terminal` 或 `终端`
- 按Enter打开

---

### 执行命令

在终端中输入以下命令并按Enter：

```bash
gh auth login
```

---

### 交互式选择步骤

使用 **↑↓ 方向键** 选择，使用 **Enter键** 确认：

#### 第1步：选择GitHub账户类型
```
? What account do you want to log into?
  > GitHub.com        ← 选择这个
    GitHub Enterprise
```

#### 第2步：选择Git协议
```
? What is your preferred protocol for Git operations?
  > HTTPS            ← 选择这个
    SSH
```

#### 第3步：认证Git凭据
```
? Authenticate Git with your GitHub HTTP credentials?
  > Yes              ← 输入 Y 或选择Yes
    No
```

#### 第4步：选择认证方式
```
? How would you like to authenticate GitHub CLI?
  > Login with a web browser    ← 选择这个
    Paste an authentication token
```

---

### 浏览器授权步骤

终端会显示：

```
! First copy your one-time code: XXXX-XXXX
  Press Enter to open github.com in your browser...
```

**操作**：

1. **复制code**：选中 `XXXX-XXXX` 这8个字符复制（例如：`ABCD-1234`）

2. **按Enter键**：浏览器会自动打开GitHub授权页面

3. **在浏览器中**：
   - 粘贴刚才复制的code
   - 点击绿色按钮 "Authorize GitHub"
   - 看到 "Successfully authenticated" 提示

4. **返回终端**：按Enter键完成

---

### 成功标志

看到以下信息表示登录成功：

```
✓ Authentication complete.
✓ Logged in as dopod2009
✓ Configured git protocol
✓ Configured git credential helper
```

---

## 🎯 登录成功后立即执行

推送代码到GitHub：

```bash
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
```

---

## ✅ 验证推送成功

访问您的GitHub仓库：
**https://github.com/dopod2009/calendar-app**

如果看到代码文件，说明推送成功！

---

## 🆘 如果遇到问题

### 问题1：终端没有反应

**解决方案**：
```bash
# 检查GitHub CLI是否安装
gh --version

# 如果未安装，执行
brew install gh
```

### 问题2：浏览器没有自动打开

**解决方案**：
- 手动打开浏览器
- 访问：https://github.com/login/device
- 输入终端显示的code

### 问题3：code过期

**解决方案**：
- 重新执行 `gh auth login`
- 在2分钟内完成授权

---

## 📊 整体流程时间表

| 步骤 | 时间 |
|------|------|
| 打开终端 | 10秒 |
| 执行登录命令 | 10秒 |
| 选择选项 | 30秒 |
| 浏览器授权 | 1分钟 |
| 推送代码 | 1分钟 |
| **总计** | **约3分钟** |

---

## 💡 提示

- 整个过程非常简单，按照提示操作即可
- 如果卡住，可以按 `Ctrl+C` 取消，重新执行
- 授权后可以随时使用 `gh` 命令操作GitHub

---

**现在请在您的终端执行 `gh auth login` 命令！**
