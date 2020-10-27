package me.archinamon.server.tcp.bind

internal typealias JsonStr = String

internal fun JsonStr.isJson(trailingLength: Int = 5): Boolean {
    return startsWith('{') && (endsWith('}') || (lastIndexOf('}') >= length - trailingLength))
}