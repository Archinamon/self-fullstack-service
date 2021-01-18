package me.archinamon.server.tcp.protocol.jrsc

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.archinamon.server.tcp.bind.*
import me.archinamon.server.tcp.protocol.ProtocolAdapter

class JRSCAdapter(private val binderSearch: (request: BindingRequest) -> SocketBinder?) : ProtocolAdapter() {

    override fun isExpected(input: ByteArray): Boolean {
        val request = input.readAsString()
        return request.isNotBlank() && request.isJson()
    }

    override suspend fun respond(router: CallRouter, input: ByteArray) {
        val inputMessage: JsonStr = input.readAsString()
        if (inputMessage.isBlank() || !inputMessage.isJson()) {
            println("jrsc: empty/wrong input")
            return
        }

        val request = try {
            JsonTranslator.translate<BindingRequest>(inputMessage)
                .also { println("Requesting... ($it)") }
        } catch (notDecoded: SerializationException) {
            notDecoded.printStackTrace()
            error("Error trying to unmarshal incoming request")
                .let(router::respond)

            return
        }

        val binder = binderSearch(request)
            ?: return error(request)
                .let(router::respond)

        val handler = binder.accept(request.command)
        router.handler(request)
            .also { response ->
                println("Output message: [$response]")
            }
    }

    private inline fun <reified R> toText(value: BindingResponse<R>): String {
        return JsonTranslator.translate(value)
    }

    private fun error(request: BindingRequest): BindingResponse<String> {
        return BindingResponse(request.command, false, "Route handler not found!")
    }

    private fun error(message: String): BindingResponse<String> {
        return BindingResponse("[empty]", false, message)
    }
}