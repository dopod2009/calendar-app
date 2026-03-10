# 📦 Alpha版本打包与发布指南

## 📋 版本信息

- **版本名称**: v1.0.0-alpha
- **版本号**: 1
- **发布日期**: 2026年3月10日
- **发布类型**: Alpha（内部测试版）

---

## 🔐 签名配置

### 1. 创建签名密钥

#### Debug签名（开发调试）
```bash
cd /Users/alex/WorkBuddy/20260309174106/keystore
keytool -genkey -v -keystore calendar-debug.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias calendar-debug-key \
  -storepass calendar_debug_2026 \
  -keypass calendar_debug_2026 \
  -dname "CN=Calendar Debug, OU=Development, O=Calendar App, L=Beijing, ST=Beijing, C=CN"
```

#### Release签名（正式发布）
```bash
cd /Users/alex/WorkBuddy/20260309174106/keystore
keytool -genkey -v -keystore calendar-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias calendar-key \
  -storepass calendar_2026_release \
  -keypass calendar_2026_release \
  -dname "CN=Calendar App, OU=Production, O=Calendar App Team, L=Beijing, ST=Beijing, C=CN"
```

**⚠️ 重要提示**:
- 请妥善保管签名密钥文件（.jks）
- 不要将签名密码提交到版本控制系统
- Release签名密钥用于所有后续版本更新

---

## 🏗️ 构建APK

### 方法1: 使用Android Studio（推荐）

1. **打开项目**
   ```bash
   cd /Users/alex/WorkBuddy/20260309174106
   open -a "Android Studio" .
   ```

2. **生成签名APK**
   - 菜单: Build → Generate Signed Bundle / APK
   - 选择: APK
   - 选择签名密钥: `keystore/calendar-release.jks`
   - 输入密码: `calendar_2026_release`
   - 选择构建变体: `release`
   - 勾选: V1 (Jar Signature) 和 V2 (Full APK Signature)
   - 点击 Finish

3. **APK输出位置**
   ```
   app/release/app-release.apk
   ```

### 方法2: 使用命令行

```bash
# 进入项目目录
cd /Users/alex/WorkBuddy/20260309174106

# 清理构建
./gradlew clean

# 构建Release APK
./gradlew assembleRelease

# APK输出位置
# app/build/outputs/apk/release/app-release.apk
```

### 方法3: 构建App Bundle（AAB，用于Google Play）

```bash
# 构建AAB
./gradlew bundleRelease

# AAB输出位置
# app/build/outputs/bundle/release/app-release.aab
```

---

## 📊 APK信息

### 预期APK大小

| 构建类型 | 预估大小 | 说明 |
|---------|---------|------|
| Debug APK | ~15-18 MB | 包含调试信息，未压缩 |
| Release APK | ~8-12 MB | 已压缩优化，已混淆 |
| Release AAB | ~10-14 MB | Google Play上传格式 |

### APK内容

- ✅ 主应用模块（app）
- ✅ 核心功能模块（core）
- ✅ 特性模块（feature）
- ✅ 第三方库依赖
- ✅ 资源文件（图片、布局、字符串）
- ✅ Native库（如果有）

---

## 🔍 APK验证

### 1. 验证签名

```bash
# 使用apksigner验证
apksigner verify --print-certs app-release.apk

# 或使用jarsigner
jarsigner -verify -verbose -certs app-release.apk
```

### 2. 查看APK信息

```bash
# 使用aapt查看
aapt dump badging app-release.apk

# 查看版本信息
aapt dump badging app-release.apk | grep version

# 查看权限
aapt dump permissions app-release.apk
```

### 3. 反编译检查（可选）

```bash
# 使用apktool
apktool d app-release.apk -o app-extracted

# 检查混淆是否生效
ls app-extracted/smali*/
```

---

## 📱 测试APK安装

### 1. 安装到测试设备

```bash
# 通过ADB安装
adb install -r app-release.apk

# 或通过ADB指定设备
adb -s <device-id> install -r app-release.apk
```

### 2. 验证安装

```bash
# 查看已安装应用
adb shell pm list packages | grep calendar

# 查看应用版本
adb shell dumpsys package com.calendar.app | grep versionName

# 启动应用
adb shell am start -n com.calendar.app/.MainActivity
```

---

## 🚀 发布Alpha版本

### 1. 准备发布包

创建发布目录结构:
```
calendar-alpha-v1.0.0/
├── app-release.apk              # 主APK文件
├── CHANGELOG.md                 # 更新日志
├── RELEASE-NOTES.md            # 发布说明
├── KNOWN-ISSUES.md             # 已知问题
├── INSTALLATION-GUIDE.md       # 安装指南
└── qa/                         # 测试相关
    ├── test-cases.md           # 测试用例
    ├── test-report-template.md # 测试报告模板
    └── feedback-form.md        # 反馈表单
```

### 2. 生成发布包

```bash
# 创建发布目录
mkdir -p calendar-alpha-v1.0.0

# 复制APK
cp app/build/outputs/apk/release/app-release.apk calendar-alpha-v1.0.0/

# 生成MD5校验
cd calendar-alpha-v1.0.0
md5sum app-release.apk > app-release.apk.md5

# 生成SHA256校验
sha256sum app-release.apk > app-release.apk.sha256

# 打包
cd ..
tar -czf calendar-alpha-v1.0.0.tar.gz calendar-alpha-v1.0.0/
```

