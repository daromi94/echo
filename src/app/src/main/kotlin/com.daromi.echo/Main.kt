package com.daromi.echo

import com.daromi.echo.server.TcpEchoServer

fun main() {
    val server = TcpEchoServer.create(Environment.TcpServer.port)

    server.start()
}
