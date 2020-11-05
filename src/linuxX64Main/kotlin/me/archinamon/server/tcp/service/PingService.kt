package me.archinamon.server.tcp.service

import me.archinamon.server.tcp.bind.BinderService
import me.archinamon.server.tcp.bind.NativeObjectBinder
import me.archinamon.server.tcp.bind.RouteCommand
import me.archinamon.server.tcp.bind.TcpSocketBinder

//todo: codegen all this file with kompiler:plugin

@NativeObjectBinder(PingService::class)
object PingService : BinderService(), TcpPingService {
    override suspend fun ping(): String {
        return "OK"
    }
}

object PingServiceBinder : TcpSocketBinder<PingService>(PingService::class) {
    init {
        bind(RouteCommand("ping"), TcpPingService::ping)
    }
}