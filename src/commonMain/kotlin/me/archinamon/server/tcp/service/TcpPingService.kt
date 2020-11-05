package me.archinamon.server.tcp.service

import me.archinamon.tcp.server.graph.TcpService

@TcpService
interface TcpPingService {
    suspend fun ping(): String
}