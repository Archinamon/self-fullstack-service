package me.archinamon.tcp.server.plugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class KompilerCLIProcessor : CommandLineProcessor {
    companion object {
        val GENERATED_PATH_OPTION = CliOption(
            GENERATED_SRC_PATH,
            "<name>",
            CommandlineArguments.GENERATED_PATH.toString(),
            required = true,
            allowMultipleOccurrences = false
        )

        const val PLUGIN_ID = "TcpServerKompilerPlugin" //has to be the same as the plugin id in KompilerSubplugin
    }

    override val pluginId = PLUGIN_ID
    override val pluginOptions = listOf(GENERATED_PATH_OPTION)

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        return when (option) {
            GENERATED_PATH_OPTION -> configuration.put(CommandlineArguments.GENERATED_PATH, value)
            else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
        }
    }
}