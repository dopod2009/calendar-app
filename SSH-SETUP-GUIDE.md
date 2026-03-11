# SSH协议配置完整指南

## 🎯 目标

使用SSH协议推送代码到GitHub，无需每次输入密码。

---

## 📋 完整步骤（5步完成）

### 步骤1: 检查现有SSH密钥

**在终端执行**:

```bash
ls -al ~/.ssh
```

**可能的结果**:

| 文件名 | 说明 |
|--------|------|
| `id_rsa.pub` 或 `id_ed25519.pub` | ✅ 已有SSH密钥，跳到步骤3 |
| 无文件或目录不存在 | ❌ 需要生成新密钥，继续步骤2 |

---

### 步骤2: 生成新的SSH密钥

**在终端执行**:

```bash
ssh-keygen -t ed25519 -C "你的邮箱@example.com"
```

**按照提示操作**:

```
Generating public/private ed25519 key pair.
Enter file in which to save the key (/Users/alex/.ssh/id_ed25519): 
# 按Enter使用默认路径

Enter passphrase (empty for no passphrase): 
# 直接按Enter（不设置密码）或输入密码

Enter same passphrase again: 
# 再次按Enter或重复密码

Your identification has been saved in /Users/alex/.ssh/id_ed25519
Your public key has been saved in /Users/alex/.ssh/id_ed25519.pub
```

**预计时间**: 10秒

---

### 步骤3: 查看并复制SSH公钥

**在终端执行**:

```bash
cat ~/.ssh/id_ed25519.pub
```

**或（如果使用旧版RSA密钥）**:

```bash
cat ~/.ssh/id_rsa.pub
```

**输出示例**:

```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBxxxxxxxxx... 你的邮箱@example.com
```

**操作**:
- 选中整行输出内容
- 按 `Command+C` 复制

---

### 步骤4: 添加SSH公钥到GitHub

#### 4.1 访问GitHub SSH设置页面

**打开浏览器访问**:

```
https://github.com/settings/ssh/new
```

#### 4.2 添加SSH Key

**填写信息**:

| 字段 | 填写内容 |
|------|---------|
| **Title** | `MacBook Pro` 或 `我的Mac` （任意名称） |
| **Key type** | `Authentication Key` |
| **Key** | 粘贴步骤3复制的公钥内容 |

**点击**: 绿色按钮 "Add SSH key"

**可能需要**: 输入GitHub密码确认

---

### 步骤5: 测试SSH连接

**在终端执行**:

```bash
ssh -T git@github.com
```

**首次连接提示**:

```
The authenticity of host 'github.com' (140.82.112.4)' can't be established.
ED25519 key fingerprint is SHA256:xxxxx.
This key is not known by any other names.
Are you sure you want to continue connecting (yes/no/[fingerprint])?
```

**输入**: `yes` 然后按Enter

**成功输出**:

```
Hi dopod2009! You've successfully authenticated, but GitHub does not provide shell access.
```

**如果看到这个提示，说明SSH配置成功！** ✅

---

## 🚀 推送代码到GitHub

### 修改远程仓库URL为SSH

**在终端执行**:

```bash
cd /Users/alex/WorkBuddy/20260309174106
git remote set-url origin git@github.com:dopod2009/calendar-app.git
```

### 推送代码

**在终端执行**:

```bash
git push -u origin main
```

**成功输出**:

```
Enumerating objects: 250, done.
Counting objects: 100% (250/250), done.
Delta compression using up to 8 threads
Compressing objects: 100% (200/200), done.
Writing objects: 100% (250/250), 1.50 MiB | 3.20 MiB/s, done.
Total 250 (delta 50), reused 0 (delta 0), pack-reused 0
remote: Resolving deltas: 100% (50/50), done.
To git@github.com:dopod2009/calendar-app.git
 * [new branch]      main -> main
branch 'main' set up to track 'origin/main'.
```

---

## ✅ 验证推送成功

**访问您的GitHub仓库**:

```
https://github.com/dopod2009/calendar-app
```

**检查项**:

