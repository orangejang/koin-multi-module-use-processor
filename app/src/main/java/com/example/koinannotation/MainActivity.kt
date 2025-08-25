package com.example.koinannotation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.modulec.ModuleCService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    
    // 注入服务示例
    private val moduleCService: ModuleCService by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 使用注入的服务
        println("ModuleC Service: ${moduleCService.getServiceName()}")
    }
}