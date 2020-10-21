package me.archinamon.server.tcp.bind

import kotlinx.serialization.Serializable

@Serializable
data class BindingRequest<T : Any?>(val cmd: String, val data: T)