- ✅ 可以看到代码文件
- ✅ 可以看到 `GITHUB-ACTIONS-QUICK-START.md`
- ✅ 可以看到 `.github/workflows` 目录
- ✅ 提交记录显示 "Initial commit: Calendar App Alpha v1.0.0"

---

## 🎯 推送成功后的下一步

### 立即配置GitHub Actions自动构建APK

#### 1. 创建签名密钥（需要Java环境）

**在终端执行**:

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
- 使用其他有Java环境的电脑创建密钥
- 或使用GitHub Codespaces创建

---

#### 2. 配置GitHub Secrets

**访问**:

```
https://github.com/dopod2009/calendar-app/settings/secrets/actions
```

**点击**: "New repository secret"

**添加4个Secrets**:

| Secret名称 | Secret值 |
|-----------|---------|
| `KEYSTORE_BASE64` | [步骤1生成的Base64字符串] |
| `KEYSTORE_PASSWORD` | `calendar2026alpha` |
| `KEY_PASSWORD` | `calendar2026alpha` |
| `KEY_ALIAS` | `calendar-key` |

---

#### 3. 触发构建

**访问**:

```
https://github.com/dopod2009/calendar-app/actions
```

**操作**:
- 点击 "Build Alpha APK"
- 点击 "Run workflow"
- 点击绿色 "Run workflow" 按钮

---

#### 4. 下载APK

**等待约9分钟构建完成后**:

- 访问: https://github.com/dopod2009/calendar-app/actions
- 点击最新的构建记录
- 页面底部 "Artifacts" 区域
- 点击 `calendar-alpha-apk` 下载

---

## 🐛 常见问题

### Q1: ssh-keygen 报错 "command not found"

**解决方案**: OpenSSH通常已预装在macOS中

```bash
# 检查SSH是否安装
which ssh-keygen

# 如果未安装，使用Xcode命令行工具
xcode-select --install
```

---

### Q2: ssh -T git@github.com 提示 "Permission denied"

**解决方案**:

```bash
# 检查SSH Agent是否运行
eval "$(ssh-agent -s)"

# 添加SSH密钥到Agent
ssh-add ~/.ssh/id_ed25519

# 再次测试
ssh -T git@github.com
```

---

### Q3: 推送时提示 "Connection refused"

**解决方案**:

```bash
# 检查SSH配置
cat ~/.ssh/config

# 如果文件不存在，创建配置
mkdir -p ~/.ssh
cat > ~/.ssh/config << EOF
Host github.com
  HostName github.com
  User git
  IdentityFile ~/.ssh/id_ed25519
  IdentitiesOnly yes
EOF

# 设置权限
chmod 600 ~/.ssh/config

# 再次测试
ssh -T git@github.com
```

---

### Q4: 已有SSH密钥但忘记是否添加到GitHub

**解决方案**:

```bash
# 查看公钥
cat ~/.ssh/id_ed25519.pub

# 或查看所有公钥
ls ~/.ssh/*.pub

# 访问GitHub检查
# https://github.com/settings/keys
```

---

## ⏱️ 时间估算

| 步骤 | 时间 |
|------|------|
| 检查/生成SSH密钥 | 30秒 |
| 复制公钥 | 10秒 |
| 添加到GitHub | 1分钟 |
| 测试连接 | 10秒 |
| 推送代码 | 1分钟 |
| **总计** | **约3分钟** |

---

## ✅ 配置完成检查清单

```
□ 已生成SSH密钥（id_ed25519.pub）
□ 已复制公钥内容
□ 已添加到GitHub SSH Keys
□ ssh -T git@github.com 测试成功
□ 已修改远程仓库URL为SSH
□ 已成功推送代码到GitHub
□ 可以在GitHub网站看到代码
```

---

## 🎉 完成！

SSH配置是一次性的，之后所有Git操作都不需要再输入密码！

---

## 📞 需要帮助？

- GitHub SSH文档: https://docs.github.com/en/authentication/connecting-to-github-with-ssh
- SSH密钥生成: https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent

---

**现在请按照步骤1-5操作，完成后告诉我结果！**
