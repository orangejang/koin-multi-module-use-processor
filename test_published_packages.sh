#!/bin/bash

echo "=== 测试发布的包 ==="

# 1. 清理并重新构建
echo "1. 清理项目..."
./gradlew clean

# 2. 发布annotation包
echo "2. 发布annotation包..."
./gradlew :annotation:publishToMavenLocal

# 3. 发布moduleC包
echo "3. 发布moduleC包..."
./gradlew :moduleC:publishToMavenLocal

# 4. 检查生成的KoinModules类
echo "4. 检查生成的KoinModules类..."
if [ -f "moduleC/build/generated/ksp/release/kotlin/com/example/koinmodules/KoinModules.kt" ]; then
    echo "✅ KoinModules.kt 已生成"
    echo "内容预览:"
    head -20 "moduleC/build/generated/ksp/release/kotlin/com/example/koinmodules/KoinModules.kt"
else
    echo "❌ KoinModules.kt 未生成"
fi

# 5. 检查本地Maven仓库
echo "5. 检查本地Maven仓库..."
LOCAL_REPO="$HOME/.m2/repository/com/example"
if [ -d "$LOCAL_REPO/koin-annotation" ]; then
    echo "✅ annotation包已发布到本地Maven仓库"
    ls -la "$LOCAL_REPO/koin-annotation/"
else
    echo "❌ annotation包未找到"
fi

if [ -d "$LOCAL_REPO/koin-modules-collector" ]; then
    echo "✅ moduleC包已发布到本地Maven仓库"
    ls -la "$LOCAL_REPO/koin-modules-collector/"
else
    echo "❌ moduleC包未找到"
fi

echo "=== 测试完成 ==="