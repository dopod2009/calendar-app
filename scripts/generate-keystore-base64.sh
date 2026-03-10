#!/bin/bash

# GitHub Actions签名密钥配置脚本
# 此脚本用于将签名密钥转换为Base64编码，以便存储在GitHub Secrets中

echo "================================"
echo "GitHub Actions签名密钥配置脚本"
echo "================================"
echo ""

# 检查密钥文件是否存在
KEYSTORE_FILE="keystore/calendar-release.jks"

if [ ! -f "$KEYSTORE_FILE" ]; then
    echo "❌ 错误: 签名密钥文件不存在: $KEYSTORE_FILE"
    echo ""
    echo "请先创建签名密钥文件，运行以下命令："
    echo ""
    echo "  keytool -genkeypair -v \\"
    echo "    -alias calendar-key \\"
    echo "    -keyalg RSA \\"
    echo "    -keysize 2048 \\"
    echo "    -validity 10000 \\"
    echo "    -keystore keystore/calendar-release.jks \\"
    echo "    -storepass calendar2026alpha \\"
    echo "    -keypass calendar2026alpha \\"
    echo "    -dname \"CN=Calendar App, OU=Development, O=Calendar Team, L=Beijing, ST=Beijing, C=CN\""
    echo ""
    exit 1
fi

echo "✅ 找到签名密钥文件: $KEYSTORE_FILE"
echo ""

# 生成Base64编码
echo "📦 正在生成Base64编码..."
BASE64_ENCODED=$(base64 -i "$KEYSTORE_FILE" | tr -d '\n')

echo ""
echo "================================"
echo "✅ Base64编码生成成功！"
echo "================================"
echo ""
echo "请将以下内容复制到GitHub Secrets中："
echo ""
echo "Secret名称: KEYSTORE_BASE64"
echo "Secret值: $BASE64_ENCODED"
echo ""
echo "================================"
echo ""

# 生成其他Secrets提示
echo "📋 还需要配置以下GitHub Secrets："
echo ""
echo "1. KEYSTORE_PASSWORD"
echo "   值: calendar2026alpha"
echo ""
echo "2. KEY_PASSWORD"
echo "   值: calendar2026alpha"
echo ""
echo "3. KEY_ALIAS"
echo "   值: calendar-key"
echo ""
echo "================================"

# 保存到文件（可选）
read -p "是否将Base64编码保存到文件？(y/n): " save_to_file

if [ "$save_to_file" = "y" ] || [ "$save_to_file" = "Y" ]; then
    OUTPUT_FILE="keystore/keystore-base64.txt"
    echo "$BASE64_ENCODED" > "$OUTPUT_FILE"
    echo ""
    echo "✅ 已保存到: $OUTPUT_FILE"
    echo ""
    echo "⚠️  重要: 此文件包含敏感信息，请不要提交到Git仓库！"
    echo ""
fi

echo "================================"
echo "✨ 配置完成！"
echo "================================"
echo ""
echo "下一步操作："
echo "1. 前往GitHub仓库的 Settings → Secrets and variables → Actions"
echo "2. 点击 'New repository secret'"
echo "3. 按照上述提示添加4个Secrets"
echo "4. 配置完成后即可触发构建"
echo ""
