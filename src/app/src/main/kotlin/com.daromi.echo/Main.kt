package com.daromi.echo

import com.daromi.echo.server.EchoServer

fun main() {
    val server = EchoServer.create(Environment.serverPort)

    server.start()
}
