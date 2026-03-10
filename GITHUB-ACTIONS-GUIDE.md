# GitHub Actions CI/CD 配置指南

## 📋 概述

本指南详细说明如何配置GitHub Actions自动构建Android APK，并在构建完成后直接下载APK文件。

---

## 🎯 功能特性

✅ **自动构建** - 推送代码或手动触发时自动构建APK  
✅ **自动签名** - 使用安全的GitHub Secrets存储签名密钥  
✅ **自动发布** - 可选自动创建GitHub Release  
✅ **PR构建** - Pull Request时自动构建Debug版本供测试  
✅ **单元测试** - 自动运行单元测试并生成报告  
✅ **Artifact存储** - APK保存30天，随时可下载

---

## 🚀 快速开始（5步完成配置）

### 步骤1: 创建GitHub仓库

```bash
# 1. 在GitHub上创建新仓库
# 访问: https://github.com/new

# 仓库名称: calendar-app
# 描述: Android日历应用
# 可见性: Public 或 Private

# 2. 初始化Git并推送代码
cd /Users/alex/WorkBuddy/20260309174106

git init
git add .
git commit -m "Initial commit: Calendar App Alpha v1.0.0"
git branch -M main
git remote add origin https://github.com/你的用户名/calendar-app.git
git push -u origin main
```

---

### 步骤2: 配置GitHub Secrets

**进入Settings页面**:
```
GitHub仓库 → Settings → Secrets and variables → Actions
```

**需要配置的4个Secrets**:

#### Secret 1: KEYSTORE_BASE64

**说明**: 签名密钥文件的Base64编码

**获取方式**:

**方法A: 使用脚本（推荐）**
```bash
# 在项目根目录执行
cd /Users/alex/WorkBuddy/20260309174106

# 如果已有密钥文件，运行脚本
./scripts/generate-keystore-base64.sh

# 如果没有密钥文件，先创建密钥（需要Java环境）
keytool -genkeypair -v \
  -alias calendar-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore keystore/calendar-release.jks \
  -storepass calendar2026alpha \
  -keypass calendar2026alpha \
  -dname "CN=Calendar App, OU=Development, O=Calendar Team, L=Beijing, ST=Beijing, C=CN"

# 然后生成Base64
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

**方法B: 手动生成（macOS/Linux）**
```bash
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

**方法C: 手动生成（Windows PowerShell）**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore\calendar-release.jks"))
```

**配置**:
```
Name: KEYSTORE_BASE64
Value: [粘贴生成的Base64字符串]
```

---

#### Secret 2: KEYSTORE_PASSWORD

```
Name: KEYSTORE_PASSWORD
Value: calendar2026alpha
```

---

#### Secret 3: KEY_PASSWORD

```
Name: KEY_PASSWORD
Value: calendar2026alpha
```

---

#### Secret 4: KEY_ALIAS

```
Name: KEY_ALIAS
Value: calendar-key
```

---

### 步骤3: 推送代码到GitHub

```bash
# 确保所有文件已提交
git add .
git commit -m "Add GitHub Actions workflow"
git push
```

---

### 步骤4: 触发构建

**方式A: 手动触发**
```
GitHub仓库 → Actions → Build Alpha APK → Run workflow → Run workflow
```

**方式B: 推送代码**
```bash
git commit --allow-empty -m "Trigger build"
git push
```

---

### 步骤5: 下载APK

**下载方式**:

**方式1: 从Artifacts下载（推荐）**
```
GitHub仓库 → Actions → 点击构建记录 → Artifacts → calendar-alpha-apk
```

**方式2: 从Releases下载（如果启用了自动发布）**
```
GitHub仓库 → Releases → 最新Release → Assets → calendar-app-v1.0.0-alpha.apk
```

---

## 📁 文件结构说明

```
.github/
└── workflows/
    ├── build-alpha.yml      # 主构建流程（Release APK）
    ├── build-pr.yml         # PR构建流程（Debug APK）
    └── test.yml             # 单元测试流程

