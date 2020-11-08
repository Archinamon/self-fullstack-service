package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class BindingRequest(val cmd: String, val data: JsonStr? = null) {

    val command
        get() = RouteCommand(cmd)

    inline fun <reified T : Any?> parseData(): T? {
        return data?.let(Json::decodeFromString)
    }
}