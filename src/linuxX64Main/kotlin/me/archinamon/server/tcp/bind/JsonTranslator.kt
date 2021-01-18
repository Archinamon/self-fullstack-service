package me.archinamon.server.tcp.bind

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonTranslator {

    val parser = Json {
        isLenient = true
    }

    inline fun <reified R> translate(input: String) = parser.decodeFromString<R>(input)

    inline fun <reified T> translate(input: T) = parser.encodeToString(input)
}