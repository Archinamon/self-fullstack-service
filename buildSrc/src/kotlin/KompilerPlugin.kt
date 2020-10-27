package me.archinamon.tcp.gradle.plugin

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.io.File
import me.archinamon.tcp.server.plugin.KompilerCLIProcessor
import me.archinamon.tcp.server.plugin.GENERATED_SRC_PATH

class KompilerPlugin : KotlinCompilerPluginSupportPlugin {

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        return project.provider {
            listOf(
                SubpluginOption(GENERATED_SRC_PATH, lazy {
                    File(project.buildDir, "generated-src/kompiler-plugin")
                        .absolutePath
                })
            )
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = KompilerCLIProcessor.PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "me.archinamon.tcp.server",
        artifactId = "plugin",
        version = "latest"
    )

    override fun getPluginArtifactForNative(): SubpluginArtifact? = getPluginArtifact()
}