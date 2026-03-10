#!/bin/bash
# Alpha版本自动构建脚本

set -e  # 遇到错误立即退出

PROJECT_ROOT="/Users/alex/WorkBuddy/20260309174106"
cd $PROJECT_ROOT

echo "🚀 开始构建Alpha版本..."
echo "📁 项目目录: $PROJECT_ROOT"
echo ""

# 1. 检查环境
echo "🔍 检查构建环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未安装Java"
    echo "请安装JDK 17或更高版本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ 错误: Java版本过低，需要JDK 17+"
    exit 1
fi

echo "✅ Java版本: $(java -version 2>&1 | head -n 1)"

# 2. 创建签名密钥
echo ""
echo "🔐 准备签名密钥..."
if [ ! -f "keystore/calendar-release.jks" ]; then
    echo "📝 创建签名密钥..."
    chmod +x scripts/create-keystore.sh
    cd scripts
    ./create-keystore.sh
    cd ..
else
    echo "✅ 签名密钥已存在"
fi

# 3. 初始化Gradle Wrapper
echo ""
echo "📦 初始化Gradle..."
if [ ! -f "gradlew" ]; then
    echo "正在生成Gradle Wrapper..."
    gradle wrapper --gradle-version 8.2
fi

# 4. 清理构建
echo ""
echo "🧹 清理旧的构建文件..."
chmod +x gradlew
./gradlew clean

# 5. 构建Debug APK（快速测试）
echo ""
echo "🔨 构建Debug APK..."
./gradlew assembleDebug

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    DEBUG_SIZE=$(ls -lh app/build/outputs/apk/debug/app-debug.apk | awk '{print $5}')
    echo "✅ Debug APK构建成功！"
    echo "📁 位置: app/build/outputs/apk/debug/app-debug.apk"
    echo "📊 大小: $DEBUG_SIZE"
else
    echo "❌ Debug APK构建失败"
    exit 1
fi

# 6. 构建Release APK（正式版本）
echo ""
echo "🔨 构建Release APK..."
./gradlew assembleRelease

if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    RELEASE_SIZE=$(ls -lh app/build/outputs/apk/release/app-release.apk | awk '{print $5}')
    echo "✅ Release APK构建成功！"
    echo "📁 位置: app/build/outputs/apk/release/app-release.apk"
    echo "📊 大小: $RELEASE_SIZE"
else
    echo "❌ Release APK构建失败"
    exit 1
fi

# 7. 生成校验文件
echo ""
echo "📝 生成校验文件..."
cd app/build/outputs/apk/release
md5sum app-release.apk > app-release.apk.md5
sha256sum app-release.apk > app-release.apk.sha256
cd -

# 8. 创建发布包
echo ""
echo "📦 创建发布包..."
RELEASE_DIR="calendar-alpha-v1.0.0-$(date +%Y%m%d)"
mkdir -p $RELEASE_DIR

cp app/build/outputs/apk/release/app-release.apk $RELEASE_DIR/
cp app/build/outputs/apk/release/app-release.apk.md5 $RELEASE_DIR/
cp app/build/outputs/apk/release/app-release.apk.sha256 $RELEASE_DIR/
cp CHANGELOG.md $RELEASE_DIR/
cp RELEASE-NOTES.md $RELEASE_DIR/
cp INTEGRATION-TEST-REPORT.md $RELEASE_DIR/

tar -czf ${RELEASE_DIR}.tar.gz $RELEASE_DIR

echo "✅ 发布包创建成功！"
echo "📁 位置: ${RELEASE_DIR}.tar.gz"

# 9. 显示构建摘要
echo ""
echo "═══════════════════════════════════════════════"
echo "🎉 Alpha版本构建完成！"
echo "═══════════════════════════════════════════════"
echo ""
echo "📱 APK信息:"
echo "  - 版本: v1.0.0-alpha"
echo "  - Debug APK: app/build/outputs/apk/debug/app-debug.apk ($DEBUG_SIZE)"
echo "  - Release APK: app/build/outputs/apk/release/app-release.apk ($RELEASE_SIZE)"
echo ""
echo "📦 发布包:"
echo "  - 位置: ${RELEASE_DIR}.tar.gz"
echo "  - 包含: APK + 文档 + 测试报告"
echo ""
echo "🚀 下一步:"
echo "  1. 测试Debug APK: adb install app/build/outputs/apk/debug/app-debug.apk"
echo "  2. 验证Release APK: adb install app/build/outputs/apk/release/app-release.apk"
echo "  3. 发送给测试用户: 解压 ${RELEASE_DIR}.tar.gz"
echo ""
echo "═══════════════════════════════════════════════"
