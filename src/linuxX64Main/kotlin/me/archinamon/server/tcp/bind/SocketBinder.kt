package me.archinamon.server.tcp.bind

interface SocketBinder {

    fun connected()

    fun disconnected()
}