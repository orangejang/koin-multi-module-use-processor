package com.example.annotation

/**
 * 用于标记Koin模块的注解
 * 被此注解标记的函数将被自动收集到KoinModules中
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class KoinModule
