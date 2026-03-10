# GitHub Actions 工作流说明

本目录包含GitHub Actions CI/CD工作流配置文件。

## 📁 文件列表

### build-alpha.yml
**用途**: 构建Release APK

**触发条件**:
- 手动触发
- 推送到main/develop分支
- Pull Request到main分支

**产物**:
- Release APK（已签名）
- 保存30天

---

### build-pr.yml
**用途**: 为Pull Request构建Debug APK

**触发条件**:
- Pull Request到main/develop分支

**产物**:
- Debug APK（未签名）
- 保存7天
- 自动在PR中评论下载链接

---

### test.yml
**用途**: 运行单元测试

**触发条件**:
- 推送到main/develop分支
- Pull Request到main分支

**产物**:
- 测试报告
- 保存7天

---

## 🚀 快速开始

查看 [GITHUB-ACTIONS-QUICK-START.md](../GITHUB-ACTIONS-QUICK-START.md) 获取详细配置步骤。

## 📖 完整文档

查看 [GITHUB-ACTIONS-GUIDE.md](../GITHUB-ACTIONS-GUIDE.md) 获取完整配置说明。
