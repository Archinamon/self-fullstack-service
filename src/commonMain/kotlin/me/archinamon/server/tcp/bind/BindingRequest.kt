package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class BindingRequest(val cmd: String, val data: JsonStr) {

    val command
        get() = RouteCommand(cmd)

    inline fun <reified T> parseData(): T {
        return Json.decodeFromString(data)
    }
}