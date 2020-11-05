package me.archinamon.server.tcp.service

import me.archinamon.tcp.server.graph.TcpService

@TcpService
interface PingService {
    suspend fun ping(): String
}