---

## 📧 分发给测试用户

### 方式1: 通过邮件分发

```
主题: 【内部测试】日历应用Alpha版本发布邀请

亲爱的测试用户：

您好！

我们诚挚地邀请您参与日历应用Alpha版本的内部测试。

📋 版本信息:
- 版本: v1.0.0-alpha
- 发布日期: 2026年3月10日
- 测试周期: 2周（2026.3.10 - 2026.3.24）

📱 安装方式:
1. 下载附件中的APK文件
2. 在Android设备上启用"未知来源应用安装"
3. 点击APK文件进行安装

⚠️ 注意事项:
- 本版本为Alpha测试版，可能存在不稳定因素
- 请勿在生产环境中使用
- 建议在测试设备上安装

📝 测试重点:
1. 日历视图功能（月/周/日视图）
2. 事件管理功能（创建/编辑/删除）
3. 提醒通知功能
4. 数据同步功能

📧 问题反馈:
- 邮件: test@calendar-app.com
- 反馈表单: https://forms.calendar-app.com/alpha-feedback

感谢您的参与和支持！

日历应用开发团队
2026年3月10日
```

### 方式2: 通过测试平台分发

**推荐平台**:
1. **Firebase App Distribution**
   ```bash
   # 上传APK到Firebase
   firebase appdistribution:distribute app-release.apk \
     --app YOUR_APP_ID \
     --token YOUR_FIREBASE_TOKEN \
     --testers "tester1@email.com, tester2@email.com" \
     --release-notes "Alpha版本首次发布"
   ```

2. **TestFlight**（仅iOS）
   - 不适用于Android

3. **蒲公英/pgyer.com**
   - 国内常用测试平台
   - 上传APK后生成下载链接

4. **fir.im**
   - 国内测试平台
   - 支持二维码下载

### 方式3: 通过云存储分发

```bash
# 上传到云存储（示例）
# 阿里云OSS
ossutil cp app-release.apk oss://calendar-app-releases/alpha/

# 腾讯云COS  
coscmd upload app-release.apk /releases/alpha/

# 生成分享链接
# 用户通过链接直接下载
```

---

## 📝 发布清单

### 发布前检查

- [ ] APK已使用Release签名
- [ ] 版本号正确（versionCode: 1, versionName: 1.0.0-alpha）
- [ ] 代码已混淆（ProGuard已启用）
- [ ] 资源已压缩
- [ ] 日志已关闭（DEBUG_MODE = false）
- [ ] API地址已配置为生产环境
- [ ] 已知问题已记录
- [ ] 更新日志已编写

### 发布后检查

- [ ] APK已在测试设备上成功安装
- [ ] 应用启动正常
- [ ] 核心功能可用
- [ ] 测试用户已收到通知
- [ ] 反馈渠道已建立

---

## 📊 版本管理

### 版本命名规范

```
格式: {major}.{minor}.{patch}-{stage}

示例:
- 1.0.0-alpha  (Alpha测试版)
- 1.0.0-beta.1 (Beta测试版1)
- 1.0.0-rc.1   (Release Candidate 1)
- 1.0.0        (正式版)
- 1.0.1        (Bug修复版)
- 1.1.0        (功能更新版)
- 2.0.0        (重大更新版)
```

### 版本号规则

- **Major**: 重大功能变更或架构重构
- **Minor**: 新增功能或功能增强
- **Patch**: Bug修复或小优化
- **Stage**: 
  - alpha: 内部测试
  - beta: 公开测试
  - rc: 候选发布
  - 无后缀: 正式发布

---

## 🔄 后续版本更新

### 更新流程

1. 修改 `versionCode` 和 `versionName`
2. 更新 `CHANGELOG.md`
3. 重新构建APK
4. 分发给测试用户
5. 收集反馈并修复问题

### 示例：从1.0.0-alpha到1.0.0-beta.1

```kotlin
// build.gradle.kts
versionCode = 2
versionName = "1.0.0-beta.1"
```

```markdown
# CHANGELOG.md

## v1.0.0-beta.1 (2026-03-17)

### 新增
- Google Calendar集成功能
- 性能监控功能

### 修复
- 修复事件删除后不刷新的问题
- 修复提醒通知延迟的问题

### 优化
- 优化启动速度，提升30%
- 减少内存占用20%
```

---

## ⚠️ 常见问题

### Q1: APK安装失败
**原因**: 未启用"未知来源应用安装"  
**解决**: 设置 → 安全 → 启用"未知来源"

### Q2: APK签名不匹配
**原因**: Debug和Release签名不同  
**解决**: 先卸载旧版本，再安装新版本

### Q3: 应用启动崩溃
**原因**: API地址未配置或网络问题  
**解决**: 检查网络连接，查看Logcat日志

### Q4: 构建失败
**原因**: 签名密钥未创建或路径错误  
**解决**: 先创建签名密钥，检查路径是否正确

---

## 📞 支持

- **技术支持**: dev@calendar-app.com
- **测试反馈**: test@calendar-app.com
- **紧急联系**: +86 138-0000-0000

---

**文档版本**: v1.0  
**最后更新**: 2026年3月10日
