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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

class KoinModuleSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(KoinModule::class.java.canonicalName)

        symbols.forEach { symbol ->
            if (symbol is KSFunctionDeclaration) {
                val packageName = symbol.packageName.asString()
                val functionName = symbol.simpleName.asString()

                logger.info("Found @KoinModule function: $packageName.$functionName")
                // 将信息写入共享文件
                writeToSharedFile(packageName, functionName)
            }
        }

        return emptyList()
    }

    private fun writeToSharedFile(packageName: String, functionName: String) {
        try {
            val sharedDir = File("build/generated/koin")
            if (!sharedDir.exists()) {
                sharedDir.mkdirs()
            }

            val sharedFile = File(sharedDir, "koin-modules.txt")
            val moduleInfo = "$packageName:$functionName"

            // 读取现有内容，避免重复
            val existingLines = if (sharedFile.exists()) {
                sharedFile.readLines().toMutableSet()
            } else {
                mutableSetOf()
            }

            existingLines.add(moduleInfo)
            sharedFile.writeText(existingLines.joinToString("\n"))

            logger.info("Written module info to shared file: $moduleInfo")
        } catch (e: Exception) {
            logger.warn("Could not write to shared file: ${e.message}")
        }
    }

    override fun finish() {
        // 检查是否应该生成KoinModules类（只在moduleC中生成）
        val shouldGenerateKoinModules = options["koin.modules.collector"] == "true"

        if (shouldGenerateKoinModules) {
            val moduleFunctions = mutableListOf<Pair<String, String>>()
            // 从共享文件读取所有模块信息
            try {
                val moduleInfoFile = File("build/generated/koin/koin-modules.txt")
                if (moduleInfoFile.exists()) {
                    val allModuleInfo = moduleInfoFile.readLines()

                    for (line in allModuleInfo) {
                        if (line.isNotBlank() && line.contains(":")) {
                            val parts = line.split(":")
                            if (parts.size == 2) {
                                moduleFunctions.add(parts[0] to parts[1])
                            }
                        }
                    }
                    logger.info("Loaded ${moduleFunctions.size} modules from shared file")
                }
            } catch (e: Exception) {
                logger.warn("Could not read shared module info: ${e.message}")
            }

            // 生成KoinModules类
            val packageName = options["koin.modules.package.name"] ?: "com.cvte.ciot.koinmodules"
            val className = options["koin.modules.file.name"] ?: "KoinModules"
            generateKoinModulesClass(moduleFunctions, packageName, className)
        } else {
            logger.info("Skipping KoinModules generation (not collector module)")
        }
    }

    private fun generateKoinModulesClass(
        moduleFunctions: List<Pair<String, String>>,
        packageName: String,
        fileName: String
    ) {
        val moduleClassName = ClassName("org.koin.core.module", "Module")
        val fileBuilder = FileSpec.builder(packageName, fileName)
            .addImport(moduleClassName.packageName, moduleClassName.simpleName)

        val koinModulesClass = TypeSpec.objectBuilder(fileName)
            .addKdoc("自动生成的Koin模块收集类\n")
            .addKdoc("包含所有被@KoinModule注解标记的模块\n")
            .addKdoc("总共收集了 ${moduleFunctions.size} 个模块\n")

        // 添加getAllModules方法 - 使用反射调用
        val getAllModulesFunc = FunSpec.builder("getAllModules")
            .returns(LIST.parameterizedBy(moduleClassName))
            .addKdoc("获取所有Koin模块\n")
            .addKdoc("@return 所有模块的列表\n")

        // 构建直接import调用代码（不使用反射）
        if (moduleFunctions.isNotEmpty()) {
            getAllModulesFunc.addStatement("val modules = mutableListOf<Module>()")

            moduleFunctions.forEach { (pkg, func) ->
                // 添加import语句
                fileBuilder.addImport(pkg, func)

                // 直接调用函数
                getAllModulesFunc.addStatement(
                    """
                    try {
                        modules.add($func())
                        println("成功加载Koin模块: ${pkg}.$func")
                    } catch (ex: Exception) {
                        println("模块加载失败: ${pkg}.$func - " + ex.javaClass.simpleName + ": " + ex.message)
                    }
                """.trimIndent()
                )
            }

            getAllModulesFunc.addStatement("return modules")
        } else {
            getAllModulesFunc.addStatement("return emptyList()")
        }

        koinModulesClass.addFunction(getAllModulesFunc.build())
        fileBuilder.addType(koinModulesClass.build())

        // 写入文件
        try {
            val dependencies = Dependencies(false)

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

            logger.info("Generated KoinModules.kt with ${moduleFunctions.size} modules using reflection")
        } catch (e: Exception) {
            logger.error("Failed to generate KoinModules.kt: ${e.message}")
        }
    }
}

class KoinModuleSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KoinModuleSymbolProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }
}
