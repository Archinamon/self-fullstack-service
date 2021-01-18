package me.archinamon.server.tcp.protocol.ws

import me.archinamon.server.tcp.bind.CallRouter
import me.archinamon.server.tcp.protocol.ProtocolAdapter

class WebSocketAdapter(private val decorator: ProtocolAdapter) : ProtocolAdapter() {

    override fun isExpected(input: ByteArray): Boolean {
        return false
    }

    override suspend fun respond(router: CallRouter, input: ByteArray) {
        TODO("Not yet implemented")
    }
}