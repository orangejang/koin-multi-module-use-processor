package com.example.modulec

import com.example.annotation.KoinModule
import org.koin.dsl.module

/**
 * ModuleC 的 Koin 模块
 */
@KoinModule
fun moduleCKoin() = module {
    single<ModuleCService> { ModuleCServiceImpl() }
}

interface ModuleCService {
    fun getServiceName(): String
}

class ModuleCServiceImpl : ModuleCService {
    override fun getServiceName(): String = "ModuleC Service"
}