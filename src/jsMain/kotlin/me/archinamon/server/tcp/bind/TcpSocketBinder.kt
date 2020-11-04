package me.archinamon.server.tcp.bind

import kotlin.reflect.KClass

actual open class TcpSocketBinder<T : BinderService> actual constructor(routeClass: KClass<out T>) {

    protected val calls: MutableMap<String, String> = HashMap()

    protected actual inline fun <reified RET> bind(
        route: RouteCommand,
        noinline processor: suspend T.() -> RET
    ) {
        calls[processor.toString().replace("\\s".toRegex(), "")] = "tcp/$route"
    }

    protected actual inline fun <reified PARAM, reified RET> bind(
        route: RouteCommand,
        noinline processor: suspend T.(PARAM) -> RET
    ) {
        calls[processor.toString().replace("\\s".toRegex(), "")] = "tcp/$route"
    }
}