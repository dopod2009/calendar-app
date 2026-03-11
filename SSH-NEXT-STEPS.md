# SSH配置 - 下一步操作

## ✅ 已完成

- ✅ SSH密钥已成功生成
- ✅ 远程仓库URL已修改为SSH协议
- ✅ 公钥已准备好

---

## 🔑 您的SSH公钥

```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIG+2peP1qyvJFQfjMEN9qM0bn8jUjcP/n8sMMGdDwLot hdmforlove@gmail.com
```

**请复制上面的公钥**（整行内容）

---

## 📋 下一步操作

### 步骤1: 添加SSH公钥到GitHub

#### 1.1 访问GitHub SSH设置页面

点击链接或复制到浏览器：

```
https://github.com/settings/ssh/new
```

#### 1.2 填写信息

| 字段 | 填写内容 |
|------|---------|
| **Title** | `MacBook Pro` 或任意名称 |
| **Key type** | `Authentication Key`（默认） |
| **Key** | 粘贴上面的公钥内容 |

#### 1.3 保存

点击绿色按钮 **"Add SSH key"**

可能需要输入GitHub密码确认。

---

### 步骤2: 测试SSH连接

添加公钥后，在终端执行：

```bash
ssh -T git@github.com
```

**如果提示**:
```
The authenticity of host 'github.com' can't be established.
...
Are you sure you want to continue connecting (yes/no/[fingerprint])?
```

**输入**: `yes` 然后按Enter

**成功标志**:
```
Hi dopod2009! You've successfully authenticated, but GitHub does not provide shell access.
```

---

### 步骤3: 推送代码到GitHub

测试连接成功后，执行：

```bash
cd /Users/alex/WorkBuddy/20260309174106
git push -u origin main
```

**成功标志**:
```
Enumerating objects: 250, done.
...
To git@github.com:dopod2009/calendar-app.git
 * [new branch]      main -> main
branch 'main' set up to track 'origin/main'.
```

---

## ✅ 验证推送成功

访问您的GitHub仓库：

```
https://github.com/dopod2009/calendar-app
```

如果看到代码文件，说明推送成功！

---

## 🎯 推送成功后的下一步

### 配置GitHub Actions自动构建APK

#### 1. 创建签名密钥

**需要Java环境**，在终端执行：

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
- 使用其他有Java的电脑
- 或使用GitHub Codespaces

---

#### 2. 配置GitHub Secrets

访问：
```
https://github.com/dopod2009/calendar-app/settings/secrets/actions
```

添加4个Secrets：

| Secret名称 | Secret值 |
|-----------|---------|
| `KEYSTORE_BASE64` | [步骤1生成的Base64字符串] |
| `KEYSTORE_PASSWORD` | `calendar2026alpha` |
| `KEY_PASSWORD` | `calendar2026alpha` |
| `KEY_ALIAS` | `calendar-key` |

---

#### 3. 触发构建

访问：
```
https://github.com/dopod2009/calendar-app/actions
```

点击 **Build Alpha APK** → **Run workflow**

---

#### 4. 下载APK

约9分钟后，从Actions页面下载APK。

---

## ⏱️ 时间估算

| 步骤 | 时间 |
|------|------|
| 添加公钥到GitHub | 1分钟 |
| 测试SSH连接 | 10秒 |
| 推送代码 | 1分钟 |
| **总计** | **约2分钟** |

---

## ✅ 完成清单

```
□ 已复制SSH公钥
□ 已添加到GitHub SSH Keys
□ ssh -T git@github.com 测试成功
□ 已成功推送代码到GitHub
□ 可以在GitHub网站看到代码
```

---

## 🎉 恭喜！

SSH配置完成后，之后所有Git操作都不需要再输入密码！

---

**现在请执行步骤1：添加SSH公钥到GitHub**
