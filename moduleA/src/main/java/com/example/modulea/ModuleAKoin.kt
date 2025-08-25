package com.example.modulea

import androidx.annotation.Keep
import com.example.annotation.KoinModule
import org.koin.dsl.module

@Keep
@KoinModule
fun moduleAKoin() = module {
    // ModuleA 的依赖注入配置
    single { "ModuleA Service" }
}