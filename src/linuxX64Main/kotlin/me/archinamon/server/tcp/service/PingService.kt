package me.archinamon.server.tcp.service

import me.archinamon.server.tcp.bind.BinderService
import me.archinamon.server.tcp.bind.NativeObjectBinder

@NativeObjectBinder(PingServiceImpl::class)
actual object PingServiceImpl : BinderService(), PingService {
    override suspend fun ping(): String {
        return "OK"
    }

    override suspend fun echo(data: String): String {
        return data
    }
}