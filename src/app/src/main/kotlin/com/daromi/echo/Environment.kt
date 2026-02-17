package com.daromi.echo

object Environment {
    object TcpServer {
        val port: Int = System.getenv("TCP_SERVER_PORT")?.toIntOrNull() ?: 8080
    }

    object UdsServer {
        val socketPath: String = System.getenv("UDS_SERVER_SOCKET_PATH") ?: "/tmp/echo.sock"
    }
}
