# GitHub Actions 配置 - 立即开始

## ✅ 代码已成功推送

您的代码已在GitHub上：
https://github.com/dopod2009/calendar-app

---

## 🚀 配置GitHub Actions自动构建APK

### 步骤1: 创建签名密钥

**前提条件**: 需要Java JDK环境

#### 检查Java环境

在终端执行：
```bash
java -version
```

**如果显示版本号**（如 `java version "17.0.x"`）:
- ✅ 有Java环境，继续下一步

**如果提示 `command not found`**:
- ❌ 没有Java环境，需要先安装
- 安装方式：`brew install openjdk@17`

---

#### 创建签名密钥（有Java环境）

在终端执行：
```bash
cd /Users/alex/WorkBuddy/20260309174106

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

**预计时间**: 10秒

---

#### 生成Base64编码

密钥创建成功后，执行：
```bash
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

**复制输出的Base64字符串**（很长的字符串）

---

### 步骤2: 配置GitHub Secrets

#### 访问Secrets设置页面

```
https://github.com/dopod2009/calendar-app/settings/secrets/actions
```

#### 添加4个Secrets

点击 **"New repository secret"** 按钮，依次添加：

---

#### Secret 1: KEYSTORE_BASE64

| 字段 | 值 |
|------|-----|
| **Name** | `KEYSTORE_BASE64` |
| **Secret** | [步骤1生成的Base64字符串] |

---

#### Secret 2: KEYSTORE_PASSWORD

| 字段 | 值 |
|------|-----|
| **Name** | `KEYSTORE_PASSWORD` |
| **Secret** | `calendar2026alpha` |

---

#### Secret 3: KEY_PASSWORD

| 字段 | 值 |
|------|-----|
| **Name** | `KEY_PASSWORD` |
| **Secret** | `calendar2026alpha` |

---

#### Secret 4: KEY_ALIAS

| 字段 | 值 |
|------|-----|
| **Name** | `KEY_ALIAS` |
| **Secret** | `calendar-key` |

---

### 步骤3: 触发构建

#### 访问Actions页面

```
https://github.com/dopod2009/calendar-app/actions
```

#### 触发工作流

1. 点击 **"Build Alpha APK"**
2. 点击 **"Run workflow"** 按钮
3. 选择 `main` 分支
4. 点击绿色 **"Run workflow"** 按钮

---

### 步骤4: 等待构建完成

**构建时间**: 约9分钟

**监控构建**:
- 在Actions页面可以看到构建进度
- 点击构建记录查看详细日志
- 各步骤会显示绿色对勾✓表示成功

---

### 步骤5: 下载APK

构建完成后：

1. 在Actions页面点击最新的构建记录
2. 滚动到页面底部 **"Artifacts"** 区域
3. 点击 **`calendar-alpha-apk`** 下载
4. 解压zip文件获得 `calendar-app-v1.0.0-alpha.apk`

---

## 📊 时间估算

| 步骤 | 时间 |
|------|------|
| 创建签名密钥 | 10秒 |
| 生成Base64 | 5秒 |
| 配置4个Secrets | 2分钟 |
| 触发构建 | 10秒 |
| 等待构建 | 9分钟 |
| 下载APK | 10秒 |
| **总计** | **约12分钟** |

---

## ✅ 完成清单

```
□ 已检查Java环境
□ 已创建签名密钥
□ 已生成Base64编码
□ 已配置KEYSTORE_BASE64
□ 已配置KEYSTORE_PASSWORD
□ 已配置KEY_PASSWORD
□ 已配置KEY_ALIAS
□ 已触发构建
□ 已下载APK
```

---

## 🎉 完成！

约12分钟后，您将获得可以安装的APK文件！

---

## 🆘 没有Java环境怎么办？

### 方式1: 安装Java（推荐）

```bash
brew install openjdk@17
```

### 方式2: 使用GitHub Codespaces

1. 访问：https://github.com/codespaces
2. 创建新的Codespace
3. 在Codespace中创建密钥
4. 下载密钥文件到本地

### 方式3: 使用其他电脑

在有Java环境的电脑上创建密钥，然后传输Base64编码。

---

**现在请先检查您的Java环境：`java -version`**
