package com.daromi.echo

import com.daromi.echo.server.UdsEchoServer

fun main() {
    // val server = TcpEchoServer.create(Environment.TcpServer.port)
    val server = UdsEchoServer.create(Environment.UdsServer.socketPath)

    server.start()
}
