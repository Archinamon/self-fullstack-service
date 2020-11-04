package me.archinamon.server.tcp.bind

inline class RouteCommand(val value: String)

internal typealias JsonStr = String

internal fun JsonStr.isJson(trailingLength: Int = 5): Boolean {
    return startsWith('{') && (endsWith('}') || (lastIndexOf('}') >= length - trailingLength))
}