package com.daromi.echo

object Environment {
    val serverPort: Int = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
}
