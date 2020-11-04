package me.archinamon.server.tcp.bind

import kotlin.reflect.KClass

expect open class TcpSocketBinder<T : BinderService>(routeClass: KClass<out T>) {

    protected inline fun <reified RET> bind(
        route: RouteCommand,
        noinline processor: suspend T.() -> RET
    )

    protected inline fun <reified PARAM, reified RET> bind(
        route: RouteCommand,
        noinline processor: suspend T.(PARAM) -> RET
    )
}