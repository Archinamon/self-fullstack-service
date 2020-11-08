package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable

@Serializable
data class BindingResponse<T : Any?>(val cmd: String, val result: Boolean, val data: T?) {

    constructor(cmd: RouteCommand, result: Boolean, data: T?) : this(cmd.value, result, data)
}