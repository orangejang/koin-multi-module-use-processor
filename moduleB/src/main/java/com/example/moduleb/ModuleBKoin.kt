package com.example.moduleb

import androidx.annotation.Keep
import com.example.annotation.KoinModule
import org.koin.dsl.module

@Keep
@KoinModule
fun moduleBKoin() = module {
    // ModuleB 的依赖注入配置
    single { "ModuleB Service" }
    factory { "ModuleB Factory" }
}