package me.archinamon.server.tcp

import me.archinamon.server.tcp.service.PingServiceBinder

class TcpBindersProvider {

    fun get() = arrayOf(
        PingServiceBinder
    )
}