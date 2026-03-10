# APK构建指南 - 快速上手

## 🎯 目标
在最短时间内构建出Alpha版本APK，提供给50名内测用户。

---

## ⚡ 最快方式：使用Android Studio（15-30分钟）

### 步骤1: 安装Android Studio（如果还没有）

**下载地址**: https://developer.android.com/studio

**安装步骤**:
1. 下载Android Studio (约1GB)
2. 运行安装程序
3. 完成初始化向导（会自动安装Android SDK）

### 步骤2: 打开项目

```bash
# 启动Android Studio
# File → Open → 选择项目目录
/Users/alex/WorkBuddy/20260309174106
```

### 步骤3: 等待Gradle同步

Android Studio会自动：
- ✅ 下载Gradle Wrapper
- ✅ 配置Android SDK
- ✅ 同步所有依赖库
- ✅ 编译项目

**预计时间**: 5-10分钟

### 步骤4: 创建签名密钥

```
菜单栏: Build → Generate Signed Bundle / APK

选择: APK → Next

点击: Create new...

填写信息:
┌─────────────────────────────────────┐
│ Key store path:                      │
│   keystore/calendar-release.jks     │
│                                      │
│ Password: calendar2026alpha          │
│ Confirm: calendar2026alpha           │
│                                      │
│ Alias: calendar-key                  │
│ Password: calendar2026alpha          │
│ Confirm: calendar2026alpha           │
│                                      │
│ Validity (years): 10000              │
│                                      │
│ Certificate:                         │
│   First and Last Name: Calendar App  │
│   Organization: Calendar Team        │
│   City: Beijing                      │
│   State: Beijing                     │
│   Country Code: CN                   │
└─────────────────────────────────────┘

点击: OK → Next
```

### 步骤5: 构建Release APK

```
选择构建变体:
  ☑ release
  
选择签名版本:
  ☑ V1 (Jar Signature)
  ☑ V2 (Full APK Signature)
  
点击: Finish
```

### 步骤6: 获取APK

**构建完成后**:

```bash
# APK位置
app/release/app-release.apk

# 文件大小
约8-12 MB

# 如何找到
Android Studio右下角会弹出通知
点击 "locate" 可以打开文件所在文件夹
```

---

## 📦 APK文件信息

| 属性 | 值 |
|------|-----|
| 文件名 | `app-release.apk` |
| 版本名 | `1.0.0-alpha` |
| 版本号 | `1` |
| 最小SDK | Android 8.0 (API 26) |
| 目标SDK | Android 14 (API 34) |
| 预期大小 | 8-12 MB |
| 签名算法 | RSA 2048-bit |

---

## 🚀 发布给内测用户

### 方式1: 邮件发送（适合少量用户）

```bash
# 1. 收件人
50名内测用户邮箱

# 2. 邮件主题
【内测邀请】日历应用 Alpha v1.0.0 内测版本

# 3. 附件
app-release.apk

# 4. 邮件正文
见下方的邮件模板
```

### 方式2: 测试平台分发（推荐）

**Firebase App Distribution**:
```bash
# 1. 上传APK
Firebase Console → App Distribution → 上传APK

# 2. 添加测试用户
添加50个测试用户邮箱

# 3. 发送邀请
系统自动发送下载链接邮件
```

**蒲公英 / fir.im**:
```bash
# 1. 注册账号
https://www.pgyer.com 或 https://fir.im

# 2. 上传APK
拖拽APK文件上传

# 3. 获取下载链接
https://www.pgyer.com/xxxxx

# 4. 分享链接
发送给测试用户
```

### 方式3: 云存储分享

```bash
# 使用腾讯云COS/阿里云OSS/百度网盘等

# 1. 上传APK
# 2. 生成分享链接
# 3. 设置访问密码（可选）
# 4. 发送给测试用户
```

---

## 📧 内测邀请邮件模板

```
主题: 【内测邀请】日历应用 Alpha v1.0.0 内测版本

尊敬的内测用户：

您好！

感谢您参与日历应用的Alpha版本内测。

【下载方式】
方式1: 点击链接直接下载
[下载链接]

方式2: 扫描二维码下载
[二维码图片]

【版本信息】
版本号: 1.0.0-alpha
发布日期: 2026年3月10日
支持系统: Android 8.0及以上

【核心功能】
✅ 日历视图（月/周/日视图）
✅ 事件管理（创建/编辑/删除）
✅ 智能提醒（本地通知）
✅ 数据同步（多设备同步）
✅ Google日历集成

【测试重点】
1. 基本功能是否正常使用
2. 数据同步是否准确
3. 提醒是否准时到达
4. Google日历集成是否正常

【反馈方式】
- 邮件: test@calendar-app.com
- 微信群: [群二维码]
- 反馈表单: [在线表单链接]

【测试周期】
内测时间: 2026年3月10日 - 2026年3月24日（2周）

【注意事项】
⚠️ 这是Alpha测试版本，可能存在不稳定情况
⚠️ 建议不要用于重要日程管理
⚠️ 遇到问题请及时反馈

感谢您的参与和支持！

日历应用开发团队
2026年3月10日
```

---

## 🔍 安装验证步骤

### 在Android设备上安装

```bash
# 1. 传输APK到手机
- 方式1: 数据线连接，复制到手机存储
- 方式2: 通过微信/QQ发送到手机
- 方式3: 扫描二维码下载

# 2. 开启安装权限
设置 → 安全 → 允许安装未知来源应用

# 3. 点击APK安装
找到APK文件 → 点击 → 安装

# 4. 首次启动
打开应用 → 授予必要权限 → 开始使用
```

### 验证功能清单

```bash
□ 应用正常启动
□ 用户可以注册/登录
□ 可以创建事件
□ 可以查看日历视图
□ 可以设置提醒
□ 可以同步数据
□ Google日历可以集成
□ 推送通知正常
```

---

## 🆘 常见问题

### Q1: 构建失败提示 "SDK location not found"

**解决方案**:
```bash
# 创建 local.properties 文件
echo "sdk.dir=/path/to/Android/sdk" > local.properties

# Mac默认路径
sdk.dir=/Users/你的用户名/Library/Android/sdk

# Windows默认路径
sdk.dir=C:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

### Q2: 构建失败提示 "Could not find com.android.tools.build:gradle"

**解决方案**:
检查网络连接，Gradle需要从Google Maven仓库下载依赖

### Q3: APK安装失败

**解决方案**:
- 检查Android版本（需要8.0及以上）
- 开启"允许安装未知来源应用"
- 卸载旧版本后重新安装

---

## 📊 构建时间估算

| 步骤 | 首次 | 后续 |
|------|------|------|
| 安装Android Studio | 15分钟 | - |
| Gradle同步 | 10分钟 | 2分钟 |
| 创建签名密钥 | 2分钟 | - |
| 构建APK | 3分钟 | 2分钟 |
| **总计** | **30分钟** | **5分钟** |

---

## ✅ 构建完成检查清单

```
□ app-release.apk 文件已生成
□ 文件大小在8-12MB之间
□ 可以在Android设备上安装
□ 应用可以正常启动
□ 核心功能可以正常使用
□ 已准备好发布给内测用户
```

---

**🎯 目标达成时间**: 30分钟内完成APK构建和发布准备

**📱 下一步**: 获取APK后，选择合适的分发方式发送给50名内测用户
