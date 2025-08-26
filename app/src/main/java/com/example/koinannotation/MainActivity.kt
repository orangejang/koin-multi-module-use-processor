package com.example.koinannotation

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.modulec.ModuleCService
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    // 注入服务示例
    private val moduleCService: ModuleCService by inject()

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.textView).text =
            "ModuleC Service: ${moduleCService.getServiceName()}"
    }
}