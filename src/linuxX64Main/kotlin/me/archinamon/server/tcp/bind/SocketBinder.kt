package me.archinamon.server.tcp.bind

interface SocketBinder {

    fun find(cmd: RouteCommand): Boolean

    fun accept(cmd: RouteCommand): CommonCallHandler
}