package me.archinamon.server

import kotlinx.cinterop.memScoped

// !!should be the same as `cgi.server.runningPort` value in gradle.properties file!!
@ExperimentalUnsignedTypes
private const val DEV_PORT: UShort = 3434U

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val serverPort = if (args.isNotEmpty()) args.first().toUShort() else DEV_PORT
    val testEnv = if (args.size > 1) args.last().toBoolean() else false

    with(Server(testEnv, serverPort)) {
        do {
            memScoped {
                handleConnections().let { readSet ->
                    acceptClient(readSet)
                    handleClients(readSet)
                }
            }
        } while (true)
    }
}