# Git推送到GitHub失败解决方案

## ❌ 错误信息

```
fatal: could not read Username for 'https://github.com': Device not configured
```

## 🔍 原因分析

Git使用HTTPS协议推送代码时，需要GitHub账号认证，但系统未配置凭据。

---

## ✅ 解决方案（3种方式，任选其一）

### 方案1: 使用SSH协议（推荐）

**优点**: 一次配置，永久使用，无需重复输入密码

**步骤**:

#### 1. 检查SSH密钥
```bash
ls -al ~/.ssh
# 查看是否有 id_rsa.pub 或 id_ed25519.pub
```

#### 2. 生成SSH密钥（如果没有）
```bash
ssh-keygen -t ed25519 -C "你的邮箱@example.com"
# 按Enter使用默认路径，设置密码（可选）
```

#### 3. 查看公钥
```bash
cat ~/.ssh/id_ed25519.pub
# 或
cat ~/.ssh/id_rsa.pub
```

#### 4. 添加到GitHub
```
1. 访问: https://github.com/settings/keys
2. 点击 "New SSH key"
3. Title: 填写 "MacBook" 或其他名称
4. Key: 粘贴步骤3复制的公钥内容
5. 点击 "Add SSH key"
```

#### 5. 测试SSH连接
```bash
ssh -T git@github.com
# 看到 "Hi dopod2009! ..." 表示成功
```

#### 6. 修改远程仓库URL为SSH
```bash
cd /Users/alex/WorkBuddy/20260309174106
git remote set-url origin git@github.com:dopod2009/calendar-app.git
git push -u origin main
```

---

### 方案2: 使用GitHub CLI（推荐）

**优点**: 图形化认证，简单快捷

**步骤**:

#### 1. 安装GitHub CLI
```bash
# macOS使用Homebrew
brew install gh

# 或访问官网下载
# https://cli.github.com/
```

#### 2. 登录GitHub
```bash
gh auth login
# 选择 GitHub.com
# 选择 HTTPS
# 选择 Login with a web browser
# 复制one-time code，按Enter打开浏览器
# 在浏览器中粘贴code并授权
```

#### 3. 推送代码
```bash
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
```

---

### 方案3: 使用Personal Access Token

**优点**: 适用于HTTPS协议

**步骤**:

#### 1. 创建Personal Access Token
```
1. 访问: https://github.com/settings/tokens
2. 点击 "Generate new token" → "Generate new token (classic)"
3. Note: 填写 "Calendar App Push"
4. Expiration: 选择 "No expiration" 或其他期限
5. Select scopes: 勾选 "repo" (完整仓库访问权限)
6. 点击 "Generate token"
7. ⚠️ 复制生成的token（只显示一次！）
```

#### 2. 使用Token推送

**方式A: 在推送时输入Token**
```bash
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
# Username: 输入你的GitHub用户名 (dopod2009)
# Password: 粘贴刚才复制的Personal Access Token
```

**方式B: 在URL中包含Token**
```bash
cd /Users/alex/WorkBuddy/20260309174106
git remote set-url origin https://你的TOKEN@github.com/dopod2009/calendar-app.git
git push -u origin main
```

**方式C: 配置Git凭据缓存**
```bash
# 配置凭据缓存（临时存储）
git config --global credential.helper cache

# 或配置永久存储（macOS Keychain）
git config --global credential.helper osxkeychain

# 然后推送
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
# 输入用户名和Token后会自动保存
```

---

## 🎯 推荐方案

根据您的使用习惯选择：

| 场景 | 推荐方案 | 理由 |
|------|---------|------|
| 长期开发 | **SSH协议** | 一次配置，永久使用 |
| 快速认证 | **GitHub CLI** | 图形化操作，最简单 |
| 临时推送 | **Personal Access Token** | 无需额外安装 |

---

## 📝 快速执行清单

### 使用SSH协议（推荐）

```bash
# 1. 生成SSH密钥
ssh-keygen -t ed25519 -C "你的邮箱@example.com"

# 2. 查看公钥
cat ~/.ssh/id_ed25519.pub

# 3. 添加到GitHub: https://github.com/settings/keys

# 4. 测试连接
ssh -T git@github.com

# 5. 修改仓库URL
cd /Users/alex/WorkBuddy/20260309174106
git remote set-url origin git@github.com:dopod2009/calendar-app.git

# 6. 推送
git push -u origin main
```

### 使用GitHub CLI（最简单）

```bash
# 1. 安装
brew install gh

# 2. 登录
gh auth login

# 3. 推送
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
```

---

## ✅ 推送成功后的下一步

推送成功后，您需要：

1. **创建签名密钥并配置GitHub Secrets**
   - 参考: `GITHUB-ACTIONS-QUICK-START.md`
   - 或: `GITHUB-ACTIONS-GUIDE.md`

2. **触发GitHub Actions构建**
   ```
   GitHub仓库 → Actions → Build Alpha APK → Run workflow
   ```

3. **下载APK**
   ```
   GitHub仓库 → Actions → 点击构建记录 → Artifacts → calendar-alpha-apk
   ```

---

## 🆘 需要帮助？

如果遇到其他问题：
- GitHub文档: https://docs.github.com/en/authentication
- SSH配置: https://docs.github.com/en/authentication/connecting-to-github-with-ssh
- GitHub CLI: https://cli.github.com/manual/
