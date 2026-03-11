# 无Java环境解决方案

## ❌ 当前状态

您的系统没有安装Java环境，无法创建签名密钥。

---

## ✅ 3种解决方案

### 方案1: 安装Java（推荐）⭐⭐⭐

#### 安装OpenJDK 17

在终端执行：

```bash
brew install openjdk@17
```

**预计时间**: 5-10分钟

#### 配置环境变量

安装完成后执行：

```bash
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

source ~/.zshrc
```

#### 验证安装

```bash
java -version
```

看到 `openjdk version "17.0.x"` 表示安装成功。

---

### 方案2: 使用GitHub Codespaces（无需本地Java）⭐⭐

#### 创建Codespace

1. 访问：https://github.com/dopod2009/calendar-app

2. 点击绿色 **"Code"** 按钮

3. 选择 **"Codespaces"** 标签

4. 点击 **"Create codespace on main"**

#### 在Codespace中创建密钥

Codespace会自动打开一个在线VS Code环境，在终端执行：

```bash
cd /workspaces/calendar-app

keytool -genkeypair -v \
  -alias calendar-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore keystore/calendar-release.jks \
  -storepass calendar2026alpha \
  -keypass calendar2026alpha \
  -dname "CN=Calendar App, OU=Development, O=Calendar Team, L=Beijing, ST=Beijing, C=CN"

base64 -i keystore/calendar-release.jks | tr -d '\n'
```

#### 复制Base64字符串

选中输出的Base64字符串，复制。

#### 配置GitHub Secrets

在Codespace浏览器中：
1. 新开标签页访问：https://github.com/dopod2009/calendar-app/settings/secrets/actions
2. 配置4个Secrets（见下文）

---

### 方案3: 使用临时密钥（仅用于测试）⭐

**⚠️ 注意**: 这种方式不安全，仅用于Alpha测试！

#### 使用预生成的测试密钥

我可以为您生成一个临时的测试密钥Base64字符串。

**优点**: 无需安装Java
**缺点**: 不安全，仅用于测试

---

## 🎯 推荐方案

| 场景 | 推荐方案 |
|------|---------|
| 愿意安装软件 | **方案1: 安装Java** |
| 不想安装软件 | **方案2: GitHub Codespaces** |
| 快速测试 | **方案3: 临时密钥** |

---

## 📋 如果选择方案1（安装Java）

### 完整步骤

```bash
# 1. 安装Java
brew install openjdk@17

# 2. 配置环境变量
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

source ~/.zshrc

# 3. 验证
java -version

# 4. 创建密钥
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

# 5. 生成Base64
base64 -i keystore/calendar-release.jks | tr -d '\n'
```

---

## 📋 如果选择方案2（GitHub Codespaces）

### 步骤

1. 访问：https://github.com/dopod2009/calendar-app
2. 点击 **Code** → **Codespaces** → **Create codespace**
3. 等待Codespace启动（约1分钟）
4. 在终端执行密钥生成命令
5. 复制Base64字符串
6. 配置GitHub Secrets

---

## 📋 如果选择方案3（临时密钥）

告诉我您选择此方案，我会生成一个临时密钥供您测试。

---

## ⏱️ 时间对比

| 方案 | 时间 |
|------|------|
| 方案1: 安装Java | 10-15分钟 |
| 方案2: Codespaces | 5分钟 |
| 方案3: 临时密钥 | 2分钟 |

---

## 💡 建议

**如果愿意安装Java**: 选择方案1，一步到位

**如果不想安装软件**: 选择方案2，使用在线环境

**如果只是快速测试**: 选择方案3，最快速

---

**请告诉我您选择哪个方案，我会继续帮您完成配置！**
