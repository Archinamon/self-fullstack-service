package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable

@Serializable
data class BindingResponse<T>(val cmd: String, val result: Boolean, val data: T) {

    val command
        get() = RouteCommand(cmd)

    constructor(cmd: RouteCommand, result: Boolean, data: T) : this(cmd.value, result, data)
}