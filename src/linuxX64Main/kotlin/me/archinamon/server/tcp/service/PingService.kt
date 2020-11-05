package me.archinamon.server.tcp.service

import me.archinamon.server.tcp.bind.BinderService
import me.archinamon.server.tcp.bind.NativeObjectBinder
import me.archinamon.server.tcp.bind.RouteCommand
import me.archinamon.server.tcp.bind.TcpSocketBinder

//todo: codegen all this file with kompiler:plugin

@NativeObjectBinder(PingServiceImpl::class)
object PingServiceImpl : BinderService(), PingService {
    override suspend fun ping(): String {
        return "OK"
    }
}

object PingServiceBinder : TcpSocketBinder<PingServiceImpl>(PingServiceImpl::class) {
    init {
        bind(RouteCommand("ping"), PingService::ping)
    }
}