package me.archinamon.server

import kotlinx.cinterop.MemScope
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.posix.AF_INET
import platform.posix.F_SETFL
import platform.posix.INADDR_ANY
import platform.posix.O_NONBLOCK
import platform.posix.SOCK_STREAM
import platform.posix.accept
import platform.posix.bind
import platform.posix.close
import platform.posix.fcntl
import platform.posix.fd_set
import platform.posix.htonl
import platform.posix.htons
import platform.posix.init_sockets
import platform.posix.listen
import platform.posix.memset
import platform.posix.posix_FD_ISSET
import platform.posix.posix_FD_SET
import platform.posix.posix_FD_ZERO
import platform.posix.recv
import platform.posix.select
import platform.posix.send
import platform.posix.sockaddr_in
import platform.posix.socket
import platform.posix.timeval
import kotlin.math.max

@ExperimentalUnsignedTypes
class AsyncTCPServer(
    private val port: UShort
) {

    private companion object {
        const val MAX_CONNECTIONS = 10
    }

    private var socketDescriptor: Int = -1
    private val clients = mutableSetOf<Int>()

    init {
        // Initialize sockets in platform-dependent way.
        init_sockets()

        // listen to public tcp port...
        socketDescriptor = buildSocket()

        println("Start TCP server listening on $port port.")
    }

    fun MemScope.handleConnections(): fd_set {
        val readEvents = alloc<fd_set>()
        posix_FD_ZERO(readEvents.ptr)
        posix_FD_SET(socketDescriptor, readEvents.ptr)

        clients.forEach { clientFd ->
            posix_FD_SET(clientFd, readEvents.ptr)
        }

        val timeout = alloc<timeval>().apply {
            tv_sec = 15
            tv_usec = 0
        }

        val max = if (clients.any()) max(socketDescriptor, max(clients.first(), clients.last())) else socketDescriptor
        select(max + 1, readEvents.ptr, null, null, timeout.ptr)
            .ensureUnixCallResult("select") { ret -> ret >= 0 }

        return readEvents
    }

    fun acceptClient(events: fd_set) {
        if (posix_FD_ISSET(socketDescriptor, events.ptr) > 0) {
            println("Incoming connection...")

            val incomeConnection = accept(socketDescriptor, null, null)
                .ensureUnixCallResult("accept") { ret -> ret != -1 }

            fcntl(incomeConnection, F_SETFL, O_NONBLOCK)
                .ensureUnixCallResult("fcntl") { ret -> ret != -1 }

            clients += incomeConnection
        }
    }

    fun handleClients(readSet: fd_set) {
        clients.forEach { clientFd ->
            if (posix_FD_ISSET(clientFd, readSet.ptr) > 0) {
                println("Awaiting client input...")

                ByteArray(1024).usePinned { buffer ->
                    val messageLength = recv(clientFd, buffer.addressOf(0), buffer.get().size.convert(), 0)

                    if (messageLength <= 0) {
                        close(clientFd)
                        clients.remove(clientFd)

                        println("Client disconnects... close connection.")
                        return@forEach
                    }

                    println("Input message: [${buffer.get().decodeToString()}]")
                    send(clientFd, buffer.addressOf(0), messageLength.convert(), 0)
                        .ensureUnixCallResult("send") { ret -> ret >= 0 }
                }
            }
        }
    }

    private fun buildSocket(): Int = memScoped {
        val serverAddr = alloc<sockaddr_in>()

        socketDescriptor = socket(AF_INET, SOCK_STREAM, 0)
            .ensureUnixCallResult("socket") { ret -> ret != -1 }

        fcntl(socketDescriptor, F_SETFL, O_NONBLOCK)
            .ensureUnixCallResult("fcntl") { ret -> ret != -1 }

        serverAddr.apply {
            memset(this.ptr, 0, sockaddr_in.size.convert())
            sin_family = AF_INET.convert()
            sin_port = htons(port)
            sin_addr.s_addr = htonl(INADDR_ANY)
        }

        bind(socketDescriptor, serverAddr.ptr.reinterpret(), sockaddr_in.size.convert())
            .ensureUnixCallResult("bind") { ret -> ret == 0 }

        listen(socketDescriptor, MAX_CONNECTIONS)
            .ensureUnixCallResult("listen") { ret -> ret == 0 }
    }
}
