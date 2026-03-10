#!/bin/bash
# 创建签名密钥脚本

KEYSTORE_DIR="../keystore"
DEBUG_KEYSTORE="$KEYSTORE_DIR/calendar-debug.jks"
RELEASE_KEYSTORE="$KEYSTORE_DIR/calendar-release.jks"

# 创建keystore目录
mkdir -p $KEYSTORE_DIR

# 创建Debug签名密钥
if [ ! -f "$DEBUG_KEYSTORE" ]; then
    echo "📝 创建Debug签名密钥..."
    keytool -genkey -v \
        -keystore $DEBUG_KEYSTORE \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -alias calendar-debug-key \
        -storepass calendar_debug_2026 \
        -keypass calendar_debug_2026 \
        -dname "CN=Calendar Debug, OU=Development, O=Calendar App, L=Beijing, ST=Beijing, C=CN"
    echo "✅ Debug签名密钥创建成功"
else
    echo "✅ Debug签名密钥已存在"
fi

# 创建Release签名密钥
if [ ! -f "$RELEASE_KEYSTORE" ]; then
    echo "📝 创建Release签名密钥..."
    keytool -genkey -v \
        -keystore $RELEASE_KEYSTORE \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -alias calendar-key \
        -storepass calendar_2026_release \
        -keypass calendar_2026_release \
        -dname "CN=Calendar App, OU=Production, O=Calendar App Team, L=Beijing, ST=Beijing, C=CN"
    echo "✅ Release签名密钥创建成功"
else
    echo "✅ Release签名密钥已存在"
fi

echo ""
echo "🎉 签名密钥创建完成！"
echo "📁 密钥位置: $KEYSTORE_DIR"
echo ""
echo "⚠️  重要提示:"
echo "1. 请妥善保管密钥文件，不要提交到版本控制"
echo "2. Release密钥用于正式发布，请勿泄露密码"
echo "3. 建议将密钥备份到安全位置"
