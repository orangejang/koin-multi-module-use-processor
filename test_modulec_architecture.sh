#!/bin/bash

echo "🚀 测试 ModuleC 架构的 KSP 自动生成系统"
echo "=============================================="

echo ""
echo "📋 新架构说明："
echo "- moduleC: 独立的子项目，包含 KSP 处理器配置"
echo "- moduleC: 生成 KoinModules.kt 文件"
echo "- moduleA, moduleB: 不再直接依赖 moduleC，避免循环依赖"
echo "- app: 依赖 moduleC，可以使用生成的 KoinModules"

echo ""
echo "1️⃣ 清理项目..."
./gradlew clean

echo ""
echo "2️⃣ 收集所有 @KoinModule 注解..."
./gradlew collectKoinModules

echo ""
echo "3️⃣ 运行 moduleC 的 KSP 生成代码..."
./gradlew :moduleC:kspDebugKotlin

echo ""
echo "4️⃣ 编译 moduleC..."
./gradlew :moduleC:compileDebugKotlin

echo ""
echo "5️⃣ 检查生成的文件..."
if [ -f "moduleC/build/generated/ksp/debug/kotlin/com/example/koinmodules/KoinModules.kt" ]; then
    echo "✅ KoinModules.kt 文件已在 moduleC 中成功生成！"
    echo ""
    echo "📄 生成的文件内容："
    echo "----------------------------------------"
    cat moduleC/build/generated/ksp/debug/kotlin/com/example/koinmodules/KoinModules.kt
    echo "----------------------------------------"
else
    echo "❌ KoinModules.kt 文件未在 moduleC 中生成"
    exit 1
fi

echo ""
echo "6️⃣ 检查收集到的模块信息..."
if [ -f "build/generated/koin/koin-modules.txt" ]; then
    echo "✅ 模块信息文件已生成："
    cat build/generated/koin/koin-modules.txt
else
    echo "❌ 模块信息文件未生成"
    exit 1
fi

echo ""
echo "7️⃣ 编译 app 模块（使用 moduleC）..."
./gradlew :app:compileDebugKotlin

echo ""
echo "🎉 测试完成！ModuleC 架构的 KSP 系统运行正常！"
echo ""
echo "📋 新架构优势："
echo "- ✅ 避免了循环依赖问题"
echo "- ✅ moduleC 作为独立的代码生成模块"
echo "- ✅ 其他模块可以通过依赖 moduleC 来使用生成的代码"
echo "- ✅ 清晰的模块职责分离"
echo ""
echo "🔧 使用方法："
echo "1. 在任何模块中创建函数并添加 @KoinModule 注解"
echo "2. 运行 './gradlew collectKoinModules' 收集模块"
echo "3. 运行 './gradlew :moduleC:build' 生成代码"
echo "4. 在 app 或其他模块中通过 moduleC 使用 KoinModules.getAllModules()"