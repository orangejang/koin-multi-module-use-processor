# Koin 依赖配置使用指南

## 概述

`koin-dependencies.gradle` 文件提供了统一的 Koin annotation 和 processor 依赖配置，便于在多个模块中复用。

## 使用方法

### 1. 在模块的 build.gradle 中应用配置

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
    // Koin 相关依赖会自动添加，无需手动配置
}
```

### 2. 版本管理

所有 Koin 相关的版本都在 `gradle/koin-dependencies.gradle` 中统一管理：

```gradle
ext {
    koinAnnotationVersion = '1.0.1-SNAPSHOT'
    koinProcessorVersion = '1.0.1-SNAPSHOT'
    koinGroupId = 'com.example'
}
```

### 3. 自动添加的依赖

应用此配置后，会自动添加以下依赖：

- `implementation 'com.example:koin-annotation:1.0.1-SNAPSHOT'`
- `ksp 'com.example:koin-processor:1.0.1-SNAPSHOT'`

## 优势

1. **统一管理**: 所有 Koin 依赖版本在一个地方管理
2. **简化配置**: 新模块只需一行 `apply from` 即可获得完整配置
3. **版本一致性**: 确保所有模块使用相同版本的依赖
4. **易于维护**: 升级版本时只需修改一个文件

## 新增模块示例

创建新的 Android 库模块时，只需在 `build.gradle` 中添加：

```gradle
plugins {
    id 'com.android.library'
    id 'kotlin-android'
    id 'com.google.devtools.ksp'
}

// 一行配置即可获得完整的 Koin 依赖
apply from: '../gradle/koin-dependencies.gradle'

android {
    // Android 配置...
}

dependencies {
    // 其他项目特定的依赖...
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation "io.insert-koin:koin-android:$koin_version"
}
```

## 注意事项

1. 确保模块已应用 `com.google.devtools.ksp` 插件
2. 路径 `../gradle/koin-dependencies.gradle` 需要根据模块位置调整
3. 如需自定义配置，可以在应用公共配置后添加额外的依赖