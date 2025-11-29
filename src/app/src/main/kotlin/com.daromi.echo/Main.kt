package com.daromi.echo

import com.daromi.echo.uds.Server

fun main() {
    val server = Server.create()

    server.start()

    Runtime.getRuntime().addShutdownHook(Thread { server.stop() })
}
