package me.archinamon.server.tcp.bind

import me.archinamon.server.tcp.TcpProtoMethod
import kotlin.reflect.KClass

actual open class TcpSocketBinder<T : Any> actual constructor(routeClass: KClass<out T>) : SocketBinder {

    override fun connected() {
        println("${this::class.qualifiedName} connected")
    }

    override fun disconnected() {
        println("${this::class.qualifiedName} disconnected")
    }

    protected actual fun bindCmd(route: String, processor: suspend T.(String) -> Boolean) {
    }

    protected actual inline fun <reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.() -> RET
    ) {
    }

    protected actual inline fun <reified PARAM, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM) -> RET
    ) {
    }

    protected actual inline fun <reified PARAM1, reified PARAM2, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM1, PARAM2) -> RET
    ) {
    }

    protected actual inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3) -> RET
    ) {
    }

    protected actual inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4) -> RET
    ) {
    }

    protected actual inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified PARAM5, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5) -> RET
    ) {
    }

    protected actual inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified PARAM5, reified PARAM6, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5, PARAM6) -> RET
    ) {
    }

    protected actual inline fun <reified PARAM1, reified PARAM2, reified PARAM3, reified PARAM4, reified PARAM5, reified PARAM6, reified PARAM7, reified RET> bind(
        route: String,
        protoMethod: TcpProtoMethod,
        noinline processor: suspend T.(PARAM1, PARAM2, PARAM3, PARAM4, PARAM5, PARAM6, PARAM7) -> RET
    ) {
    }

}