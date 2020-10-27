package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable

@Serializable
data class BindingRequest(val cmd: String, val data: JsonStr)