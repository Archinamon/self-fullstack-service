package me.archinamon.server.tcp.protocol

import me.archinamon.server.tcp.bind.CallRouter

abstract class ProtocolAdapter {

    protected fun ByteArray.readAsString() = decodeToString()
        .trim('\u0000', '\n', '\t', '\r')

    abstract fun isExpected(input: ByteArray): Boolean

    abstract suspend fun respond(router: CallRouter, input: ByteArray)
}