scripts/
└── generate-keystore-base64.sh  # 密钥Base64生成脚本
```

---

## 🔧 工作流说明

### 1. build-alpha.yml（主构建流程）

**触发条件**:
- 手动触发（workflow_dispatch）
- 推送到main或develop分支
- Pull Request到main分支

**执行步骤**:
1. 检出代码
2. 设置JDK 17
3. 设置Android SDK
4. 创建签名密钥
5. 构建Release APK
6. 上传APK到Artifacts
7. （可选）创建GitHub Release

**产物**:
- APK文件名: `calendar-app-v1.0.0-alpha.apk`
- 存储时间: 30天
- 存储位置: Actions → 构建记录 → Artifacts

---

### 2. build-pr.yml（PR构建流程）

**触发条件**:
- Pull Request到main或develop分支

**产物**:
- Debug APK（未签名）
- 存储时间: 7天
- 自动在PR中评论下载链接

---

### 3. test.yml（测试流程）

**触发条件**:
- 推送到main或develop分支
- Pull Request到main分支

**执行内容**:
- 运行单元测试
- 生成测试报告
- 上传测试结果

---

## 🔐 安全最佳实践

### ✅ 已实施的安全措施

1. **签名密钥加密存储**
   - 使用GitHub Secrets存储敏感信息
   - Base64编码传输，安全可靠
   - 不会出现在代码仓库中

2. **最小权限原则**
   - 使用官方GitHub Actions
   - 仅请求必要的权限

3. **密钥隔离**
   - 存储密码和密钥密码分开存储
   - 可以随时更换密钥

---

### ⚠️ 安全建议

1. **定期更换签名密钥**
   - Alpha/Beta/Release使用不同密钥
   - 正式发布前更换生产密钥

2. **限制仓库访问权限**
   - 仅信任的协作者可访问
   - 启用分支保护规则

3. **监控构建日志**
   - 检查是否有敏感信息泄露
   - Secrets会被自动隐藏

---

## 📊 构建时间估算

| 步骤 | 时间 |
|------|------|
| 检出代码 | 30秒 |
| 设置JDK | 1分钟 |
| 设置Android SDK | 2分钟 |
| Gradle同步 | 3分钟 |
| 构建APK | 2分钟 |
| 上传Artifacts | 30秒 |
| **总计** | **约9分钟** |

---

## 🎨 自定义配置

### 修改版本号

编辑 `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        versionCode = 2
        versionName = "1.0.1-alpha"
    }
}
```

### 修改构建触发条件

编辑 `.github/workflows/build-alpha.yml`:
```yaml
on:
  push:
    branches: [ main, develop, release/* ]
  workflow_dispatch:
    inputs:
      build_type:
        description: 'Build type'
        required: true
        default: 'release'
        type: choice
        options:
        - release
        - debug
```

### 添加通知功能

在 `build-alpha.yml` 末尾添加:
```yaml
- name: Send notification on success
  if: success()
  run: |
    curl -X POST "${{ secrets.WEBHOOK_URL }}" \
      -H "Content-Type: application/json" \
      -d '{"text": "✅ APK构建成功！下载地址: ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}"}'
```

---

## 🐛 常见问题

### Q1: 构建失败提示 "Could not find com.android.tools.build:gradle"

**原因**: Gradle依赖下载失败

**解决方案**:
- 检查网络连接
- GitHub Actions默认可以访问Google Maven仓库
- 如果持续失败，可以配置镜像源

---

### Q2: 构建失败提示 "Keystore was tampered with"

**原因**: Base64解码错误或密码错误

**解决方案**:
```bash
# 重新生成Base64编码
base64 -i keystore/calendar-release.jks | tr -d '\n'

# 更新GitHub Secret: KEYSTORE_BASE64
```

---

### Q3: APK下载后无法安装

**原因**: APK未正确签名

**解决方案**:
- 检查签名密钥是否正确配置
- 检查KEYSTORE_PASSWORD和KEY_PASSWORD是否正确
- 查看构建日志中的签名步骤

---

### Q4: 如何查看构建日志？

**步骤**:
```
GitHub仓库 → Actions → 点击构建记录 → 点击各个步骤查看详细日志
```

---

### Q5: Artifacts在哪里下载？

**步骤**:
```
GitHub仓库 → Actions → 点击构建记录 → 页面底部 "Artifacts" 区域
```

**注意**: 需要登录GitHub账号才能下载Artifacts

---

## 📱 发布给内测用户

### 方式1: 直接分享Artifacts链接

```
1. 完成构建后
2. 进入 Actions 页面
3. 点击构建记录
4. 复制浏览器地址栏URL
5. 发送给测试用户

测试用户访问URL后：
- 登录GitHub账号
- 下载 Artifacts
- 解压获得APK
```

### 方式2: 使用GitHub Release

**已在工作流中配置，手动触发时会自动创建Release**

```
GitHub仓库 → Releases → 最新Release → 复制APK下载链接
```

### 方式3: 使用Firebase App Distribution

**在工作流中添加**:
```yaml
- name: Upload to Firebase App Distribution
  uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    serviceCredentialsFileContent: ${{ secrets.FIREBASE_CREDENTIALS }}
    groups: testers
    file: app/build/outputs/apk/release/calendar-app-v1.0.0-alpha.apk
```

---

## ✅ 配置检查清单

```
□ GitHub仓库已创建
□ 代码已推送到GitHub
□ KEYSTORE_BASE64已配置
□ KEYSTORE_PASSWORD已配置
□ KEY_PASSWORD已配置
□ KEY_ALIAS已配置
□ 工作流文件已推送
□ 手动触发构建成功
□ APK可以正常下载
□ APK可以正常安装
```

---

## 🎉 完成！

按照以上步骤配置完成后：

✅ 每次推送代码都会自动构建APK  
✅ 可以随时从GitHub下载APK  
✅ 支持自动发布Release  
✅ 所有流程完全自动化  

**预计配置时间**: 15-20分钟  
**每次构建时间**: 约9分钟  

---

## 📞 需要帮助？

- GitHub Actions文档: https://docs.github.com/en/actions
- Android构建文档: https://developer.android.com/build
- 问题反馈: 在GitHub仓库创建Issue
