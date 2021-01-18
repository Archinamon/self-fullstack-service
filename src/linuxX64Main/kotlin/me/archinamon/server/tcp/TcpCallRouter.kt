package me.archinamon.server.tcp

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import me.archinamon.posix.ensureUnixCallResult
import me.archinamon.server.tcp.bind.CallRouter
import me.archinamon.server.tcp.protocol.ProtocolAdapter
import platform.posix.recv
import platform.posix.send

class TcpCallRouter(
    override val clientDescriptor: Int,
    override val onDisconnectDispatcher: (fd: Int) -> Unit,
    override val supportedProtocols: Array<ProtocolAdapter>
) : CallRouter() {

    override suspend fun proceed() {
        val inputData: ByteArray = ByteArray(1024).usePinned { buffer ->
            val messageLength = recv(clientDescriptor, buffer.addressOf(0), buffer.get().size.convert(), 0)

            if (messageLength <= 0) {
                return onDisconnectDispatcher(clientDescriptor)
            }

            return@usePinned buffer.get()
        }

        acceptProtocol(inputData)
            ?.respond(this, inputData)
            ?: "Protocol not supported!".respond()
    }

    override fun String.respond() = encodeToByteArray().respond()

    override fun ByteArray.respond() {
        this.usePinned { buffer ->
            send(clientDescriptor, buffer.addressOf(0), buffer.get().size.convert(), 0)
                .ensureUnixCallResult("send") { ret -> ret >= 0 }
        }
    }
}