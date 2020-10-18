package me.archinamon.server.tcp.bind

import kotlin.reflect.KClass

expect open class TcpSocketBinder<T : Any>(routeClass: KClass<out T>) {

    protected inline fun <reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.() -> RET
    )

    protected inline fun <reified PARAM, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM) -> RET
    )

    protected inline fun <reified PARAM1, reified PARAM2, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM1, PARAM2) -> RET
    )

    protected inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3) -> RET
    )

    protected inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4) -> RET
    )

    protected inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified PARAM5, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5) -> RET
    )

    protected inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified PARAM5, reified PARAM6, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5, PARAM6) -> RET
    )

    protected inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified PARAM5, reified PARAM6, reified PARAM7, reified RET> bind(
        route: String,
        method: HttpMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5, PARAM6, PARAM7) -> RET
    )
}