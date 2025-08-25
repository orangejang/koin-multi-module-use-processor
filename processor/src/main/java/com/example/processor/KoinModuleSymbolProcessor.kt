package com.example.processor

import com.example.annotation.KoinModule
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

class KoinModuleSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KoinModuleSymbolProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }
}

class KoinModuleSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    
    private var hasGenerated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(KoinModule::class.qualifiedName!!)
        val unprocessed = symbols.filterNot { it.validate() }.toList()
        
        // 只在第一次处理时生成文件，避免重复生成
        if (!hasGenerated) {
            try {
                generateKoinModulesClass(symbols.filterIsInstance<KSFunctionDeclaration>().toList())
                hasGenerated = true
            } catch (e: Exception) {
                logger.error("Error generating KoinModules: ${e.message}")
            }
        }
        
        return unprocessed
    }
    
    private fun generateKoinModulesClass(elements: List<KSFunctionDeclaration>) {
        val moduleFunctions = mutableListOf<Pair<String, String>>() // (packageName, functionName)
        
        // 收集当前扫描到的所有被@KoinModule注解的函数
        for (element in elements) {
            val packageName = element.packageName.asString()
            val functionName = element.simpleName.asString()
            
            moduleFunctions.add(Pair(packageName, functionName))
            logger.info("Found @KoinModule function: $packageName.$functionName")
        }
        
        // 读取预先收集的模块信息
        // 读取预先收集的模块信息
        try {
            // 从 KSP 选项中获取项目根目录
            val projectDir = options["projectDir"] ?: System.getProperty("user.dir", ".")
            val moduleInfoPath = "$projectDir/build/generated/koin/koin-modules.txt"
            val moduleInfoFile = File(moduleInfoPath)
            
            logger.info("Project dir from options: ${options["projectDir"]}")
            logger.info("Final project dir: $projectDir")
            logger.info("Looking for module info at: ${moduleInfoFile.absolutePath}")
            logger.info("File exists: ${moduleInfoFile.exists()}")

            moduleInfoFile.readLines().forEach { line ->
                if (line.isNotBlank()) {
                    val parts = line.split(":")
                    if (parts.size == 2) {
                        val pkg = parts[0]
                        val func = parts[1]
                        // 避免重复添加
                        if (!moduleFunctions.any { it.first == pkg && it.second == func }) {
                            moduleFunctions.add(Pair(pkg, func))
                            logger.info("Added external @KoinModule function: $pkg.$func")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn("Could not read external module info: ${e.message}")
        }
        
        // 生成KoinModules类
        val packageName = "com.example.koinmodules"
        val fileName = "KoinModules"
        
        val fileBuilder = FileSpec.builder(packageName, fileName)
            .addImport("org.koin.core.module", "Module")
        
        // 不添加函数导入，使用完全限定名调用
        
        val koinModulesClass = TypeSpec.objectBuilder(fileName)
            .addKdoc("自动生成的Koin模块收集类\n")
            .addKdoc("包含所有被@KoinModule注解标记的模块\n")
            .addKdoc("总共收集了 ${moduleFunctions.size} 个模块\n")
        
        // 添加getAllModules方法
        val getAllModulesFunc = FunSpec.builder("getAllModules")
            .returns(LIST.parameterizedBy(ClassName("org.koin.core.module", "Module")))
            .addKdoc("获取所有Koin模块\n")
            .addKdoc("@return 所有模块的列表\n")
        
        // 构建模块列表 - 使用完全限定名
        if (moduleFunctions.isNotEmpty()) {
            val moduleList = moduleFunctions.joinToString(",\n        ") { (pkg, func) ->
                "$pkg.$func()"
            }
            getAllModulesFunc.addStatement("return listOf(\n        $moduleList\n    )")
        } else {
            getAllModulesFunc.addStatement("return emptyList()")
        }
        
        koinModulesClass.addFunction(getAllModulesFunc.build())
        fileBuilder.addType(koinModulesClass.build())
        
        // 写入文件
        try {
            val dependencies = Dependencies(false)
            
            // 检查文件是否已经存在，如果存在则先删除
            try {
                val file = codeGenerator.createNewFile(
                    dependencies,
                    packageName,
                    fileName
                )
                
                file.use { outputStream ->
                    outputStream.writer().use { writer ->
                        fileBuilder.build().writeTo(writer)
                    }
                }
                
                logger.info("Generated KoinModules.kt with ${moduleFunctions.size} modules")
            } catch (fileExistsException: Exception) {
                if (fileExistsException.message?.contains("already exists") == true) {
                    logger.warn("KoinModules.kt already exists, skipping generation")
                } else {
                    throw fileExistsException
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to write KoinModules.kt: ${e.message}")
            logger.error("Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }
}