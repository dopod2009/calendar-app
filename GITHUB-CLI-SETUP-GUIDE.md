# GitHub CLI 快速配置指南

## 🚀 完整操作步骤

### 步骤1: 安装GitHub CLI

**打开终端，执行以下命令**:

```bash
brew install gh
```

**预计时间**: 2-5分钟（取决于网络速度）

**如果遇到问题**:
- 确保已安装Homebrew
- 如果没有Homebrew，访问 https://brew.sh 安装
- 或直接下载GitHub CLI: https://cli.github.com/

---

### 步骤2: 登录GitHub

**执行登录命令**:

```bash
gh auth login
```

**按照提示操作**:

```
? What account do you want to log into?
  > GitHub.com        # 选择这个

? What is your preferred protocol for Git operations?
  > HTTPS            # 选择这个（推荐）
  > SSH

? Authenticate Git with your GitHub HTTP credentials?
  > Yes              # 选择Yes

? How would you like to authenticate GitHub CLI?
  > Login with a web browser    # 选择这个（最简单）
  > Paste an authentication token
```

**浏览器登录流程**:

```
! First copy your one-time code: XXXX-XXXX
  Press Enter to open github.com in your browser...

# 1. 按Enter会自动打开浏览器
# 2. 在浏览器中粘贴刚才显示的code (XXXX-XXXX)
# 3. 点击 "Authorize GitHub" 授权
# 4. 看到成功提示后，返回终端
# 5. 继续按Enter完成登录

✓ Authentication complete.
✓ Logged in as dopod2009
```

**预计时间**: 1-2分钟

---

### 步骤3: 推送代码到GitHub

**登录成功后，推送代码**:

```bash
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
```

**成功输出示例**:

```
Enumerating objects: 250, done.
Counting objects: 100% (250/250), done.
Delta compression using up to 8 threads
Compressing objects: 100% (200/200), done.
Writing objects: 100% (250/250), 1.50 MiB | 3.20 MiB/s, done.
Total 250 (delta 50), reused 0 (delta 0), pack-reused 0
remote: Resolving deltas: 100% (50/50), done.
To https://github.com/dopod2009/calendar-app.git
 * [new branch]      main -> main
branch 'main' set up to track 'origin/main'.
```

**预计时间**: 30秒-2分钟（取决于网络速度）

---

### 步骤4: 验证推送成功

**访问GitHub仓库**:

```
https://github.com/dopod2009/calendar-app
```

**检查项**:
- ✅ 仓库中有代码文件
- ✅ 可以看到 `GITHUB-ACTIONS-QUICK-START.md` 文件
- ✅ 可以看到 `.github/workflows` 目录
- ✅ 提交记录显示 "Initial commit: Calendar App Alpha v1.0.0"

---

## ✅ 完成清单

```
□ 已安装GitHub CLI (gh --version)
□ 已登录GitHub账号 (gh auth status)
□ 已推送代码到GitHub (git push成功)
□ 可以在GitHub网站看到代码
```

---

## 🎯 推送成功后的下一步

### 立即配置GitHub Actions

按照 `GITHUB-ACTIONS-QUICK-START.md` 文档操作：

#### 1. 创建签名密钥

**如果您的电脑有Java环境**:

```bash
cd /Users/alex/WorkBuddy/20260309174106

# 创建密钥
keytool -genkeypair -v \
  -alias calendar-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore keystore/calendar-release.jks \
  -storepass calendar2026alpha \
  -keypass calendar2026alpha \
  -dname "CN=Calendar App, OU=Development, O=Calendar Team, L=Beijing, ST=Beijing, C=CN"

# 生成Base64编码
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

**如果没有Java环境**:

使用其他有Java环境的电脑，或者使用GitHub Codespaces创建密钥。

---

#### 2. 配置GitHub Secrets

访问: `https://github.com/dopod2009/calendar-app/settings/secrets/actions`

点击 "New repository secret"，添加以下4个Secrets:

| Secret名称 | Secret值 |
|-----------|---------|
| `KEYSTORE_BASE64` | [步骤1生成的Base64字符串] |
| `KEYSTORE_PASSWORD` | `calendar2026alpha` |
| `KEY_PASSWORD` | `calendar2026alpha` |
| `KEY_ALIAS` | `calendar-key` |

---

#### 3. 触发构建

访问: `https://github.com/dopod2009/calendar-app/actions`

点击 "Build Alpha APK" → "Run workflow" → "Run workflow"

---

#### 4. 下载APK

构建完成后（约9分钟）:

访问: `https://github.com/dopod2009/calendar-app/actions`

点击最新的构建记录 → Artifacts → `calendar-alpha-apk`

---

## 🆘 常见问题

### Q1: brew install gh 很慢

**解决方案**:
- 使用国内镜像源
- 或直接下载安装包: https://cli.github.com/

### Q2: gh auth login 报错

**解决方案**:
```bash
# 检查GitHub CLI版本
gh --version

# 手动清除缓存重试
rm -rf ~/.config/gh
gh auth login
```

### Q3: git push 仍然失败

**解决方案**:
```bash
# 检查认证状态
gh auth status

# 如果未认证，重新登录
gh auth login

# 如果已认证，尝试刷新token
gh auth refresh
```

---

## 📞 需要帮助？

- GitHub CLI文档: https://cli.github.com/manual/
- GitHub认证文档: https://docs.github.com/en/authentication

---

**💡 提示**: 完成GitHub CLI登录后，您可以随时使用 `gh` 命令操作GitHub，非常方便！
