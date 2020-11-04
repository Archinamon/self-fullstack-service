package me.archinamon.server.tcp.bind

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class CallRouter {

    protected abstract val clientDescriptor: Int
    protected abstract val onDisconnectDispatcher: (fd: Int) -> Unit

    val parser = Json {
        isLenient = true
    }

    abstract suspend fun proceed(binderSearch: (request: BindingRequest) -> SocketBinder?)

    abstract fun respond(response: String)

    inline fun <reified R> toText(value: BindingResponse<R>): String {
        return parser.encodeToString(value)
    }
}