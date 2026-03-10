# GitHub Actions 快速配置清单

## 🚀 5步完成配置

### ✅ Step 1: 创建GitHub仓库并推送代码

```bash
# 1. 在GitHub创建新仓库
# 访问: https://github.com/new
# 仓库名: calendar-app

# 2. 推送代码
cd /Users/alex/WorkBuddy/20260309174106

git init
git add .
git commit -m "Initial commit: Calendar App Alpha v1.0.0"
git branch -M main
git remote add origin https://github.com/你的用户名/calendar-app.git
git push -u origin main
```

---

### ✅ Step 2: 创建签名密钥（如果没有Java环境，跳过此步）

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
- 使用其他有Java环境的电脑创建密钥文件
- 或者使用在线工具生成密钥
- 或者使用GitHub Codespaces创建密钥

---

### ✅ Step 3: 配置GitHub Secrets

进入: `GitHub仓库 → Settings → Secrets and variables → Actions`

添加以下4个Secrets:

| Secret名称 | Secret值 | 说明 |
|-----------|---------|------|
| `KEYSTORE_BASE64` | [步骤2生成的Base64字符串] | 签名密钥Base64编码 |
| `KEYSTORE_PASSWORD` | `calendar2026alpha` | 密钥库密码 |
| `KEY_PASSWORD` | `calendar2026alpha` | 密钥密码 |
| `KEY_ALIAS` | `calendar-key` | 密钥别名 |

---

### ✅ Step 4: 触发构建

**方式A: 手动触发（推荐）**
```
GitHub仓库 → Actions → Build Alpha APK → Run workflow → Run workflow
```

**方式B: 推送代码**
```bash
git commit --allow-empty -m "Trigger build"
git push
```

---

### ✅ Step 5: 下载APK

```
GitHub仓库 → Actions → 点击构建记录 → Artifacts → calendar-alpha-apk
```

**下载后**: 解压zip文件获得 `calendar-app-v1.0.0-alpha.apk`

---

## 📋 详细配置说明

完整配置说明请查看: [GITHUB-ACTIONS-GUIDE.md](./GITHUB-ACTIONS-GUIDE.md)

---

## ⏱️ 时间估算

| 步骤 | 时间 |
|------|------|
| 创建仓库并推送 | 2分钟 |
| 创建签名密钥 | 2分钟 |
| 配置Secrets | 3分钟 |
| 首次构建 | 9分钟 |
| **总计** | **约16分钟** |

---

## 🎯 成功标志

✅ Actions页面显示绿色对勾  
✅ 可以下载calendar-alpha-apk.zip  
✅ 解压后获得APK文件  
✅ APK可以安装到Android设备  

---

## 🆘 遇到问题？

查看详细文档: [GITHUB-ACTIONS-GUIDE.md](./GITHUB-ACTIONS-GUIDE.md)

常见问题章节有详细的问题排查步骤。
