package com.example.koinannotation

import android.app.Application
import com.example.koinmodules.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化Koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            // 使用自动生成的KoinModules类加载所有模块
            modules(KoinModules.getAllModules())
        }
    }
}