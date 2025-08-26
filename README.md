# Koin Multi-Module Use Processor

一个基于 KSP (Kotlin Symbol Processing) 的 Koin 依赖注入框架扩展项目，用于在多模块 Android 项目中自动收集和管理
Koin 模块。

## 项目概述

本项目提供了一套完整的解决方案，通过注解处理器自动收集各个模块中的 Koin
模块定义，并生成统一的模块注册代码，简化多模块项目中的依赖注入配置。

## 项目结构

```
koin-multi-module-use-processor/
├── annotation/                    # 注解定义模块
│   └── src/main/java/
│       └── com/example/annotation/
├── processor/                     # KSP 注解处理器
│   └── src/main/java/
│       └── com/example/processor/
├── app/                          # 主应用模块
├── moduleA/                      # 示例模块 A
├── moduleB/                      # 示例模块 B  
├── moduleC/                      # 示例模块 C
├── gradle/                       # 公共 Gradle 配置
│   ├── koin-dependencies.gradle  # Koin 依赖统一配置
│   └── README.md                # 配置使用指南
└── README.md         # 项目总结
```

## 核心功能

### 1. 自动模块收集

- 通过 `@KoinModule` 注解标记需要收集的 Koin 模块
- KSP 处理器自动扫描所有模块中的注解
- 生成统一的模块注册代码

### 2. 多模块支持

- 支持 Android Library 和 Application 模块
- 自动处理模块间的依赖关系
- 提供灵活的配置选项

### 3. 统一依赖管理

- 通过公共 Gradle 配置文件统一管理版本
- 简化新模块的配置过程
- 确保版本一致性

## 快速开始

### 1. 添加依赖

在模块的 `build.gradle` 文件中添加：

```gradle
plugins {
    id 'com.android.library' // 或 'com.android.application'
    id 'kotlin-android'
    id 'com.google.devtools.ksp'
}

// 应用 Koin 依赖配置
apply from: '../gradle/koin-dependencies.gradle'

dependencies {
    // 其他依赖...
    implementation "io.insert-koin:koin-android:$koin_version"
}
```

### 2. 使用注解

在你的 Koin 模块中使用 `@KoinModule` 注解：

```kotlin
@KoinModule
val myModule = module {
      single<MyRepository> { MyRepositoryImpl() }
      factory<MyUseCase> { MyUseCaseImpl(get()) }
}
```

### 3. 配置处理器

在 app 模块的 `build.gradle` 中配置 KSP 参数：

```gradle
ksp {
    arg("koin.modules.collector", "true")
    arg("koin.modules.package.name", "com.example.modules")
    arg("koin.modules.file.name", "KoinModules")
}
```

### 4. 使用生成的代码

在 Application 中使用自动生成的模块：

```kotlin
class MyApplication : Application() {
   override fun onCreate() {
      super.onCreate()

      startKoin {
         androidContext(this@MyApplication)
         modules(KoinModules.allModules)
      }
   }
}
```

## 版本信息

当前版本：`1.0.1-SNAPSHOT`

### 依赖版本

- Kotlin: 1.8.0
- Koin: 3.4.0
- KSP: 1.8.0-1.0.9

## 构建和发布

### 发布到本地仓库

```bash
# 发布 annotation 模块
./gradlew :annotation:publishToMavenLocal

# 发布 processor 模块  
./gradlew :processor:publishToMavenLocal
```

### 构建项目

```bash
# 清理并构建
./gradlew clean build

# 运行 KSP 处理器
./gradlew :app:kspDebugKotlin
```

## 配置选项

### KSP 参数

| 参数名                         | 描述       | 默认值                   |
|-----------------------------|----------|-----------------------|
| `koin.modules.collector`    | 是否启用模块收集 | `false`               |
| `koin.modules.package.name` | 生成代码的包名  | `com.example.modules` |
| `koin.modules.file.name`    | 生成文件的类名  | `KoinModules`         |

### 注解说明

- `@KoinModule`: 标记需要被收集的 Koin 模块定义

## 开发指南

### 添加新模块

1. 创建新的 Android Library 模块
2. 在 `build.gradle` 中应用公共配置：
   ```gradle
   apply from: '../gradle/koin-dependencies.gradle'
   ```
3. 使用 `@KoinModule` 注解标记你的模块
4. 重新构建项目

### 自定义处理器

如需自定义处理器行为，可以修改 `processor` 模块中的 `KoinModuleSymbolProcessor.kt` 文件。

## 故障排除

### 常见问题

1. **KSP 处理器未运行**
   - 确保已应用 `com.google.devtools.ksp` 插件
   - 检查 KSP 配置参数是否正确

2. **依赖解析失败**
   - 确保已发布 annotation 和 processor 到本地仓库
   - 检查版本号是否一致

3. **生成的代码找不到**
   - 检查 KSP 参数配置
   - 确认 `koin.modules.collector` 设置为 `true`

### 调试技巧

- 使用 `--info` 参数查看详细构建日志
- 检查 `build/generated/ksp/` 目录下的生成文件
- 使用 `./gradlew :app:dependencies` 查看依赖树

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 更新日志

### v1.0.1-SNAPSHOT

- 迁移到本地仓库依赖
- 添加公共 Gradle 配置文件
- 优化模块配置流程
- 完善文档和使用指南

### v1.0.0

- 初始版本发布
- 基础的 KSP 注解处理器
- 多模块支持
- 自动模块收集功能