package me.archinamon.tcp.server.plugin

import org.jetbrains.kotlin.config.CompilerConfigurationKey

const val GENERATED_SRC_PATH = "gen-src-path"

object CommandlineArguments {
    val GENERATED_PATH: CompilerConfigurationKey<String> = CompilerConfigurationKey.create("gen path")
}