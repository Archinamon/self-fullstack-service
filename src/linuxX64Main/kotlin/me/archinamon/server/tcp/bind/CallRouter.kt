package me.archinamon.server.tcp.bind

import me.archinamon.server.tcp.protocol.ProtocolAdapter

abstract class CallRouter {

    protected abstract val clientDescriptor: Int
    protected abstract val onDisconnectDispatcher: (fd: Int) -> Unit
    protected abstract val supportedProtocols: Array<ProtocolAdapter>

    protected fun acceptProtocol(input: ByteArray): ProtocolAdapter? {
        return supportedProtocols.find { adapter -> adapter.isExpected(input) }
    }

    abstract suspend fun proceed()

    abstract fun String.respond()

    abstract fun ByteArray.respond()

    inline fun <reified R> respond(response: BindingResponse<R>) {
        JsonTranslator.translate(response).respond()
    }
}