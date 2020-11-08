package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class BindingRequest(val cmd: String, val data: JsonStr? = null) {

    val command
        get() = RouteCommand(cmd)

    inline fun <reified T : Any?> parseData(json: Json): T? {
        return when (T::class) {
            String::class -> data as T
            Int::class -> data?.toIntOrNull() as T
            UInt::class -> data?.toUIntOrNull() as T
            Long::class -> data?.toLongOrNull() as T
            ULong::class -> data?.toULongOrNull() as T
            Float::class -> data?.toFloatOrNull() as T
            Byte::class -> data?.toByteOrNull() as T
            UByte::class -> data?.toUByteOrNull() as T
            Double::class -> data?.toDoubleOrNull() as T
            Short::class -> data?.toShortOrNull() as T
            UShort::class -> data?.toUShortOrNull() as T
            Boolean::class -> data?.toBoolean() as T
            else -> data?.let(json::decodeFromString)
        }
    }
}