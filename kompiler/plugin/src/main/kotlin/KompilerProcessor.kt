package me.archinamon.tcp.server.plugin

import de.jensklingenberg.mpapt.model.AbstractProcessor
import de.jensklingenberg.mpapt.model.RoundEnvironment
import de.jensklingenberg.mpapt.utils.KotlinPlatformValues
import org.jetbrains.kotlin.platform.TargetPlatform
import java.io.File
import me.archinamon.tcp.server.graph.TcpService

class KompilerProcessor(private val generatedRootDir: File) : AbstractProcessor() {

    private val serviceClass = TcpService::class.qualifiedName!!

    override fun process(roundEnvironment: RoundEnvironment) {
    }

    override fun isTargetPlatformSupported(platform: TargetPlatform): Boolean {
        return when (val targetName = platform.first().platformName) {
            KotlinPlatformValues.JS -> true
            KotlinPlatformValues.JVM -> false
            KotlinPlatformValues.NATIVE -> true
            else -> {
                log(targetName)
                false
            }
        }
    }

    override fun getSupportedAnnotationTypes() = setOf(serviceClass)
}