package me.archinamon.server

import kotlinx.cinterop.memScoped

// !!should be the same as `cgi.server.runningPort` value in gradle.properties file!!
@ExperimentalUnsignedTypes
private const val DEV_PORT: UShort = 3434U

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val serverPort = if (args.isNotEmpty()) args.first().toUShort() else DEV_PORT

    with(AsyncTCPServer(serverPort)) {
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