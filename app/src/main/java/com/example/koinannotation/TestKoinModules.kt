package com.example.koinannotation

import com.example.koinmodules.KoinModules
import org.koin.core.context.startKoin

/**
 * 测试生成的 KoinModules 类
 */
class TestKoinModules {
    
    fun initializeKoin() {
        // 使用自动生成的 KoinModules 来初始化 Koin
        startKoin {
            modules(KoinModules.getAllModules())
        }
        
        println("成功初始化了 ${KoinModules.getAllModules().size} 个 Koin 模块")
    }
}