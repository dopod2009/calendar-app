# Android签名密钥说明

## 密钥信息

- **密钥库文件**: `calendar-release.jks`
- **别名**: `calendar-key`
- **密钥算法**: RSA 2048-bit
- **有效期**: 10000天
- **存储密码**: `calendar2026alpha`
- **密钥密码**: `calendar2026alpha`

## 如何创建密钥

### 前提条件
需要安装Java JDK 8或更高版本

### 创建命令
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

### 验证密钥
```bash
keytool -list -v -keystore keystore/calendar-release.jks -storepass calendar2026alpha
```

## 安全提示

⚠️ **重要**: 此密钥仅用于Alpha测试版本，正式发布版本请使用独立的安全密钥！

- 不要将密钥文件提交到版本控制系统
- 密钥密码应该妥善保管
- 正式发布时请使用不同的密钥
