package me.archinamon.tcp.server.plugin

import de.jensklingenberg.mpapt.common.MpAptProject
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension
import java.io.File

open class KompilerComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        val generatedPath = configuration.get(CommandlineArguments.GENERATED_PATH) ?: "generated/kompiler-plugin"
        val generatedDir = File(generatedPath).apply {
            mkdirs()
        }

        val processor = KompilerProcessor(generatedDir)
        val mpapt = MpAptProject(processor, configuration)

        StorageComponentContainerContributor.registerExtension(project, mpapt)
        ClassBuilderInterceptorExtension.registerExtension(project, mpapt)
        JsSyntheticTranslateExtension.registerExtension(project, mpapt)
        IrGenerationExtension.registerExtension(project, mpapt)
    }
}