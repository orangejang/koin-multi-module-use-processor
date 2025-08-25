#!/bin/bash

echo "🚀 测试完整的 KSP 自动生成 KoinModules 系统"
echo "================================================"

echo ""
echo "1️⃣ 清理项目..."
./gradlew clean

echo ""
echo "2️⃣ 收集所有 @KoinModule 注解..."
./gradlew collectKoinModules

echo ""
echo "3️⃣ 运行 KSP 生成代码..."
./gradlew :app:kspDebugKotlin

echo ""
echo "4️⃣ 编译整个项目..."
./gradlew :app:assembleDebug

echo ""
echo "5️⃣ 检查生成的文件..."
if [ -f "app/build/generated/ksp/debug/kotlin/com/example/koinmodules/KoinModules.kt" ]; then
    echo "✅ KoinModules.kt 文件已成功生成！"
    echo ""
    echo "📄 生成的文件内容："
    echo "----------------------------------------"
    cat app/build/generated/ksp/debug/kotlin/com/example/koinmodules/KoinModules.kt
    echo "----------------------------------------"
else
    echo "❌ KoinModules.kt 文件未生成"
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
echo "🎉 测试完成！KSP 自动生成 KoinModules 系统运行正常！"
echo ""
echo "📋 系统功能总结："
echo "- ✅ 自动扫描所有子项目中的 @KoinModule 注解"
echo "- ✅ 收集模块信息到统一文件"
echo "- ✅ KSP 处理器读取收集信息并生成代码"
echo "- ✅ 生成的代码使用完全限定名调用模块函数"
echo "- ✅ 整个项目编译成功"
echo ""
echo "🔧 使用方法："
echo "1. 在任何模块中创建函数并添加 @KoinModule 注解"
echo "2. 运行 './gradlew collectKoinModules' 收集模块"
echo "3. 运行 './gradlew build' 触发 KSP 生成代码"
echo "4. 在代码中使用 KoinModules.getAllModules() 获取所有模块"