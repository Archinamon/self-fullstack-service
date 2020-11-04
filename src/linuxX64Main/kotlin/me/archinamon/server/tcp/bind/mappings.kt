package me.archinamon.server.tcp.bind

const val SUCCESS = true
const val FAILURE = false

inline fun <reified R> RouteCommand.success(data: R): BindingResponse<R> {
    return BindingResponse(this, SUCCESS, data)
}