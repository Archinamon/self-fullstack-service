package me.archinamon.server.tcp.bind

import me.archinamon.web.json.serialize.BeforeExecCall
import me.archinamon.web.json.serialize.CallExecCallback
import me.archinamon.web.json.serialize.JsCallSerializer
import kotlin.reflect.KClass

class TcpCallAgent<S : BinderService>(
    tcpBinder: TcpSocketBinder<S>,
    override var beforeSend: BeforeExecCall? = null
) : JsCallSerializer, CallExecCallback {

    private val calls = tcpBinder.boundCalls()

    suspend inline fun <reified RET : Any, T> call(
        noinline function: suspend T.() -> RET
    ): RET {
        return null as RET
    }

    suspend inline fun <reified PARAM : Any, reified RET : Any, T> call(
        noinline function: suspend T.(PARAM) -> RET,
        param: PARAM
    ): RET {
        return null as RET
    }

    suspend inline fun <reified PARAM : Any?, reified RET : Any, T> call(
        noinline function: suspend T.(PARAM?) -> RET,
        param: PARAM?
    ): RET {
        return null as RET
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun exceptionHelper(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            console.log(e)
        }
    }

    inline fun <reified PAR> serialize(value: PAR): String? {
        return value?.let {
            @Suppress("UNCHECKED_CAST")
            trySerialize((PAR::class as KClass<Any>), it as Any)
        }
    }
}