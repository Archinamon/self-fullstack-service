package me.archinamon.server.tcp.protocol.http

import me.archinamon.server.tcp.bind.CallRouter
import me.archinamon.server.tcp.protocol.ProtocolAdapter

class HttpAdapter : ProtocolAdapter() {

    override fun isExpected(input: ByteArray): Boolean {
        return false
    }

    override suspend fun respond(router: CallRouter, input: ByteArray) {
        TODO("Not yet implemented")
    }
}