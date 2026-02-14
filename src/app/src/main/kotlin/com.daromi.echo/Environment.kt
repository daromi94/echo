package com.daromi.echo

object Environment {
    object TcpServer {
        val port: Int = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
    }
}
