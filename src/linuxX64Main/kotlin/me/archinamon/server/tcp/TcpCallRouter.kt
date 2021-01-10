package me.archinamon.server.tcp

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.serialization.SerializationException
import me.archinamon.posix.ensureUnixCallResult
import me.archinamon.server.tcp.bind.BindingRequest
import me.archinamon.server.tcp.bind.BindingResponse
import me.archinamon.server.tcp.bind.CallRouter
import me.archinamon.server.tcp.bind.JsonStr
import me.archinamon.server.tcp.bind.SocketBinder
import me.archinamon.server.tcp.bind.isJson
import platform.posix.recv
import platform.posix.send

class TcpCallRouter(
    override val clientDescriptor: Int,
    override val onDisconnectDispatcher: (fd: Int) -> Unit
) : CallRouter() {

    override suspend fun proceed(binderSearch: (request: BindingRequest) -> SocketBinder?) {
        val incomeMessage: JsonStr = ByteArray(1024).usePinned { buffer ->
            val messageLength = recv(clientDescriptor, buffer.addressOf(0), buffer.get().size.convert(), 0)

            if (messageLength <= 0) {
                return onDisconnectDispatcher(clientDescriptor)
            }

            val rawStr = buffer.get().decodeToString()

            return@usePinned rawStr
                .also { println("Input message: [$it]") }
                .substring(0, rawStr.lastIndexOf('}') + 1)
        }

        if (incomeMessage.isBlank() || !incomeMessage.isJson()) {
            return
        }

        val request = try {
            parser.decodeFromString(BindingRequest.serializer(), incomeMessage)
                .also { println("Requesting... ($it)") }
        } catch (notDecoded: SerializationException) {
            notDecoded.printStackTrace()
            respondError("Error trying to unmarshal incoming request")
            return
        }

        val binder = binderSearch(request)
        if (binder == null) {
            respondError(request)
            return
        }

        val handler = binder.accept(request.command)
        this.handler(request)
            .also { println("Output message: [$it]") }
    }

    override fun respond(response: String) {
        println("Responding... ($response)")
        response.sendln()
    }

    private fun respondError(request: BindingRequest) {
        val response = BindingResponse(request.command, false, "Route handler not found!")
        respond(toText(response))
    }

    private fun respondError(message: String) {
        val response = BindingResponse("[empty]", false, message)
        respond(toText(response))
    }

    @Suppress("SpellCheckingInspection")
    private fun String.sendln() = "$this\n".encodeToByteArray().usePinned { buffer ->
        send(clientDescriptor, buffer.addressOf(0), buffer.get().size.convert(), 0)
            .ensureUnixCallResult("send") { ret -> ret >= 0 }
    }
}