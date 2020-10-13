package me.archinamon.server

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.posix.AF_INET
import platform.posix.SOCK_STREAM
import platform.posix.accept
import platform.posix.bind
import platform.posix.init_sockets
import platform.posix.listen
import platform.posix.memset
import platform.posix.posix_htons
import platform.posix.recv
import platform.posix.send
import platform.posix.sockaddr_in
import platform.posix.socket

// !!should be the same as `cgi.server.runningPort` value in gradle.properties file!!
private const val DEV_PORT: Short = 3434

fun main(args: Array<String>) {
    val serverPort = if (args.isNotEmpty()) args.first().toShort() else DEV_PORT
    val testEnv = if (args.size > 1) args.last().toBoolean() else false

    // Initialize sockets in platform-dependent way.
    init_sockets()

    println("Start TCP server listening on $serverPort port.")

    memScoped {
        val buffer = ByteArray(1024)
        val prefixBuffer = "echo: ".encodeToByteArray()
        val serverAddr = alloc<sockaddr_in>()

        val listenFd = socket(AF_INET, SOCK_STREAM, 0)
            .ensureUnixCallResult("socket") { ret -> ret != -1 }

        serverAddr.apply {
            memset(this.ptr, 0, sockaddr_in.size.convert())
            sin_family = AF_INET.convert()
            sin_port = posix_htons(serverPort).convert()
        }

        bind(listenFd, serverAddr.ptr.reinterpret(), sockaddr_in.size.convert())
            .ensureUnixCallResult("bind") { ret -> ret == 0 }

        listen(listenFd, 10)
            .ensureUnixCallResult("listen") { ret -> ret == 0 }

        val commFd = accept(listenFd, null, null)
            .ensureUnixCallResult("accept") { ret -> ret != -1 }

        buffer.usePinned { pinned ->
            while (true) {
                val length = recv(commFd, pinned.addressOf(0), buffer.size.convert(), 0).toInt()
                    .ensureUnixCallResult("read") { ret -> ret >= 0 }

                if (length == 0) {
                    break
                }

                val cmd = pinned.get().decodeToString()
                println("Incoming command:\n$cmd")

                send(commFd, prefixBuffer.refTo(0), prefixBuffer.size.convert(), 0)
                    .ensureUnixCallResult("write") { ret -> ret >= 0 }
                send(commFd, pinned.addressOf(0), length.convert(), 0)
                    .ensureUnixCallResult("write") { ret -> ret >= 0 }
            }
        }
    }
}