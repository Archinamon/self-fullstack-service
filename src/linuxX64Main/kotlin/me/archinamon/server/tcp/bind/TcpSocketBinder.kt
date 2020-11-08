package me.archinamon.server.tcp.bind

import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

actual open class TcpSocketBinder<T : BinderService> actual constructor(
    protected val routeClass: KClass<out T>
) : SocketBinder {

    private val handlers: MutableMap<RouteCommand, CommonCallHandler> = HashMap()

    override fun find(cmd: RouteCommand): Boolean {
        return cmd in handlers.keys
    }

    override fun accept(cmd: RouteCommand): CommonCallHandler = handlers[cmd]
        ?: throw IllegalStateException("Nullable handler has been registered!") // should never happens

    protected actual inline fun <reified RET> bind(
        route: RouteCommand,
        noinline processor: suspend T.() -> RET
    ) = acceptHandler(route) { request: BindingRequest ->
        runBlocking {
            handlingError(request.command) {
                BinderService.provideService(routeClass)
                    .processor()
                    .let(request.command::success)
                    .also { response ->
                        respond(toText(response))
                    }
            }
        }
    }

    protected actual inline fun <reified PARAM : Any?, reified RET> bind(
        route: RouteCommand,
        noinline processor: suspend T.(PARAM?) -> RET
    ) = acceptHandler(route) { request: BindingRequest ->
        runBlocking {
            handlingError(request.command) {
                BinderService.provideService(routeClass)
                    .processor(request.parseData())
                    .let(request.command::success)
                    .also { response ->
                        respond(toText(response))
                    }
            }
        }
    }

    protected fun acceptHandler(cmd: RouteCommand, handler: CommonCallHandler) {
        if (cmd in handlers) {
            throw IllegalStateException("Handler for [$cmd] already registered!")
        }

        handlers[cmd] = handler
    }

    protected suspend inline fun <reified R> CallRouter.handlingError(
        cmd: RouteCommand,
        crossinline block: suspend () -> BindingResponse<R>
    ): BindingResponse<*> = try {
        block()
    } catch (any: Exception) {
        // first print exception to log
        any.printStackTrace()

        val message = if (any is ServiceException) {
            any.message
        } else "Error proceeding [$cmd] command. See logs for more details."

        BindingResponse(cmd, FAILURE, message).also { responseDto ->
            respond(toText(responseDto))
        }
    }
}