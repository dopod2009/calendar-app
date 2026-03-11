# GitHub Codespaces 创建签名密钥 - 详细步骤

## 🎯 目标

使用GitHub Codespaces在线环境创建Android签名密钥，无需本地安装Java。

---

## 📋 完整步骤（8步完成）

### 步骤1: 访问GitHub仓库

**打开浏览器，访问**:

```
https://github.com/dopod2009/calendar-app
```

---

### 步骤2: 创建Codespace

#### 2.1 点击Code按钮

在仓库页面，找到绿色的 **"Code"** 按钮（通常在页面右上角），点击它。

#### 2.2 选择Codespaces标签

在弹出的菜单中：
- 点击 **"Codespaces"** 标签（而不是 "Local" 标签）

#### 2.3 创建新的Codespace

点击 **"Create codespace on main"** 按钮（或 "Create new codespace"）

**首次使用可能需要授权**，点击同意即可。

---

### 步骤3: 等待Codespace启动

**等待时间**: 约30秒-1分钟

**您会看到**:
- 加载动画
- "Setting up your codespace..." 提示
- 自动打开在线VS Code编辑器

**成功标志**:
- 看到VS Code界面
- 底部终端区域显示命令提示符
- 左侧文件浏览器显示项目文件

---

### 步骤4: 在Codespace终端执行命令

#### 4.1 打开终端（如果未打开）

- 如果底部没有终端窗口
- 点击菜单 **"Terminal"** → **"New Terminal"**
- 或使用快捷键：`Ctrl + Shift + `` `（反引号）

#### 4.2 确认当前目录

终端应该自动定位到项目目录，提示符显示：

```
@codespace-xxxxx ➜ /workspaces/calendar-app $
```

如果不在项目目录，执行：
```bash
cd /workspaces/calendar-app
```

---

### 步骤5: 创建签名密钥

#### 5.1 复制并执行命令

**在Codespace终端中粘贴并执行**（整段复制）:

```bash
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

**执行时间**: 约5秒

#### 5.2 成功标志

看到以下输出：

```
Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 10,000 days
	for: CN=Calendar App, OU=Development, O=Calendar Team, L=Beijing, ST=Beijing, C=CN
[Storing keystore/calendar-release.jks]
```

---

### 步骤6: 生成Base64编码

#### 6.1 执行命令

在Codespace终端中粘贴并执行：

```bash
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

#### 6.2 复制输出结果

**输出示例**:

```
/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMCwsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/...（很长的字符串）
```

**操作**:
1. 在终端中选中整个Base64字符串
2. 按 `Ctrl+C` 或右键复制
3. **保存到记事本或其他地方**，稍后需要使用

**注意**: 这个字符串很长，可能有几千个字符，确保完整复制。

---

### 步骤7: 配置GitHub Secrets

#### 7.1 新开浏览器标签页

**访问**:

```
https://github.com/dopod2009/calendar-app/settings/secrets/actions
```

#### 7.2 添加第一个Secret: KEYSTORE_BASE64

1. 点击 **"New repository secret"** 按钮

2. 填写信息：

| 字段 | 内容 |
|------|------|
| **Name** | `KEYSTORE_BASE64` |
| **Secret** | 粘贴步骤6复制的Base64字符串 |

3. 点击 **"Add secret"** 按钮

---

#### 7.3 添加第二个Secret: KEYSTORE_PASSWORD

1. 再次点击 **"New repository secret"**

2. 填写信息：

| 字段 | 内容 |
|------|------|
| **Name** | `KEYSTORE_PASSWORD` |
| **Secret** | `calendar2026alpha` |

3. 点击 **"Add secret"**

---

#### 7.4 添加第三个Secret: KEY_PASSWORD

1. 点击 **"New repository secret"**

2. 填写信息：

| 字段 | 内容 |
|------|------|
| **Name** | `KEY_PASSWORD` |
| **Secret** | `calendar2026alpha` |

3. 点击 **"Add secret"**

---

#### 7.5 添加第四个Secret: KEY_ALIAS

1. 点击 **"New repository secret"**

2. 填写信息：

| 字段 | 内容 |
|------|------|
| **Name** | `KEY_ALIAS` |
| **Secret** | `calendar-key` |

3. 点击 **"Add secret"**

---

### 步骤8: 触发构建

#### 8.1 访问Actions页面

**新开浏览器标签页，访问**:

```
https://github.com/dopod2009/calendar-app/actions
```

#### 8.2 触发工作流

1. 在Actions页面，点击左侧 **"Build Alpha APK"** 工作流

2. 点击右侧 **"Run workflow"** 按钮

3. 在弹出的菜单中：
   - Branch: 选择 `main`
   - 点击绿色 **"Run workflow"** 按钮

#### 8.3 监控构建

- 构建会立即开始
- 点击刚触发的构建记录查看进度
- 各步骤会显示黄色圆圈（进行中）或绿色对勾（完成）

**构建时间**: 约9分钟

---

## ✅ 完成清单

```
□ 已访问GitHub仓库
□ 已创建Codespace
□ 已在Codespace中创建密钥
□ 已生成Base64编码
□ 已配置KEYSTORE_BASE64 Secret
□ 已配置KEYSTORE_PASSWORD Secret
□ 已配置KEY_PASSWORD Secret
□ 已配置KEY_ALIAS Secret
□ 已触发GitHub Actions构建
□ 等待构建完成
```

---

## 🎉 构建完成后

### 下载APK

1. 在Actions页面点击已完成的构建记录（绿色对勾）

2. 滚动到页面底部 **"Artifacts"** 区域

3. 点击 **`calendar-alpha-apk`** 下载

4. 解压zip文件获得APK: `calendar-app-v1.0.0-alpha.apk`

---

## ⏱️ 时间估算

| 步骤 | 时间 |
|------|------|
| 创建Codespace | 1分钟 |
| 创建签名密钥 | 10秒 |
| 生成Base64 | 5秒 |
| 配置4个Secrets | 2分钟 |
| 触发构建 | 10秒 |
| 等待构建 | 9分钟 |
| **总计** | **约13分钟** |

---

## 🆘 常见问题

### Q1: Codespace启动失败

**解决方案**:
- 刷新页面重试
- 或清除浏览器缓存
- 或使用无痕模式

### Q2: keytool命令未找到

**解决方案**:
Codespace自带Java环境，如果提示未找到：
```bash
# 检查Java
java -version

# 如果没有，手动安装
sudo apt-get update
sudo apt-get install openjdk-17-jdk
```

### Q3: Base64字符串太长，复制不完整

**解决方案**:
```bash
# 保存到文件
base64 -i keystore/calendar-release.jks | tr -d '\n' > keystore-base64.txt

# 查看文件
cat keystore-base64.txt
```

然后在Codespace文件浏览器中打开 `keystore-base64.txt`，全选复制。

### Q4: Secrets添加后看不到内容

**说明**:
GitHub Secrets添加后无法查看内容，这是正常的安全设计。只要添加时没有报错，就表示配置成功。

---

## 💡 提示

- Codespace是免费的在线开发环境
- 每月有60小时免费额度
- 记得使用后停止Codespace（点击左下角绿色按钮 → Stop Current Codespace）

---

## 🎯 现在开始！

**立即访问**: https://github.com/dopod2009/calendar-app

按照步骤1-8操作，约13分钟后获得APK！

---

**祝您操作顺利！如有问题随时告诉我。**
