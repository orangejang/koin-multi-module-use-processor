package com.example.koinannotation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    
    // 注入服务示例
    private val moduleAService: String by inject()
    private val moduleBService: String by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 使用注入的服务
        println("ModuleA Service: $moduleAService")
        println("ModuleB Service: $moduleBService")
    }
}